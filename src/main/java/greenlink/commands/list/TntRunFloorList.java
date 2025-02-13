package greenlink.commands.list;

import greenlink.arena.Arena;
import greenlink.arena.ArenaManager;
import greenlink.arena.Floor;
import greenlink.utils.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TntRunFloorList extends AbstractCommand {
    @Override
    protected void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return;
        if (args.length == 0) return;
        Arena arenaByName = ArenaManager.getInstance().getArenaByName(args[0]);
        if (arenaByName == null) return;
        for (Floor floor : arenaByName.getFloors()) {
            int number = floor.getNumber();
            Location center = floor.getCenter();

            TextComponent formattedMessage = Component.text()
                    .append(Component.text(number + " этаж", NamedTextColor.GOLD))
                    .append(Component.text(", Координаты Точки ", NamedTextColor.WHITE))
                    .append(Component.text("(x: " + center.getBlockX() +
                            ", y: " + center.getBlockY() +
                            ", z: " + center.getBlockZ() + ")", NamedTextColor.AQUA))
                    .clickEvent(ClickEvent.runCommand("/teleport " + center.getX() + " " + center.getY() + " " + center.getZ()))
                    .build();

            player.sendMessage(formattedMessage);
        }
        player.sendMessage(Component.text("(клик для телепортации)", NamedTextColor.GRAY));
    }

    @Override
    protected void onConsoleCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

    }

    @Override
    protected List<String> onPlayerTab(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return List.of();
        if (args.length == 1) return ArenaManager.getInstance().getArenas().values().stream().map(Arena::getArenaName).toList();
        return List.of();
    }

    @Override
    protected List<String> onConsoleTab(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return List.of();
    }
}
