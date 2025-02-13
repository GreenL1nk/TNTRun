package greenlink.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractCommand implements TabExecutor {
    public void register(JavaPlugin plugin, String name) {
        PluginCommand command = plugin.getCommand(name);
        if (command != null) {
            command.setTabCompleter(this);
            command.setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] strings) {
        if (commandSender instanceof Player) onPlayerCommand(((Player) commandSender), strings);
        else onConsoleCommand(commandSender, strings);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] strings) {
        List<String> args = null;
        if (commandSender instanceof Player) args = onPlayerTab(((Player) commandSender), strings);
        else args = onConsoleTab(commandSender, strings);
        return args;
    }

    protected abstract void onPlayerCommand(@NotNull Player player, @NotNull String[] args);

    protected abstract void onConsoleCommand(@NotNull CommandSender commandSender, @NotNull String[] args);

    protected abstract List<String> onPlayerTab(@NotNull Player player, @NotNull String[] args);

    protected abstract List<String> onConsoleTab(@NotNull CommandSender commandSender, @NotNull String[] args);
}