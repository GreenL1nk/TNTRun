package greenlink;

import greenlink.statistic.PlayerStats;
import greenlink.statistic.StatisticPeriod;
import greenlink.utils.AbstractInventoryHolder;
import greenlink.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class StatsGui extends AbstractInventoryHolder {

    StatisticPeriod statisticPeriod;

    public StatsGui(Player requester, StatisticPeriod statisticPeriod) {
        super(Component.text("Статистика за " + statisticPeriod.getDisplayName()), 3, requester);
        this.statisticPeriod = statisticPeriod;
        init();
    }

    @Override
    protected void init() {

        PlayerStats playerStatistics = TntRun.getInstance().getStatsService().getPlayerStats(requester, statisticPeriod.getPeriod());

        ItemStack wins = Utils.getItem(Component.text("ПОБЕД", NamedTextColor.GREEN, TextDecoration.BOLD), Material.SUNFLOWER);
        Utils.addLore(wins, Component.text(playerStatistics.getWins(), NamedTextColor.YELLOW));

        ItemStack loses = Utils.getItem(Component.text("ПОРАЖЕНИЙ", NamedTextColor.RED, TextDecoration.BOLD), Material.RED_MUSHROOM);
        Utils.addLore(loses, Component.text(playerStatistics.getLosses(), NamedTextColor.YELLOW));

        ItemStack games = Utils.getItem(Component.text("ПАРТИЙ", NamedTextColor.BLUE, TextDecoration.BOLD), Material.PAINTING);
        Utils.addLore(games, Component.text(playerStatistics.getGamesPlayed(), NamedTextColor.YELLOW));

        ItemStack bestTime = Utils.getItem(Component.text("ЛУЧШЕЕ ВРЕМЯ", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD), Material.CLOCK);
        Utils.addLore(bestTime, Component.text(playerStatistics.getBestTime(), NamedTextColor.YELLOW));

        ItemStack cuts = Utils.getItem(Component.text("ПОДРЕЗАНИЙ", NamedTextColor.DARK_AQUA, TextDecoration.BOLD), Material.PUMPKIN_PIE);
        Utils.addLore(cuts, Component.text(playerStatistics.getCuts(), NamedTextColor.YELLOW));

        ItemStack blocks = Utils.getItem(Component.text("СЛОМАНО БЛОКОВ", NamedTextColor.WHITE, TextDecoration.BOLD), Material.LIME_GLAZED_TERRACOTTA);
        Utils.addLore(blocks, Component.text(playerStatistics.getBlocksBroken(), NamedTextColor.YELLOW));

        ItemStack stats = Utils.getItem(Component.text("СТАТИСТИКА", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD), Material.CONDUIT);
        Utils.addLore(stats, Component.text(statisticPeriod.getDisplayName(), NamedTextColor.YELLOW));

        this.inventory.setItem(10, wins);
        this.inventory.setItem(11, loses);
        this.inventory.setItem(12, games);
        this.inventory.setItem(13, stats);
        this.inventory.setItem(14, bestTime);
        this.inventory.setItem(15, cuts);
        this.inventory.setItem(16, blocks);

    }

    @Override
    public void click(InventoryClickEvent event) {
        if (event.getRawSlot() == 13) {
            display(requester, statisticPeriod.next());
            playChangeSound();
        }
    }

    public static void display(Player requester, StatisticPeriod statisticPeriod) {
        StatsGui menu = new StatsGui(requester, statisticPeriod);
        Bukkit.getServer().getScheduler().runTaskLater(TntRun.getInstance(), menu::open, 1);
    }

    @Override
    public void close(InventoryCloseEvent event) {

    }

    public void playChangeSound() {
        requester.playSound(requester.getLocation(), Sound.valueOf(ConfigValue.SOUND_STATS_CHANGE.getString()), 1, 1);
    }
}