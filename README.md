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

More examples of the api usage:

<details>
	<summary><b>Simple arguments</b></summary>
    
```java
JCommand.create("simpleArguments")
        .assertRequirements((commandSender) -> commandSender.isOp())   // The command will only be executed if certain conditions are met
        .withArguments(new IntegerArgument("int"), new BooleanArgument("boolean"))  // The arguments are parsed in order
        .executes((commandSender, args) -> {
            int number = args.get("int").getAsInt();    // You can get the argument by name
            boolean bool = args.get(1).getAsBoolean();  // Or by position
         })
        .register();
```
    
</details>  

<details>
	<summary><b>Executors</b></summary>
    
```java
JCommand.create("executorExample")
        .executesPlayer((player, args) -> {    // Will only be executed if the command is called by a player
             // Do something
        })
        .executesConsole((console, args) -> {  // Will only be executed if the command is called from console
             // Do something
         })
        .executes((console, args) -> {         // Executes no matter who called the command
             // Do something
         })
        .register();
```
    
</details>  

<details>
	<summary><b>subCommands</b></summary>
    
```java
JCommand.create("essentials")
        .withSubcommands(JCommand.create("warp")
                 .withArguments(new StringArgument("permission"))
                 .withArguments(new StringArgument("groupName"))
                 .executes((sender, args) -> {
                     //perm group add code
                 })
                )
         .withSubcommands(JCommand.create("setWarp")
                  .withArguments(new StringArgument("permission"))
                  .withArguments(new StringArgument("userName"))
                  .executes((sender, args) -> {
                      //perm user add code
                  })
                )
          .withSubcommands(JCommand.create("spawn")
                  .withArguments(new StringArgument("permission"))
                  .withArguments(new StringArgument("userName"))
                  .executes((sender, args) -> {
                      //perm user remove code
                  })
           )
          .register();
```
    
</details> 


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
