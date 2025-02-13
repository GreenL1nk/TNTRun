package greenlink.listeners;

import greenlink.arena.Arena;
import greenlink.arena.ArenaManager;
import greenlink.game.session.GameSession;
import greenlink.game.session.GameSessionManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WorldJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        removePlayerFromSessions(event.getPlayer());
        Arena arena = ArenaManager.getInstance().getArenaByWorld(event.getPlayer().getWorld());
        if (arena == null) return;
        GameSession session = GameSessionManager.getInstance().getSession(arena.getWorld().getName());
        if (session == null) {
            session = GameSessionManager.getInstance().createSession(arena);
        }
        session.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        removePlayerFromSessions(event.getPlayer());
        Arena arena = ArenaManager.getInstance().getArenaByWorld(event.getPlayer().getWorld());
        if (arena == null) return;
        GameSession session = GameSessionManager.getInstance().getSession(arena.getWorld().getName());
        if (session == null) {
            session = GameSessionManager.getInstance().createSession(arena);
        }
        if (session.getArena().getWorld().getPlayers().size() >= session.getMaxPlayers()) {
            event.getPlayer().teleport(event.getFrom().getSpawnLocation());
            return;
        }
        event.getPlayer().clearTitle();
        session.addPlayer(event.getPlayer());
        event.getPlayer().getWorld().getPlayers().forEach(p -> p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                String.format("&a%s &eприсоединился к игре!", event.getPlayer().getName()))));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayerFromSessions(event.getPlayer());
    }

    public void removePlayerFromSessions(Player player) {
        GameSessionManager.getInstance().getSessions().forEach((s, gameSession) -> gameSession.removePlayer(player));
    }
}
