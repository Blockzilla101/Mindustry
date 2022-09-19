package mindustry.bomberman;

import arc.struct.*;
import mindustry.bomberman.MapRules.*;
import mindustry.bomberman.dialogs.*;
import mindustry.game.*;

import static mindustry.Vars.*;

public class Vars{
    public static MapRules rules;
    public static BombermanGridImage gridImage = new BombermanGridImage(0, 0);
    public static RegionGridImage regionImage = new RegionGridImage(0, 0);

    public static BombermanSettingsDialog bombermanSettingsDialog = new BombermanSettingsDialog();
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

            if (!rules.spawnsByTeam.containsKey(item.value)) rules.spawnsByTeam.put(item.value, new Seq<>());
            rules.spawnsByTeam.get(item.value).add(item.key);
        });
        rules.spawns = spawns;
        rules.unbreakable = cleanupMarkedChunks(rules.unbreakable);
        rules.playableRegion = cleanupMarkedChunks(rules.playableRegion);
        rules.endGameRegion = cleanupMarkedChunks(rules.endGameRegion);
        rules.endGameRegionWalls = cleanupMarkedChunks(rules.endGameRegionWalls);
        rules.midGameClearChunks = cleanupMarkedChunks(rules.midGameClearChunks);
        rules.midGameBreakableChunks = cleanupMarkedChunks(rules.midGameBreakableChunks);
        rules.safeChunks = cleanupMarkedChunks(rules.safeChunks);
        if (rules.startingStage == PlayingStage.mid || rules.startingStage == PlayingStage.end) rules.startStageLength = 0;
        if (rules.startingStage == PlayingStage.end) rules.midStageLength = 0;
    }

    private static IntSeq cleanupMarkedChunks(IntSeq map){
        var temp = new IntSeq();
        map.each(chunk -> {
            var x = Grid.unpackX(chunk);
            var y = Grid.unpackY(chunk);
            if (x > editor.width() || x < 0) return;
            if (y > editor.height() || y < 0) return;
            temp.addUnique(chunk);
        });
        return temp;
    }
}
