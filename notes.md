## crashes


executing command from matterbridge:

```
[15:18:26 WARN]: [Matterlink] Task #3 for Matterlink v0.1 generated an exception
org.bukkit.command.CommandException: Unhandled exception executing 'list' in org.bukkit.craftbukkit.v1_15_R1.command.VanillaCommandWrapper(list)
        at org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:169) ~[patched_1.15.2.jar:git-Paper-194]
        at org.bukkit.craftbukkit.v1_15_R1.CraftServer.dispatchCommand(CraftServer.java:752) ~[patched_1.15.2.jar:git-Paper-194]
        at moe.nikky.matterlink.Matterlink$commandSenderFor$1.execute(Matterlink.kt:244) ~[?:?]
        at moe.nikky.matterlink.command.CustomCommand$execute$2.invokeSuspend(CustomCommand.kt:37) ~[?:?]
        at moe.nikky.matterlink.command.CustomCommand$execute$2.invoke(CustomCommand.kt) ~[?:?]
        at kotlinx.coroutines.intrinsics.UndispatchedKt.startUndispatchedOrReturnIgnoreTimeout(Undispatched.kt:102) ~[?:?]
        at kotlinx.coroutines.TimeoutKt.setupTimeout(Timeout.kt:111) ~[?:?]
        at kotlinx.coroutines.TimeoutKt.withTimeout(Timeout.kt:32) ~[?:?]
        at moe.nikky.matterlink.command.CustomCommand.execute(CustomCommand.kt:36) ~[?:?]
        at moe.nikky.matterlink.command.BridgeCommandRegistry.handleCommand(BridgeCommandRegistry.kt:54) ~[?:?]
        at moe.nikky.matterlink.handlers.ServerChatHandler.processApiMessage(ServerChatHandler.kt:45) ~[?:?]
        at moe.nikky.matterlink.handlers.ServerChatHandler$run$1.invokeSuspend(ServerChatHandler.kt:19) ~[?:?]
        at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33) ~[?:?]
        at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:56) ~[?:?]
        at kotlinx.coroutines.EventLoopImplBase.processNextEvent(EventLoop.common.kt:272) ~[?:?]
        at kotlinx.coroutines.BlockingCoroutine.joinBlocking(Builders.kt:79) ~[?:?]
        at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking(Builders.kt:54) ~[?:?]
        at kotlinx.coroutines.BuildersKt.runBlocking(Unknown Source) ~[?:?]
        at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking$default(Builders.kt:36) ~[?:?]
        at kotlinx.coroutines.BuildersKt.runBlocking$default(Unknown Source) ~[?:?]
        at moe.nikky.matterlink.handlers.ServerChatHandler.run(ServerChatHandler.kt:15) ~[?:?]
        at org.bukkit.craftbukkit.v1_15_R1.scheduler.CraftTask.run(CraftTask.java:84) ~[patched_1.15.2.jar:git-Paper-194]
        at org.bukkit.craftbukkit.v1_15_R1.scheduler.CraftScheduler.mainThreadHeartbeat(CraftScheduler.java:452) ~[patched_1.15.2.jar:git-Paper-194]
        at net.minecraft.server.v1_15_R1.MinecraftServer.b(MinecraftServer.java:1246) ~[patched_1.15.2.jar:git-Paper-194]
        at net.minecraft.server.v1_15_R1.DedicatedServer.b(DedicatedServer.java:430) ~[patched_1.15.2.jar:git-Paper-194]
        at net.minecraft.server.v1_15_R1.MinecraftServer.a(MinecraftServer.java:1164) ~[patched_1.15.2.jar:git-Paper-194]
        at net.minecraft.server.v1_15_R1.MinecraftServer.run(MinecraftServer.java:958) ~[patched_1.15.2.jar:git-Paper-194]
        at java.lang.Thread.run(Thread.java:748) [?:1.8.0_242]
Caused by: java.lang.IllegalArgumentException: Cannot make moe.nikky.matterlink.MessageInterceptingCommandRunner@7814db7f a vanilla command listener
        at org.bukkit.craftbukkit.v1_15_R1.command.VanillaCommandWrapper.getListener(VanillaCommandWrapper.java:86) ~[patched_1.15.2.jar:git-Paper-194]
        at org.bukkit.craftbukkit.v1_15_R1.command.VanillaCommandWrapper.execute(VanillaCommandWrapper.java:44) ~[patched_1.15.2.jar:git-Paper-194]
        at org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:159) ~[patched_1.15.2.jar:git-Paper-194]
        ... 27 more
```

