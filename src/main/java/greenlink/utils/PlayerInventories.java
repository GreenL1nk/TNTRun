package greenlink.utils;

import greenlink.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PlayerInventories {
    public static void setLobbyInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, CustomItem.ARENA_TELEPORT.getAbstractItem());
        player.getInventory().setItem(4, CustomItem.STATS.getAbstractItem());
        player.getInventory().setItem(8, CustomItem.EXIT.getAbstractItem());

        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.SLOW_FALLING);
        player.removePotionEffect(PotionEffectType.CONFUSION);
    }
}
