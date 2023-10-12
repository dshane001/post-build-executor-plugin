package com.github.dshane001.postbuildexecutorplugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.dshane001.testtemplate.settings.AppSettingsState",
    storages = [Storage("VaultHotDeployerPlugin.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState?> {
    var postBuildCommand = "python3 /c/Workspace/dev_tools_deployment/hot_deploy_family.py " +
            "-f safety_v " +
            "--maven-offline-mode " +
            "--maven-executable=/c/ProgramData/chocolatey/lib/maven/apache-maven-3.9.4/bin/mvn " +
            "--maven-echo-command=echo"

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
