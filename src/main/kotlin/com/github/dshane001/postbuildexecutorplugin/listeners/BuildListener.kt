package com.github.dshane001.postbuildexecutorplugin.listeners

import com.github.dshane001.postbuildexecutorplugin.model.BuildStatus
import com.github.dshane001.postbuildexecutorplugin.servicelocator.ServiceLocator
import com.intellij.openapi.project.Project
import com.intellij.task.ProjectTaskContext
import com.intellij.task.ProjectTaskListener
import com.intellij.task.ProjectTaskManager

class BuildListener(private val project: Project) : ProjectTaskListener {
    private val buildNotifier = ServiceLocator.buildNotifier

    override fun finished(result: ProjectTaskManager.Result) {
        buildNotifier(
            buildStatus = when {
                result.hasErrors() -> BuildStatus.ERROR
                result.isAborted -> BuildStatus.CANCELLED
                else -> BuildStatus.SUCCESS
            },
            project = project
        )
    }

    override fun started(context: ProjectTaskContext) {
        buildNotifier(BuildStatus.STARTED, project)
    }
}