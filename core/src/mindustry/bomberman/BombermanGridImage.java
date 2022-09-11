package mindustry.bomberman;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import static mindustry.Vars.*;

public class BombermanGridImage extends Element{
    private int imageWidth, imageHeight;
    public BombermanGridImage(int w, int h){
        this.imageWidth = w;
        this.imageHeight = h;
    }

    @Override
    public void draw(){
        float xspace = (getWidth() / imageWidth);
        float yspace = (getHeight() / imageHeight);
        float s = 1f;

        Draw.color(Color.black);
        for(int x = 0; x <= imageWidth; x += Grid.size){
            if(x % Grid.size != 0) continue;
            Fill.crect((int)(this.x + xspace * x - s), y - s, 2, getHeight() + (x == imageWidth ? 1 : 0));
        }

        for(int y = 0; y <= imageHeight; y += Grid.size){
            if(y % Grid.size != 0) continue;
            Fill.crect(x - s, (int)(this.y + y * yspace - s), getWidth(), 2);
        }

        Draw.color(Color.gray);
        Draw.alpha(0.4f);
        for(int x = 0; x < imageWidth; x += Grid.size * 2){
            for(int y = 0; y < imageHeight; y += Grid.size * 2){
                Fill.crect((int)(this.x + xspace * x - s + 2), (int)(this.y + y * yspace - s + 2), Grid.size * xspace - 2, Grid.size * yspace - 2);
            }
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
