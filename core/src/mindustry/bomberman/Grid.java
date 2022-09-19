package mindustry.bomberman;

import arc.math.geom.*;
import arc.struct.*;

import mindustry.game.*;
import mindustry.world.*;

import java.util.*;

import static mindustry.Vars.*;
import static mindustry.bomberman.Vars.*;

public class Grid{
    public static int size;
    public static int offset;

    public static void init(int size){
        if(size % 2 == 0){
            offset = (size / 2) - 1;
        }else{
            offset = (size - 1) / 2;
        }
        Grid.size = size;
    }

    /**
     * @param x x pos of a tile
     * @param y y pos of a tile
     * @return center of the chunk the tile is in
     */
    private static int[] center(int x, int y){
        int[] centered = new int[2];

        int cornerX = x - rules.xOffset;
        int cornerY = y - rules.yOffset;

        cornerX = cornerX % size == 0 ? cornerX : cornerX - (cornerX % size);
        cornerY = cornerY % size == 0 ? cornerY : cornerY - (cornerY % size);

        centered[0] = cornerX + offset + rules.xOffset;
        centered[1] = cornerY + offset + rules.yOffset;

        return centered;
    }

    public static int centerX(int x){
        return center(x, 0)[0];
    }

    public static int centerY(int y){
        return center(0, y)[1];
    }

    public static int unpackX(int xy){
        return (short)(xy >>> 16);
    }

    public static int unpackY(int xy){
        return (short)(xy & 0xFFFF);
    }

    public static void recalculateChunks(){
        GPos.chunks.clear();
        for(int x = rules.xOffset; x < editor.width(); x += size){
            for(int y = rules.yOffset; y < editor.height(); y += size){
                var chunk = new GPos(centerX(x), centerY(y));
                GPos.chunks.put(chunk.packed(), chunk);
            }
        }
    }

    public static void moveMarkedChunks(int deltaX, int deltaY, IntSeq markedChunks){
        var temp = new IntSeq();
        markedChunks.each(pos -> temp.add(Point2.pack(unpackX(pos) + deltaX, unpackY(pos) + deltaY)));
        markedChunks.clear();
        markedChunks.addAll(temp);
    }

    public static void moveAllMarkedChunks(int deltaX, int deltaY){
        moveMarkedChunks(deltaX, deltaY, rules.unbreakable);
        moveMarkedChunks(deltaX, deltaY, rules.playableRegion);
        moveMarkedChunks(deltaX, deltaY, rules.endGameRegion);
        moveMarkedChunks(deltaX, deltaY, rules.endGameRegionWalls);
        moveMarkedChunks(deltaX, deltaY, rules.midGameClearChunks);
        moveMarkedChunks(deltaX, deltaY, rules.midGameBreakableChunks);
        moveMarkedChunks(deltaX, deltaY, rules.safeChunks);

        var spawns = new ObjectMap<Integer, Team>();
        rules.spawns.forEach(item -> {
            var x = Grid.unpackX(item.key) + deltaX;
            var y = Grid.unpackY(item.key) + deltaY;
            spawns.put(Point2.pack(x, y), item.value);
        });
        rules.spawns = spawns;
    }

    public static void updatePlayableRegions(GPos starter){
        rules.playableRegion.add(starter.packed());
        var neighbours = starter.neighbourChunks(true);
        for(var neighbour : neighbours){
            if(neighbour == null) continue;
            if(rules.unbreakable.contains(neighbour.packed()) && !rules.midGameClearChunks.contains(neighbour.packed()) && !rules.midGameBreakableChunks.contains(neighbour.packed())) continue;
            if(rules.playableRegion.contains(neighbour.packed())) continue;
            updatePlayableRegions(neighbour);
        }
    }

    public static void updateEndGameRegion(GPos starter){
        rules.endGameRegion.add(starter.packed());
        var neighbours = starter.neighbourChunks(true);
        for(var neighbour : neighbours){
            if(neighbour == null) continue;
            if(!rules.playableRegion.contains(neighbour.packed())) continue;
            if(rules.endGameRegion.contains(neighbour.packed())) continue;
            if(rules.endGameRegionWalls.contains(neighbour.packed())) continue;
            updateEndGameRegion(neighbour);
        }
    }

    public static void reset(){
        GPos.chunks.clear();
    }

    /**
     * Represents a chunk inside the grid
     */
    public static class GPos{
        private static final IntMap<GPos> chunks = new IntMap<>();

        public static GPos create(int x, int y){
            var chunk = new GPos(x, y);
            chunks.put(chunk.packed(), chunk);
            return chunk;
        }

        public int x, y;

        public GPos(int x, int y, boolean center){
            this.x = center ? Grid.centerX(x) : x;
            this.y = center ? Grid.centerY(y) : y;
        }

        public GPos(int x, int y){
            this(x, y, true);
        }

        /**
         * @return Tile at the center of this chunk
         */
        public Tile tile(){
            return world.tiles.get(this.x, this.y);
        }

        /**
         * @return All the tiles in inside this chunk
         */
        public Seq<Tile> tiles(){
            var tiles = new Seq<Tile>();

            var cornerX = this.x - Grid.offset;
            var cornerY = this.y - Grid.offset;

            for(int x = cornerX; x < cornerX + Grid.size; x++){
                for(int y = cornerY; y < cornerY + Grid.size; y++){
                    tiles.add(world.tiles.get(x, y));
                }
            }

            return tiles.filter(Objects::nonNull);
        }

        public int packed(){
            return Point2.pack(this.x, this.y);
        }

        public Seq<GPos> neighbourChunks(boolean corners){
            var neighbours = new Seq<GPos>();
            var cornerTopLeft = GPos.from(this.x - Grid.size, this.y + Grid.size);
            var cornerTopRight = GPos.from(this.x + Grid.size, this.y + Grid.size);
            var cornerBotLeft = GPos.from(this.x - Grid.size, this.y - Grid.size);
            var cornerBotRight = GPos.from(this.x + Grid.size, this.y - Grid.size);

            var sideTop = GPos.from(this.x, this.y + Grid.size);
            var sideBot = GPos.from(this.x, this.y - Grid.size);
            var sideLeft = GPos.from(this.x - Grid.size, this.y);
            var sideRight = GPos.from(this.x + Grid.size, this.y);

            if(corners){
                return neighbours.addAll(cornerTopLeft, cornerTopRight, cornerBotLeft, cornerBotRight, sideTop, sideBot, sideLeft, sideRight);
            }else{
                return neighbours.addAll(sideTop, sideBot, sideLeft, sideRight);
            }
        }

        public boolean air(){
            return this.tile().block().isAir();
        }

        /**
         * @param x x pos of a tile in the chunk
         * @param y y pos of a tile in the chunk
         * @return Creates a chunk from x and y
         */
        public static GPos from(int x, int y){
            return from(Point2.pack(Grid.centerX(x), Grid.centerY(y)));
        }

        public static GPos from(Tile tile){
            return from(tile.x, tile.y);
        }

        public static GPos from(int xy){
            return chunks.get(xy);
        }

        @Override
        public boolean equals(Object o){
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            GPos gPos = (GPos)o;
            return x == gPos.x && y == gPos.y;
        }
    }
}
