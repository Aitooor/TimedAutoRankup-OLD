package me.gatogamer.timedrankup.commands;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.gatogamer.timedrankup.TimedRankup;
import me.gatogamer.timedrankup.rank.Rank;
import me.gatogamer.timedrankup.user.User;
import me.gatogamer.timedrankup.utils.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
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
