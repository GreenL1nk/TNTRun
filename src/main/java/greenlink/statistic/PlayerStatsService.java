package greenlink.statistic;

import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlayerStatsService {

    private final PlayerStatsRepository repository = new PlayerStatsRepository();

    public void updateMainStats(String uuid, PlayerStats stats) throws SQLException {
        repository.updatePlayerStats(uuid, stats);
        addDailyStats(uuid, stats);
    }

    private void addDailyStats(String uuid, PlayerStats stats) throws SQLException {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        repository.insertDailyStats(uuid, stats, currentDate);
    }

    public PlayerStats getPlayerStats(Player player, String period) {
        String uuid = player.getUniqueId().toString();
        PlayerStats cachedStats = PlayerCache.getPlayerStats(uuid);
        if (cachedStats != null && period.equals("all_time")) {
            return cachedStats;
        }

        List<PlayerStats> statsForPeriod = null;
        try {
            statsForPeriod = repository.getStatsForPeriod(player, period);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        PlayerStats aggregatedStats = aggregateStats(statsForPeriod);
        if (period.equals("all_time")) PlayerCache.updatePlayerStats(uuid, aggregatedStats);
        return aggregateStats(statsForPeriod);
    }

    private PlayerStats aggregateStats(List<PlayerStats> statsForPeriod) {
        int wins = 0, losses = 0, gamesPlayed = 0, cuts = 0, blocksBroken = 0;
        double bestTime = Double.MAX_VALUE;
        String uuid = "";
        String name = "";

        for (PlayerStats stats : statsForPeriod) {
            uuid = stats.getUuid();
            name = stats.getName();
            wins += stats.getWins();
            losses += stats.getLosses();
            gamesPlayed += stats.getGamesPlayed();
            cuts += stats.getCuts();
            blocksBroken += stats.getBlocksBroken();
            if (stats.getBestTime() < bestTime) bestTime = stats.getBestTime();
        }

        return new PlayerStats(uuid, name, wins, losses, gamesPlayed, cuts, blocksBroken, bestTime);
    }
}
