package mindustry.bomberman;

import mindustry.bomberman.dialogs.*;

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
}
