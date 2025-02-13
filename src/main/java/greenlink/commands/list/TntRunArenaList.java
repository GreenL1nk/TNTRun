package greenlink.commands.list;

import greenlink.arena.Arena;
import greenlink.arena.ArenaManager;
import greenlink.utils.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TntRunArenaList extends AbstractCommand {
    @Override
    protected void onPlayerCommand(@NotNull Player player, @NotNull String[] args) {
        if (!player.isOp()) return;
        for (Arena arena : ArenaManager.getInstance().getArenas().values()) {
            TextComponent formattedMessage = Component.text()
                    .append(Component.text("Мир: ", NamedTextColor.GRAY))
                    .append(Component.text(arena.getWorld().getName(), NamedTextColor.GREEN))
                    .append(Component.text(" | ", NamedTextColor.GRAY))
                    .append(Component.text("Название: ", NamedTextColor.GRAY))
                    .append(Component.text(arena.getArenaName(), NamedTextColor.GOLD))
                    .build();

            player.sendMessage(formattedMessage);
        }
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
