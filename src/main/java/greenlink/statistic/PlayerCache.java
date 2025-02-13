package greenlink.statistic;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerCache {
    private static final ConcurrentHashMap<String, PlayerStats> cache = new ConcurrentHashMap<>();

    public static PlayerStats getPlayerStats(String uuid) {
        return cache.get(uuid);
    }

    public static void updatePlayerStats(String uuid, PlayerStats stats) {
        cache.put(uuid, stats);
    }

    public static void removePlayerStats(String uuid) {
        cache.remove(uuid);
    }
}