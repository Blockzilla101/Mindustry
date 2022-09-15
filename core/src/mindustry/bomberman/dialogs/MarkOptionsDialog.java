package mindustry.bomberman.dialogs;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.bomberman.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

public class MarkOptionsDialog extends BaseDialog{
    public ChunkMarkType type = ChunkMarkType.unbreakable;
    Table table = new Table();

    public MarkOptionsDialog(){
        super("Chunk Mark Options");
        closeOnBack();

        shown(() -> {
            cont.clear();
            table.clear();

            title("Mark Type");
            table.table(Tex.button, t -> {
                t.margin(10f);
                var group = new ButtonGroup<>();
                var style = Styles.flatTogglet;

                t.defaults().size(140f, 50f);
                t.button("unbreakable", style, () -> type = ChunkMarkType.unbreakable).group(group).checked(b -> type == ChunkMarkType.unbreakable).tooltip("chunks that cannot be broken");
                t.button("end region border", style, () -> type = ChunkMarkType.endRegionWall).group(group).checked(b -> type == ChunkMarkType.endRegionWall).tooltip("border Chunks for ending area");
                t.row();

                t.button("midgame clear", style, () -> type = ChunkMarkType.midGameClear).group(group).checked(b -> type == ChunkMarkType.midGameClear).tooltip("chunks that are cleared when mid game is reached");
                t.button("midgame breakable", style, () -> type = ChunkMarkType.midGameBreakable).group(group).checked(b -> type == ChunkMarkType.midGameBreakable).tooltip("chunks that are converted to breakable when mid game is reached");
                t.row();

                t.button("playable", style, () -> type = ChunkMarkType.playableRegionStarter).group(group).checked(b -> type == ChunkMarkType.playableRegionStarter).tooltip("recalculate playable region from chunk");
                t.button("end region", style, () -> type = ChunkMarkType.endRegionStarter).group(group).checked(b -> type == ChunkMarkType.endRegionStarter).tooltip("recalculate end game region from chunk");
            }).fill(false).expand(false, false);
            table.row();

            cont.row();
            cont.add(table).center();
        });

        buttons.defaults().size(200f, 50f);
        buttons.button("@cancel", this::hide);
        buttons.button("@ok", this::hide);
    }

    void title(String text){
        table.add(text).color(Pal.accent).padTop(20).padRight(100f).padBottom(-3);
        table.row();
        table.image().color(Pal.accent).height(3f).padRight(100f).padBottom(20);
        table.row();
    }

    @Nullable
    public IntSeq getCurrentSelectedMarker() {
        return switch(type) {
            case unbreakable -> Vars.rules.unbreakable;
            case midGameClear -> Vars.rules.midGameClearChunks;
            case midGameBreakable -> Vars.rules.midGameBreakableChunks;
            case endRegionWall -> Vars.rules.endGameRegionWalls;
            default -> null;
        };
    }

    public enum ChunkMarkType{
        unbreakable, midGameClear, midGameBreakable, endRegionWall, playableRegionStarter, endRegionStarter
    }
}
