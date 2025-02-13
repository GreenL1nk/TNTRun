package greenlink.game;

import greenlink.TntRun;
import greenlink.game.players.RunPlayer;
import greenlink.game.session.GameSession;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameTimerManager {

    private final Map<Player, Double> playerTimes = new ConcurrentHashMap<>();
    private @Getter double gameTimer = 0;
    GameSession gameSession;
    private @Getter int gameTimeId;

    public GameTimerManager(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void stopTimer(Player player) {
        playerTimes.put(player, gameTimer);
    }

    public void removePlayer(Player player) {
        playerTimes.remove(player);
        player.sendActionBar(Component.empty());
    }

    public void startGlobalTimer() {
        gameTimeId = new BukkitRunnable() {
            @Override
            public void run() {

                gameTimer += 0.1;
                for (RunPlayer runPlayer : gameSession.getRunPlayers()) {
                    runPlayer.getPlayer().sendActionBar(LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(String.format("&6%.1f секунд", gameTimer)));
                }
                for (Map.Entry<Player, Double> playerDoubleEntry : playerTimes.entrySet()) {
                    playerDoubleEntry.getKey().sendActionBar(LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(String.format("&6%.1f секунд", playerDoubleEntry.getValue())));
                }
            }
        }.runTaskTimer(TntRun.getInstance(), 2, 2).getTaskId();
    }
}