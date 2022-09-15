package mindustry.bomberman.dialogs;

import arc.scene.ui.layout.*;
import mindustry.bomberman.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

public class GridSettingsDialog extends BaseDialog{
    public boolean invert = false;
    private boolean prevInvert;
    private int prevXOffset, prevYOffset;

    public GridSettingsDialog(){
        super("Grid Settings");

        closeOnBack();
        shown(() -> {
            cont.clear();

            prevInvert = invert;
            prevXOffset = Vars.rules.xOffset;
            prevYOffset = Vars.rules.yOffset;

            Table table = new Table();

            table.table(t -> {
                t.label(() -> "X Offset");
                t.slider(0, 3, 1, Vars.rules.xOffset, val -> Vars.rules.xOffset = (int)val);
            });
            table.row();

            table.table(t -> {
                t.label(() -> "Y Offset");
                t.slider(0, 3, 1, Vars.rules.yOffset, val -> Vars.rules.yOffset = (int)val);
            });
            table.row();

            table.check("Invert", invert, v -> invert = v);
            table.row();

            table.button("Clear marked regions", () -> {
                Vars.rules.unbreakable.clear();
                Vars.rules.playableRegion.clear();
                Vars.rules.endGameRegion.clear();
                Vars.rules.endGameRegionWalls.clear();
                Vars.rules.midGameClearChunks.clear();
                Vars.rules.midGameBreakableChunks.clear();
                Vars.rules.safeChunks.clear();
            }).growX();

            table.row();
            table.button("Clear spawns", () -> {
                Vars.rules.spawns.clear();
            }).growX();

            cont.row();
            cont.add(table);
        });

        buttons.defaults().size(200f, 50f);
        buttons.button("@cancel", () -> {
            Vars.rules.xOffset = prevXOffset;
            Vars.rules.yOffset = prevYOffset;
            invert = prevInvert;
            hide();
        });

        buttons.button("@ok", () -> {
            Grid.recalculateChunks();
            Grid.moveAllMarkedChunks(Vars.rules.xOffset - prevXOffset, Vars.rules.yOffset - prevYOffset);
            hide();
        });
    }
}
