package mindustry.bomberman;

public class Vars{
    public static MapRules rules;
    public static BombermanGridImage gridImage = new BombermanGridImage(0, 0);
    public static RegionGridImage regionImage = new RegionGridImage(0, 0);

    public static GridSettingsDialog gridSettingsDialog = new GridSettingsDialog();
    public static MapRulesDialog rulesDialog = new MapRulesDialog();

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
