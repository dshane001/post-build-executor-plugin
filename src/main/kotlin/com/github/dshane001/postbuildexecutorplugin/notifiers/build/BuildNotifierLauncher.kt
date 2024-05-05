package com.github.dshane001.postbuildexecutorplugin.notifiers.build

import com.github.dshane001.postbuildexecutorplugin.model.BuildStatus
import com.github.dshane001.postbuildexecutorplugin.services.PostBuildExecutorPluginService
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BuildNotifierLauncher: BuildNotifier {
    override fun invoke(buildStatus: BuildStatus, project: Project) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            runAction(buildStatus, project)
        }
    }

    private fun runAction(buildStatus: BuildStatus, project: Project) {
        val service = project.service<PostBuildExecutorPluginService>()
        thisLogger().info("Build status: $buildStatus")

        when (buildStatus) {
            BuildStatus.STARTED -> {
                service.setIconToBuildState()
            }
            BuildStatus.SUCCESS -> {
                service.setIconToExecutingState()
                service.executeCommandInTerminal(project)
            }
            BuildStatus.ERROR -> {
                service.setIconToErrorState()
            }
            BuildStatus.CANCELLED -> {
                service.setIconToReadyState()
            }
        }
    }
}