package greenlink;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum ConfigValue {

    DEBUG_MODE("debugMode", true),

    // Основные параметры арены
    MAX_PLAYERS("arena.maxPlayers", 24),
    MAX_PLAYERS_MINI("arena.maxPlayersMini", 12),
    MIN_PLAYERS("arena.minPlayers", 4),
    MIN_PLAYERS_MINI("arena.minPlayersMini", 3),

    // Команды победы и поражения
    WIN_COMMANDS("game.winCommands", List.of("say Победитель: %player%", "give %player% diamond 1")),
    LOSE_COMMANDS("game.loseCommands", List.of("say Поражение: %player%", "give %player% coal 1")),

    // Тайминги и вероятности
    BLOCK_DISAPPEAR_TIME("timing.blockDisappearTime", 0.3),
    NORMAL_BONUS_SPAWN_TIME("timing.normalBonusSpawnTime", 15),
    SPECIAL_BONUS_NEED_SPAWN("timing.specialBonusNeedSpawn", 25),
    SPECIAL_BONUS_NEED_SPAWN_MINI("timing.specialBonusNeedSpawnMini", 15),

    // Параметры частиц при падении игрока
    FALL_PARTICLE_TYPE("particles.fallType", "DRAGON_BREATH"),
    FALL_PARTICLE_COUNT("particles.fallCount", 15),

    // Звуковые команды
    SOUND_STATS_OPEN("sounds.statsOpen", "BLOCK_NOTE_BLOCK_BELL"),
    SOUND_STATS_CHANGE("sounds.statsChange", "BLOCK_ANVIL_BREAK"),
    SOUND_PLAYER_CUT("sounds.playerCut", "ENTITY_PLAYER_LEVELUP"),
    SOUND_BONUS_PICKUP("sounds.bonusPickup", "ENTITY_EXPERIENCE_ORB_PICKUP"),
    SOUND_SPECIAL_BONUS_PICKUP("sounds.specialBonusPickup", "ENTITY_PLAYER_LEVELUP"),
    SOUND_GAME_START_TICK("sounds.gameStartTick", "BLOCK_LEVER_CLICK"),
    SOUND_DOUBLE_JUMP("sounds.doubleJump", "ENTITY_BLAZE_SHOOT"),
    SOUND_SLOW_FALL_CUBE("sounds.slowFallCube", "BLOCK_BEACON_ACTIVATE");

    private final String path;
    @Getter
    private final Object defaultValue;

    ConfigValue(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public Object getValue() {
        FileConfiguration config = TntRun.getInstance().getConfig();
        if (!config.contains(path)) {
            config.set(path, defaultValue);
            TntRun.getInstance().saveConfig();
        }
        return config.get(path, defaultValue);
    }

    public int getInt() {
        return (Integer) getValue();
    }

    public long getLong() {
        return (Long) getValue();
    }

    public double getDouble() {
        return (Double) getValue();
    }

    public boolean getBoolean() {
        return (Boolean) getValue();
    }

    public String getString() {
        return (String) getValue();
    }

    public List<String> getStringList() {
        FileConfiguration config = TntRun.getInstance().getConfig();
        return config.getStringList(path);
    }

    public void setValue(Object value) {
        TntRun.getInstance().getConfig().set(path, value);
        TntRun.getInstance().saveConfig();
    }

    public static void getAllValues() {
        for (ConfigValue value : ConfigValue.values()) {
            value.getValue();
        }
    }
}