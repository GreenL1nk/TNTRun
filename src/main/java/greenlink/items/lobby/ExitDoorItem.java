package greenlink.items.lobby;

import greenlink.TntRun;
import greenlink.items.AbstractItem;
import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ExitDoorItem extends AbstractItem {
    public ExitDoorItem(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        Bukkit.getScheduler().callSyncMethod(TntRun.getInstance(),
                () -> Bukkit.dispatchCommand(event.getPlayer(), "mvtp TNTRun-Lobby"));
    }
}