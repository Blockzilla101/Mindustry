package mindustry.bomberman;

import arc.*;
import arc.graphics.*;
import arc.struct.*;
import arc.util.*;
import arc.util.serialization.*;
import arc.util.serialization.Json.*;

public class MarkedChunkSeq extends IntSeq implements JsonSerializable {
    public transient boolean render = true;
    public transient float renderOpacity = 0.25f;
    public transient Color renderColor;
    public String name;

    public static ObjectMap<String, MarkedChunkSeq> all = new ObjectMap<>();

    MarkedChunkSeq(){}

    MarkedChunkSeq(String name, Color color) {
        super();
        this.name = name;
        this.renderColor = color;
        MarkedChunkSeq.all.put(name, this);
    }

    public void load() {
        this.renderColor = Color.valueOf(Core.settings.getString(Strings.format("@.color", this.settingsName()), this.renderColor.toString()));
        this.renderOpacity = Core.settings.getFloat(Strings.format("@.opacity", this.settingsName()), this.renderOpacity);
        this.render = Core.settings.getBool(Strings.format("@.render", this.settingsName()), this.render);
    }

    public void save() {
        Core.settings.put(Strings.format("@.color", this.settingsName()), this.renderColor.toString());
        Core.settings.put(Strings.format("@.opacity", this.settingsName()), this.renderOpacity);
        Core.settings.put(Strings.format("@.render", this.settingsName()), this.render);
    }

    private String settingsName() {
        return Strings.format("bomberman.marked-chunk.@", this.name);
    }

    @Override
    public void write(Json json){
        json.writeFields(this);
    }

    @Override
    public void read(Json json, JsonValue jsonData){
        json.readFields(this, jsonData);
        this.renderColor = MarkedChunkSeq.all.get(this.name).renderColor;
        MarkedChunkSeq.all.put(this.name, this);
    }

    public static void updateAllMap() {
        MarkedChunkSeq.all.put("unbreakable", Vars.rules.unbreakable);
        MarkedChunkSeq.all.put("playable-region", Vars.rules.playableRegion);
        MarkedChunkSeq.all.put("end-region", Vars.rules.endGameRegion);
        MarkedChunkSeq.all.put("end-region-wall", Vars.rules.endGameRegionWalls);
        MarkedChunkSeq.all.put("midgame-clear", Vars.rules.midGameClearChunks);
        MarkedChunkSeq.all.put("midgame-breakable", Vars.rules.midGameBreakableChunks);
        MarkedChunkSeq.all.put("safe", Vars.rules.safeChunks);
    }
}
