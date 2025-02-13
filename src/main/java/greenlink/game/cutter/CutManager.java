package greenlink.game.cutter;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CutManager {

    private final Map<Block, CutterInfo> recentDestroyedBlocks = new ConcurrentHashMap<>();

    public boolean checkIfCutOff(Player player) {
        Location playerLocation = player.getLocation();
        long currentTime = System.currentTimeMillis();

        for (Block block : recentDestroyedBlocks.keySet()) {
            if (block.getLocation().distance(playerLocation) <= 1.5) {
                CutterInfo cutterInfo = recentDestroyedBlocks.get(block);
                if (cutterInfo != null && (currentTime - cutterInfo.getDestructionTime()) <= 1000) {
                    return true;
                }
            }
        }
        return false;
    }

    public Player getCutter(Player player) {
        Location playerLocation = player.getLocation();
        long currentTime = System.currentTimeMillis();

        for (Block block : recentDestroyedBlocks.keySet()) {
            if (block.getLocation().distance(playerLocation) <= 1.5) {
                CutterInfo cutterInfo = recentDestroyedBlocks.get(block);
                if (cutterInfo != null && (currentTime - cutterInfo.getDestructionTime()) <= 1000) {
                    return cutterInfo.getCutter();
                }
            }
        }
        return null;
    }

    public void recordBlockDestruction(Block block, Player player) {
        recentDestroyedBlocks.put(block, new CutterInfo(player, System.currentTimeMillis()));

        recentDestroyedBlocks.entrySet().removeIf(entry -> (System.currentTimeMillis() - entry.getValue().getDestructionTime()) > 1000);
    }
}