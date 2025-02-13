package greenlink.game.bonus;

import greenlink.ConfigValue;
import greenlink.TntRun;
import greenlink.arena.Floor;
import greenlink.game.session.GameSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BonusSpawnController {
    private final GameSession gameSession;
    private final Map<Floor, List<UUID>> floorBonuses = new ConcurrentHashMap<>();
    private final int maxBonusesPerFloor;

    public BonusSpawnController(GameSession gameSession, int maxBonusesPerFloor) {
        this.gameSession = gameSession;
        this.maxBonusesPerFloor = maxBonusesPerFloor;
    }

    public void spawnInitialBonuses() {
        for (Floor floor : gameSession.getArena().getFloors()) {
            for (int i = 0; i < maxBonusesPerFloor; i++) {
                spawnBonusOnFloor(floor, false);
            }
        }
    }

    public void spawnBonusOnFloor(Floor floor, boolean isRare) {
        if (floorBonuses.getOrDefault(floor, new ArrayList<>()).size() >= maxBonusesPerFloor) {
            return;
        }
        Location randomLocation = gameSession.getRandomValidLocationOnFloor(floor);
        if (randomLocation == null) return;
        UUID bonusEntity = gameSession.getBonusManager().createBonusEntity(randomLocation, isRare, floor);
        floorBonuses.computeIfAbsent(floor, k -> new ArrayList<>()).add(bonusEntity);
    }

    public void onBonusPickup(UUID bonusId, Floor floor) {
        floorBonuses.get(floor).remove(bonusId);
        scheduleRespawn(floor, ConfigValue.NORMAL_BONUS_SPAWN_TIME.getInt());
    }

    private void scheduleRespawn(Floor floor, int delaySeconds) {
        Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), () -> spawnBonusOnFloor(floor, false), delaySeconds * 20L);
    }
}