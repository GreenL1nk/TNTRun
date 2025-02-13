package greenlink.game.session;

import greenlink.ConfigValue;
import greenlink.TntRun;
import greenlink.arena.Arena;
import greenlink.arena.Floor;
import greenlink.game.GameZone;
import greenlink.game.players.RunPlayer;
import greenlink.statistic.PlayerStats;
import greenlink.statistic.StatisticPeriod;
import greenlink.utils.FireworkUtils;
import greenlink.utils.PlayerInventories;
import lombok.Data;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ArenaSession {

    private final Arena arena;
    private int arenaHandler;
    private GameSession gameSession;
    private GameZone gameZone;
    private Set<RunPlayer> processedPlayers = new HashSet<>();

    public ArenaSession(Arena arena, GameSession gameSession) {
        this.arena = arena;
        this.gameSession = gameSession;
        this.gameZone = new GameZone(gameSession);
    }

    public void startArena() {
        for (RunPlayer runPlayer : gameSession.getRunPlayers()) {
            Player player = runPlayer.getPlayer();
            player.getInventory().clear();
            player.closeInventory();
            player.setLevel(0);
        }

        arenaHandler = Bukkit.getScheduler().scheduleSyncRepeatingTask(TntRun.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (gameSession.getRunPlayers().isEmpty()) {
                    gameSession.endGame();
                    return;
                }

                List<RunPlayer> playersCopy = new ArrayList<>(gameSession.getRunPlayers());

                for (RunPlayer runPlayer : playersCopy) {
                    if (shouldRemovePlayer(runPlayer) && !processedPlayers.contains(runPlayer)) {
                        setLosePlayer(runPlayer);
                        gameSession.getRunPlayers().remove(runPlayer);
                    } else {
                        handlePlayer(runPlayer);
                    }
                }
            }
        }, 20, 1);
    }

    private boolean shouldRemovePlayer(RunPlayer runPlayer) {
        if (!runPlayer.getPlayer().isOnline()) return true;
        if (gameSession.determinePlayerFloor(runPlayer.getPlayer().getLocation().getY()) == null) return true;
        if (!gameSession.getRunPlayers().contains(runPlayer)) return true;
        return false;
    }

    private void handlePlayer(final RunPlayer runPlayer) {
        processedPlayers.add(runPlayer);
        Player player = runPlayer.getPlayer();
        Location plloc = player.getLocation();
        Location plufloc = plloc.clone().add(0, -1, 0);

        gameZone.destroyBlock(plufloc, player);

        if (gameSession.getRunPlayers().size() > 1) {
            if (arena.isLoseLocation(plloc)) {
                setLosePlayer(runPlayer);
            }
        }

        if (gameSession.getRunPlayers().size() == 1) {
            RunPlayer winner = gameSession.getRunPlayers().get(0);
            startWinnerEnding(winner);
        }
    }

    public void setLosePlayer(RunPlayer runPlayer) {
        runPlayer.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                String.format("&a%s &5вылетел!", runPlayer.getPlayer().getName()))));
        runPlayer.getPlayer().teleport(arena.getTeleportLocation());
        runPlayer.getPlayer().showTitle(Title.title(LegacyComponentSerializer.legacyAmpersand().deserialize("&dВы вылетели"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("&aПовезёт в другой раз!")));
        gameSession.getGameTimerManager().stopTimer(runPlayer.getPlayer());
        PlayerInventories.setLobbyInventory(runPlayer.getPlayer());

        PlayerStats playerStatistics = TntRun.getInstance().getStatsService().getPlayerStats(runPlayer.getPlayer(), "all_time");
        playerStatistics.updateBestTime(gameSession.getGameTimerManager().getGameTimer());
        playerStatistics.incrementLosses();
        playerStatistics.incrementGamesPlayed();

        List<String> winCommands = ConfigValue.LOSE_COMMANDS.getStringList();
        for (String command : winCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", runPlayer.getPlayer().getName()));
        }
        gameSession.getRunPlayers().remove(runPlayer);

        runPlayer.getPlayer().setAllowFlight(false);
        runPlayer.getPlayer().setFlying(false);

        runPlayer.getPlayer().teleport(runPlayer.getLastLocation());
    }

    private void startWinnerEnding(RunPlayer runPlayer) {
        Floor floorByNumber = arena.getFloorByNumber(3);
        if (floorByNumber != null) {
            runPlayer.getPlayer().teleport(floorByNumber.getCenter().add(0, 1, 0));
        }
        gameSession.getRunPlayers().remove(runPlayer);
        PlayerInventories.setLobbyInventory(runPlayer.getPlayer());
        runPlayer.getPlayer().showTitle(Title.title(LegacyComponentSerializer.legacyAmpersand().deserialize("&aПоздравляем!"),
                LegacyComponentSerializer.legacyAmpersand().deserialize(String.format(
                        "&aВы одержали победу за %.1f секунд!", gameSession.getGameTimerManager().getGameTimer())
                )));


        PlayerStats playerStatistics = TntRun.getInstance().getStatsService().getPlayerStats(runPlayer.getPlayer(), StatisticPeriod.ALL_TIME.getPeriod());
        playerStatistics.updateBestTime(gameSession.getGameTimerManager().getGameTimer());
        playerStatistics.incrementWins();
        playerStatistics.incrementGamesPlayed();

        List<String> winCommands = ConfigValue.WIN_COMMANDS.getStringList();
        for (String command : winCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", runPlayer.getPlayer().getName()));
        }

        runPlayer.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize(String.format(
                        "&eИгра окончена. Победил &a%s&e за &a%.1f секунд", runPlayer.getPlayer().getName(), gameSession.getGameTimerManager().getGameTimer())
                )));

        gameSession.endGame();
        Bukkit.getScheduler().cancelTask(arenaHandler);

        FireworkUtils.launchFireworksAroundArena(arena.getFloorByNumber(3).getCenter(), arena.isMini() ? 13:25);

        runPlayer.getPlayer().setAllowFlight(false);
        runPlayer.getPlayer().setFlying(false);

        if (runPlayer.getPlayer().getWorld().getPlayers().size() < gameSession.getMinPlayers()) {
            runPlayer.getPlayer().teleport(gameSession.getArena().getTeleportLocation());
        }
    }
}
