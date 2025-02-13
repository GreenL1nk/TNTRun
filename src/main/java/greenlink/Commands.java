package greenlink;

import greenlink.commands.TntRunCommand;
import greenlink.commands.delete.DelArena;
import greenlink.commands.delete.DelFloor;
import greenlink.commands.delete.DelTntRunTel;
import greenlink.commands.list.TntRunArenaList;
import greenlink.commands.list.TntRunFloorList;
import greenlink.commands.list.TntRunTelList;
import greenlink.commands.setplayers.TntRunMax;
import greenlink.commands.setplayers.TntRunMin;
import greenlink.commands.setplayers.TntRunMiniMax;
import greenlink.commands.setplayers.TntRunMiniMin;
import greenlink.commands.setters.SetTntRunArena;
import greenlink.commands.setters.SetTntRunArenaMini;
import greenlink.commands.setters.SetTntRunFloor;
import greenlink.commands.setters.SetTntRunTel;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands {

    public static void init(JavaPlugin plugin) {
        new SetTntRunArena().register(plugin, "settntrunarena");
        new SetTntRunArenaMini().register(plugin, "settntrunarenamini");
        new SetTntRunFloor().register(plugin, "settntrunfloor");
        new SetTntRunTel().register(plugin, "settntruntel");

        new DelArena().register(plugin, "delarena");
        new DelFloor().register(plugin, "delfloor");
        new DelTntRunTel().register(plugin, "deltntruntel");

        new TntRunFloorList().register(plugin, "tntrunfloorlist");
        new TntRunArenaList().register(plugin, "tntrunarenalist");
        new TntRunTelList().register(plugin, "tntruntellist");

        new TntRunMax().register(plugin, "tntrunmax");
        new TntRunMiniMax().register(plugin, "tntrunminimax");
        new TntRunMin().register(plugin, "tntrunmin");
        new TntRunMiniMin().register(plugin, "tntrunminimin");

        new TntRunCommand().register(plugin, "tntrun");
    }

}
