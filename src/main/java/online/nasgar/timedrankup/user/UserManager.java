package online.nasgar.timedrankup.user;

import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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