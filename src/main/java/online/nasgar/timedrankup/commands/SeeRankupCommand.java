package online.nasgar.timedrankup.commands;

import lombok.RequiredArgsConstructor;
import me.yushust.message.MessageHandler;
import online.nasgar.timedrankup.TimedRankup;
import online.nasgar.timedrankup.user.User;
import online.nasgar.timedrankup.utils.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class SeeRankupCommand implements CommandExecutor {
    private final TimedRankup timedRankup;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            MessageHandler messageHandler = timedRankup.getMessageHandler();

            User user = timedRankup.getUserManager().get(player.getUniqueId());
            List<String> formattedRanks = new ArrayList<>();

            timedRankup.getRankManager().getRanksInverted().forEach(rank -> {
                String time;
                long userTime = user.getTime().get();
                int rankTime = rank.getTime();

                if (userTime > rankTime) {
                    time = "0s";
                } else {
                    time = TimeUtils.formatTime(Duration.ofSeconds(rankTime - userTime));
                }

                formattedRanks.add(messageHandler.replacing(
                        player, "commands.listed.format",
                        "%rank_prefix%", rank.getPrefix(),
                        "%rank_pending_time%", time,
                        "%rank_needed_time%", TimeUtils.formatTime(
                                Duration.ofSeconds(rankTime)
                        )
                ));
            });

            messageHandler.sendReplacingIn(
                    player, "placeholder", "commands.listed.send",
                    "%ranks%", String.join("\n", formattedRanks)
            );
        }
        return true;
    }
}
