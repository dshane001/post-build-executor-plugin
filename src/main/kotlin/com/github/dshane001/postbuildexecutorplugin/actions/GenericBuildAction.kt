package com.github.dshane001.postbuildexecutorplugin.actions

import com.github.dshane001.postbuildexecutorplugin.model.CommandType
import com.github.dshane001.postbuildexecutorplugin.services.PostBuildExecutorPluginService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.IconLoader
import com.intellij.util.application
import javax.swing.Icon

open class GenericBuildAction(private val commandType: CommandType): AnAction() {
    private val readyIcon: Icon = IconLoader.getIcon("/icons/checkmark-green-16x16.png", GenericBuildAction::class.java.classLoader)
    private val buildIcon: Icon = IconLoader.getIcon("/icons/hammer-orange-16x16.png", GenericBuildAction::class.java.classLoader)
    private val executingIcon: Icon = IconLoader.getIcon("/icons/arrow-24-white-16x16.png", GenericBuildAction::class.java.classLoader)
    private val errorIcon: Icon = IconLoader.getIcon("/icons/x-mark-red-16x16.png", GenericBuildAction::class.java.classLoader)

    private var icon: Icon = readyIcon

    init {
        templatePresentation.icon = icon
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(event: AnActionEvent) {
        event.presentation.icon = icon
        super.update(event)
    }

    override fun actionPerformed(event: AnActionEvent) {
        thisLogger().info("Event received: $commandType")

        val project = getEventProject(event) ?: throw IllegalStateException("Project cannot be null")
        val service = project.service<PostBuildExecutorPluginService>()
        service.processCommand(commandType, event, this)
    }

    fun setIconToReadyState() {
        thisLogger().info("Setting icon to ready")
        setIconAndReloadToolbars(readyIcon)
    }

    fun setIconToBuildState() {
        thisLogger().info("Setting icon to build")
        setIconAndReloadToolbars(buildIcon)
    }

    fun setIconToExecutingState() {
        thisLogger().info("Setting icon to running")
        setIconAndReloadToolbars(executingIcon)
    }

    fun setIconToErrorState() {
        thisLogger().info("Setting icon to error")
        setIconAndReloadToolbars(errorIcon)
    }

    private fun setIconAndReloadToolbars(icon: Icon) {
        this.icon = icon
        application.invokeLater {
            ActionToolbarImpl.updateAllToolbarsImmediately()
        }
    }
}
