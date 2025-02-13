package greenlink.placeholder;

import greenlink.TntRun;
import greenlink.game.players.RunPlayer;
import greenlink.game.session.GameSession;
import greenlink.game.session.GameSessionManager;
import greenlink.game.session.GameState;
import greenlink.statistic.PlayerStats;
import greenlink.statistic.PlayerStatsService;
import greenlink.statistic.StatisticPeriod;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DataPlaceholder extends PlaceholderExpansion {

    @Override
    @NotNull
    public String getAuthor() {
        return "GreenLink";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "tntrun";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] args = params.split("_");
        String param = args[0];
        GameSession session = GameSessionManager.getInstance().getSession(player.getWorld().getName());
        PlayerStatsService playerManager = TntRun.getInstance().getStatsService();
        PlayerStats playerStatistics = playerManager.getPlayerStats(player, StatisticPeriod.ALL_TIME.getPeriod());
        if (session == null) return null;
        return switch (param) {
            case "inGame" -> session.getState() == GameState.RUNNING ? "true" : "false";
            case "playersCount" -> String.valueOf(session.getTotalPlayers());
            case "leftPlayers" -> String.valueOf(session.getRunPlayers().size());
            case "arenaName" -> session.getArena().getArenaName();
            case "gameStatus" -> {
                if (session.getState() == GameState.PRE_RUN) {
                    int countdownTimer = session.getCountdownTimer();
                    Component timer = countdownTimer > 3 ? LegacyComponentSerializer.legacyAmpersand().deserialize("&a" + countdownTimer)
                            : LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + countdownTimer);
                    session.showTitle(session.getRunPlayers().stream().map(RunPlayer::getPlayer).toList(), timer, Component.empty());
                    yield GameState.PRE_RUN.getWithArg(countdownTimer);
                }
                else yield session.getState().getMessage();
            }
            case "gameTime" -> {
                if (session.getState() == GameState.WAITING) yield GameState.WAITING.getMessage();
                else yield String.valueOf(session.getGameTimerManager().getGameTimer());
            }
            case "blocksDestroyed" -> String.valueOf(session.getArenaSession().getGameZone().getPlayersBlocksDestroyed().getOrDefault(player.getUniqueId(), 0));

            case "floor3" -> String.valueOf(session.getFloorPlayersCount().get(session.getArena().getFloorByNumber(3)));
            case "floor2" -> String.valueOf(session.getFloorPlayersCount().get(session.getArena().getFloorByNumber(2)));
            case "floor1" -> String.valueOf(session.getFloorPlayersCount().get(session.getArena().getFloorByNumber(1)));
            case "bestTime" -> {
                if (playerStatistics.getBestTime() > 0) yield String.valueOf(playerStatistics.getBestTime());
                else yield "&cНет рекорда";
            }
            case "wins" -> String.valueOf(playerStatistics.getWins());
            case "losses" -> String.valueOf(playerStatistics.getLosses());

            default -> null;
        };
    }
}