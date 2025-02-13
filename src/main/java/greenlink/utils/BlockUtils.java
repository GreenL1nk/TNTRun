package greenlink.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

    public static List<Block> getBlocksInRadius(Location center, int radius) {
        List<Block> blocks = new ArrayList<>();
        World world = center.getWorld();

        if (world == null) return blocks;

        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        double radiusSquared = Math.pow(radius + 0.5, 2);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distanceSquared = Math.pow(x, 2) + Math.pow(z, 2);
                if (distanceSquared <= radiusSquared) {
                    Location loc = new Location(world, centerX + x, centerY, centerZ + z);
                    blocks.add(loc.getBlock());
                }
            }
        }

        return blocks;
    }
}