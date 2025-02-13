package greenlink.statistic;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlayerStatsRepository {

    public void updatePlayerStats(String uuid, PlayerStats stats) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            String query = "UPDATE player_stats SET wins = ?, losses = ?, games_played = ?, cuts = ?, blocks_broken = ?, best_time = ? WHERE uuid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, stats.getWins());
                stmt.setInt(2, stats.getLosses());
                stmt.setInt(3, stats.getGamesPlayed());
                stmt.setInt(4, stats.getCuts());
                stmt.setInt(5, stats.getBlocksBroken());
                stmt.setDouble(6, stats.getBestTime());
                stmt.setString(7, uuid);
                stmt.executeUpdate();
            }
        }
    }

    public void insertDailyStats(String uuid, PlayerStats stats, String date) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            String query = """
                INSERT INTO player_stats_daily (uuid, date, wins, losses, games_played, cuts, blocks_broken, best_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (uuid, date) DO UPDATE SET
                    wins = excluded.wins,
                    losses = excluded.losses,
                    games_played = excluded.games_played,
                    cuts = excluded.cuts,
                    blocks_broken = excluded.blocks_broken,
                    best_time = CASE 
                        WHEN excluded.best_time < player_stats_daily.best_time OR player_stats_daily.best_time = 0 
                        THEN excluded.best_time 
                        ELSE player_stats_daily.best_time 
                    END;
                """;

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, uuid);
                stmt.setString(2, date);
                stmt.setInt(3, stats.getWins());
                stmt.setInt(4, stats.getLosses());
                stmt.setInt(5, stats.getGamesPlayed());
                stmt.setInt(6, stats.getCuts());
                stmt.setInt(7, stats.getBlocksBroken());
                stmt.setDouble(8, stats.getBestTime());
                stmt.executeUpdate();
            }
        }
    }

    public List<PlayerStats> getStatsForPeriod(Player player, String period) throws SQLException {
        List<PlayerStats> statsList = new ArrayList<>();
        String startDate = getStartDate(period);
        if (startDate.equals("all_time")) {
            return List.of(getStatsForAllTime(player));
        }

        try (Connection connection = Database.getConnection()) {
            String query = "SELECT wins, losses, games_played, cuts, blocks_broken, best_time FROM player_stats_daily WHERE uuid = ? AND date >= ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setString(2, startDate);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int wins = rs.getInt("wins");
                        int losses = rs.getInt("losses");
                        int gamesPlayed = rs.getInt("games_played");
                        int cuts = rs.getInt("cuts");
                        int blocksBroken = rs.getInt("blocks_broken");
                        double bestTime = rs.getDouble("best_time");
                        statsList.add(new PlayerStats(player.getUniqueId().toString(), player.getName(), wins, losses, gamesPlayed, cuts, blocksBroken, bestTime));
                    }
                }
            }
        }

        return statsList;
    }

    public PlayerStats getStatsForAllTime(Player player) throws SQLException {
        int wins = 0, losses = 0, gamesPlayed = 0, cuts = 0, blocksBroken = 0;
        double bestTime = -1;

        String queryMain = "SELECT wins, losses, games_played, cuts, blocks_broken, best_time FROM player_stats WHERE uuid = ?";
        String uuid= player.getUniqueId().toString();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryMain)) {

            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                wins += resultSet.getInt("wins");
                losses += resultSet.getInt("losses");
                gamesPlayed += resultSet.getInt("games_played");
                cuts += resultSet.getInt("cuts");
                blocksBroken += resultSet.getInt("blocks_broken");
                bestTime = Math.max(bestTime, resultSet.getDouble("best_time"));
            }
        }

        if (bestTime == -1) {
            bestTime = 0;
        }

        return new PlayerStats(uuid, player.getName(), wins, losses, gamesPlayed, cuts, blocksBroken, bestTime);
    }

    private String getStartDate(String period) {
        LocalDate now = LocalDate.now();
        return switch (period) {
            case "day" -> now.minusDays(1).format(DateTimeFormatter.ISO_DATE);
            case "week" -> now.minusWeeks(1).format(DateTimeFormatter.ISO_DATE);
            case "month" -> now.minusMonths(1).format(DateTimeFormatter.ISO_DATE);
            case "all_time" -> "all_time";
            default -> throw new IllegalArgumentException("Invalid period");
        };
    }
}
