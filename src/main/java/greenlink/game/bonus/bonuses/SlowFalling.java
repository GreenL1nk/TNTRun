package greenlink.game.bonus.bonuses;

import greenlink.ConfigValue;
import greenlink.game.bonus.AbstractBonus;
import greenlink.game.players.RunPlayer;
import greenlink.game.session.GameSession;
import greenlink.game.session.GameSessionManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowFalling extends AbstractBonus {

    int delay = 20 * 5;
    PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, delay, 0);

    public SlowFalling(String bonusName, ItemStack itemStack) {
        super(bonusName, itemStack);
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        GameSession session = GameSessionManager.getInstance().getSession(player.getWorld().getName());
        if (session == null) return;
        RunPlayer runPlayer = session.getAlivePlayer(player);
        if (runPlayer == null) return;
        player.addPotionEffect(potionEffect);
        player.playSound(player.getLocation(), Sound.valueOf(ConfigValue.SOUND_SLOW_FALL_CUBE.getString()), 1, 1);
        consume(runPlayer);
    }
}
