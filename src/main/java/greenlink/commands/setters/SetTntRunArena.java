package greenlink.commands.setters;

import greenlink.arena.ArenaManager;
import greenlink.utils.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetTntRunArena extends AbstractCommand {
    @Override
    protected void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return;
        if (args.length == 0) return;

        if (ArenaManager.getInstance().tryAddArena(player.getWorld(), args[0], false)) {
            player.sendMessage("Этот мир установлен как большая арена с именем " + args[0]);
        }
        else {
            player.sendMessage("У этого мира уже есть арена с именем " + args[0]);
        }
    }

    @Override
    protected void onConsoleCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

    }

    @Override
    protected List<String> onPlayerTab(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return List.of();
        if (args.length == 1) return List.of("arenaName");
        return List.of();
    }

    @Override
    protected List<String> onConsoleTab(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return List.of();
    }
}
