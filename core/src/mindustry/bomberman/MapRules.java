package mindustry.bomberman;

import arc.struct.*;
import mindustry.game.*;

public class MapRules{
    public int xOffset = 0;
    public int yOffset = 0;

    public ObjectMap<Grid.GPos, Team> spawns = new ObjectMap<>();
    public ObjectMap<Team, Seq<Grid.GPos>> spawnsByTeam = new ObjectMap<>();
    public IntSeq unbreakable = new IntSeq();

    public PlayingStage startingStage = PlayingStage.start;

    public IntSeq playableRegion = new IntSeq();
    public IntSeq endGameRegion = new IntSeq();
    public IntSeq endGameRegionWalls = new IntSeq();
    public IntSeq midGameClearChunks = new IntSeq();
    public IntSeq midGameBreakableChunks = new IntSeq();
    public IntSeq safeChunks = new IntSeq();

    public int startStageLength = 15 * 60 * 1000;
    public int midStageLength = 5 * 60 * 1000;
    public int endStageLength = 10 * 60 * 1000;

    public enum PlayingStage {
        start, mid, end
    }
}
