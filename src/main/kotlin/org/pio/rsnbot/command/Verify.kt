package org.pio.rsnbot.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.pio.rsnbot.config.Config
import java.security.SecureRandom


class Verify {
    data class Mojang(
        val name: String,
        val id: String
    )

    data class New(
        val qq: Long,
        val uuid: String,
        val code: Int
    )

    private val api = Config.api["serverAPI"]

    @OptIn(DelicateCoroutinesApi::class)
    fun handle(){
        val qq: Long = 494568169
        val last: Long = 3500232942
        GlobalEventChannel
            .filterIsInstance<GroupMessageEvent>()
            .filter { it.group.id == qq }
            .filter { it.sender.id != last }
            .subscribeAlways<GroupMessageEvent> {
                val verify = Regex("(^#verify)").containsMatchIn(it.message.contentToString())
                val verifyID = Regex("(^#verify)").replace(it.message.contentToString(),"").trim()
                if (verify){
                    try {
                        GlobalScope.launch{
                            if (verifyPress(verifyID)){
                                verifyNext(verifyPressTo(verifyID),it)
                            } else {
                                it.sender.group.sendMessage("无法验证用户ID")
                            }
                        }
                    } catch (e:Exception){
                        it.sender.group.sendMessage("连接API失败，请与管理员联系。")
                    }
                }
                message(it)
            }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun message(it: GroupMessageEvent) {
        val verifyDel = Regex("(^#vdel)").containsMatchIn(it.message.contentToString())
        val verifyIDDel = Regex("(^#vdel)").replace(it.message.contentToString(),"").trim()
        if (verifyDel){
            if (it.sender.id == 2402210783 || it.sender.id == 2987572939) {
                try {
                    GlobalScope.launch {
                        delNext(verifyPressTo(verifyIDDel), it)
                    }
                } catch (e: Exception) {
                    it.sender.group.sendMessage("连接API失败，请与管理员联系。")
                }
            }
        }
    }

    private fun verifyPress(verifyID:String) : Boolean{
        val request = org.pio.rsnbot.utils.Request().request(
            api = "https://api.mojang.com/users/profiles/minecraft",
            uuid = verifyID,
            data = Mojang::class.java,
            type = ""
        )
        if (request != null) {
            return true
        }
        return false
    }

    private fun verifyPressTo(verifyID:String) : String? {
        val request = org.pio.rsnbot.utils.Request().request(
            api = "https://api.mojang.com/users/profiles/minecraft",
            uuid = verifyID,
            data = Mojang::class.java,
            type = ""
        )
        if (request != null) {
            return request.id
        }
        return null
    }

    private suspend fun verifyNext(UUID: String?, it:GroupMessageEvent) {
        if (UUID != null) {
            val secureRandom = SecureRandom.getInstance("SHA1PRNG")
            val code = secureRandom.nextInt(900000) + 100000
            if (org.pio.rsnbot.utils.Request().post(New(it.sender.id, UUID, code), "", UUID, api.toString())) {
                it.source.group.sendMessage("请至游戏内执行指令完成验证\n/verify $code")
            } else {
                it.source.group.sendMessage("该ID已经绑定过了！如需帮助请联系管理员")
            }
        }
    }

    private suspend fun delNext(UUID: String?, it:GroupMessageEvent) {
        if (UUID != null) {
            if (org.pio.rsnbot.utils.Request().delete(UUID)) {
                it.source.group.sendMessage("已删除")
            } else {
                it.source.group.sendMessage("用户不存在")
            }
        }
    }
}
