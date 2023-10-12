package com.github.dshane001.postbuildexecutorplugin.servicelocator

import com.github.dshane001.postbuildexecutorplugin.notifiers.build.BuildNotifierLauncher

object ServiceLocator: NotifierLocator {
    override val buildNotifier by lazy { BuildNotifierLauncher() }
}
