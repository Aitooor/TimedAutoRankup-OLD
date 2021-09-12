package me.gatogamer.timedrankup.user;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.gatogamer.timedrankup.TimedRankup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Getter
@Setter
public class User {

    private final UUID uuid;
    private String rank;
    private final AtomicInteger time = new AtomicInteger(0);
    private boolean dataLoaded = false;

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public void save() {
        if (dataLoaded) {
            getMySQL().update("UPDATE PlayerData SET Rank='" + this.rank + "', Time=" + this.time.get() + " WHERE UUID='" + this.uuid.toString()+"'");
        }
    }

    @SneakyThrows
    public void load() {
        ResultSet resultSet = getIfExists();
        if (resultSet != null) {
            rank = resultSet.getString("Rank");
            time.set(resultSet.getInt("Time"));
        } else {
            rank = getPlugin().getConfig().getString("default rank");
            getMySQL().update("INSERT INTO PlayerData (UUID, Rank, Time) VALUES ('" + this.uuid.toString() + "', '" + this.rank + "', '" + this.time.get() + "')");
        }
        dataLoaded = true;
    }

    public ResultSet getIfExists() {
        try {
            ResultSet rs = getMySQL().query("SELECT * FROM PlayerData WHERE UUID='" + uuid.toString() + "'");
            if (rs.next()) {
                return rs;
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public MySQL getMySQL() {
        return getPlugin().getMySQL();
    }

    public TimedRankup getPlugin() {
        return TimedRankup.getPlugin(TimedRankup.class);
    }

    public Player getAsPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}