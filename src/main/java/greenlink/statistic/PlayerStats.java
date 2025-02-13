package greenlink.statistic;

import lombok.Data;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class PlayerStats {

    private String uuid;
    private String name;
    private int wins;
    private int losses;
    private int gamesPlayed;
    private int cuts;
    private int blocksBroken;
    private double bestTime;

    private final PlayerStatsRepository repository = new PlayerStatsRepository();

    public PlayerStats(String uuid, String name, int wins, int losses, int gamesPlayed, int cuts, int blocksBroken, double bestTime) {
        this.uuid = uuid;
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.gamesPlayed = gamesPlayed;
        this.cuts = cuts;
        this.blocksBroken = blocksBroken;
        this.bestTime = bestTime;
    }

    public double getBestTime() {
        return Math.round(bestTime * 100.0) / 100.0;
    }

    public void incrementWins() {
        this.wins++;
        saveToMainStats();
        saveDailyStats();
    }

    public void incrementLosses() {
        this.losses++;
        saveToMainStats();
        saveDailyStats();
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
        saveToMainStats();
        saveDailyStats();
    }

    public void incrementCuts() {
        this.cuts++;
        saveToMainStats();
        saveDailyStats();
    }

    public void incrementBlocksBroken() {
        this.blocksBroken++;
        saveToMainStats();
        saveDailyStats();
    }

    public void updateBestTime(double newTime) {
        if (newTime > this.bestTime || this.bestTime == 0) {
            this.bestTime = newTime;
            saveToMainStats();
            saveDailyStats();
        }
    }

    private void saveToMainStats() {
        try {
            repository.updatePlayerStats(uuid, this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveDailyStats() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        try {
            repository.insertDailyStats(uuid, this, currentDate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
