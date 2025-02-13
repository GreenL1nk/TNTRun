package greenlink.game.bonus.bonuses;

import greenlink.game.bonus.AbstractBonus;
import greenlink.game.bonus.Bonus;
import greenlink.game.players.RunPlayer;
import greenlink.game.session.GameSession;
import greenlink.game.session.GameSessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PoisonGrenade extends AbstractBonus {
    public PoisonGrenade(String bonusName, ItemStack itemStack) {
        super(bonusName, itemStack);
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GameSession session = GameSessionManager.getInstance().getSession(player.getWorld().getName());
        if (session == null) return;
        RunPlayer runPlayer = session.getAlivePlayer(player);
        if (runPlayer == null) return;
        consume(runPlayer);
    }


    @Override
    public void consume(RunPlayer runPlayer) {
        runPlayer.consumeBonus(Bonus.getBonusByItem(this));
    }
}
