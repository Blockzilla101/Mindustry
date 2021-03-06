package mindustry.antigrief;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.struct.*;

import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.gen.Player;
import mindustry.graphics.*;
import mindustry.net.Administration.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.payloads.*;

import static arc.Core.camera;
import static mindustry.Vars.*;

public class TileInfos{
    private final ObjectMap<Integer, Seq<TileInfo>> infos = new ObjectMap<>();
    private int width;

    private String lastMapName;

    public int nthDeconstructed = 0;

    public TileInfos() {
        Events.on(WorldLoadEvent.class, e -> {
            if (state.map.name().equals(lastMapName)) return;
            lastMapName = state.map.name();
            resize(world.width(), world.height());
        });
    }

    public void add(TileInfo info, int x, int y) {
        if (infos.get(y * width + x) == null) infos.put(y * width + x, new Seq<>());
        while(infos.get(y * width + x).size >= antiGrief.maxInfosPerTile) {
            infos.get(y * width + x).remove(0);
        }
        infos.get(y * width + x).add(info);
    }

    public void add(TileInfo info, Tile tile) {
        add(info, tile.x, tile.y);
    }

    public Seq<TileInfo> get(int x, int y) {
        if (infos.get(y * width + x) == null) return new Seq<>();
        return infos.get(y * width + x);
    }

    public Seq<TileInfo> get(Tile tile) {
        return get(tile.x, tile.y);
    }

    public TileInfo getLast(Tile tile) {
        return getLast(tile.x, tile.y);
    }

    public void remove(TileInfo info, Tile tile) {
        remove(info, tile.x, tile.y);
    }

    public void remove(TileInfo info, int x, int y) {
        if (get(x, y).size == 0) return;
        infos.get(y * width + x).remove(info);
    }

    public TileInfo getLast(int x, int y) {
        var infos = get(x, y);
        if (infos.size == 0) return null;
        return infos.get(infos.size - 1);
    }

    public void resize(int width, int height) {
        this.width = width;
        infos.clear(width * height);
    }

    public void drawDeconstructed(){
        if (!antiGrief.commands.displayRemoved) return;

        Seq<TileInfo> lastBroken = new Seq<>();

        infos.each((loc, infos2) -> {
            var nth = nthDeconstructed;
            for(int i = infos2.size - 1; i >= 0; i--){
                if (infos2.get(i).interaction == InteractionType.broke || infos2.get(i).interaction == InteractionType.picked_up) {
                    if (nth != 0){
                        nth--;
                    } else {
                        lastBroken.add(infos2.get(i));
                        break;
                    }
                }
            }
        });

        Draw.z(Layer.block);
        for(int i = 0; i < lastBroken.size; i++){
            if (lastBroken.get(i).block == null) continue;
            Block b = lastBroken.get(i).block;
            var info = lastBroken.get(i);
            if(!camera.bounds(Tmp.r1).grow(tilesize * 2f).overlaps(Tmp.r2.setSize(b.size * tilesize).setCenter(info.x * tilesize + b.offset, info.y * tilesize + b.offset))) continue;

            Draw.alpha(0.95f);
            Draw.mixcol(Color.white, 0.2f + Mathf.absin(Time.globalTime, 6f, 0.2f));
            if (antiGrief.displayFullSizeBlocks) {
                Draw.rect(b.fullIcon, info.x * tilesize + b.offset, info.y * tilesize + b.offset, b.rotate ? info.rotation * 90 : 0f);
            } else {
                Draw.rect(b.fullIcon, info.x * tilesize, info.y * tilesize, b.rotate ? info.rotation * 90 : 0f);
            }
        }
        Draw.reset();
    }

    public static class TileInfo {
        public Block block;

        public int x;
        public int y;

        public int rotation;
        public Object config;

        public InteractionType interaction;
        public SemiPlayer player;

        public long timestamp;

        public TileInfo(Block block, int x, int y, int rotation, Object config, InteractionType interaction, SemiPlayer player){
            this.block = block;
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.config = config;
            this.interaction = interaction;
            this.player = player;

            timestamp = Time.millis();
        }

        public String toString(boolean withTimestamp, boolean color) {
            if (block == null) return "???";
            StringBuilder str = new StringBuilder();
            str.append(player.name);
            str.append(color ? "[#85c1e9] " : "[white] ");
            str.append(interaction.name().replace("_", " "));
            str.append("[white] ");
            str.append(Fonts.getUnicodeStr(block.name));
            str.append("[]");

            if (interaction == InteractionType.configured) {
                if(block instanceof MessageBlock){
                    if (config.equals("")) {
                        str.append(" to empty");
                    }
                }else if(block instanceof SwitchBlock){
                    if ((Boolean)config) {
                        str.append(" to on");
                    } else {
                        str.append(" to off");
                    }
                }else if(block instanceof Sorter || block instanceof ItemSource || block instanceof LiquidSource){
                    if (config != null){
                        str.append(" to ").append(Fonts.getUnicodeStr(block instanceof LiquidSource ? ((Liquid)config).name : ((Item)config).name));
                    }else{
                        str.append(" to ").append("none");
                    }
                }else if(block instanceof CommandCenter){
                    str.append(" to ");
                    var command = (UnitCommand)config;
                    if(command == UnitCommand.attack){
                        str.append("\ue86e");
                    }else if(command == UnitCommand.rally){
                        str.append("\ue86c");
                    }else if(command == UnitCommand.idle) {
                        str.append("\ue815");
                    }
                }else if(block instanceof UnitFactory){
                    str.append(" to ");
                    if((Integer)config != -1){
                        str.append(Fonts.getUnicodeStr(((UnitFactory)block).plans.get((Integer)config).unit.name));
                    }else{
                        str.append("none");
                    }
                }else if(block instanceof BlockForge){
                    str.append(" to ");
                    if (config != null) {
                        str.append(Fonts.getUnicodeStr(((Block)config).name));
                    } else {
                        str.append("none");
                    }
                }
            }else if(interaction == InteractionType.rotated){
                str.append(" to ");
                if(rotation == 0){
                    str.append("\ue803"); //right
                }else if(rotation == 1){
                    str.append("\ue804"); //up
                }else if(rotation == 2){
                    str.append("\ue802"); //left
                }else if(rotation == 3){
                    str.append("\ue805"); //down
                }
            }

            if(withTimestamp) {
                str.append(color ? " [#47c7ae]" : " [white]");
                str.append(AntiGrief.prettyTime(Time.millis() - timestamp));
                str.append(color ? " [#85c1e9]" : " [white]");
                str.append("ago");
            }

            return str.toString();
        }

        public String toString(boolean withTimestamp) {
            return toString(withTimestamp, false);
        }

        public String toString() {
            return toString(false, false);
        }
    }

    public static class SemiPlayer {
        public String name;
        public int id;

        public SemiPlayer(Player p){
            this(p.name, p.id);
        }

        public SemiPlayer(String name, int id){
            this.name = name;
            this.id = id;
        }

        public TraceInfo getTrace() {
            return antiGrief.tracer.get(id);
        }
    }

    public enum InteractionType {
        built, broke, configured, rotated, picked_up, dropped
    }
}
