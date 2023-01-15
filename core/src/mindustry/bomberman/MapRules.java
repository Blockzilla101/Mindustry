package mindustry.bomberman;

import arc.graphics.*;
import arc.struct.*;
import mindustry.game.*;

public class MapRules{
    public int xOffset = 0;
    public int yOffset = 0;

    public ObjectMap<Integer, Team> spawns = new ObjectMap<>();
    public IntMap<Seq<Integer>> spawnsByTeam = new IntMap<>();
    public PlayingStage startingStage = PlayingStage.start;

    public MarkedChunkSeq unbreakable = new MarkedChunkSeq("unbreakable", Color.cyan);
    public MarkedChunkSeq playableRegion = new MarkedChunkSeq("playable-region", Color.magenta);
    public MarkedChunkSeq endGameRegion = new MarkedChunkSeq("end-region", Color.red);
    public MarkedChunkSeq endGameRegionWalls = new MarkedChunkSeq("end-region-wall", Color.yellow);
    public MarkedChunkSeq midGameClearChunks = new MarkedChunkSeq("midgame-clear", Color.teal);
    public MarkedChunkSeq midGameBreakableChunks = new MarkedChunkSeq("midgame-breakable", Color.navy);
    public MarkedChunkSeq safeChunks = new MarkedChunkSeq("safe", Color.green);

    public int startStageLength = 15 * 60 * 1000;
    public int midStageLength = 5 * 60 * 1000;
    public int endStageLength = 10 * 60 * 1000;

    public enum PlayingStage {
        start, mid, end
    }
}
