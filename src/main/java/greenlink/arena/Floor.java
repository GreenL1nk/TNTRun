package greenlink.arena;

import lombok.Data;
import org.bukkit.Location;

@Data
public class Floor {

    private final int number;
    private final Location center;

    public Location getCenter() {
        return center.clone();
    }
}
