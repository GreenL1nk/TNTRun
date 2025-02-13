package greenlink.game;

import greenlink.ConfigValue;
import greenlink.TntRun;
import greenlink.game.cutter.CutManager;
import greenlink.game.session.GameSession;
import greenlink.game.session.GameState;
import greenlink.statistic.PlayerStats;
import greenlink.statistic.StatisticPeriod;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class GameZone {

    private HashSet<Block> blockstodestroy = new HashSet<>();
    public @Getter HashSet<Block> destroyedBlocks = new HashSet<>();
    public @Getter HashMap<UUID, Integer> playersBlocksDestroyed = new HashMap<>();


    GameSession gameSession;
    @Getter CutManager cutManager;
    private final int SCAN_DEPTH = 2;

    public GameZone(GameSession gameSession) {
        this.gameSession = gameSession;
        this.cutManager = new CutManager();
    }

    public void destroyBlock(Location loc, Player destroyedBy) {
        int y = loc.getBlockY() + 1;
        Block block = null;
        for (int i = 0; i <= SCAN_DEPTH; i++) {
            block = getBlockUnderPlayer(y, loc);
            y--;
            if (block != null) {
                break;
            }
        }
        if (block != null) {
            final Block fblock = block;
            if (!blockstodestroy.contains(fblock) && gameSession.getArenaBlocks().stream().anyMatch(b -> b.getLocation().equals(fblock.getLocation()))) {
                blockstodestroy.add(fblock);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (gameSession.getState() == GameState.RUNNING) {
                            blockstodestroy.remove(fblock);
                            fblock.setType(Material.AIR);
                            destroyedBlocks.add(fblock);
                            cutManager.recordBlockDestruction(fblock, destroyedBy);

                            PlayerStats playerStatistics = TntRun.getInstance().getStatsService().getPlayerStats(destroyedBy, StatisticPeriod.ALL_TIME.getPeriod());
                            playerStatistics.incrementBlocksBroken();

                            playersBlocksDestroyed.put(destroyedBy.getUniqueId(), playersBlocksDestroyed.getOrDefault(destroyedBy.getUniqueId(), 0) + 1);
                        }
                    }
                }.runTaskLater(TntRun.getInstance(), (long) (ConfigValue.BLOCK_DISAPPEAR_TIME.getDouble() * 10));
            }
        }
    }

    private static double PLAYER_BOUNDINGBOX_ADD = 0.3;

    private Block getBlockUnderPlayer(int y, Location location) {
        PlayerPosition loc = new PlayerPosition(location.getX(), y, location.getZ());
        Block b11 = loc.getBlock(location.getWorld(), +PLAYER_BOUNDINGBOX_ADD, -PLAYER_BOUNDINGBOX_ADD);
        if (b11.getType() != Material.AIR && b11.getType() != Material.LANTERN) {
            return b11;
        }
        Block b12 = loc.getBlock(location.getWorld(), -PLAYER_BOUNDINGBOX_ADD, +PLAYER_BOUNDINGBOX_ADD);
        if (b12.getType() != Material.AIR && b12.getType() != Material.LANTERN) {
            return b12;
        }
        Block b21 = loc.getBlock(location.getWorld(), +PLAYER_BOUNDINGBOX_ADD, +PLAYER_BOUNDINGBOX_ADD);
        if (b21.getType() != Material.AIR && b21.getType() != Material.LANTERN) {
            return b21;
        }
        Block b22 = loc.getBlock(location.getWorld(), -PLAYER_BOUNDINGBOX_ADD, -PLAYER_BOUNDINGBOX_ADD);
        if (b22.getType() != Material.AIR && b22.getType() != Material.LANTERN) {
            return b22;
        }
        return null;
    }

    private static class PlayerPosition {

        private double x;
        private int y;
        private double z;

        public PlayerPosition(double x, int y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Block getBlock(World world, double addx, double addz) {
            return world.getBlockAt(NumberConversions.floor(x + addx), y, NumberConversions.floor(z + addz));
        }
    }
}
