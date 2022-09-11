package mindustry.bomberman;

import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.bomberman.MapRules.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.ui;

public class MapRulesDialog extends BaseDialog{
    int startStageLength, midStageLength, endStageLength;
    PlayingStage startingStage;

    Table table = new Table();

    public MapRulesDialog(){
        super("Bomberman Map Rules");

        closeOnBack();
        shown(() -> {
            cont.clear();
            table.clear();

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
                t.button("mid", style, () -> startingStage = PlayingStage.mid).group(group).checked(b -> startingStage == PlayingStage.mid);;
                t.button("end", style, () -> startingStage = PlayingStage.end).group(group).checked(b -> startingStage == PlayingStage.end);;
            }).fill(false).expand(false, false);
            table.row();

            title("Stage Lengths");

            table.label(() -> "Start Stage: [lightgray](sec)");
            table.field(String.valueOf(startStageLength), TextFieldFilter.digitsOnly, s -> startStageLength = Strings.parseInt(s));
            table.row();

            table.label(() -> "Mid Stage: [lightgray](sec)");
            table.field(String.valueOf(midStageLength), TextFieldFilter.digitsOnly, s -> midStageLength = Strings.parseInt(s));
            table.row();

            table.label(() -> "End Stage: [lightgray](sec)");
            table.field(String.valueOf(endStageLength), TextFieldFilter.digitsOnly, s -> endStageLength = Strings.parseInt(s));

            cont.row();
            cont.add(table).center();
        });

        buttons.defaults().size(200f, 50f);
        buttons.button("@cancel", this::hide);
        buttons.button("@ok", () -> {
            Vars.rules.startStageLength = startStageLength * 1000;
            Vars.rules.midStageLength = midStageLength * 1000;
            Vars.rules.endStageLength = endStageLength * 1000;
            Vars.rules.startingStage = startingStage;
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
