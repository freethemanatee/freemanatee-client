package me.zopac.freemanatee.module.modules.chat

import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import me.zopac.freemanatee.event.events.PacketEvent
import me.zopac.freemanatee.module.Module
import me.zopac.freemanatee.setting.Settings
import me.zopac.freemanatee.util.Friends
import me.zopac.freemanatee.util.MessageDetectionHelper
import me.zopac.freemanatee.util.MessageSendHelper
import net.minecraft.network.play.server.SPacketChat

@Module.Info(
        name = "AutoTPA",
        category = Module.Category.CHAT
)

class AutoTPA : Module() {

    private val friends = register(Settings.b("AlwaysAcceptFriends", true))
    private val mode = register(Settings.e<Mode>("Response", Mode.DENY))

    @EventHandler
    private val receiveListener = Listener(EventHook { event: PacketEvent.Receive ->
        if (event.packet is SPacketChat && MessageDetectionHelper.isTPA(true, (event.packet as SPacketChat).getChatComponent().unformattedText)) {
            val name = (event.packet as SPacketChat).getChatComponent().unformattedText.split(" ").toTypedArray()[0]

            when (mode.value) {
                Mode.ACCEPT -> MessageSendHelper.sendServerMessage("/tpaccept $name")
                Mode.DENY -> {
                    if (friends.value && Friends.isFriend(name)) {
                        MessageSendHelper.sendServerMessage("/tpaccept $name")
                    } else {
                        MessageSendHelper.sendServerMessage("/tpdeny $name")
                    }
                }
            }
        }
    })

    enum class Mode {
        ACCEPT, DENY
    }
}