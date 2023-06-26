package mindustry.bomberman;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.struct.*;
import arc.util.*;

public enum ChunkSettings {
    unbreakable("unbreakable", Color.cyan, () -> Vars.rules.unbreakable),
    playableRegion("playable-region", Color.magenta, () -> Vars.rules.playableRegion),
    endRegion("end-region", Color.red, () -> Vars.rules.endGameRegion),
    endRegionWall("end-region-wall", Color.yellow, () -> Vars.rules.endGameRegionWalls),
    midGameClear("mid-game-clear", Color.teal, () -> Vars.rules.midGameClearChunks),
    midGameBreakable("mid-game-breakable", Color.navy, () -> Vars.rules.midGameBreakableChunks),
    safe("safe", Color.green, () -> Vars.rules.safeChunks);

    public final String name;
    public float opacity = 0.25f;
    public Color color;
    public boolean render = true;
    public final Prov<IntSeq> prov;

    public static final ChunkSettings[] all = values();

    ChunkSettings(String name, Color color, Prov<IntSeq> prov) {
        this.name = name;
        this.color = color;
        this.prov = prov;
    }

    public String settingsName() {
        return Strings.format("bomberman.chunk.@", this.name);
    }

    public static void load() {
        for (var item : all) {
            item.color = Color.valueOf(Core.settings.getString(Strings.format("@.color", item.settingsName()), item.color.toString()));
            item.opacity = Core.settings.getFloat(Strings.format("@.opacity", item.settingsName()), item.opacity);
            item.render = Core.settings.getBool(Strings.format("@.render", item.settingsName()), item.render);
        }
    }

    public static void save() {
        for (var item : all) {
            Core.settings.put(Strings.format("@.color", item.settingsName()), item.color.toString());
            Core.settings.put(Strings.format("@.opacity", item.settingsName()), item.opacity);
            Core.settings.put(Strings.format("@.render", item.settingsName()), item.render);
        }
    }
}
