package greenlink.arena;

import greenlink.schem.SchemaUtils;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
public class Arena {

    private World world;
    private String arenaName;
    private boolean isMini;
    Set<Floor> floors = new HashSet<>();
    private @Nullable Location teleportLocation;

    public Arena(boolean isMini, String arenaName, World world, @Nullable Location teleportLocation) {
        this.isMini = isMini;
        this.arenaName = arenaName;
        this.world = world;
        this.teleportLocation = teleportLocation;
    }

    public boolean tryAddFloor(Floor floor) {
        if (existsFloor(floor.getNumber())) {
            return false;
        }
        floors.add(floor);
        SchemaUtils.saveClipboard(this, floor);
        ArenaManager.getInstance().saveArenaToConfig(this);
        return true;
    }

    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    @Nullable
    private Integer getLowestFloorY() {
        Integer lowestY = null;
        for (Floor floor : floors) {
            if (lowestY == null) {
                lowestY = floor.getCenter().getBlockY();
                continue;
            }
            if (floor.getCenter().getBlockY() < lowestY) {
                lowestY = floor.getCenter().getBlockY();
            }
        }
        return lowestY;
    }

    @Nullable
    public Floor getFloorByNumber(int floorNumber) {
        return floors.stream().filter(floor -> floor.getNumber() == floorNumber).findFirst().orElse(null);
    }

    public boolean isLoseLocation(Location playerLocation) {
        Integer lowestFloorY = getLowestFloorY();
        if (lowestFloorY == null) return false;
        return playerLocation.getBlockY() < lowestFloorY - 6;
    }

    public void setTeleportLocation(Location location) {
        this.teleportLocation = location;
        ArenaManager.getInstance().saveArenaToConfig(this);
    }

    public boolean removeFloorByNumber(int floorNumber) {
        Optional<Floor> first = floors.stream().filter(floor -> floor.getNumber() != floorNumber).findFirst();
        if (first.isPresent()) {
            floors.remove(first.get());
            ArenaManager.getInstance().saveArenaToConfig(this);
            return true;
        }
        else return false;
    }

    public boolean existsFloor(int floorNumber) {
        return floors.stream().anyMatch(floor -> floor.getNumber() == floorNumber);
    }
}
