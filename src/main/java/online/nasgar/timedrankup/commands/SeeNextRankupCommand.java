package online.nasgar.timedrankup.commands;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import online.nasgar.timedrankup.TimedRankup;
import online.nasgar.timedrankup.rank.Rank;
import online.nasgar.timedrankup.user.User;
import online.nasgar.timedrankup.utils.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@RequiredArgsConstructor
public class SeeNextRankupCommand implements CommandExecutor {
    private final TimedRankup timedRankup;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            User user = timedRankup.getUserManager().get(player.getUniqueId());

            Rank rank = timedRankup.getRankManager().getNextApplicable(user);
            if (rank != null) {
                timedRankup.getConfig().getStringList("commands.see.next").forEach(s -> {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, s
                            .replaceAll("%new_rank_prefix%", rank.getPrefix())
                            .replaceAll("%parsedTime%", TimeUtils.formatTime(Duration.ofSeconds(rank.getTime()-user.getTime().get()))))
                    ));
                });
            } else {
                timedRankup.getConfig().getStringList("commands.see.noMore").forEach(s -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));

            }
        }
        return true;
    }
}
