package com.github.dshane001.postbuildexecutorplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NonNls

@Service(Service.Level.PROJECT)
class ModuleDirectoryRetrieverService {
    fun getModuleDirectory(project: Project): String? {
        // Get the current file from the editor
        val currentFile: VirtualFile = getCurrentFocusedEditorFile(project) ?: return null

        // Find the module for the current file
        val module: Module = ModuleUtil.findModuleForFile(currentFile, project) ?: run {
            Messages.showErrorDialog(
                project,
                "Cannot get the Intellij module of File: %s. Bailing out.".format(currentFile.name),
                "PostBuildExecutorPlugin Error"
            )
            return null
        }

        // Use the module's content roots to find the module base directory
        val contentRoots = ModuleRootManager.getInstance(module).contentRoots

        // Assuming the first content root is the module's base directory
        var moduleBaseDir: VirtualFile = contentRoots.firstOrNull() ?: run {
            Messages.showErrorDialog(
                project,
                "Cannot find the content root or module: %s. Bailing out.".format(module.name),
                "PostBuildExecutorPlugin Error"
            )
            return null
        }

        while(moduleBaseDir.findChild("src") == null) {
            moduleBaseDir = moduleBaseDir.parent ?: run {
                Messages.showErrorDialog(
                    project,
                    "Cannot find the main module directory from module file: %s. Bailing out.".format(moduleBaseDir.path),
                    "PostBuildExecutorPlugin Error"
                )
                return null
            }
        }

        return moduleBaseDir.path
    }

    fun getCurrentFocusedEditorFilePath(project: Project): @NonNls String? {
        // Get the current file from the editor
        return getCurrentFocusedEditorFile(project)?.path
    }

    private fun getCurrentFocusedEditorFile(project: Project): VirtualFile? {
        // Get the current file from the editor
        val fileEditorManager = FileEditorManager.getInstance(project)
        val currentFile: VirtualFile = fileEditorManager.selectedFiles.firstOrNull() ?: run {
            Messages.showErrorDialog(
                project,
                "Cannot get module directory of the currently opened editor file, no file is currently selected. Please open a file in the main editor before performing this action.",
                "PostBuildExecutorPlugin Error"
            )
            return null
        }
        return currentFile
    }
}