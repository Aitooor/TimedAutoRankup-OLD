package online.nasgar.timedrankup.commands;

import lombok.RequiredArgsConstructor;
import online.nasgar.timedrankup.TimedRankup;
import online.nasgar.timedrankup.rank.Rank;
import online.nasgar.timedrankup.user.User;
import online.nasgar.timedrankup.utils.TimeUtils;
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
                timedRankup.getMessageHandler().sendReplacingIn(
                        player, "placeholder",
                        "commands.see.next",
                        "%new_rank_prefix%", rank.getPrefix(),
                        "%parsedTime%", TimeUtils.formatTime(
                                Duration.ofSeconds(rank.getTime() - user.getTime().get())
                        )
                );
            } else {
                timedRankup.getMessageHandler().send(player, "commands.see.noMore");
            }
        }
        return true;
    }
}
