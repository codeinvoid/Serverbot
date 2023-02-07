package org.pio.rsnbot

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.pio.rsnbot.command.Verify
import org.pio.rsnbot.config.Config

object RSNBot : KotlinPlugin(
    JvmPluginDescription(
        id = "org.pio.rsnbot",
        name = "RSNBot",
        version = "0.1.0",
    ) {
        author("hoige")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        Verify().handle()
        Config.reload()
        Config.save()
    }
}