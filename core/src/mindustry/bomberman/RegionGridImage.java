package mindustry.bomberman;

import arc.graphics.g2d.*;
import arc.scene.*;

import static mindustry.Vars.*;

public class RegionGridImage extends Element{
    private int imageWidth, imageHeight;

    public RegionGridImage(int w, int h){
        this.imageWidth = w;
        this.imageHeight = h;
    }

    @Override
    public void draw(){
        float xspace = (getWidth() / imageWidth);
        float yspace = (getHeight() / imageHeight);
        float s = 1f;

        int jumpx = (int)(Math.max(Grid.size, xspace) / xspace);
        int jumpy = (int)(Math.max(Grid.size, yspace) / yspace);

        for(int x = 0; x <= imageWidth; x += jumpx){
            Fill.crect((int)(this.x + xspace * x - s), y - s, 2, getHeight() + (x == imageWidth ? 1 : 0));
        }

        for(int y = 0; y <= imageHeight; y += jumpy){
            Fill.crect(x - s, (int)(this.y + y * yspace - s), getWidth(), 2);
        }
    }

    @Override
    public void updateVisibility(){
        this.visible = editor.isBomberman && EditorState.gridEnabled && state.isEditor();
    }

    public void setImageSize(int w, int h){
        this.imageWidth = w;
        this.imageHeight = h;
    }
}
