package greenlink.listeners;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Collection;

public class MainListener implements Listener {


    @EventHandler
    public void onPotion(PotionSplashEvent event) {

        event.setCancelled(true);
        if (event.getPotion().getShooter() instanceof LivingEntity entity) {
            Collection<LivingEntity> affectedEntities = event.getAffectedEntities();
            affectedEntities.clear();

            Location hittedLocation = event.getPotion().getLocation();
            Collection<Player> nearbyEntitiesByType = hittedLocation.getNearbyEntitiesByType(Player.class, 4);
            affectedEntities.addAll(nearbyEntitiesByType);

            for (LivingEntity affectedEntity : nearbyEntitiesByType) {
                if (affectedEntity.getUniqueId().equals(entity.getUniqueId())) {
                    affectedEntities.remove(affectedEntity);
                }
            }

            affectedEntities.forEach(livingEntity -> livingEntity.addPotionEffects(event.getPotion().getEffects()));
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() != EntityDamageEvent.DamageCause.POISON) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
