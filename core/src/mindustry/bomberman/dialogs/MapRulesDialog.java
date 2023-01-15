package mindustry.bomberman.dialogs;

import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.bomberman.*;
import mindustry.bomberman.MapRules.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.ui;

public class MapRulesDialog extends BaseDialog{
    public boolean invert = false;
    int startStageLength, midStageLength, endStageLength;
    PlayingStage startingStage;

    private int prevXOffset;
    private int prevYOffset;
    private boolean prevInvert;

    private Table table = new Table();

    public MapRulesDialog(){
        super("Bomberman Map Rules");

        closeOnBack();
        shown(() -> {
            cont.clear();
            table.clear();

            prevXOffset = Vars.rules.xOffset;
            prevYOffset = Vars.rules.yOffset;
            prevInvert = invert;

            startStageLength = Vars.rules.startStageLength / 1000;
            midStageLength = Vars.rules.midStageLength / 1000;
            endStageLength = Vars.rules.endStageLength / 1000;
            startingStage = Vars.rules.startingStage;

            table.margin(10f);
            table.center();

            title("Starting Stage");
            table.table(Tex.button, t -> {
                t.margin(10f);
                var group = new ButtonGroup<>();
                var style = Styles.flatTogglet;

                t.defaults().size(140f, 50f);
                t.button("start", style, () -> startingStage = PlayingStage.start).group(group).checked(b -> startingStage == PlayingStage.start);
                t.button("mid", style, () -> startingStage = PlayingStage.mid).group(group).checked(b -> startingStage == PlayingStage.mid);
                t.button("end", style, () -> startingStage = PlayingStage.end).group(group).checked(b -> startingStage == PlayingStage.end);
            }).fill(false).expand(false, false);
            table.row();

            title("Stage Lengths");

            table.table(t -> {
                t.label(() -> "Start Stage: [lightgray](sec)").expand(false, false);
                t.field(String.valueOf(startStageLength), TextFieldFilter.digitsOnly, s -> startStageLength = Strings.parseInt(s)).expand(false, false);
            });
            table.row();

            table.table(t -> {
                t.label(() -> "Mid Stage: [lightgray](sec)");
                t.field(String.valueOf(midStageLength), TextFieldFilter.digitsOnly, s -> midStageLength = Strings.parseInt(s)).expand(false, false);
            });
            table.row();

            table.table(t -> {
                t.label(() -> "End Stage: [lightgray](sec)").expand(false, false);
                t.field(String.valueOf(endStageLength), TextFieldFilter.digitsOnly, s -> endStageLength = Strings.parseInt(s)).expand(false, false);
            });
            table.row();

            title("Grid Settings");
            table.table(t -> {
                t.label(() -> "X Offset");
                t.slider(0, 2, 1, Vars.rules.xOffset, val -> Vars.rules.xOffset = (int)val);
            });
            table.row();

            table.table(t -> {
                t.label(() -> "Y Offset");
                t.slider(0, 2, 1, Vars.rules.yOffset, val -> Vars.rules.yOffset = (int)val);
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
            cont.add(table).center();
        });

        buttons.defaults().size(200f, 50f);
        buttons.button("@cancel", () -> {
            Vars.rules.xOffset = prevXOffset;
            Vars.rules.yOffset = prevYOffset;
            invert = prevInvert;
            hide();
        });
        buttons.button("@ok", () -> {
            Vars.rules.startStageLength = startStageLength * 1000;
            Vars.rules.midStageLength = midStageLength * 1000;
            Vars.rules.endStageLength = endStageLength * 1000;
            Vars.rules.startingStage = startingStage;

            if (Vars.rules.xOffset != prevXOffset || Vars.rules.yOffset != prevYOffset) {
                Grid.recalculateChunks();
                Grid.moveAllMarkedChunks(Vars.rules.xOffset - prevXOffset, Vars.rules.yOffset - prevYOffset);
                Vars.cleanupRules();
            }

            hide();
        });
    }

    void title(String text) {
        table.add(text).color(Pal.accent).padTop(20).padRight(100f).padBottom(-3);
        table.row();
        table.image().color(Pal.accent).height(3f).padRight(100f).padBottom(20);
        table.row();
    }
}
