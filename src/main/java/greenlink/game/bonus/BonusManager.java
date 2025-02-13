package greenlink.game.bonus;

import greenlink.TntRun;
import greenlink.arena.Floor;
import greenlink.game.players.RunPlayer;
import greenlink.game.session.GameSession;
import greenlink.utils.SkullCreator;
import lombok.Data;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class BonusManager {

    private final Map<UUID, BonusEntity> entities = new ConcurrentHashMap<>();
    private final BonusSpawnController spawnController;
    private int specialBonusCounter = 0;
    private final int specialBonusThreshold;
    private final Random random = new Random();
    GameSession gameSession;
    ItemStack defaultSkull = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTdiYzFiNjRjYmEzZGM0Y2VmZTRlMTIxYzNjZGJiYjBmYTk5YWJhMGUxMTNiNWM5MTY4MTVmYzliMzA0ZTYzNiJ9fX0=");
    ItemStack rareSkull = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzEyNjExNjU2M2U5MDRjZGU3ZjUyYWUwZmI1ZTA3NjZlNjBhYmY0NzU3OTU3ZGU5ZGQzYjA2ZWRmMWY4YmQ4ZSJ9fX0=");
    Integer taskId = null;

    public BonusManager(GameSession gameSession, int specialBonusThreshold) {
        this.spawnController = new BonusSpawnController(gameSession, getMaxBonuses(gameSession));
        this.specialBonusThreshold = specialBonusThreshold;
        this.gameSession = gameSession;
    }

    private int getMaxBonuses(GameSession gameSession) {
        return gameSession.getArena().isMini() ? 1 : 2;
    }

    public void onBonusPickedUp(RunPlayer runPlayer, UUID bonusId, Floor floor) {
        BonusEntity bonusEntity = entities.get(bonusId);
        removeBonus(bonusId);
        runPlayer.addBonuses(bonusEntity.isRare());
        spawnController.onBonusPickup(bonusId, floor);
        specialBonusCounter++;
        if (specialBonusCounter >= specialBonusThreshold || random.nextInt(specialBonusThreshold) < specialBonusCounter) {
            spawnController.spawnBonusOnFloor(floor, true);
            specialBonusCounter = 0;
        }
    }

    public UUID createBonusEntity(Location location, boolean isRare, Floor floor) {
        schedulerTask();
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class, (e) -> {
            e.setGravity(false);
            e.setVisible(false);
            e.setInvulnerable(true);
            e.setMarker(false);
            e.setBasePlate(false);
            e.setArms(false);
            e.setSilent(true);
            if (isRare) {
                e.getEquipment().setHelmet(rareSkull);
                e.customName(LegacyComponentSerializer.legacyAmpersand().deserialize("&cОсобый бонус"));
            }
            else {
                e.getEquipment().setHelmet(defaultSkull);
                e.customName(LegacyComponentSerializer.legacyAmpersand().deserialize("&e&lБонус"));
            }
            e.setDisabledSlots(EquipmentSlot.values());
            e.setCustomNameVisible(true);
        });
        entities.put(armorStand.getUniqueId(), new BonusEntity(armorStand.getUniqueId(), isRare, floor, armorStand));
        return armorStand.getUniqueId();
    }

    public void schedulerTask() {
        if (taskId != null) return;
        taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TntRun.getInstance(), () -> {
            if (entities.isEmpty()) {
                Bukkit.getServer().getScheduler().cancelTask(taskId);
                taskId = null;
                return;
            }
            entities.values().forEach(bonus -> {
                Entity entity = bonus.getEntity();
                entity.setRotation(entity.getLocation().getYaw() + 2, entity.getLocation().getPitch());
                checkIfEntityAboveAir(entity);
            });
        }, 0, 1);
    }

    private void checkIfEntityAboveAir(Entity entity) {
        Location locationBelow = entity.getLocation().clone().add(0, 0, 0);
        if (locationBelow.getBlock().getType() == Material.AIR) {
            removeBonus(entity.getUniqueId());
        }
    }

    public void removeBonus(UUID bonusId) {
        BonusEntity entity = entities.remove(bonusId);
        if (entity != null) {
            entity.getEntity().remove();
        }
    }

    public void removeEntities() {
        entities.values().forEach(bonus -> bonus.getEntity().remove());
        Collection<List<Block>> values = gameSession.getFloorBlocks().values();
        for (List<Block> value : values) {
            for (Block block : value) {
                block.getWorld().getNearbyEntitiesByType(ArmorStand.class, block.getLocation(), 2).forEach(Entity::remove);
            }
        }
    }
}