# cloud-minecraft

This directory hosts Minecraft specific implementations of cloud. Their features are summarized in the table below:

| Module | Platform | Minecraft Version | Brigadier Support?|
|--|--|--|--|
| `cloud-bukkit` | Bukkit, Spigot, Paper | 1.8+ | Via commodore for MC 1.13+ |
| `cloud-paper` | Bukkit, Spigot, Paper | 1.8+ | Via Paper for MC 1.15+, Via commodore for MC 1.13+ |
| `cloud-bungee` | BungeeCord | 1.8+ | No |
| `cloud-velocity` | Velocity 1.1.0 | 1.7+ | Yes |

## cloud-bukkit
Bukkit mappings for cloud. If `commodore` is present on the classpath and the server is running at least version 1.13+, Brigadier mappings will be available.

### dependency
```xml
<dependency>
    <groupId>com.intellectualsites</groupId>
    <artifactId>cloud-bukkit</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### setup
Simply do:
```java
final BukkitCommandManager<YourSender> bukkitCommandManager = new BukkitCommandManager<>(
  yourPlugin, yourExecutionCoordinator, forwardMapper, backwardsMapper);
```
The `forwardMapper` is a function that maps your chosen sender type to Bukkit's [CommandSender](https://jd.bukkit.org/org/bukkit/command/CommandSender.html), and the `backwardsMapper`does the opposite. In the case that you don't need a custom sender type, you can simply use `CommandSender`as the generic type and pass `Function.identity()` as the forward and backward mappers.

### commodore
To use commodore, include it as a dependency:
```xml
<dependency>
    <groupId>me.lucko</groupId>
    <artifactId>commodore</artifactId>
    <version>1.9</version>
</dependency>
```

Then initialize the commodore mappings using:
```java
try {
  bukkitCommandManager.registerBrigadier();
} catch (final Exception e) {
  plugin.getLogger().warning("Failed to initialize Brigadier support: " + e.getMessage());
}
```
The mappings will then be created and registered automatically whenever a new command is registered.

**Note:** The mapper must be initialized *before* any commands are registered.

You can check whether or not the running server supports Brigadier, by using `bukkitCommandManager.queryCapability(...)`.

## cloud-paper

An example plugin using the `cloud-paper` API can be found [here](https://github.com/Sauilitired/cloud/tree/master/cloud-minecraft/cloud-bukkit).

`cloud-paper`works on all Bukkit derivatives and has graceful fallbacks for cases where Paper specific features are missing. It is initialized the same way as the Bukkit manager, except `PaperCommandManager`is used instead. When using Paper 1.15+ Brigadier mappings are available even without commodore present.

### dependency
```xml
<dependency>
    <groupId>com.intellectualsites</groupId>
    <artifactId>cloud-paper</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### asynchronous completions
`cloud-paper`supports asynchronous completions when running on Paper. First check if the capability is present, by using `paperCommandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)` and then initialize the asynchronous completion listener by using `paperCommandManager.registerAsynchronousCompletions()`.

## cloud-bungee
BungeeCord mappings for cloud.

### dependency
```xml
<dependency>
    <groupId>com.intellectualsites</groupId>
    <artifactId>cloud-bungee</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### setup
Simply do:
```java
final BungeeCommandManager<YourSender> bungeeCommandManager = new BungeeCommandManager<>(
  yourPlugin, yourExecutionCoordinator, forwardMapper, backwardsMapper);
```
The `forwardMapper` is a function that maps your chosen sender type to Bungee's [CommandSender](https://ci.md-5.net/job/BungeeCord/ws/api/target/apidocs/net/md_5/bungee/api/CommandSender.html), and the `backwardsMapper`does the opposite. In the case that you don't need a custom sender type, you can simply use `CommandSender`as the generic type and pass `Function.identity()` as the forward and backward mappers.

## cloud-velocity

cloud mappings for Velocity 1.1.0-SNAPSHOT.

An example plugin using the `cloud-velocity` API can be found [here](https://github.com/Sauilitired/cloud/tree/master/cloud-minecraft/cloud-velocity-test).

### dependency
```xml
<dependency>
    <groupId>com.intellectualsites</groupId>
    <artifactId>cloud-velocity</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```
Simply do:
```java
final VelocityCommandManager<YourSender> bukkitCommandManager = new VelocityCommandManager<>(
  proxyServer, yourExecutionCoordinator, forwardMapper, backwardsMapper);
```
The `forwardMapper` is a function that maps your chosen sender type to Velocity's [CommandSource](https://jd.velocitypowered.com/1.1.0/com/velocitypowered/api/command/CommandSource.html), and the `backwardsMapper`does the opposite. In the case that you don't need a custom sender type, you can simply use `CommandSource`as the generic type and pass `Function.identity()` as the forward and backward mappers.