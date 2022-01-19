package online.nasgar.timedrankup.placeholders;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import online.nasgar.timedrankup.TimedRankup;
import online.nasgar.timedrankup.rank.Rank;
import online.nasgar.timedrankup.rank.RankManager;
import online.nasgar.timedrankup.user.User;
import online.nasgar.timedrankup.utils.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class NyaPlaceholders extends PlaceholderExpansion {

    private final TimedRankup timedRankup;

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        User user = timedRankup.getUserManager().get(player.getUniqueId());
        RankManager rankManager = timedRankup.getRankManager();

        if (params.equalsIgnoreCase("rank_prefix")) {
            return rankManager.get(user.getRank()).getPrefix();
        }

        if (params.equalsIgnoreCase("rank_name")) {
            return user.getRank();
        }

        if (params.equalsIgnoreCase("next_rank_prefix")) {
            return rankManager.getNextApplicable(user).getPrefix();
        }

        if (params.equalsIgnoreCase("next_rank_name")) {
            return rankManager.getNextApplicable(user).getName();
        }

        if (params.equalsIgnoreCase("next_rank_time")) {
            Rank rank = rankManager.getNextApplicable(user);
            if (rank == null) {
                return ChatColor.translateAlternateColorCodes('&', timedRankup.getConfig().getString("no more ranks", "no more ranks"));
            }
            return TimeUtils.formatTime(Duration.ofSeconds(rank.getTime() - user.getTime().get()));
        }

        if (params.toLowerCase().startsWith("time_for_")) {
            String rankName = params.split("time_for_")[1];
            Rank rank = rankManager.get(rankName);

            if (rank == null) {
                return "null rank";
            }

            return TimeUtils.formatTime(Duration.ofSeconds(rank.getTime() - user.getTime().get()));
        }

        return "not valid placeholder.";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "timedrankup";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gatogamer_";
    }

    @Override
    public @NotNull String getVersion() {
        return timedRankup.getDescription().getVersion();
    }
}
