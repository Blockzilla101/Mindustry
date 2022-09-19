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
        if (!editor.isBomberman) return;
        float xspace = (getWidth() / imageWidth);
        float yspace = (getHeight() / imageHeight);
        float s = 1f;

        Draw.color(Color.black);
        for(int x = Vars.rules.xOffset; x <= imageWidth; x += Grid.size){
            Fill.crect((int)(this.x + xspace * x - s), y - s, 2, getHeight() + (x == imageWidth ? 1 : 0));
        }

        for(int y = Vars.rules.yOffset; y <= imageHeight; y += Grid.size){
            Fill.crect(x - s, (int)(this.y + y * yspace - s), getWidth(), 2);
        }

        Draw.color(Color.black);
        Draw.alpha(0.3f);
        for(int x = (Vars.bombermanSettingsDialog.invert ? Grid.size : 0) + Vars.rules.xOffset; x < imageWidth; x += Grid.size * 2){
            for(int y = (Vars.bombermanSettingsDialog.invert ? Grid.size : 0) + Vars.rules.yOffset; y < imageHeight; y += Grid.size * 2){
                var width = Grid.size * xspace - 2;
                var height = Grid.size * yspace - 2;
                // fixme: figure how to make the chunks not be drawn out of bounds
                Fill.crect((int)(this.x + xspace * x - s + 2), (int)(this.y + y * yspace - s + 2), width, height);
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
