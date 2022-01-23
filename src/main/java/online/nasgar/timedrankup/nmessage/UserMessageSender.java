package online.nasgar.timedrankup.nmessage;

import me.yushust.message.send.MessageSender;
import org.bukkit.ChatColor;

import org.bukkit.entity.Player;

public class UserMessageSender implements MessageSender<Player> {

    @Override
    public void send(Player user, String mode, String message) {
        user.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
