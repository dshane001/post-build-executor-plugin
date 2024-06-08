package com.github.dshane001.postbuildexecutorplugin.toolwindow

import com.intellij.build.BuildTextConsoleView
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManager
import java.io.File
import java.nio.charset.StandardCharsets

class CommandOutputConsoleView(val project: Project) {
    private val buildTextConsoleView = getBuildTextConsoleView()

    fun printError(errorMessage: String) {
        buildTextConsoleView.append(errorMessage, false)
        setFocusOnConsoleView()
        return
    }

    fun printMessage(errorMessage: String) {
        buildTextConsoleView.append(errorMessage, true)
        return
    }

    fun setFocusOnConsoleView() {
        ApplicationManager.getApplication().invokeLater {
            ToolWindowManager.getInstance(project).getToolWindow(POST_BUILD_EXECUTOR_TOOL_NAME)!!.activate(null)
        }
    }

    private fun getBuildTextConsoleView(): BuildTextConsoleView {
        val toolWindow: ToolWindow = ToolWindowManager.getInstance(project).getToolWindow(POST_BUILD_EXECUTOR_TOOL_NAME)!!
        val contentManager: ContentManager = toolWindow.contentManager
        val content: Content = contentManager.findContent(POST_BUILD_EXECUTOR_TOOL_NAME)
        return content.component as BuildTextConsoleView
    }

    fun clear() {
        // Reset all ANSI attributes to their default values
        printMessage("\u001B[0m")
        buildTextConsoleView.clear()
    }

    fun runCommand(commands: MutableList<String>, onSuccess: () -> Unit, onError: (Int) -> Unit, onExit: () -> Unit) {
        val processBuilder = ProcessBuilder(commands)
        processBuilder.directory(File(project.basePath!!))
        val process = processBuilder.start()
        val processHandler = ColoredProcessHandler(process, commands.joinToString(" "), StandardCharsets.UTF_8)
        buildTextConsoleView.attachToProcess(processHandler)
        processHandler.addProcessListener(object : ProcessAdapter() {
            override fun processTerminated(event: ProcessEvent) {
                val exitCode = process.exitValue()
                printMessage("\n**** Process terminated with exit code: $exitCode")
                if (exitCode != 0) {
                    onError(exitCode)
                }
                else {
                    onSuccess()
                }
                onExit()

            }
        })

        processHandler.startNotify()
    }
}