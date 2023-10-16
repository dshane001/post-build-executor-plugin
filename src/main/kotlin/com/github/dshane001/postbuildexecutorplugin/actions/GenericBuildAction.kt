package com.github.dshane001.postbuildexecutorplugin.actions

import com.github.dshane001.postbuildexecutorplugin.model.CommandType
import com.github.dshane001.postbuildexecutorplugin.services.PostBuildExecutorPluginService
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.service

open class GenericBuildAction(private val commandType: CommandType, private val pluginId: String): AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val actionManager = ActionManager.getInstance()
        val project = event.project ?: throw kotlin.RuntimeException()
        val service = project.service<PostBuildExecutorPluginService>()
        service.commandType = commandType
        actionManager.getAction(pluginId).actionPerformed(
            AnActionEvent(
                null, DataManager.getInstance().dataContext,
                ActionPlaces.UNKNOWN, Presentation(),
                ActionManager.getInstance(), 0
            )
        )
    }
}
