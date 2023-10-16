package com.github.dshane001.postbuildexecutorplugin.services

import com.github.dshane001.postbuildexecutorplugin.model.CommandType
import com.github.dshane001.postbuildexecutorplugin.model.CommandType.NONE
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import java.io.IOException
import java.lang.IllegalStateException

@Service(Service.Level.PROJECT)
class PostBuildExecutorPluginService {
    var commandType: CommandType = NONE

//    init {
//        thisLogger().info(MyBundle.message("projectService", project.name))
//    }

    fun executeCommandInTerminal(project: Project) {
        if (commandType == NONE) {
            return
        }

        ApplicationManager.getApplication().invokeLater {
            try {
                val terminalView = TerminalToolWindowManager.getInstance(project)
                val window = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)
                val contentManager = window?.contentManager

                val widget = when (val content = contentManager?.findContent("Post Build Executor")) {
                    null -> terminalView.createLocalShellWidget(project.basePath, "Post Build Executor")
                    else -> TerminalToolWindowManager.getWidgetByContent(content) as ShellTerminalWidget
                }

                widget.executeCommand(getCommand(commandType, project))
            } catch (e: IOException) {
                thisLogger().error("Cannot run command in local terminal. Error: ", e)
            } finally {
                commandType = NONE
            }
        }
    }

    private fun getCommand(commandType: CommandType, project: Project): String {
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
}
