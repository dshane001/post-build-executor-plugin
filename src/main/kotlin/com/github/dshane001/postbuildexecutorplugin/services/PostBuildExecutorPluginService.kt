package com.github.dshane001.postbuildexecutorplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.dshane001.postbuildexecutorplugin.settings.AppSettingsState
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import java.io.IOException

@Service(Service.Level.PROJECT)
class PostBuildExecutorPluginService {

//    init {
//        thisLogger().info(MyBundle.message("projectService", project.name))
//    }

    fun executeCommandInTerminal(project: Project) {
        ApplicationManager.getApplication().invokeLater {
            try {
                val settings: AppSettingsState = AppSettingsState.getInstance()
                val terminalView = TerminalToolWindowManager.getInstance(project)
                val window = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)
                val contentManager = window?.contentManager

                val widget = when (val content = contentManager?.findContent("Post Build Executor")) {
                    null -> terminalView.createLocalShellWidget(project.basePath, "Post Build Executor")
                    else -> TerminalToolWindowManager.getWidgetByContent(content) as ShellTerminalWidget
                }

                widget.executeCommand(settings.postBuildCommand + getModuleOption(project))
            } catch (e: IOException) {
                thisLogger().error("Cannot run command in local terminal. Error: ", e)
            }
        }
    }

    private fun getModuleOption(project: Project): String {
        val fileEditorManager = FileEditorManager.getInstance(project)
        val virtualFile = fileEditorManager.selectedFiles.firstOrNull() ?: return ""
        val module = ProjectRootManager.getInstance(project).fileIndex.getModuleForFile(virtualFile) ?: return ""
        val moduleDir = module.guessModuleDir() ?: return ""

        return " -m " + moduleDir.name
    }
}
