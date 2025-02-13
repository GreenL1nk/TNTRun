package greenlink.commands.list;

import greenlink.arena.Arena;
import greenlink.arena.ArenaManager;
import greenlink.utils.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TntRunTelList extends AbstractCommand {
    @Override
    protected void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return;
        for (Arena arena : ArenaManager.getInstance().getArenas().values()) {
            World world = arena.getWorld();
            String arenaName = arena.getArenaName();
            Component location = getComponent(arena);
            TextComponent formattedMessage = Component.text()
                    .append(Component.text("Мир: " + world.getName(), NamedTextColor.GOLD))
                    .append(Component.text(", Арена: " + arenaName, NamedTextColor.WHITE))
                    .append(Component.text(", Координаты Точки ", NamedTextColor.WHITE))
                    .append(location)
                    .build();

            player.sendMessage(formattedMessage);
        }
        player.sendMessage(Component.text("(клик для телепортации)", NamedTextColor.GRAY));
    }

    private static @NotNull Component getComponent(Arena arena) {
        Location teleportLocation = arena.getTeleportLocation();
        return teleportLocation == null ? Component.text("Не установлена", NamedTextColor.RED) :
                Component.text("(x: " + teleportLocation.getBlockX() +
                        ", y: " + teleportLocation.getBlockY() +
                        ", z: " + teleportLocation.getBlockZ() + ")", NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.runCommand("/teleport " + teleportLocation.getX() + " " + teleportLocation.getY() + " " + teleportLocation.getZ()));
    }

    @Override
    protected void onConsoleCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

    }

    @Override
    protected List<String> onPlayerTab(@NotNull Player player, @NotNull String[] args) {
        return List.of();
    }

    @Override
    protected List<String> onConsoleTab(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return List.of();
    }
}
