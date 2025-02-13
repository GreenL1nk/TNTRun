package greenlink.utils;

import greenlink.TntRun;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FireworkUtils {

    public static void launchFireworksAroundArena(Location center, int radius) {
        double interval = radius / 2.0;

        List<Location> fireworkLocations = getPerimeterLocations(center.add(0, 1, 0), radius, interval, center.getWorld());

        new BukkitRunnable() {
            int timeElapsed = 0;

            @Override
            public void run() {
                if (timeElapsed >= 100) {
                    cancel();
                    return;
                }

                for (Location loc : fireworkLocations) {
                    launchFirework(loc);
                }

                timeElapsed += 20;
            }
        }.runTaskTimer(TntRun.getInstance(), 0L, 10L);
    }

    private static List<Location> getPerimeterLocations(Location center, int radius, double interval, World world) {
        List<Location> locations = new ArrayList<>();
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();
        int y = center.getBlockY();

        double circumference = 2 * Math.PI * radius;

        int points = (int) Math.floor(circumference / interval);

        double angleStep = 360.0 / points;

        for (int i = 0; i < points; i++) {
            double angle = Math.toRadians(i * angleStep);
            int x = centerX + (int) (radius * Math.cos(angle));
            int z = centerZ + (int) (radius * Math.sin(angle));
            locations.add(new Location(world, x, y, z));
        }

        return locations;
    }

    private static void launchFirework(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder()
                .withColor(Color.GREEN, Color.RED, Color.PURPLE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withFlicker()
                .build());
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }
}