package greenlink.schem;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import greenlink.TntRun;
import greenlink.arena.Arena;
import greenlink.arena.Floor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class SchemaUtils {

    public static Clipboard pasteSchema(Location location, String schemaName, Arena arena) {
        File schema = getFileFromFolder(schemaName,  "Arenas/" + arena.getArenaName());
        ClipboardFormat format = ClipboardFormats.findByFile(schema);

        if (format != null) {
            try (ClipboardReader reader = format.getReader(new FileInputStream(schema))) {
                Clipboard clipboard = reader.read();
                World world = BukkitAdapter.adapt(location.getWorld());

                try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).maxBlocks(-1).build()) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                    return clipboard;
                }
            } catch (IOException | WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private static EditSession createEditSession(World world) {
        return WorldEdit.getInstance()
                .newEditSessionBuilder()
                .world(world)
                .maxBlocks(-1)
                .build();
    }

    public static File getFileFromFolder(String fileName, String folderName) {
        File folder = new File(TntRun.getInstance().getDataFolder(), folderName);

        if (folder.exists() && folder.isDirectory()) {

            File file = new File(folder, fileName);

            if (file.exists() && file.isFile()) {
                return file;
            } else {
                Bukkit.getLogger().log(Level.INFO, String.valueOf("Файл не найден: " + file.getPath()));
                return null;
            }
        } else {
            Bukkit.getLogger().log(Level.INFO, String.valueOf("Папка не найдена: " + folder.getPath()));
            return null;
        }
    }

    public static void saveClipboard(Arena arena, Floor floor) {
        BlockVector3 floorCenter = BukkitAdapter.asBlockVector(floor.getCenter());
        int radius = arena.isMini() ? 13 : 25;

        BlockVector3 pos1 = floorCenter.subtract(radius, 0, radius);
        BlockVector3 pos2 = floorCenter.add(radius, 0, radius);
        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(arena.getWorld()), pos1, pos2);

        Clipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(floorCenter);

        try (EditSession editSession = createEditSession(region.getWorld())) {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }

        File file = new File(TntRun.getInstance().getDataFolder(), "Arenas/" + arena.getArenaName() + "/floor_" + floor.getNumber() + ".schem");
        file.getParentFile().mkdirs();

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
