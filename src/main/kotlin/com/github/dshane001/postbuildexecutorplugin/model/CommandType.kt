package com.github.dshane001.postbuildexecutorplugin.model

import com.github.dshane001.postbuildexecutorplugin.settings.AppSettingsState
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.task.ProjectTaskManager

enum class CommandType: CommandString {
    REBUILD_FILE_AND_EXECUTE_COMMAND {
        override fun getCommand(): String {
            return AppSettingsState.getInstance().postBuildFileCommand
        }

        override fun getBuildActionName(): String {
            return "Recompile File"
        }

        override fun triggerBuild(project: Project) {
            val projectTaskManager = ProjectTaskManager.getInstance(project)
            val fileEditorManager = FileEditorManager.getInstance(project)
            val virtualFile = fileEditorManager.selectedFiles.firstOrNull() ?: throw IllegalStateException("Could not find the selected file in the current editor, bailing out")
            projectTaskManager.compile(virtualFile)
        }
    },
    REBUILD_MODULE_AND_EXECUTE_COMMAND {
        override fun getCommand(): String {
            return AppSettingsState.getInstance().postBuildModuleCommand
        }

        override fun getBuildActionName(): String {
            return "Build Module (incrementally)"
        }

        override fun triggerBuild(project: Project) {
            val projectTaskManager = ProjectTaskManager.getInstance(project)
            val fileEditorManager = FileEditorManager.getInstance(project)
            val virtualFile = fileEditorManager.selectedFiles.firstOrNull() ?: throw IllegalStateException("Could not find the selected file in the current editor, bailing out")
            val module = ProjectRootManager.getInstance(project).fileIndex.getModuleForFile(virtualFile) ?: throw IllegalStateException("Could not find the module for the current selected file in the editor, bailing out")
            projectTaskManager.build(module)
        }
    },
    REBUILD_PROJECT_AND_EXECUTE_COMMAND {
        override fun getCommand(): String {
            return AppSettingsState.getInstance().postBuildProjectCommand
        }

        override fun getBuildActionName(): String {
            return "Build Project (incrementally)"
        }

        override fun triggerBuild(project: Project) {
            val projectTaskManager = ProjectTaskManager.getInstance(project)
            projectTaskManager.buildAllModules()
        }
    };
}