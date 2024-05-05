package com.github.dshane001.postbuildexecutorplugin.services

import com.github.dshane001.postbuildexecutorplugin.actions.GenericBuildAction
import com.github.dshane001.postbuildexecutorplugin.model.CommandType
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service(Service.Level.PROJECT)
class PostBuildExecutorPluginService {
    private var exitCodeFile: File = File("/tmp/post-build-executor.exitCode")
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val actionManager = ActionManager.getInstance() ?: throw IllegalStateException("ActionManager cannot be null")
    private var action: GenericBuildAction? = null
    private var command: String = ""

    @Suppress("DialogTitleCapitalization")
    fun processCommand(
        commandType: CommandType,
        pluginId: String,
        event: AnActionEvent,
        genericBuildAction: GenericBuildAction
    ) {
        val project = event.project ?: throw kotlin.RuntimeException()
        val service = project.service<PostBuildExecutorPluginService>()
        val dataContext = DataManager.getInstance().dataContextFromFocusAsync.blockingGet(5, TimeUnit.SECONDS)

        val originalCommand = getCommand(project, commandType)

        service.action = genericBuildAction
        service.command = "(set -e; $originalCommand); echo $? > ${exitCodeFile.absolutePath}"

        if (service.command.isEmpty()) {
            Messages.showMessageDialog(
                "Post Build Executor Plugin: Error",
                "No command found for command type: " + commandType + ".\n" +
                "Make sure you have a command set in Settings > Tools > Post Build Executor.\n" +
                "Build aborted.",
                Messages.getInformationIcon()
            )
            return
        }

        actionManager.getAction(pluginId).actionPerformed(
            AnActionEvent(
                null,
                dataContext ?: throw NullPointerException("DataContext is null"),
                ActionPlaces.UNKNOWN,
                Presentation(),
                ActionManager.getInstance(),
                0
            )
        )
    }

    fun executeCommandInTerminal(project: Project) {
        ApplicationManager.getApplication().invokeLater {
            try {
                val widget = getPostBuildExecutorShellTerminalWidget(project)

                thisLogger().info("Running command: $command")
                widget.executeCommand(command)
                waitForCommandToFinish(widget)
            } catch (e: IOException) {
                setIconToErrorState()
                thisLogger().error("Cannot run command in local terminal. Error: ", e)
            }
        }
    }

    fun setIconToBuildState() {
        thisLogger().info("Setting icon to build")
        action?.setIconToBuildState()
    }

    fun setIconToExecutingState() {
        thisLogger().info("Setting icon to running")
        action?.setIconToExecutingState()
    }

    fun setIconToErrorState() {
        thisLogger().info("Setting icon to error")
        action?.setIconToErrorState()
    }

    fun setIconToReadyState() {
        thisLogger().info("Setting icon to ready")
        action?.setIconToReadyState()
    }

    private fun getCommand(project: Project, commandType: CommandType): String {
        val fileEditorManager = FileEditorManager.getInstance(project)
        val virtualFile = fileEditorManager.selectedFiles.firstOrNull() ?: return ""
        val module = ProjectRootManager.getInstance(project).fileIndex.getModuleForFile(virtualFile) ?: return ""
        val moduleDir = module.guessModuleDir() ?: throw IllegalStateException("Could not guess module dir of module: $module")
        val moduleDirName = moduleDir.name
        val moduleName = module.name
        val postBuildCommandString = commandType.getCommand()

        // replace if exists
        return postBuildCommandString.replace("\$MODULE_DIR", moduleDirName).replace("\$MODULE_NAME", moduleName) // get the module
    }

    private fun waitForCommandToFinish(widget: ShellTerminalWidget) {
        executor.submit {
            do {
                TimeUnit.MILLISECONDS.sleep(500)
            } while (widget.hasRunningCommands())

            thisLogger().info("Command done")

            val exitCode = readCommandExitCode()
            if (exitCode == null || exitCode != 0) {
                setIconToErrorState()
            }
            else {
                setIconToReadyState()
            }
        }
    }

    private fun readCommandExitCode(): Int? {
        try {
            // Check if the file exists
            if (!exitCodeFile.exists()) {
                println("File $exitCodeFile not found.")
                return null
            }

            // Read the integer from the file
            val content = exitCodeFile.readText().trim()
            return content.toIntOrNull()
        } catch (e: FileNotFoundException) {
            println("File $exitCodeFile not found.")
            return null
        } catch (e: NumberFormatException) {
            println("Unable to parse integer from file $exitCodeFile.")
            return null
        }
    }

    private fun getPostBuildExecutorShellTerminalWidget(project: Project): ShellTerminalWidget {
        val terminalView = TerminalToolWindowManager.getInstance(project)
        val window = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)
        val contentManager = window?.contentManager

        val widget = when (val content = contentManager?.findContent("Post Build Executor")) {
            null -> terminalView.createLocalShellWidget(project.basePath, "Post Build Executor")
            else -> TerminalToolWindowManager.getWidgetByContent(content) as ShellTerminalWidget
        }

        return widget
    }
}
