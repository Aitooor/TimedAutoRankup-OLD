package online.nasgar.timedrankup.rank;

import lombok.Getter;
import online.nasgar.timedrankup.TimedRankup;
import online.nasgar.timedrankup.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RankManager {

    private final List<Rank> ranks = new ArrayList<>();
    private final List<Rank> ranksInverted = new ArrayList<>();
    private final Map<String, Rank> ranksMap = new ConcurrentHashMap<>();

    public RankManager(TimedRankup timedRankup) {
        timedRankup.getConfig().getStringList("enabled rankups").forEach(s -> {
            Rank rank = new Rank(s, timedRankup.getConfig().getString("rankups."+s+".prefix"), timedRankup.getConfig().getInt("rankups."+s+".time"));
            ranks.add(rank);
            ranksMap.put(s, rank);
        });
        ranksInverted.addAll(ranks);
        Collections.reverse(ranksInverted);
    }

    public Rank get(String name) {
        return ranksMap.get(name);
    }

    public Rank getApplicable(User user) {
        return
                ranks.stream()
                        .filter(rank -> user.getTime().get() > rank.getTime()) // Checking if the player has the time.
                        .findFirst() // Getting the first element, may be the rank that the player will have.
                        .orElse(null); // Else, returning null, this means that player just doesn't have any rankUp pending.
    }

    public Rank getNextApplicable(User user) {
        return
                ranksInverted.stream()
                        .filter(rank -> !user.getRank().equals(rank.getName()) && rank.getTime() > user.getTime().get()) // Checking if the player has the time and checking if the player doesn't have that rank.
                        .findFirst() // Getting the first element, may be the rank that the player will have.
                        .orElse(null); // Else, returning null, this means that player just doesn't have any rankUp pending.
    }

}
