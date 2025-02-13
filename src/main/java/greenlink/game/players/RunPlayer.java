package greenlink.game.players;

import greenlink.ConfigValue;
import greenlink.game.bonus.Bonus;
import greenlink.game.session.GameSession;
import lombok.Data;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Data
public class RunPlayer {

    private final Player player;
    private HashMap<Bonus, Integer> countBonus = new HashMap<>();
    private boolean canDoubleJump = false;
    private Location lastLocation;

    public RunPlayer(Player player) {
        this.player = player;
    }

    public void addBonuses(boolean isRare) {
        if (isRare) {
            List<Bonus> rareBonuses = List.of(
                    Bonus.DOUBLE_JUMP,
                    Bonus.CONFUSION_GRENADE,
                    Bonus.POISON_GRENADE,
                    Bonus.SLOW_FALLING
            );

            Bonus selectedBonus = rareBonuses.get(new Random().nextInt(rareBonuses.size()));

            addBonus(selectedBonus, selectedBonus == Bonus.DOUBLE_JUMP ? 3 : 1, true);

            player.playSound(player.getLocation(), Sound.valueOf(ConfigValue.SOUND_SPECIAL_BONUS_PICKUP.getString()), 1, 0.1F);
        } else {
            addBonus(Bonus.DOUBLE_JUMP, 1, false);
            player.playSound(player.getLocation(), Sound.valueOf(ConfigValue.SOUND_BONUS_PICKUP.getString()), 1, 1);
        }
    }

    public boolean hasBonus(Bonus bonus) {
        boolean hasBonus = false;
        if (countBonus.containsKey(bonus)) {
            hasBonus = countBonus.get(bonus) > 0;
        }
        return hasBonus;
    }

    public void consumeBonus(Bonus bonus) {
        if (player == null) return;
        if (!hasBonus(bonus)) return;
        countBonus.put(bonus, countBonus.getOrDefault(bonus, 0) - 1);
        if (countBonus.get(bonus) <= 0) countBonus.remove(bonus);
    }

    public void addBonus(Bonus bonus, int count, boolean isRare) {
        if (player == null) return;
        ItemStack item = player.getInventory().getItem(bonus.getSlot());
        if (item == null) {
            player.getInventory().setItem(bonus.getSlot(), bonus.getAbstractBonus().asQuantity(count));
        }
        else {
            item.setAmount(item.getAmount() + count);
        }
        countBonus.put(bonus, countBonus.getOrDefault(bonus, 0) + count);
        if (!isRare) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize("&aВы подобрали бонусный предмет: &e" + bonus.getAbstractBonus().getBonusName()));
        }
        else {
            if (bonus  == Bonus.DOUBLE_JUMP) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize("&aВы подобрали&c особый &aбонусный предмет: &c3" + " Двойных прыжка"));
            }
            else player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize("&aВы подобрали&c особый &aбонусный предмет: &c" + bonus.getAbstractBonus().getBonusName()));
        }
        bonus.getAbstractBonus().onPickUp(this);
    }

    public void setLastLocation(Location lastLocation, GameSession gameSession) {
        if (gameSession.getFloorBlocks().get(gameSession.getArena().getFloorByNumber(3))
                .stream()
                .anyMatch(block -> {
                    Location playerLocation = player.getLocation();
                    return block.getX() == playerLocation.getBlockX() &&
                            block.getZ() == playerLocation.getBlockZ()
                            ;
                })) this.lastLocation = gameSession.getArena().getTeleportLocation();
        else this.lastLocation = lastLocation;
    }
}
