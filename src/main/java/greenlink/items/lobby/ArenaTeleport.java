package greenlink.items.lobby;

import greenlink.arena.Arena;
import greenlink.arena.ArenaManager;
import greenlink.items.AbstractItem;
import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaTeleport extends AbstractItem {

    public ArenaTeleport(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        event.setCancelled(true);
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        Arena arenaByName = ArenaManager.getInstance().getArenaByWorld(event.getPlayer().getWorld());
        if (arenaByName == null) {
            return;
        }
        Location nextTeleportLocation = ArenaManager.getInstance().getNextTeleportLocation(arenaByName);
        if (nextTeleportLocation == null) return;
        event.getPlayer().clearTitle();
        event.getPlayer().teleport(nextTeleportLocation);
    }
}
