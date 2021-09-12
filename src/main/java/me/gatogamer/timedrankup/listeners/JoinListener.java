package me.gatogamer.timedrankup.listeners;

import lombok.RequiredArgsConstructor;
import me.gatogamer.timedrankup.TimedRankup;
import me.gatogamer.timedrankup.user.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class JoinListener implements Listener {

    private final TimedRankup timedRankup;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        User user = timedRankup.getUserManager().get(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(timedRankup, user::load);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        User user = timedRankup.getUserManager().get(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(timedRankup, user::save);
        timedRankup.getUserManager().delete(event.getPlayer().getUniqueId());
    }

}
