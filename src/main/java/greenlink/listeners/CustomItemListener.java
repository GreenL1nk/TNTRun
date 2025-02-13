package greenlink.listeners;

import greenlink.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CustomItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            CustomItem customItem = CustomItem.getItemByStack(event.getItem());
            if (customItem != null) {
                customItem.getAbstractItem().onUse(event);
            }
        }
    }

    @EventHandler
    public void test(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            if (event.getMessage().contains("/test")) {
//                for (CustomItem value : CustomItem.values()) {
//                    event.getPlayer().getInventory().addItem(value.getAbstractItem());
//                }
//                GameSession session = GameSessionManager.getInstance().getSession(player.getWorld().getName());
//                RunPlayer runPlayer = session.getAlivePlayer(player);
//                session.randomTeleport(runPlayer.getPlayer());
//                for (Bonus value : Bonus.values()) {
//                    runPlayer.addBonus(value, 1, true);
//                }
            }
        }
    }

}
