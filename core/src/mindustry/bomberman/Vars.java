package mindustry.bomberman;

import arc.*;
import arc.struct.*;
import mindustry.bomberman.MapRules.*;
import mindustry.bomberman.dialogs.*;
import mindustry.game.*;

import static mindustry.Vars.*;

public class Vars{
    public static MapRules rules;
    public static BombermanGridImage gridImage = new BombermanGridImage(0, 0);
    public static RegionGridImage regionImage = new RegionGridImage(0, 0);

    public static RegionRenderSettingsDialog regionRenderSettingsDialog = new RegionRenderSettingsDialog();
    public static MapRulesDialog rulesDialog = new MapRulesDialog();
    public static MarkOptionsDialog markOptions = new MarkOptionsDialog();
    public static TeamSelectorDialog teamSelector = new TeamSelectorDialog();

    public static void reset() {
        rules = new MapRules();
        Grid.init(3);
        Grid.reset();
        gridImage.setImageSize(0, 0);
        regionImage.setImageSize(0, 0);
        EditorState.renderRegions = false;
        EditorState.gridEnabled = false;

        if (Core.settings.getInt("bomberman.version", 0) == 0) {
            MarkedChunkSeq.all.each((k, v) -> v.save());
            Core.settings.put("bomberman.version", 1);
        }

        MarkedChunkSeq.all.each((k, v) -> v.load());
    }

    public static void cleanupRules() {
        var spawns = new ObjectMap<Integer, Team>();
        rules.spawnsByTeam.clear();
        rules.spawns.forEach(item -> {
            var x = Grid.unpackX(item.key);
            var y = Grid.unpackY(item.key);
            if (x > editor.width() || x < 0) return;
            if (y > editor.height() || y < 0) return;
            spawns.put(item.key, item.value);

            if (!rules.spawnsByTeam.containsKey(item.value.id)) rules.spawnsByTeam.put(item.value.id, new Seq<>());
            rules.spawnsByTeam.get(item.value.id).add(item.key);
        });
        rules.spawns = spawns;
        MarkedChunkSeq.all.each((m, v) -> cleanupMarkedChunks(v));
        if (rules.startingStage == PlayingStage.mid || rules.startingStage == PlayingStage.end) rules.startStageLength = 0;
        if (rules.startingStage == PlayingStage.end) rules.midStageLength = 0;
    }

    private static void cleanupMarkedChunks(MarkedChunkSeq map){
        var temp = new IntSeq();

        map.each(chunk -> {
            var x = Grid.unpackX(chunk);
            var y = Grid.unpackY(chunk);
            if (x > editor.width() || x < 0) return;
            if (y > editor.height() || y < 0) return;
            temp.addUnique(chunk);
        });

        map.clear();
        map.addAll(temp);
    }
}
