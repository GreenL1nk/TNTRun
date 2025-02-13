package greenlink.commands.delete;

import greenlink.arena.Arena;
import greenlink.arena.ArenaManager;
import greenlink.utils.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelArena extends AbstractCommand {
    @Override
    protected void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return;
        if (args.length == 0) return;
        Arena arenaByName = ArenaManager.getInstance().getArenaByName(args[0]);
        if (arenaByName == null) return;
        ArenaManager.getInstance().removeArena(arenaByName.getWorld());
        player.sendMessage("Арена " + arenaByName.getArenaName() + " успешно удалена!");
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
