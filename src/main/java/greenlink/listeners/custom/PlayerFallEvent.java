package greenlink.listeners.custom;

import greenlink.arena.Floor;
import greenlink.game.session.GameSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerFallEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final GameSession session;
    private final Floor previousFloor;
    private final Floor newFloor;
    private final Player player;
    private final boolean wasCutOff;
    private final Player cutOffBy;
    private final Location fallLocation;

    public PlayerFallEvent(GameSession session, Floor previousFloor, Floor newFloor, Player player, boolean wasCutOff, Player cutOffBy, Location fallLocation) {
        this.session = session;
        this.previousFloor = previousFloor;
        this.newFloor = newFloor;
        this.player = player;
        this.wasCutOff = wasCutOff;
        this.cutOffBy = cutOffBy;
        this.fallLocation = fallLocation;
    }

    public boolean wasCutOff() {
        return wasCutOff;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}