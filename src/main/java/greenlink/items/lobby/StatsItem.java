package greenlink.items.lobby;

import greenlink.ConfigValue;
import greenlink.StatsGui;
import greenlink.items.AbstractItem;
import greenlink.statistic.StatisticPeriod;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class StatsItem extends AbstractItem {
    public StatsItem(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        StatsGui.display(event.getPlayer(), StatisticPeriod.ALL_TIME);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf(ConfigValue.SOUND_STATS_OPEN.getString()), 1, 1);
    }
}
