package mindustry.antigrief;

import arc.*;
import arc.struct.*;

import mindustry.game.*;
import mindustry.gen.*;
import mindustry.net.Administration.*;

import static mindustry.Vars.*;

public class PlayerHandler{
    private final ObjectMap<Integer, String> players = new ObjectMap<>();
    private String lastMap;

    public PlayerHandler() {
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if (net.active()) Groups.player.each(p -> handleJoin(p.id));
            if (state.map.name().equals(lastMap) && net.active()) return;
            lastMap = state.map.name();
            players.clear();
        });
    }

    public void handleJoin(int id) {
        if (players.containsKey(id)) return;
        var player = Groups.player.getByID(id);
        if (player != null) {
            players.put(id, player.name);

            var trace = antiGrief.tracer.get(id);
            if(trace == null){
                // admin check is done in tracer.trace()
                antiGrief.tracer.trace(player, t -> {
                    if(antiGrief.joinMessages) sendMessage(player.id, t, false);
                });
            }else{
                if (antiGrief.joinMessages) sendMessage(id, trace, false);
            }
        }
    }

    public void handleLeave(int id) {
        if (players.containsKey(id)) {
            if (antiGrief.leaveMessages) sendMessage(id, antiGrief.tracer.get(id), true);
            players.remove(id);
        }
    }

    private void sendMessage(int playerId, TraceInfo trace, boolean leaving) {
        if (trace != null && antiGrief.autoTrace) {
            AntiGrief.sendMessage(players.get(playerId) + "[#f8c471] " + (leaving ? "left." : "joined.") + " [#f5b041]uuid:[] " + trace.uuid + " [#f5b041]ip:[] " + trace.ip);
        } else {
            AntiGrief.sendMessage(players.get(playerId) + "[#f8c471] " + (leaving ? "left." : "joined.") + " [#f5b041]id:[] " + playerId);
        }
    }
}
