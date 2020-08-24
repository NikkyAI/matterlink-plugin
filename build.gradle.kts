plugins {
    id("ru.endlesscode.bukkitgradle") version "0.8.2"
    kotlin("jvm") version "1.3.70"
    id("kotlinx-serialization") version "1.3.70"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

// Project information
group = "com.example"
description = "My first Bukkit plugin with Gradle"
version = "0.1"

bukkit {
    // Attributes for plugin.yml
    meta.apply {
        setName("Matterlink")
        setDescription("My amazing plugin, that doing nothing")
        setMain("moe.nikky.matterlink.Matterlink")
        setVersion(project.version)
        setUrl("https://github.com/NikkyAI/matterlink-plugin")
        setAuthors(listOf("NikkyAI"))
    }
    // INFO: Here used default values
    run.apply {
        // Core type. It can be 'spigot' or 'paper'
        setCore("paper")
        // Accept EULA
        eula = true
        // Set online-mode flag
        onlineMode = false
        // Debug mode (listen 5005 port, if you use running from IDEA this option will be ignored)
        debug = true
        // Set server encoding (flag -Dfile.encoding)
        encoding = "UTF-8"
        // JVM arguments
        javaArgs = "-Xmx1G"
        // Bukkit arguments
        bukkitArgs = ""
    }
}


//repositories {
//    spigot() // Adds spigot repo
//}

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://papermc.io/repo/repository/maven-public/") {
        name = "paper"
    }
}
// Let's add needed API to project
dependencies {
//    compileOnly(paperApi())
    implementation(kotlin("stdlib"))
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.3.5")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-runtime", version = "0.20.0")

    implementation(group = "com.github.kittinunf.fuel", name = "fuel", version = "2.2.2")
    implementation(group = "com.github.kittinunf.fuel", name = "fuel-kotlinx-serialization", version = "2.2.2")
    implementation(group = "com.github.kittinunf.fuel", name = "fuel-coroutines", version = "2.2.2")
    implementation(group = "com.github.kittinunf.result", name = "result", version = "2.2.0")

    implementation(group = "blue.endless", name = "jankson", version = "1.2.0")

    compileOnly((ext["paperApi"] as groovy.lang.Closure<Dependency >).invoke())
//    compileOnly(group = "com.destroystokyo.paper", name = "paper-api", version = "1.15.2-R0.1-SNAPSHOT")
}

afterEvaluate {
    tasks.getByName<de.undercouch.gradle.tasks.download.Download>("downloadPaperclip") {
        src("https://papermc.io/ci/job/Paper-1.15/lastSuccessfulBuild/artifact/paperclip.jar")
    }
}
