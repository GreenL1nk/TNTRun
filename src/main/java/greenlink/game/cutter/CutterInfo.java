package greenlink.game.cutter;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class CutterInfo {
    private final Player cutter;
    private final long destructionTime;

    public CutterInfo(Player cutter, long destructionTime) {
        this.cutter = cutter;
        this.destructionTime = destructionTime;
    }

}