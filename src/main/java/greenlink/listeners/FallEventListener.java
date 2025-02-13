package greenlink.listeners;

import greenlink.ConfigValue;
import greenlink.TntRun;
import greenlink.arena.Floor;
import greenlink.game.players.RunPlayer;
import greenlink.game.session.ArenaSession;
import greenlink.game.session.GameSession;
import greenlink.game.session.GameSessionManager;
import greenlink.listeners.custom.PlayerFallEvent;
import greenlink.statistic.PlayerStats;
import greenlink.statistic.StatisticPeriod;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class FallEventListener implements Listener {

    private Map<Player, Integer> lastCutFloor = new HashMap<>();
    private Map<Player, BukkitTask> cutRemovalTasks = new HashMap<>();

    @EventHandler
    public void onPlayerFall(PlayerFallEvent event) {
        GameSession session = event.getSession();
        Floor previousFloor = event.getPreviousFloor();
        Floor newFloor = event.getNewFloor();
        Player player = event.getPlayer();
        boolean wasCutOff = event.wasCutOff();
        Player cutOffBy = event.getCutOffBy();

        if (wasCutOff && cutOffBy != null) {
            Integer lastFloorCut = getLastCutFloor(player);

            if (lastFloorCut == null || lastFloorCut != previousFloor.getNumber()) {
                cutOffBy.playSound(cutOffBy.getLocation(), Sound.valueOf(ConfigValue.SOUND_PLAYER_CUT.getString()), 1, 2);
                session.getArena().getWorld().getPlayers().forEach(p -> {
                    p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(String.format("&c%s &6подрезал &c%s &6на &e%s &6этаже",
                            cutOffBy.getName(),
                            player.getName(),
                            previousFloor.getNumber())));
                });
                PlayerStats playerStatistics = TntRun.getInstance().getStatsService().getPlayerStats(cutOffBy, StatisticPeriod.ALL_TIME.getPeriod());
                playerStatistics.incrementCuts();

                markPlayerCut(player, previousFloor.getNumber());
            }
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameSession session = GameSessionManager.getInstance().getSession(player.getWorld().getName());
        if (session == null) return;
        RunPlayer runPlayer = session.getAlivePlayer(player);
        if (runPlayer == null) return;

        if (event.getFrom().getY() == event.getTo().getY()) return;

        Floor currentFloor = session.determinePlayerFloor(event.getTo().getY());
        Floor previousFloor = session.determinePlayerFloor(event.getFrom().getY());

        if (currentFloor == previousFloor) return;

        session.updateFloorPlayersCount(previousFloor, currentFloor);

        ArenaSession arenaSession = session.getArenaSession();
        if (arenaSession == null) return;
        boolean wasCutOff = arenaSession.getGameZone().getCutManager().checkIfCutOff(player);
        Player cutOffBy = wasCutOff ? arenaSession.getGameZone().getCutManager().getCutter(player) : null;
        if (cutOffBy == player) {
            wasCutOff = false;
            cutOffBy = null;
        }

        PlayerFallEvent fallEvent = new PlayerFallEvent(session, previousFloor, currentFloor, player, wasCutOff, cutOffBy, event.getFrom());
        Bukkit.getPluginManager().callEvent(fallEvent);
    }

    public void markPlayerCut(Player player, int floorNumber) {
        if (cutRemovalTasks.containsKey(player)) {
            cutRemovalTasks.get(player).cancel();
        }

        lastCutFloor.put(player, floorNumber);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), () -> {
            lastCutFloor.remove(player);
            cutRemovalTasks.remove(player);
        }, 3 * 20L);

        cutRemovalTasks.put(player, task);
    }

    public Integer getLastCutFloor(Player player) {
        return lastCutFloor.get(player);
    }
}