package greenlink.utils;

import greenlink.ConfigValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;


public class Utils {

    public static void debugLog(String message) {
        if (ConfigValue.DEBUG_MODE.getBoolean()) {
            Bukkit.getLogger().info("[DEBUG] " + message);
        }
    }

    public static ItemStack getPotionItem(Color color, PotionEffect potionEffect) {
        ItemStack itemStack = new ItemStack(Material.SPLASH_POTION);
        PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
        itemMeta.setColor(color);
        itemMeta.addCustomEffect(potionEffect, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemMeta.lore(List.of());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public static ItemStack getItem(Component name, Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void addLore(ItemStack itemStack, Component ... components) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<Component> lore = itemMeta.lore();
        if (lore == null) lore = new ArrayList<>();
        for (Component component : components) {
            lore.add(component.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
    }
}
