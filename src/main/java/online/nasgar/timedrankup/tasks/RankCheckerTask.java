package online.nasgar.timedrankup.tasks;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import online.nasgar.timedrankup.TimedRankup;
import online.nasgar.timedrankup.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class RankCheckerTask extends BukkitRunnable {

    private final TimedRankup timedRankup;

    @Override
    public void run() {
        timedRankup.getUserManager().getUsers().forEach((uuid, user) -> {
            if (user.isDataLoaded()) {
                user.getTime().incrementAndGet();
                Rank nextRank = timedRankup.getRankManager().getApplicable(user);
                String actualRank = user.getRank();
                if (!nextRank.getName().equals(user.getRank())) {
                    user.setRank(nextRank.getName());
                    Bukkit.getScheduler().runTaskAsynchronously(timedRankup, user::save);
                    Player player = user.getAsPlayer();
                    String playerName = player.getName();

                    timedRankup.getConfig().getStringList("handle rankup commands").forEach(s -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                PlaceholderAPI.setPlaceholders(player, s
                                        .replaceAll("%player%", playerName)
                                        .replaceAll("%rank_name%", nextRank.getName())
                                        .replaceAll("%rank_prefix%", nextRank.getPrefix())
                                        .replaceAll("%old_rank_name%", actualRank)
                                )
                        );
                    });

                    timedRankup.getMessageHandler().sendReplacingIn(
                            player, "placeholder",
                            "ranked.own",
                            "%player%", playerName,
                            "%rank_prefix%", nextRank.getPrefix(),
                            "%rank_name%", nextRank.getName()
                    );
                }
            }
        });
    }
}
