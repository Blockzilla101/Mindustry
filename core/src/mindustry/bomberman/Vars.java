package mindustry.bomberman;

import arc.struct.*;
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

    public static void sanitizeRules() {
        var spawns = new ObjectMap<Integer, Team>();
        rules.spawns.forEach(item -> {
            var x = Grid.unpackX(item.key);
            var y = Grid.unpackY(item.key);
            if (x > editor.width() || x < 0) return;
            if (y > editor.height() || y < 0) return;
            spawns.put(item.key, item.value);
        });
        rules.spawns = spawns;
        rules.unbreakable = sanitizeMarks(rules.unbreakable);
        rules.playableRegion = sanitizeMarks(rules.playableRegion);
        rules.endGameRegion = sanitizeMarks(rules.endGameRegion);
        rules.endGameRegionWalls = sanitizeMarks(rules.endGameRegionWalls);
        rules.midGameClearChunks = sanitizeMarks(rules.midGameClearChunks);
        rules.midGameBreakableChunks = sanitizeMarks(rules.midGameBreakableChunks);
        rules.safeChunks = sanitizeMarks(rules.safeChunks);
    }

    private static IntSeq sanitizeMarks(IntSeq map){
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
