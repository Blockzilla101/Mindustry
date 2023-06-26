package mindustry.bomberman.dialogs;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.bomberman.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.maps.filters.*;
import mindustry.ui.dialogs.*;

import static mindustry.Vars.ui;

public class RegionRenderSettingsDialog extends BaseDialog{
    private Table table = new Table();

    public boolean renderAllRegions = true;
    public boolean renderSpawns = true;

    public RegionRenderSettingsDialog(){
        super("Render Settings");

        closeOnBack();
        shown(() -> {
            save();

            cont.clear();
            table.clear();

            title("Global");

            table.check("Render All", this.renderAllRegions, b -> this.renderAllRegions = b);
            table.row();

            title("Per Region Type");

            for(var item: ChunkSettings.all) {
                table.table(t -> {
                    (new FilterOption.SliderOption(name, () -> item.opacity, f -> item.opacity = f, 0f, 1f, 0.05f)).build(t, false);
                    t.button(b -> {
                        b.left();
                        b.table(Tex.pane, in -> {
                            in.stack(new Image(Tex.alphaBg), new Image(Tex.whiteui){{
                                update(() -> setColor(item.color));
                            }}).grow();
                        }).margin(4).size(50f).padRight(10);
                        b.add("Region Color");
                    }, () -> ui.picker.show(item.color, item.color::set)).left().width(250f).row();
                });
                table.row();
            }

            cont.row();
            cont.add(table);
        });

        buttons.defaults().size(200f, 50f);
        buttons.button("@cancel", () -> {
            load();
            hide();
        });

        buttons.button("@ok", () -> {
            save();
            hide();
        });
    }

    void title(String text){
        table.add(text).color(Pal.accent).padTop(20).padRight(100f).padBottom(-3);
        table.row();
        table.image().color(Pal.accent).height(3f).padRight(100f).padBottom(20);
        table.row();
    }

    void load(){
        renderSpawns = Core.settings.getBool("bomberman.render-settings.render-spawns", true);
        renderAllRegions = Core.settings.getBool("bomberman.render-settings.render-all", true);
        ChunkSettings.load();
    }

    void save(){
        Core.settings.put("bomberman.render-settings.render-spawns", renderSpawns);
        Core.settings.put("bomberman.render-settings.render-all", renderAllRegions);
        ChunkSettings.save();
    }
}
