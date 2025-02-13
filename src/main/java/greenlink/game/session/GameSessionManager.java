package greenlink.game.session;

import greenlink.ConfigValue;
import greenlink.arena.Arena;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GameSessionManager {
    private final Map<String, GameSession> sessions = new HashMap<>();
    private static GameSessionManager instance;

    private GameSessionManager() {}

    public static GameSessionManager getInstance() {
        if (instance == null) {
            instance = new GameSessionManager();
        }
        return instance;
    }

    public GameSession createSession(Arena arena) {
        String worldName = arena.getWorld().getName();
        if (existsSession(worldName)) {
            return sessions.get(worldName);
        }
        Integer minPlayers = (Integer) (arena.isMini() ? ConfigValue.MIN_PLAYERS_MINI.getValue() : ConfigValue.MIN_PLAYERS.getValue());
        Integer maxPlayers = (Integer) (arena.isMini() ? ConfigValue.MAX_PLAYERS_MINI.getValue() : ConfigValue.MAX_PLAYERS.getValue());
        GameSession newSession = new GameSession(worldName, minPlayers, maxPlayers, arena);
        sessions.put(worldName, newSession);
        return newSession;
    }

    @Nullable
    public GameSession getSession(String worldName) {
        return sessions.get(worldName);
    }

    public void removeSession(String worldName) {
        if (existsSession(worldName)) {
            GameSession session = sessions.remove(worldName);
            if (session != null) {
                session.cleanup();
            }
        }
    }

    public boolean existsSession(String worldName) {
        return sessions.containsKey(worldName);
    }
}
