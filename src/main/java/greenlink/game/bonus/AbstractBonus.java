package greenlink.game.bonus;

import greenlink.game.players.RunPlayer;
import greenlink.items.AbstractItem;
import lombok.Getter;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractBonus extends AbstractItem {
    @Getter
    String bonusName;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public AbstractBonus(String bonusName, ItemStack itemStack) {
        super(itemStack);
        this.bonusName = bonusName;
    }

    public void onPickUp(RunPlayer player) {
        super.onPickUp(player.getPlayer());
    }

    public void consume(RunPlayer runPlayer) {
        super.consume(runPlayer.getPlayer());
        runPlayer.consumeBonus(Bonus.getBonusByItem(this));
    }

    public void applyWithCooldown(UUID uuid, PlayerInteractEvent event) {
        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(uuid, 0L);

        if (currentTime - lastUsed >= 500) {
            onUse(event);
            cooldowns.put(uuid, currentTime);
        }
    }
}