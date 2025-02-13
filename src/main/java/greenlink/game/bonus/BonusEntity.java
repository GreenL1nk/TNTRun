package greenlink.game.bonus;

import greenlink.arena.Floor;
import lombok.Data;
import org.bukkit.entity.Entity;

import java.util.UUID;

@Data
public class BonusEntity {

    private UUID uuid;
    private Entity entity;
    private boolean isRare;
    private Floor floor;

    public BonusEntity(UUID uuid, boolean isRare, Floor floor, Entity entity) {
        this.uuid = uuid;
        this.isRare = isRare;
        this.floor = floor;
        this.entity = entity;
    }
}
