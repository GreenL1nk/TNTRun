package greenlink.game.bonus;

import greenlink.game.bonus.bonuses.ConfusionGrenade;
import greenlink.game.bonus.bonuses.DoubleJump;
import greenlink.game.bonus.bonuses.PoisonGrenade;
import greenlink.game.bonus.bonuses.SlowFalling;
import greenlink.utils.SkullCreator;
import greenlink.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Bonus {

    DOUBLE_JUMP(new DoubleJump("Двойной прыжок", new ItemStack(Material.FEATHER)), 0, "&7"),
    CONFUSION_GRENADE(new ConfusionGrenade("Тошнотворная граната",
            Utils.getPotionItem(Color.PURPLE, new PotionEffect(PotionEffectType.CONFUSION, 10 * 20, 10))
    ), 1, "&e"),
    POISON_GRENADE(new PoisonGrenade("Отрава",
            Utils.getPotionItem(Color.GREEN, new PotionEffect(PotionEffectType.POISON, 3 * 20, 3))
    ), 2, "&a"),
    SLOW_FALLING(new SlowFalling("Кубик медленного падения", SkullCreator.whiteCube), 3, "&f")
    ;

    @Getter
    private final AbstractBonus abstractBonus;
    @Getter
    private final int slot;
    private final String color;

    Bonus(AbstractBonus abstractBonus, int slot, String color) {
        this.abstractBonus = abstractBonus;
        this.slot = slot;
        this.color = color;
    }

    private void create() {
        ItemMeta meta = abstractBonus.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(color + abstractBonus.bonusName).decoration(TextDecoration.ITALIC, false));
        abstractBonus.setItemMeta(meta);
    }

    public static void init() {
        for (Bonus bonus : Bonus.values()) {
            bonus.create();
        }
    }

    @Nullable
    public static Bonus getBonusByItem(ItemStack itemStack) {
        return Arrays.stream(Bonus.values())
                .filter(item -> item.getAbstractBonus().getItemMeta().equals(itemStack.getItemMeta()))
                .findFirst().orElse(null);
    }

    public static Set<Bonus> getBonusesInInventory(Player player) {
        Set<Bonus> items = new HashSet<>();
        Arrays.stream(player.getInventory().getContents())
                .filter(itemStack -> itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta() != null)
                .forEach(extraContent -> Arrays.stream(Bonus.values())
                        .filter(bonus -> bonus.getAbstractBonus().getItemMeta().equals(extraContent.getItemMeta()))
                        .forEach(items::add));
        return items;
    }
}
