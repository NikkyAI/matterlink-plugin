package moe.nikky.matterlink

import kotlinx.serialization.Serializable
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by nikky on 09/07/18.
 * @author Nikky
 */

@Serializable
data class Paste(
    val encrypted: Boolean = false,
    val description: String,
    val sections: List<PasteSection>
)

@Serializable
data class PasteSection(
    val name: String,
    val syntax: String = "text",
    val contents: String
)

@Serializable
data class PasteResponse(
    val id: String,
    val link: String
)

object PasteUtil {
    private const val DEFAULT_KEY = "uKJoyicVJFnmpnrIZMklOURWxrCKXYaiBWOzPmvon"

    fun paste(paste: Paste, key: String = ""): PasteResponse {
        val apiKey = key.takeIf { it.isNotBlank() } ?: DEFAULT_KEY

        val url = URL("https://api.paste.ee/v1/pastes")
        val http = url.openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true

        val out = jsonNonstrict.stringify(Paste.serializer(), paste)
                .toByteArray()

        http.setFixedLengthStreamingMode(out.size)
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        http.setRequestProperty("X-Auth-Token", apiKey)
        http.connect()
        http.outputStream.use { os ->
            os.write(out)
        }

        val textResponse = http.inputStream.bufferedReader().use { it.readText() }
        logger.fine("response: $textResponse")
//        val jsonObject = jankson.load(http.inputStream)
        return jsonNonstrict.parse(PasteResponse.serializer(), textResponse)
    }
}