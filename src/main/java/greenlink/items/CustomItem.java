package greenlink.items;

import greenlink.items.lobby.ArenaTeleport;
import greenlink.items.lobby.ExitDoorItem;
import greenlink.items.lobby.StatsItem;
import lombok.Getter;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum CustomItem {
    EXIT("&cВыйти в лобби", new ExitDoorItem(new ItemStack(Material.DARK_OAK_DOOR))),
    STATS("&eМоя статистика", new StatsItem(new ItemStack(Material.GLOBE_BANNER_PATTERN))),
    ARENA_TELEPORT("&dСледующая арена", new ArenaTeleport(new ItemStack(Material.CHORUS_FLOWER)));

    @Getter
    private final AbstractItem abstractItem;
    private final String itemName;

    CustomItem(String itemName, AbstractItem abstractItem) {
        this.abstractItem = abstractItem;
        this.itemName = itemName;
    }

    private void create() {
        if (abstractItem == null) return;
        ItemMeta meta = abstractItem.getItemMeta();
        if (itemName != null) meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(itemName)
                .decoration(TextDecoration.ITALIC, false));
        abstractItem.setItemMeta(meta);
    }

    public static void init() {
        for (CustomItem item : CustomItem.values()) {
            item.create();
        }
    }

    @Nullable
    public static CustomItem getItemByStack(ItemStack itemStack) {
        return Arrays.stream(CustomItem.values())
                .filter(item -> item.getAbstractItem().getItemMeta().equals(itemStack.getItemMeta()))
                .findFirst().orElse(null);
    }
}
