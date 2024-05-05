package com.github.dshane001.postbuildexecutorplugin.model

import com.github.dshane001.postbuildexecutorplugin.settings.AppSettingsState

enum class CommandType: CommandString {
    FILE {
        override fun getCommand(): String {
            return settings.postBuildFileCommand
        }
    },
    MODULE {
        override fun getCommand(): String {
            return settings.postBuildModuleCommand
        }
    },
    PROJECT {
        override fun getCommand(): String {
            return settings.postBuildProjectCommand
        }
    };

    val settings: AppSettingsState = AppSettingsState.getInstance()
}