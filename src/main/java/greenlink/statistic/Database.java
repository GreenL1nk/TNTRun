package greenlink.statistic;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:plugins/TNTRun/player_statistics.db");
        config.setUsername("");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            String createPlayerStatsTable = """
                    CREATE TABLE IF NOT EXISTS player_stats (
                        uuid TEXT PRIMARY KEY,
                        player_name TEXT,
                        wins INTEGER DEFAULT 0,
                        losses INTEGER DEFAULT 0,
                        games_played INTEGER DEFAULT 0,
                        cuts INTEGER DEFAULT 0,
                        blocks_broken INTEGER DEFAULT 0,
                        best_time REAL
                    );
                    """;
            stmt.execute(createPlayerStatsTable);

            String createPlayerPeriodicStatsTable = """
                    CREATE TABLE IF NOT EXISTS player_stats_daily (
                        uuid TEXT,
                        date TEXT,
                        wins INTEGER DEFAULT 0,
                        losses INTEGER DEFAULT 0,
                        games_played INTEGER DEFAULT 0,
                        cuts INTEGER DEFAULT 0,
                        blocks_broken INTEGER DEFAULT 0,
                        best_time REAL,
                        PRIMARY KEY (uuid, date),
                        FOREIGN KEY (uuid) REFERENCES player_stats(uuid)
                    );
                    """;
            stmt.execute(createPlayerPeriodicStatsTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}