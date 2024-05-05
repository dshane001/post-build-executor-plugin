// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.dshane001.postbuildexecutorplugin.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class AppSettingsComponent {
    val myMainPanel: JPanel
    private val postBuildProjectCommandText = JBTextField()
    private val postBuildModuleCommandText = JBTextField()
    private val postBuildFileCommandText = JBTextField()

    init {
        myMainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Command to execute post build (Project): "), postBuildProjectCommandText, 1, true)
            .addLabeledComponent(JBLabel("Command to execute post build (Module): "), postBuildModuleCommandText, 1, true)
            .addLabeledComponent(JBLabel("Command to execute post build (File): "), postBuildFileCommandText, 1, true)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = postBuildFileCommandText

    var postBuildProjectCommand: String
        get() = postBuildProjectCommandText.text
        set(newText) {
            postBuildProjectCommandText.text = newText
        }

    var postBuildModuleCommand: String
        get() = postBuildModuleCommandText.text
        set(newText) {
            postBuildModuleCommandText.text = newText
        }

    var postBuildFileCommand: String
        get() = postBuildFileCommandText.text
        set(newText) {
            postBuildFileCommandText.text = newText
        }
}