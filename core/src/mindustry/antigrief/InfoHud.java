package mindustry.antigrief;

import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.antigrief.TileInfos.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.experimental.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.units.*;

import static mindustry.Vars.*;

public class InfoHud extends Table{
    public InfoHud() {
        background(Tex.pane);
        update(() -> {
            clear();
            var tile = AntiGrief.getCursorTile();
            if (tile == null) {
                add("Tile out of map");
                return;
            }

            var infos = antiGrief.tileInfos.get(tile);
            add(Fonts.getUnicodeStr(tile.block().name) + " (" + tile.x + ", " + tile.y + ")").center();

            if (infos.size == 0) {
                add(" No info found.");
                return;
            }

            int added = 0;
            for(int i = infos.size - 1; i >= 0 && added < antiGrief.maxInfoInHud; i--) {
                var info = infos.get(i);
                if (info.block == null) continue;
                StringBuilder str = new StringBuilder(info.player.name + "[white] " + info.interaction.name().replace("_", " ") + " " + Fonts.getUnicodeStr(info.block.name));

                if (info.interaction == InteractionType.configured) {
                    if(info.block instanceof MessageBlock){
                        if (info.config.equals("")) {
                            str.append(" to empty");
                        }
                    }else if(info.block instanceof SwitchBlock){
                        if ((Boolean)info.config) {
                            str.append(" to T");
                        } else {
                            str.append(" to F");
                        }
                    }else if(info.block instanceof Sorter || info.block instanceof ItemSource || info.block instanceof LiquidSource){
                        if (info.config != null){
                            str.append(" to ").append(Fonts.getUnicodeStr(info.block instanceof LiquidSource ? ((Liquid)info.config).name : ((Item)info.config).name));
                        }else{
                            str.append(" to ").append("none");
                        }
                    }else if(info.block instanceof CommandCenter){
                        str.append(" to ");
                        var command = (UnitCommand)info.config;
                        if(command == UnitCommand.attack){
                            str.append(Iconc.commandAttack);
                        }else if(command == UnitCommand.rally){
                            str.append(Iconc.commandRally);
                        }else if(command == UnitCommand.idle) {
                            str.append(Iconc.cancel);
                        }
                    }else if(info.block instanceof UnitFactory){
                        str.append(" to ");
                        if((Integer)info.config != -1){
                            str.append(Fonts.getUnicodeStr(((UnitFactory)info.block).plans.get((Integer)info.config).unit.name));
                        }else{
                            str.append("none");
                        }
                    }else if(info.block instanceof BlockForge){
                        str.append(" to ");
                        if (info.config != null) {
                            str.append(Fonts.getUnicodeStr(((Block)info.config).name));
                        } else {
                            str.append("none");
                        }
                    }
                }else if(info.interaction == InteractionType.rotated){
                    str.append(" to ");
                    if(info.rotation == 0){
                        str.append(Iconc.right);
                    }else if(info.rotation == 1){
                        str.append(Iconc.up);
                    }else if(info.rotation == 2){
                        str.append(Iconc.left);
                    }else if(info.rotation == 3){
                        str.append(Iconc.down);
                    }
                }

                row();
                str.append(" ").append(AntiGrief.prettyTime(Time.millis() - info.timestamp)).append(" ago");
                add(str).left();
                added++;
            }
        });
    }
}
