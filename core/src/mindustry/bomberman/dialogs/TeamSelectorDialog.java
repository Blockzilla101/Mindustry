package mindustry.bomberman.dialogs;

import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.bomberman.*;
import mindustry.bomberman.MapRules.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

public class TeamSelectorDialog extends BaseDialog{
    public Team currentTeam = Team.sharded;

    Table buttonTable = new Table();
    int page = 0;

    public TeamSelectorDialog(){
        super("Select Team");

        closeOnBack();
        shown(() -> {
            cont.clear();

            var table = new Table();

            table.margin(10f);
            table.center();

            table.table(Tex.button, t -> buttonTable = t).fill(false).expand(false, false);
            rebuildTeams();
            table.row();

            table.table(Tex.clear, t -> {
                t.margin(10f);
                var group = new ButtonGroup<>();
                for(int i = 0; i < 4; i++){
                    var finalI = i;
                    t.button(String.valueOf(i + 1), Styles.squareTogglet, () -> {
                        page = finalI;
                        rebuildTeams();
                    }).group(group).checked(b -> page == finalI).expand(false, false).size(45f);
                }
            });

            cont.row();
            cont.add(table).center();
        });
    }

    void rebuildTeams(){
        buttonTable.clear();
        buttonTable.margin(10f);
        var group = new ButtonGroup<>();
        for(int i = page * 64; i < 64 + (page * 64); i++){
            if (i % 8 == 0 && i != (page * 64)) buttonTable.row();
            var team = Team.get(i);
            buttonTable.button(Tex.whiteui, Styles.squareTogglei, 38f, () -> {
                currentTeam = team;
                hide();
            }).group(group).pad(1f).checked(b -> currentTeam == team).size(50f).tooltip(team.localized()).with(a -> a.getStyle().imageUpColor = team.color);
        }
        if (currentTeam.id >= page * 64 || currentTeam.id < page * 64) group.uncheckAll();
        buttonTable.defaults().size(140f, 50f);
    }
}
