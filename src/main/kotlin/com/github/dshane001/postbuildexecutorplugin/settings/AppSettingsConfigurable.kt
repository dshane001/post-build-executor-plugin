// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.github.dshane001.postbuildexecutorplugin.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered in an applicationConfigurable EP
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Post Build Executor"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return mySettingsComponent!!.preferredFocusedComponent
    }

    override fun createComponent(): JComponent {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.myMainPanel
    }

    override fun isModified(): Boolean {
        val settings: AppSettingsState = AppSettingsState.getInstance()
        var modified = false
        modified = modified || mySettingsComponent!!.postBuildProjectCommand != settings.postBuildProjectCommand
        modified = modified || mySettingsComponent!!.postBuildModuleCommand != settings.postBuildModuleCommand
        modified = modified || mySettingsComponent!!.postBuildFileCommand != settings.postBuildFileCommand
        return modified
    }

    override fun apply() {
        val settings: AppSettingsState = AppSettingsState.getInstance()
        settings.postBuildProjectCommand = mySettingsComponent!!.postBuildProjectCommand
        settings.postBuildModuleCommand = mySettingsComponent!!.postBuildModuleCommand
        settings.postBuildFileCommand = mySettingsComponent!!.postBuildFileCommand
    }

    override fun reset() {
        val settings: AppSettingsState = AppSettingsState.getInstance()
        mySettingsComponent!!.postBuildProjectCommand = settings.postBuildProjectCommand
        mySettingsComponent!!.postBuildModuleCommand = settings.postBuildModuleCommand
        mySettingsComponent!!.postBuildFileCommand = settings.postBuildFileCommand
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}