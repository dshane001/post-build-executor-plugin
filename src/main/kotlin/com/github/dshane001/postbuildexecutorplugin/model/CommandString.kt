package com.github.dshane001.postbuildexecutorplugin.model

import com.intellij.openapi.project.Project

interface CommandString {
    fun getCommand(): String
    fun getBuildActionName(): String
    fun triggerBuild(project: Project)
}