package com.github.dshane001.postbuildexecutorplugin.servicelocator

import com.github.dshane001.postbuildexecutorplugin.notifiers.build.BuildNotifier

interface NotifierLocator {
    val buildNotifier: BuildNotifier
}