sending command from ingame:

```
[15:20:24 WARN]: [Matterlink] Task #4 for Matterlink v0.1 generated an exception
org.bukkit.command.CommandException: Unhandled exception executing 'list' in org.bukkit.craftbukkit.v1_15_R1.command.VanillaCommandWrapper(list)
        at org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:169) ~[patched_1.15.2.jar:git-Paper-194]
        at org.bukkit.craftbukkit.v1_15_R1.CraftServer.dispatchCommand(CraftServer.java:752) ~[patched_1.15.2.jar:git-Paper-194]
        at moe.nikky.matterlink.Matterlink$commandSenderFor$1.execute(Matterlink.kt:244) ~[?:?]
        at moe.nikky.matterlink.command.CustomCommand$execute$2.invokeSuspend(CustomCommand.kt:37) ~[?:?]
        at moe.nikky.matterlink.command.CustomCommand$execute$2.invoke(CustomCommand.kt) ~[?:?]
        at kotlinx.coroutines.intrinsics.UndispatchedKt.startUndispatchedOrReturnIgnoreTimeout(Undispatched.kt:102) ~[?:?]
        at kotlinx.coroutines.TimeoutKt.setupTimeout(Timeout.kt:111) ~[?:?]
        at kotlinx.coroutines.TimeoutKt.withTimeout(Timeout.kt:32) ~[?:?]
        at moe.nikky.matterlink.command.CustomCommand.execute(CustomCommand.kt:36) ~[?:?]
        at moe.nikky.matterlink.command.BridgeCommandRegistry.handleCommand(BridgeCommandRegistry.kt:81) ~[?:?]
        at moe.nikky.matterlink.handlers.ChatProcessor.processTask(ChatProcessor.kt:50) ~[?:?]
        at moe.nikky.matterlink.handlers.ChatProcessor$run$1.invokeSuspend(ChatProcessor.kt:16) ~[?:?]
        at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33) ~[?:?]
        at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:56) ~[?:?]
        at kotlinx.coroutines.EventLoopImplBase.processNextEvent(EventLoop.common.kt:272) ~[?:?]
        at kotlinx.coroutines.BlockingCoroutine.joinBlocking(Builders.kt:79) ~[?:?]
        at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking(Builders.kt:54) ~[?:?]
        at kotlinx.coroutines.BuildersKt.runBlocking(Unknown Source) ~[?:?]
        at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking$default(Builders.kt:36) ~[?:?]
        at kotlinx.coroutines.BuildersKt.runBlocking$default(Unknown Source) ~[?:?]
        at moe.nikky.matterlink.handlers.ChatProcessor.run(ChatProcessor.kt:13) ~[?:?]
        at org.bukkit.craftbukkit.v1_15_R1.scheduler.CraftTask.run(CraftTask.java:84) ~[patched_1.15.2.jar:git-Paper-194]
        at org.bukkit.craftbukkit.v1_15_R1.scheduler.CraftScheduler.mainThreadHeartbeat(CraftScheduler.java:452) ~[patched_1.15.2.jar:git-Paper-194]
        at net.minecraft.server.v1_15_R1.MinecraftServer.b(MinecraftServer.java:1246) ~[patched_1.15.2.jar:git-Paper-194]
        at net.minecraft.server.v1_15_R1.DedicatedServer.b(DedicatedServer.java:430) ~[patched_1.15.2.jar:git-Paper-194]
        at net.minecraft.server.v1_15_R1.MinecraftServer.a(MinecraftServer.java:1164) ~[patched_1.15.2.jar:git-Paper-194]
        at net.minecraft.server.v1_15_R1.MinecraftServer.run(MinecraftServer.java:958) ~[patched_1.15.2.jar:git-Paper-194]
        at java.lang.Thread.run(Thread.java:748) [?:1.8.0_242]
Caused by: java.lang.IllegalArgumentException: Cannot make moe.nikky.matterlink.MessageInterceptingCommandRunner@35b66604 a vanilla command listener
        at org.bukkit.craftbukkit.v1_15_R1.command.VanillaCommandWrapper.getListener(VanillaCommandWrapper.java:86) ~[patched_1.15.2.jar:git-Paper-194]
        at org.bukkit.craftbukkit.v1_15_R1.command.VanillaCommandWrapper.execute(VanillaCommandWrapper.java:44) ~[patched_1.15.2.jar:git-Paper-194]
        at org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:159) ~[patched_1.15.2.jar:git-Paper-194]
        ... 27 more
```