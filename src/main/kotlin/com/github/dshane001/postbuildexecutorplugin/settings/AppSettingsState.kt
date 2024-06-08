package com.github.dshane001.postbuildexecutorplugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.dshane001.postbuildexecutorplugin.settings.AppSettingsState",
    storages = [Storage("PostBuildExecutorPlugin.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState?> {
    var postBuildProjectCommand = ""
    var postBuildModuleCommand = ""
    var postBuildFileCommand = ""
    var commandInterpreter = "/usr/bin/bash -c"

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        private var instance: AppSettingsState? = null

        fun getInstance() = (instance ?: ApplicationManager.getApplication().getService(
            AppSettingsState::class.java
        ))!!
    }
}
