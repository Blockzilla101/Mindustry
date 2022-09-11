package mindustry.bomberman;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.struct.*;

import static mindustry.Vars.*;

public class RegionGridImage extends Element{
    private int imageWidth, imageHeight;

    public RegionGridImage(int w, int h){
        this.imageWidth = w;
        this.imageHeight = h;
    }

    @Override
    public void draw(){
        renderMarkedChunks(Vars.rules.midGameBreakableChunks, Color.teal);
        renderMarkedChunks(Vars.rules.midGameClearChunks, Color.navy);
        renderMarkedChunks(Vars.rules.endGameRegionWalls, Color.yellow);
        renderMarkedChunks(Vars.rules.safeChunks, Color.green);
        renderMarkedChunks(Vars.rules.endGameRegion, Color.red);
        renderMarkedChunks(Vars.rules.unbreakable, Color.cyan);
        renderMarkedChunks(Vars.rules.playableRegion, Color.magenta);
    }

    void renderMarkedChunks(IntSeq chunks, Color color){
        float xspace = (getWidth() / imageWidth);
        float yspace = (getHeight() / imageHeight);
        float s = 1f;

        Draw.color(color);
        Draw.alpha(0.1f);
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
