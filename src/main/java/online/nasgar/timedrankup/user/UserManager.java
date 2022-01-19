package online.nasgar.timedrankup.user;

import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Getter
public class UserManager {
    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    public User get(UUID uuid) {
        return users.computeIfAbsent(uuid, User::new);
    }

    public void delete(UUID uuid) {
        users.remove(uuid);
    }
}