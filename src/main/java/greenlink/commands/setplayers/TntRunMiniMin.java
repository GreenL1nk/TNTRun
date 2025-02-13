package greenlink.commands.setplayers;

import greenlink.ConfigValue;
import greenlink.utils.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TntRunMiniMin extends AbstractCommand {
    @Override
    protected void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return;
        if (args.length == 0) return;
        ConfigValue.MIN_PLAYERS_MINI.setValue(Integer.parseInt(args[0]));
    }

    @Override
    protected void onConsoleCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

    }

    @Override
    protected List<String> onPlayerTab(@NotNull Player player, @NotNull String[] args) {
        if (args.length == 1) return List.of("number");
        return List.of();
    }

    @Override
    protected List<String> onConsoleTab(@NotNull CommandSender commandSender, @NotNull String[] args) {
        return List.of();
    }
}
