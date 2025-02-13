package greenlink.game.bonus;

import greenlink.game.players.RunPlayer;
import greenlink.game.session.GameSession;
import greenlink.game.session.GameSessionManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BonusListener implements Listener {

    @EventHandler
    public void onPickUp(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameSession session = GameSessionManager.getInstance().getSession(player.getWorld().getName());
        if (session == null) return;
        RunPlayer runPlayer = session.getAlivePlayer(player);
        if (runPlayer == null) return;
        for (BonusEntity entity : session.getBonusManager().getEntities().values()) {
            if (entity.getEntity().getLocation().distance(player.getLocation()) < 1.5) {
                session.getBonusManager().onBonusPickedUp(runPlayer, entity.getEntity().getUniqueId(),
                        entity.getFloor());
            }
        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GameSession session = GameSessionManager.getInstance().getSession(player.getWorld().getName());
        if (session == null) return;
        RunPlayer runPlayer = session.getAlivePlayer(player);
        if (runPlayer == null) return;
        if (event.getItem() != null && event.getHand() == EquipmentSlot.HAND) {
            Bonus bonus = Bonus.getBonusByItem(event.getItem());
            if (bonus != null) {
                if (!runPlayer.hasBonus(bonus)) return;
                bonus.getAbstractBonus().applyWithCooldown(player.getUniqueId(), event);
            }
        }
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        Player player = event.getPlayer();
        GameSession session = GameSessionManager.getInstance().getSession(player.getWorld().getName());
        if (session == null) return;
        RunPlayer runPlayer = session.getAlivePlayer(player);
        if (runPlayer == null) return;
        event.setCancelled(true);
        if (!runPlayer.hasBonus(Bonus.DOUBLE_JUMP)) {
            disableFly(event);
        }
        else {
            if (runPlayer.isCanDoubleJump()) {
                Bonus.DOUBLE_JUMP.getAbstractBonus().onFly(event);
            }
            else disableFly(event);
        }
    }

    private void disableFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        player.setFlying(false);
        player.setAllowFlight(false);
    }
}