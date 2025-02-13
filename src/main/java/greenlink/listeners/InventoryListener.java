package greenlink.listeners;

import greenlink.utils.AbstractInventoryHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (event.getView().getTopInventory().getHolder() instanceof AbstractInventoryHolder) {
                if (event.getRawSlot() > event.getView().getTopInventory().getSize() - 1) {
                    if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) event.setCancelled(true); // TODO check the used slots
                    ((AbstractInventoryHolder) event.getView().getTopInventory().getHolder()).clickFromPlayerInventory(event);
                }
            }
            else event.setCancelled(true);
            if (event.getClickedInventory().getHolder() instanceof AbstractInventoryHolder) {
                ((AbstractInventoryHolder) event.getClickedInventory().getHolder()).click(event);
            }
            else event.setCancelled(true);
            if (event.getInventory().getHolder() instanceof AbstractInventoryHolder && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                ((AbstractInventoryHolder) event.getInventory().getHolder()).shiftClickFromPlayerInventory(event);
            }
            else event.setCancelled(true);
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof AbstractInventoryHolder) {
            ((AbstractInventoryHolder) event.getInventory().getHolder()).close(event);
        }
    }

}