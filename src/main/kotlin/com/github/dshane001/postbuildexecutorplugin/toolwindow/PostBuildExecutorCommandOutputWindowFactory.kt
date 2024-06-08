package com.github.dshane001.postbuildexecutorplugin.toolwindow

import com.intellij.build.BuildTextConsoleView
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


class PostBuildExecutorCommandOutputWindowFactory: ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myBuildTextConsoleView = BuildTextConsoleView(project, true, emptyList())
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(myBuildTextConsoleView.component, "Post Build Executor", false)
        toolWindow.contentManager.addContent(content)
    }
}

val POST_BUILD_EXECUTOR_TOOL_NAME: String = "Post Build Executor"