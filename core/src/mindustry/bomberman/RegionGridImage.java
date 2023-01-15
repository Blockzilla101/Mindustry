package mindustry.bomberman;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.struct.*;
import mindustry.bomberman.dialogs.MarkOptionsDialog.*;
import mindustry.content.*;

import static mindustry.Vars.*;

public class RegionGridImage extends Element{
    private int imageWidth, imageHeight;

    public RegionGridImage(int w, int h){
        this.imageWidth = w;
        this.imageHeight = h;
    }

    @Override
    public void draw(){
        if(!editor.isBomberman) return;

        MarkedChunkSeq.all.each((k, v) -> renderMarkedChunks(v));

        float xspace = (getWidth() / imageWidth);
        float yspace = (getHeight() / imageHeight);
        float s = 1f;

        if(Vars.regionRenderSettingsDialog.renderSpawns){
            Vars.rules.spawns.forEach((item) -> {
                Draw.color(item.value.color);
                Draw.alpha(0.3f);
                var x = Grid.unpackX(item.key) - Grid.offset;
                var y = Grid.unpackY(item.key) - Grid.offset;
                var offset = EditorState.gridEnabled ? 2 : 0;
                Fill.crect((int)(this.x + xspace * x - s + offset), (int)(this.y + y * yspace - s + offset), Grid.size * xspace - offset, Grid.size * yspace - offset);
                Draw.alpha(1f);
                Fill.crect(this.x + xspace * (x + Grid.offset) - s, this.y + (y + Grid.offset) * yspace - s, xspace, yspace);
            });
        }
    }

    void renderMarkedChunks(MarkedChunkSeq chunks){
        float xspace = (getWidth() / imageWidth);
        float yspace = (getHeight() / imageHeight);
        float s = 1f;

        Draw.color(chunks.renderColor);
        Draw.alpha(chunks.renderOpacity);
        chunks.each(c -> {
            var x = Grid.unpackX(c) - Grid.offset;
            var y = Grid.unpackY(c) - Grid.offset;
            var offset = EditorState.gridEnabled ? 2 : 0;
            Fill.crect((int)(this.x + xspace * x - s + offset), (int)(this.y + y * yspace - s + offset), Grid.size * xspace - offset, Grid.size * yspace - offset);
        });
    }

    @Override
    public void updateVisibility(){
        this.visible = editor.isBomberman && EditorState.renderRegions && state.isEditor();
    }

    public void setImageSize(int w, int h){
        this.imageWidth = w;
        this.imageHeight = h;
    }
}
