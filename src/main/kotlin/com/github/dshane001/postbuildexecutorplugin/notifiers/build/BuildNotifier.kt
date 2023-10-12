package com.github.dshane001.postbuildexecutorplugin.notifiers.build

import com.github.dshane001.postbuildexecutorplugin.model.BuildStatus
import com.intellij.openapi.project.Project

fun interface BuildNotifier {
    operator fun invoke(buildStatus: BuildStatus, project: Project)
}