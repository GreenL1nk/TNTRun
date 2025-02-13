package greenlink;

import greenlink.arena.ArenaManager;
import greenlink.game.bonus.Bonus;
import greenlink.game.bonus.BonusListener;
import greenlink.items.CustomItem;
import greenlink.listeners.*;
import greenlink.placeholder.DataPlaceholder;
import greenlink.statistic.PlayerStatsService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TntRun extends JavaPlugin {

    @Getter
    private static TntRun instance;
    @Getter
    PlayerStatsService statsService;

    
    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new CustomItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
        Bukkit.getPluginManager().registerEvents(new BonusListener(), this);
        Bukkit.getPluginManager().registerEvents(new FallEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);

        ConfigValue.getAllValues();

        Commands.init(this);
        CustomItem.init();
        Bonus.init();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            ArenaManager.getInstance();
            new DataPlaceholder().register();
        }, 1);

        statsService = new PlayerStatsService();
    }
    
    @Override
    public void onDisable() {

    }
}