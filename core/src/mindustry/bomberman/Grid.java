package mindustry.bomberman;

import arc.*;
import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;

import arc.util.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;

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
    }

    /**
     * @param chunk The chunk to start walking from
     * @param range Maximum number of chunks to walk in each direction
     * @param postCheck Whether to the do the endOn check after or before adding the chunk
     * @param endOn Chunk on which stop walking at
     * @return The walked chunks
     */
    public static Seq<GPos> getNeighbouring(GPos chunk, int range, boolean postCheck, Boolf<GPos> endOn){
        var chunks = new Seq<GPos>();
        var iRange = 0;
        // figure out a better of doing this:
        // walk up
        iRange = range;
        for(int i = chunk.y + size; i < world.height() && iRange > 0; i += size){
            if(postCheck) chunks.add(GPos.from(chunk.x, i));
            if(endOn.get(GPos.from(chunk.x, i))) break;
            if(!postCheck) chunks.add(GPos.from(chunk.x, i));
            iRange--;
        }
        // walk down
        iRange = range;
        for(int i = chunk.y - size; i > 0 && iRange > 0; i -= size){
            if(postCheck) chunks.add(GPos.from(chunk.x, i));
            if(endOn.get(GPos.from(chunk.x, i))) break;
            if(!postCheck) chunks.add(GPos.from(chunk.x, i));
            iRange--;
        }
        // walk right
        iRange = range;
        for(int i = chunk.x + size; i < world.width() && iRange > 0; i += size){
            if(postCheck) chunks.add(GPos.from(i, chunk.y));
            if(endOn.get(GPos.from(i, chunk.y))) break;
            if(!postCheck) chunks.add(GPos.from(i, chunk.y));
            iRange--;
        }
        // walk left
        iRange = range;
        for(int i = chunk.x - size; i > 0 && iRange > 0; i -= size){
            if(postCheck) chunks.add(GPos.from(i, chunk.y));
            if(endOn.get(GPos.from(i, chunk.y))) break;
            if(!postCheck) chunks.add(GPos.from(i, chunk.y));
            iRange--;
        }
        return chunks;
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

        public boolean hasXY(int x, int y){
            return this.x == Grid.centerX(x) && this.y == Grid.centerY(y);
        }

        public boolean hasXY(int xy){
            return hasXY(chunks.get(xy).x, chunks.get(xy).y);
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

        public Corners<Tile> cornerTiles(){
            var mid = this.tile();

            var cX = mid.x - Grid.offset;
            var cY = mid.y + Grid.offset;

            var topLeft = world.tiles.get(cX, cY);
            var topRight = world.tiles.get(cX + Grid.size - 1, cY);
            var botLeft = world.tiles.get(cX, cY - Grid.size + 1);
            var botRight = world.tiles.get(cX + Grid.size - 1, cY - Grid.size + 1);

            var tiles = new Corners<Tile>();
            tiles.add(topLeft, topRight, botLeft, botRight);
            return tiles;
        }

        public Sides<Tile> sideTiles(){
            var mid = this.tile();

            var up = world.tiles.get(mid.x, mid.y + 1);
            var down = world.tiles.get(mid.x, mid.y - 1);
            var left = world.tiles.get(mid.x - 1, mid.y);
            var right = world.tiles.get(mid.x + 1, mid.y);

            var tiles = new Sides<Tile>();
            tiles.add(up, down, left, right);
            return tiles;
        }

        public int packed(){
            return Point2.pack(this.x, this.y);
        }

        public Seq<GPos> neighbourSideChunks(){
            return neighbourChunks(false);
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

        public boolean breakable(){
            return !rules.unbreakable.contains(this.packed());
        }

        public boolean inPlayableRegion(){
            return rules.playableRegion.contains(this.packed());
        }

        public boolean inEndGameRegion(){
            return rules.endGameRegion.contains(this.packed());
        }

        public boolean isSafe(){
            return rules.safeChunks.contains(this.packed());
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

    public static class Corners<T> extends Seq<T>{
        public T topLeft(){
            return this.get(0);
        }

        public T topRight(){
            return this.get(1);
        }

        public T bottomLeft(){
            return this.get(3);
        }

        public T bottomRight(){
            return this.get(4);
        }
    }

    public static class Sides<T> extends Seq<T>{
        public T sideUp(){
            return this.get(0);
        }

        public T sideDown(){
            return this.get(1);
        }

        public T sideLeft(){
            return this.get(3);
        }

        public T sideRight(){
            return this.get(4);
        }
    }
}
