package greenlink.game.session;

import greenlink.ConfigValue;
import greenlink.TntRun;
import greenlink.arena.Arena;
import greenlink.arena.Floor;
import greenlink.game.GameTimerManager;
import greenlink.game.bonus.BonusManager;
import greenlink.game.players.RunPlayer;
import greenlink.schem.SchemaUtils;
import greenlink.utils.BlockUtils;
import greenlink.utils.PlayerInventories;
import greenlink.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameSession {
    private final String worldName;
    private @Getter final List<RunPlayer> runPlayers;
    private @Getter GameState state;
    private @Getter final int minPlayers;
    private @Getter final int maxPlayers;
    private @Getter int countdownTimer;
    private BukkitTask countdownTask;
    private @Getter final Arena arena;
    @Getter @Nullable
    private ArenaSession arenaSession;
    private @Getter HashMap<Floor, List<Block>> floorBlocks = new HashMap<>();
    private @Getter BonusManager bonusManager;
    private @Getter GameTimerManager gameTimerManager;
    private @Getter int totalPlayers = 0;
    private @Getter HashMap<Floor, Integer> floorPlayersCount = new HashMap<>();
    private boolean isRestarted = false;

    public GameSession(String worldName, int minPlayers, int maxPlayers, Arena arena) {
        this.worldName = worldName;
        this.arena = arena;
        this.restoreArena();
        this.runPlayers = new ArrayList<>();
        this.state = GameState.WAITING;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.countdownTimer = 9;
        this.bonusManager = new BonusManager(this, arena.isMini() ?
                ConfigValue.SPECIAL_BONUS_NEED_SPAWN_MINI.getInt() :
                ConfigValue.SPECIAL_BONUS_NEED_SPAWN.getInt());
        bonusManager.removeEntities();
        this.gameTimerManager = new GameTimerManager(this);
    }

    public void cleanup() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        Bukkit.getScheduler().cancelTask(gameTimerManager.getGameTimeId());
        runPlayers.clear();
        state = GameState.WAITING;
        countdownTimer = 9;
        countdownTask = null;
        arenaSession = null;
    }

    @Nullable
    public RunPlayer getAlivePlayer(Player player) {
        return runPlayers.stream().filter(runPlayer -> runPlayer.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public void endGame() {
        if (state != GameState.RUNNING) {
            throw new IllegalStateException("Игра не может быть завершена, так как она не запущена.");
        }
        state = GameState.FINISHED;
        cleanup();
        GameSessionManager.getInstance().removeSession(worldName);
        GameSession newSession = GameSessionManager.getInstance().createSession(arena);
        newSession.isRestarted = true;
        Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), () -> {
            newSession.getArena().getWorld().getPlayers().forEach(newSession::addPlayer);
        }, 20 * 10);
    }

    public void addPlayer(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        PlayerInventories.setLobbyInventory(player);
        if (runPlayers.contains(getAlivePlayer(player))) return;
        if (state != GameState.WAITING && state != GameState.PRE_RUN) {
            if (isOnArena(player)) {
                player.teleport(arena.getTeleportLocation());
            }
            return;
        }
        if (runPlayers.size() >= maxPlayers) {
            return;
        }
        runPlayers.add(new RunPlayer(player));
        if (GameState.PRE_RUN == state) randomTeleport(player);
        if (runPlayers.size() >= minPlayers && countdownTask == null) {
            if (isRestarted) {
                countdownTask = Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), () -> {
                    startCountdown();
                    isRestarted = false;
                }, 20 * 5);
            } else {
                startCountdown();
            }
        }
    }

    private void startCountdown() {
        state = GameState.PRE_RUN;
        runPlayers.forEach(runPlayer -> {
            runPlayer.setLastLocation(runPlayer.getPlayer().getLocation(), this);
            teleportIfNeed(runPlayer);
        });
        countdownTask = Bukkit.getScheduler().runTaskTimer(TntRun.getInstance(), () -> {
            if (runPlayers.size() < minPlayers) {
                state = GameState.WAITING;
                countdownTask.cancel();
                countdownTask = null;
                countdownTimer = 9;
                runPlayers.forEach(runPlayer -> {
                    runPlayer.getPlayer().clearTitle();
                    runPlayer.getPlayer().teleport(arena.getTeleportLocation());
                });
                return;
            }

            if (countdownTimer > 0) {
//                Component timer = countdownTimer > 3 ? LegacyComponentSerializer.legacyAmpersand().deserialize("&a" + countdownTimer)
//                        : LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + countdownTimer);
//                showTitle(runPlayers.stream().map(RunPlayer::getPlayer).toList(), timer, Component.empty());
                playCountdownSound();
                countdownTimer--;
            } else {
                startGame();
                if (countdownTask != null) {
                    countdownTask.cancel();
                }
            }
        }, 0L, 20L);
    }

    public void startGame() {
        if ((state != GameState.WAITING && state != GameState.PRE_RUN) || runPlayers.isEmpty()) {
            throw new IllegalStateException("Игра не может быть запущена.");
        }
        state = GameState.RUNNING;

        showTitle(runPlayers.stream().map(RunPlayer::getPlayer).toList(), LegacyComponentSerializer.legacyAmpersand().deserialize("&aИгра началась!"), Component.empty());

        arenaSession = new ArenaSession(arena, this);
        arenaSession.startArena();
        totalPlayers = getRunPlayers().size();
        gameTimerManager.startGlobalTimer();
        bonusManager.getSpawnController().spawnInitialBonuses();
        floorPlayersCount.put(arena.getFloorByNumber(3), totalPlayers);
    }

    public void restoreArena() {
        int radius = arena.isMini() ? 13 : 25;
        for (Floor floor : arena.getFloors()) {
            SchemaUtils.pasteSchema(floor.getCenter(), "floor_" + floor.getNumber() + ".schem", arena);
            floorBlocks.put(floor, BlockUtils.getBlocksInRadius(floor.getCenter(), radius));
            floorPlayersCount.put(floor, 0);
        }
    }

    public void showTitle(List<Player> targets, Component mainTitle, Component subtitle) {
        final Title title = Title.title(mainTitle, subtitle);
        for (Audience target : targets) {
            target.showTitle(title);
        }
    }

    private void playCountdownSound() {
        for (RunPlayer player : runPlayers) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.valueOf(ConfigValue.SOUND_GAME_START_TICK.getString()), 1.0f, 1.0f);
        }
    }

    public void removePlayer(Player player) {
        runPlayers.remove(getAlivePlayer(player));
        gameTimerManager.removePlayer(player);
    }

    public void randomTeleport(Player player) {
        Random random = new Random();
        List<Block> blocks = floorBlocks.get(arena.getFloorByNumber(3));

        Location location = blocks.stream()
                .skip(random.nextInt(blocks.size()))
                .findFirst()
                .orElse(arena.getFloorByNumber(3).getCenter().getBlock())
                .getLocation();

        player.teleport(location.add(0.5, 1, 0.5));
    }

    public boolean isOnArena(Player player) {
        boolean isOnArena = false;
        for (Floor floor : arena.getFloors()) {
            isOnArena = floorBlocks.get(floor)
                    .stream()
                    .anyMatch(block -> {
                        Location playerLocation = player.getLocation();
                        return block.getX() == playerLocation.getBlockX() &&
                                block.getZ() == playerLocation.getBlockZ() &&
                                (playerLocation.getBlockY() == block.getY() + 1 ||
                                        playerLocation.getBlockY() == block.getY() + 2);
                    });
            if (isOnArena) break;
        }
        return isOnArena;
    }

    public void teleportIfNeed(RunPlayer runPlayer) {
        Player player = runPlayer.getPlayer();
        boolean isOnFloor = floorBlocks.get(arena.getFloorByNumber(3))
                .stream()
                .anyMatch(block -> {
                    Location playerLocation = player.getLocation();
                    return block.getX() == playerLocation.getBlockX() &&
                            block.getZ() == playerLocation.getBlockZ() &&
                            (playerLocation.getBlockY() == block.getY() + 1 ||
                                    playerLocation.getBlockY() == block.getY() + 2);
                });

        if (!isOnFloor) {
            randomTeleport(player);
            Utils.debugLog("Игрок " + player.getName() + " был телепортирован на арену.");
        } else {
            Utils.debugLog("Игрок " + player.getName() + " уже находится на арене.");
        }
    }

    public Location getRandomValidLocationOnFloor(Floor floor) {
        Random random = new Random();
        if (arenaSession == null) return null;
        HashSet<Block> destroyedBlocks = arenaSession.getGameZone().getDestroyedBlocks();
        List<Block> blocks = getFloorBlocks().get(floor);

        blocks.removeAll(destroyedBlocks);

        Location location = blocks.stream()
                .skip(random.nextInt(blocks.size()))
                .findFirst()
                .orElseThrow()
                .getLocation();

        location.add(0.5, 0, 0.5);
        return location;
    }

    public boolean isBlockValid(Block block, Location playerLocation) {
        HashSet<Block> destroyedBlocks = arenaSession.getGameZone().getDestroyedBlocks();
        List<Block> blocks = getFloorBlocks().get(determinePlayerFloor(playerLocation.getY()));
        blocks.removeAll(destroyedBlocks);
        return blocks.contains(block);
    }

    @Nullable
    public Floor determinePlayerFloor(double playerY) {
        Floor firstFloor = arena.getFloorByNumber(1);
        Floor secondFloor = arena.getFloorByNumber(2);
        Floor thirdFloor = arena.getFloorByNumber(3);

        if (playerY < firstFloor.getCenter().getBlockY()) {
            return null;
        } else if (playerY >= firstFloor.getCenter().getBlockY() && playerY < secondFloor.getCenter().getBlockY()) {
            return firstFloor;
        } else if (playerY >= secondFloor.getCenter().getBlockY() && playerY < thirdFloor.getCenter().getBlockY()) {
            return secondFloor;
        } else {
            return thirdFloor;
        }
    }

    public void updateFloorPlayersCount(Floor oldFloor, Floor newFloor) {
        if (oldFloor != null) {
            floorPlayersCount.put(oldFloor, Math.max(0, floorPlayersCount.get(oldFloor) - 1));
        }
        if (newFloor != null) {
            floorPlayersCount.put(newFloor, floorPlayersCount.getOrDefault(newFloor, 0) + 1);
        }
    }

    public List<Block> getArenaBlocks() {
        List<Block> allBlocks = new ArrayList<>();
        for (List<Block> blocks : floorBlocks.values()) {
            allBlocks.addAll(blocks);
        }
        return allBlocks;
    }
}