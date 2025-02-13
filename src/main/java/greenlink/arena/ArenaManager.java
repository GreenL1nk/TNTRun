package greenlink.arena;

import greenlink.TntRun;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ArenaManager {
    private static ArenaManager instance;
    @Getter
    private final HashMap<World, Arena> arenas = new LinkedHashMap<>();
    private final Plugin plugin;
    private final FileConfiguration config;

    private ArenaManager() {
        this.plugin = TntRun.getInstance();
        this.config = plugin.getConfig();
        loadArenasFromConfig();
    }

    public static ArenaManager getInstance() {
        if (instance == null) {
            instance = new ArenaManager();
        }
        return instance;
    }

    public boolean tryAddArena(World world, String arenaName, boolean isMini) {
        if (arenas.containsKey(world)) {
            return false;
        }
        Arena arena = new Arena(isMini, arenaName, world, null);
        arenas.put(world, arena);
        saveArenaToConfig(arena);
        return true;
    }

    public boolean isArenaExists(World world) {
        return arenas.containsKey(world);
    }

    @Nullable
    public Arena getArenaByWorld(World world) {
        return arenas.get(world);
    }

    @Nullable
    public Arena getArenaByName(String arenaName) {
        for (Arena arena : arenas.values()) {
            if (arena.getArenaName().equals(arenaName)) {
                return arena;
            }
        }
        return null;
    }

    public @Nullable Arena getNextArena(Arena currentArena) {
        if (arenas.size() == 1) return null;
        if (arenas.isEmpty()) return null;

        List<World> worlds = new ArrayList<>(arenas.keySet());
        int currentIndex = worlds.indexOf(currentArena.getWorld());

        if (currentIndex == -1) {
            return null;
        }

        int nextIndex = (currentIndex + 1) % worlds.size();
        return arenas.get(worlds.get(nextIndex));
    }

    public @Nullable Location getNextTeleportLocation(Arena currentArena) {
        Arena nextArena = getNextArena(currentArena);
        boolean isCurrentMini = currentArena.isMini();

        while (nextArena != null) {
            if (nextArena.isMini() == isCurrentMini && nextArena.getTeleportLocation() != null) {
                return nextArena.getTeleportLocation();
            }

            nextArena = getNextArena(nextArena);

            if (nextArena == currentArena) {
                break;
            }
        }
        return null;
    }

    public void removeArena(World world) {
        arenas.remove(world);
        config.set("arenas." + world.getName(), null);
        plugin.saveConfig();
    }

    public void saveArenaToConfig(Arena arena) {
        config.set("arenas." + arena.getWorld().getName() + ".name", arena.getArenaName());
        config.set("arenas." + arena.getWorld().getName() + ".isMini", arena.isMini());
        config.set("arenas." + arena.getWorld().getName() + ".teleportLocation", arena.getTeleportLocation());
        for (Floor floor : arena.getFloors()) {
            config.set("arenas." + arena.getWorld().getName() + ".floors." + floor.getNumber(), floor.getCenter());
        }
        plugin.saveConfig();
    }

    private void loadArenasFromConfig() {
        if (config.contains("arenas")) {
            for (String worldName : config.getConfigurationSection("arenas").getKeys(false)) {
                World world = Bukkit.getWorld(worldName);
                String arenaName = config.getString("arenas." + worldName + ".name");
                boolean isMini = config.getBoolean("arenas." + worldName + ".isMini");
                Location teleportLocation = config.getLocation("arenas." + worldName + ".teleportLocation");
                if (world != null && arenaName != null) {
                    Arena arena = new Arena(isMini, arenaName, world, teleportLocation);
                    arenas.put(world, arena);
                    ConfigurationSection floorSection= config.getConfigurationSection("arenas." + worldName + ".floors");
                    if (floorSection != null) {
                        for (String floorNumber : floorSection.getKeys(false)) {
                            arena.addFloor(new Floor(Integer.parseInt(floorNumber), config.getLocation("arenas." + worldName + ".floors." + floorNumber)));
                        }
                    }
                } else {
                    plugin.getLogger().warning("Мир " + worldName + " не найден, арена не загружена.");
                }
            }
        }
    }
}