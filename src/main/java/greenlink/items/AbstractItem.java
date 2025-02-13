package greenlink.items;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItem extends ItemStack {
    public AbstractItem(ItemStack itemStack) {
        super(itemStack);
    }
    public void onUse(PlayerInteractEvent event) {

    }

    public void onPickUp(Player player) {

    }

    public void onFly(PlayerToggleFlightEvent event) {

    }

    public void consume(Player player) {
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack == null) continue;
            if (itemStack.getItemMeta().equals(this.getItemMeta())) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                if (itemStack.getAmount() == 0) {
                    onConsume(player);
                }
            }
        }
    }

    public void onConsume(Player player) {

    }
}
