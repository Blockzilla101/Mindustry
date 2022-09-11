package mindustry.bomberman;

import arc.scene.ui.layout.*;
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

            table.label(() -> "X Offset");
            table.slider(0, 3, 1, 0, val -> Vars.rules.xOffset = (int)val);

            table.row();

            table.label(() -> "Y Offset");
            table.slider(0, 3, 1, 0, val -> Vars.rules.yOffset = (int)val);

            table.row();

            table.check("Invert", invert, v -> invert = v);

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
        buttons.button("@ok", this::hide);
    }
}
