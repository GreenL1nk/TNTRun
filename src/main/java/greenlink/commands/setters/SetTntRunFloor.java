package greenlink.commands.setters;

import greenlink.arena.Arena;
import greenlink.arena.ArenaManager;
import greenlink.arena.Floor;
import greenlink.utils.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetTntRunFloor extends AbstractCommand {
    @Override
    protected void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return;
        if (args.length == 0) return;
        if (args.length < 2) return;

        Arena arenaByName = ArenaManager.getInstance().getArenaByName(args[0]);
        if (arenaByName == null) return;
        if (arenaByName.tryAddFloor(new Floor(Integer.parseInt(args[1]), player.getLocation().add(0, -1, 0)))) {
            player.sendMessage("Этаж " + args[1] + " успешно добавлен");
        }
        else {
            player.sendMessage("Этаж " + args[1] + " уже существует");
        }
    }

    @Override
    protected void onConsoleCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

    }

    @Override
    protected List<String> onPlayerTab(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return List.of();
        if (args.length == 1) return ArenaManager.getInstance().getArenas().values().stream().map(Arena::getArenaName).toList();
        if (args.length == 2) return List.of("floorNumber");
        return List.of();
    }

    @Override
    protected List<String> onConsoleTab(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return List.of();
    }
}
