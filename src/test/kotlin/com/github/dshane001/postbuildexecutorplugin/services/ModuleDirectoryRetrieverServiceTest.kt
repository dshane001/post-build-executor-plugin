package com.github.dshane001.postbuildexecutorplugin.services

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class ModuleDirectoryRetrieverServiceTest: StringSpec({
    val project: Project = mockk<Project>()
    val fileEditorManager: FileEditorManager = mockk<FileEditorManager>()
    val virtualFile: VirtualFile = mockk<VirtualFile>()

    val moduleDirectoryRetrieverService = ModuleDirectoryRetrieverService()

    "When a file is selected in the editor, getCurrentFocusedEditorFilePath() should return it's path" {
        mockkStatic(FileEditorManager::class) {
            every { FileEditorManager.getInstance(project) } returns fileEditorManager
            every { fileEditorManager.selectedFiles } returns arrayOf(virtualFile)
            every { virtualFile.path } returns "filePath"

            moduleDirectoryRetrieverService.getCurrentFocusedEditorFilePath(project) shouldBe "filePath"
        }
    }

    "When no file is selected in the editor, getCurrentFocusedEditorFilePath() should return null" {
        mockkStatic(FileEditorManager::class, Messages::class) {
            every { FileEditorManager.getInstance(project) } returns fileEditorManager
            every { fileEditorManager.selectedFiles } returns emptyArray()
            every { virtualFile.path } returns "filePath"
            every {
                Messages.showErrorDialog(
                    project,
                    "Cannot get module directory of the currently opened editor file, no file is currently selected. Please open a file in the main editor before performing this action.",
                    "PostBuildExecutorPlugin Error"
                )
            } just Runs

            moduleDirectoryRetrieverService.getCurrentFocusedEditorFilePath(project) shouldBe null
        }
    }
})