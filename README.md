# jCommands [![](https://jitpack.io/v/divios/JCommands.svg)](https://jitpack.io/#divios/JCommands)
Light weight and simple library to parse spigot commands in the go.

## Usage

jCommands provides a very simple command creation abstraction. Here is an example:

```java
    JCommand.create("example")
        .assertPermission("example.permission.node")
        .assertUsage("/example <player>")
        .withArguments(new PlayerArgument("target"))
        .executes((commandSender, args) -> {
            args.get("target").getAsPlayer().sendMessage("This is an example");
        })
        .register();
```

As you can see, it comes with various methods to assert the functions of the command and 
some predefined arguments that will tabComplete automatically for you, in this case, 
PlayerArgument returns all the online players in the server, although it can be override. 


# Instalation

To get the jar, either download it from the releases tab either here on [GitHub](https://github.com/divios/jCommands/releases) or [build it locally](https://github.com/divios/jCommands#build-locally).

## With Jitpack:

Gradle:

```groovy
repositories {
        maven { url 'https://jitpack.io' }
}

```

```groovy
dependencies {
        compileOnly 'com.github.divios:jCommands:Tag'
}
```

Maven:

```xml
<repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
        <groupId>com.github.divios</groupId>
        <artifactId>jCommands</artifactId>
        <version>Tag</version>
        <scope>provided</scope>
</dependency>
```
Replace `Tag` with a release tag for jParser. Example: `1.0`. You can also use `master` as the tag to get the latest version, though you will have to clear your maven caches in order to update it.

## Build locally:

For Windows, use Git Bash. For Linux or OSX, just ensure you have Git installed. Navigate to the directory where you want to clone the repository, and run:

```
git clone https://github.com/divios/jCommands
cd RedLib
./gradlew jar
```

After running these commands, the jar will be at `build/libs/jCommands.jar`.
You may also need to add the jar to your classpath. After that, you should be good to go!
