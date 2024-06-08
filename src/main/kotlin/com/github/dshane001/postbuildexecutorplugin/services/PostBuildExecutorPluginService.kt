package com.github.dshane001.postbuildexecutorplugin.services

import com.github.dshane001.postbuildexecutorplugin.actions.GenericBuildAction
import com.github.dshane001.postbuildexecutorplugin.model.BuildStatus
import com.github.dshane001.postbuildexecutorplugin.model.CommandType
import com.github.dshane001.postbuildexecutorplugin.settings.AppSettingsState
import com.github.dshane001.postbuildexecutorplugin.toolwindow.CommandOutputConsoleView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages


@Service(Service.Level.PROJECT)
class PostBuildExecutorPluginService {
    private var action: GenericBuildAction? = null
    private var command: String? = null
    private var settings: AppSettingsState = AppSettingsState.getInstance()

    fun processCommand(
        commandType: CommandType,
        event: AnActionEvent,
        genericBuildAction: GenericBuildAction
    ) {
        val project = event.project ?: throw NoSuchElementException("Could not get project from event")
        val commandOutputConsoleView = CommandOutputConsoleView(project)

        try {
            commandOutputConsoleView.clear()
            commandOutputConsoleView.printMessage("Triggering Intellij Build Type: %s\n".format(commandType.getBuildActionName()))
            val originalCommand = getCommand(project, commandType) ?: run {
                commandOutputConsoleView.printError("Could not get the Command to execute, bailing out.")
                return
            }

            action = genericBuildAction
            command = originalCommand

            commandType.triggerBuild(project)
        }
        catch (e: Exception) {
            commandOutputConsoleView.printError("Could not process command: ${e.message}, bailing out")
            throw e
        }
    }

    fun buildFinished(project: Project, buildStatus: BuildStatus) {
        val commandOutputConsoleView = CommandOutputConsoleView(project)
        commandOutputConsoleView.printMessage("  -> Build Status: $buildStatus\n")

        if (action == null) {
            commandOutputConsoleView.printError("No action found, could not execute the command.")
            return
        }

        when (buildStatus) {
            BuildStatus.STARTED -> {
                setIconToBuildState()
            }
            BuildStatus.SUCCESS -> {
                setIconToExecutingState()
                executeCommandInTerminal(project)
            }
            BuildStatus.ERROR -> {
                setIconToErrorState()
            }
            BuildStatus.CANCELLED -> {
                setIconToReadyState()
            }
        }
    }

    private fun executeCommandInTerminal(project: Project) {
        val commandOutputConsoleView = CommandOutputConsoleView(project)

        try {
            setIconToExecutingState()
            commandOutputConsoleView.printMessage("Running command...\n")
            commandOutputConsoleView.printMessage("$ ")

            val commands: MutableList<String> = mutableListOf()
            val commandInterpreter = settings.commandInterpreter
            if (commandInterpreter.isNotBlank()) {
                commands.addAll(settings.commandInterpreter.split(" "))
            }
            commands.add(command!!)
            commandOutputConsoleView.runCommand(
                commands,
                onSuccess = {
                    setIconToReadyState()
                },
                onError = {
                    exitCode ->
                        setIconToErrorState()
                        commandOutputConsoleView.setFocusOnConsoleView()
                },
                onExit = {
                    action = null
                    command = null
                }
            )
        } catch (e: Exception) {
            commandOutputConsoleView.printError("Error executing process: ${e.message}")
            setIconToErrorState()
        }
    }

    private fun setIconToBuildState() {
        thisLogger().info("Setting icon to build")
        action?.setIconToBuildState()
    }

    private fun setIconToExecutingState() {
        thisLogger().info("Setting icon to running")
        action?.setIconToExecutingState()
    }

    private fun setIconToErrorState() {
        thisLogger().info("Setting icon to error")
        action?.setIconToErrorState()
    }

    private fun setIconToReadyState() {
        thisLogger().info("Setting icon to ready")
        action?.setIconToReadyState()
    }

    private fun getCommand(project: Project, commandType: CommandType): String? {
        var postBuildCommandString = commandType.getCommand()
        if (postBuildCommandString.isEmpty()) {
            Messages.showErrorDialog(
                project,
                """
                    No command found for action ${commandType.getBuildActionName()}.
                    Make sure you have a command set in Settings > Tools > Post Build Executor. Build aborted.
                """,
                "PostBuildExecutorPlugin Error"
            )
            return null
        }

        if (postBuildCommandString.contains("\$MODULE_DIR")) {
            val moduleDirectoryRetrieverService = project.service<ModuleDirectoryRetrieverService>()
            val moduleDir = moduleDirectoryRetrieverService.getModuleDirectory(project) ?: return null
            postBuildCommandString = postBuildCommandString.replace("\$MODULE_DIR", moduleDir)
        }

        return postBuildCommandString
    }
}
