package mindustry.bomberman.dialogs;

import arc.*;
import arc.graphics.*;
import arc.input.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.bomberman.*;
import mindustry.editor.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.*;

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
                t.button("unbreakable", style, () -> {
                    type = ChunkMarkType.unbreakable;
                    hide();
                }).group(group).tooltip("chunks that cannot be broken").checked((b) -> type == ChunkMarkType.unbreakable);
                t.button("end region border", style, () -> {
                    type = ChunkMarkType.endRegionWall;
                    hide();
                }).group(group).tooltip("border Chunks for ending area").checked((b) -> type == ChunkMarkType.endRegionWall);
                t.row();

                t.button("midgame clear", style, () -> {
                    type = ChunkMarkType.midGameClear;
                    hide();
                }).group(group).tooltip("chunks that are cleared when mid game is reached").checked((b) -> type == ChunkMarkType.midGameClear);
                t.button("midgame breakable", style, () -> {
                    type = ChunkMarkType.midGameBreakable;
                    hide();
                }).group(group).tooltip("chunks that are converted to breakable when mid game is reached").checked((b) -> type == ChunkMarkType.midGameBreakable);
                t.row();

                t.button("playable", style, () -> {
                    type = ChunkMarkType.playableRegionStarter;
                    hide();
                }).group(group).tooltip("recalculate playable region from chunk").checked((b) -> type == ChunkMarkType.playableRegionStarter);
                t.button("end region", style, () -> {
                    type = ChunkMarkType.endRegionStarter;
                    hide();
                }).group(group).tooltip("recalculate end game region from chunk").checked((b) -> type == ChunkMarkType.endRegionStarter);
                t.row();

                t.button("safe chunk", style, () -> {
                    type = ChunkMarkType.safeChunk;
                    hide();
                }).group(group).tooltip("chunks in which players are immune").checked((b) -> type == ChunkMarkType.safeChunk);
                t.button("spawn", style, () -> {
                    type = ChunkMarkType.spawn;
                    hide();
                }).group(group).tooltip("remove spawns").checked((b) -> type == ChunkMarkType.spawn);

            }).fill(false).expand(false, false);
            table.row();

            cont.row();
            cont.add(table).center();
        });
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
            case safeChunk -> Vars.rules.safeChunks;
            default -> null;
        };
    }

    public void updateLine(int x1, int x2, int y1, int y2, boolean removing) {
        if (!removing && type == ChunkMarkType.spawn){
            ui.showInfoFade("You cannot add spawns using the chunk marker", 10f);
            return;
        }

        if ((type == ChunkMarkType.spawn || ui.editor.view.lastTool == EditorTool.teamMarker) && removing){
            Vars.rules.spawns.remove(Point2.pack(x2, y2));
            return;
        }

        if (Vars.markOptions.getCurrentSelectedMarker() == null) {
            if (removing) {
                ui.showInfoFade("You cannot remove end region or playable region chunks", 10f);
            } else {
                updateMark(Point2.pack(x2, y2), false);
            }
            return;
        }

        if (x1 != x2 && y1 != y2) {
            if (Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
                y2 = y1;
            } else {
                x2 = x1;
            }
        }

        int start = 0, end = 0;
        if (x1 == x2) {
            start = y1;
            end = y2;
        }

        if (y1 == y2) {
            start = x1;
            end = x2;
        }

        if (start > end) {
            var temp = end;
            end = start;
            start = temp;
        }

        var skipOther = this.skipOther();
        for(var i = start; i <= end; i += Grid.size) {
            if (i % 2 == skipOther && skipOther >= 0) continue;
            updateMark(Point2.pack(x1 == x2 ? x1 : i, y1 == y2 ? y1 : i), removing);
        }
    }

    public int skipOther() {
        if (Core.input.keyDown(KeyCode.space)) {
            return 0;
        } else if (Core.input.keyDown(KeyCode.altLeft)) {
            return 1;
        }
        return -1;
    }

    public void updateMark(int chunk, boolean removing) {
        if (getCurrentSelectedMarker() != null) {
            if (!removing) {
                getCurrentSelectedMarker().addUnique(chunk);
            } else {
                getCurrentSelectedMarker().removeValue(chunk);
            }
        } else {
            if (type == ChunkMarkType.endRegionStarter) {
                Vars.rules.endGameRegion.clear();
                Grid.updateEndGameRegion(Grid.GPos.from(chunk));
            }
            if (type == ChunkMarkType.playableRegionStarter) {
                Vars.rules.playableRegion.clear();
                Grid.updatePlayableRegions(Grid.GPos.from(chunk));
                Vars.rules.endGameRegion.each(c -> {
                    if (!Vars.rules.playableRegion.contains(c)) Vars.rules.endGameRegion.removeValue(c);
                });
            }
        }
    }

    public Color getTypeColor() {
        return switch(type){
            case unbreakable -> ChunkSettings.unbreakable.color;
            case midGameBreakable -> ChunkSettings.midGameBreakable.color;
            case midGameClear -> ChunkSettings.midGameClear.color;
            case safeChunk -> ChunkSettings.safe.color;
            case endRegionStarter -> ChunkSettings.endRegion.color;
            case playableRegionStarter -> ChunkSettings.playableRegion.color;
            case endRegionWall -> ChunkSettings.endRegionWall.color;
            default -> Color.white;
        };
    }

    public enum ChunkMarkType{
        unbreakable, midGameClear, midGameBreakable, endRegionWall, playableRegionStarter, endRegionStarter, safeChunk, spawn
    }
}
