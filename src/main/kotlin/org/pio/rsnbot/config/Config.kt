package org.pio.rsnbot.config

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginData("config") {
    val token : String by value()
    val api : MutableMap<String, String> by value()
}

