# post-build-executor-plugin
The Post Build Executor Plugin enables a command to be executed after a build was successful. 

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "post-build-executor-plugin"</kbd> >
  <kbd>Install</kbd>

- Manually:

  Download the [latest release](https://github.com/dshane001/post-build-executor-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Usage
To use this, simply install the plugin and go to <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>Post Build Executor Plugin</kbd> and set the commands you would like to execute.

3 Types of commands can be triggered:
- Per file builds
- Per module builds
- Per project builds

For each of the different types of build you can specify a custom command.

The command will only get executed if the build is successful.

The command will only get executed if the build was triggered by one of these specific actions:
- <kbd>Tools</kbd> > <kbd>Build Project and Run Command</kbd>
- <kbd>Tools</kbd> > <kbd>Build Module and Run Command</kbd>
- <kbd>Tools</kbd> > <kbd>Recompile File and Run Command</kbd>

## Varible Interpolation
You can use the following variables in your command (they will be replaced by this plugin):

| Variable     | Description |
|--------------|-------------|
| $MODULE_DIR  | The name of the module directory the current active editor or target module points to |
| $MODULE_NAME | The maven name for the module in which the current active editor or target module points to |

These can be useful if you need to execute some command that depends on the module that got compiled (or the module that contains the current active editor)

<!-- Plugin description -->
This Fancy IntelliJ Platform Plugin will allow you to run a custom command after each build.
<!-- Plugin description end -->
---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
