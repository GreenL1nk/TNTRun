package greenlink.game.bonus.bonuses;

import greenlink.ConfigValue;
import greenlink.TntRun;
import greenlink.game.bonus.AbstractBonus;
import greenlink.game.bonus.Bonus;
import greenlink.game.players.RunPlayer;
import greenlink.game.session.GameSession;
import greenlink.game.session.GameSessionManager;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EnumMoveType;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class DoubleJump extends AbstractBonus {

    HashMap<Player, Integer> tasks = new HashMap<>();
    private final HashMap<UUID, Long> lastJumpTimes = new HashMap<>();

    private static final long JUMP_DELAY = 500;

    public DoubleJump(String bonusName, ItemStack itemStack) {
        super(bonusName, itemStack);
    }

    @Override
    public void onPickUp(RunPlayer runPlayer) {
        runPlayer.setCanDoubleJump(true);
        runPlayer.getPlayer().setAllowFlight(true);
    }

    @Override
    public void onFly(PlayerToggleFlightEvent event) {
        GameSession session = GameSessionManager.getInstance().getSession(event.getPlayer().getWorld().getName());
        if (session == null) return;

        Player player = event.getPlayer();
        RunPlayer runPlayer = session.getAlivePlayer(player);
        if (runPlayer == null) return;

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (lastJumpTimes.containsKey(playerId) && (currentTime - lastJumpTimes.get(playerId)) < JUMP_DELAY) {
            return;
        }

        lastJumpTimes.put(playerId, currentTime);

        runPlayer.setCanDoubleJump(false);
        player.setFlying(false);
        player.setAllowFlight(false);

        if (player instanceof CraftPlayer craftPlayer) {
            EntityHuman entityhuman = craftPlayer.getHandle();
            entityhuman.move(EnumMoveType.SELF, new Vec3D(0.0, 1.1999999284744263, 0.0));

            Vector vector = player.getLocation().getDirection().multiply(0.2).setY(0.8);
            player.setVelocity(vector);
        }


        player.playSound(player.getLocation(), Sound.valueOf(ConfigValue.SOUND_DOUBLE_JUMP.getString()), 1, 1);
        consume(runPlayer);

        if (!runPlayer.hasBonus(Bonus.DOUBLE_JUMP)) return;

        tasks.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(TntRun.getInstance(), () -> {
            if (session.getAlivePlayer(player) == null) {
                Bukkit.getScheduler().cancelTask(tasks.get(player));
                tasks.remove(player);
                return;
            }

            Location blockLocation = player.getLocation().clone().add(0, -1, 0);
            boolean blockValid = session.isBlockValid(blockLocation.getBlock(), player.getLocation());
            if (!blockValid) {
                return;
            }

            if (blockLocation.getBlock().getType() != Material.AIR && blockLocation.getBlock().getType() != Material.LANTERN) {
                double yDifference = player.getLocation().getY() - blockLocation.getBlock().getLocation().getY() - 1.0;

                if (yDifference >= 0 && yDifference < 0.01) {
                    runPlayer.setCanDoubleJump(true);
                    player.setAllowFlight(true);

                    Bukkit.getScheduler().cancelTask(tasks.get(player));
                    tasks.remove(player);
                }
            }
        }, 1, 1));
    }
}
