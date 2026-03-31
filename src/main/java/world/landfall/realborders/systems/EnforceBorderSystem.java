package world.landfall.realborders.systems;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.particle.config.Particle;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSpawner;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem;
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.soundset.config.SoundSet;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.time.WorldTimeSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.worldgen.loader.util.ColorUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import world.landfall.realborders.RealBorders;
import world.landfall.realborders.components.EnforceBorderComponent;

import java.util.List;

public class EnforceBorderSystem extends DelayedEntitySystem<EntityStore> {

    private static final float BORDER_VOLUME = .8f;


    public EnforceBorderSystem() {
        super(1);
    }

    @Override
    public void tick(float dt, int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        var ref = archetypeChunk.getReferenceTo(index);
        if (!ref.isValid()) return;
        var player = store.getComponent(ref, PlayerRef.getComponentType());
        if (player == null || !player.isValid())
            return;
        var statMap = store.getComponent(ref, EntityStatMap.getComponentType());
        if (statMap == null) return;
        var world = store.getExternalData().getWorld();
        var chunkStore = world.getChunkStore().getStore();
        var worldBorderData = chunkStore.getResource(RealBorders.WORLD_BORDER_RES);
        if (worldBorderData.activeSafeZones.isEmpty())
            return;
        var hurtLevels = worldBorderData.activeSafeZones.stream().map( worldBorder -> {
            var c = worldBorder.center;
            var r = worldBorder.blockRadius;
            var pos = player.getTransform().getPosition();
            return hurtLevel(c, r, pos);
        }).toList();
        var hurtLevel = 4;
        for (var x : hurtLevels)
            if (x < hurtLevel)
                hurtLevel = x;
        switch(hurtLevel) {
            case 0 -> {}
            case 1 -> {}
            case 2 -> statMap.subtractStatValue(DefaultEntityStatTypes.getHealth(), 1);
            case 3 -> statMap.subtractStatValue(DefaultEntityStatTypes.getHealth(), 4);
            case 4 -> statMap.subtractStatValue(DefaultEntityStatTypes.getHealth(), 16);
        };
        for (var x : hurtLevels)
            if (x < hurtLevel)
                hurtLevel = x;
        for (var worldBorder : worldBorderData.activeSafeZones) {
            if (hurtLevel > 0) {
                int finalHurtLevel = hurtLevel;
                var c = worldBorder.center;
                var r = worldBorder.blockRadius;
                var pos = player.getTransform().getPosition();
                world.execute(() -> {
                    if (finalHurtLevel > 1)
                        SoundUtil.playSoundEvent2d(ref, SoundEvent.getAssetMap().getIndex("SFX_Player_Hurt"), SoundCategory.SFX, store);
                    SoundUtil.playSoundEvent2d(ref, SoundEvent.getAssetMap().getIndex("SFX_Portal_Neutral_Open"), SoundCategory.SFX, BORDER_VOLUME / (6 - finalHurtLevel), 1, store);
    //                ParticleUtil.spawnParticleEffect("MagicPortal_Fire", new Vector3d(pos).add(0, 2, 0), store);

                    // Spawn border particles
                    // (yes I am aware this spawns 5 billion fucking packets)
                    // ((No I do not give a shit))
                    // (((You should've seen the code for the jetpack in 830)))
                    var pos1 = new Vector2i(c).add(r, r);
                    var pos2 = new Vector2i(c).add(-r, r);
                    var pos3 = new Vector2i(c).add(-r, -r);
                    var pos4 = new Vector2i(c).add(r, -r);
                    var posList = List.of(pos1, pos2, pos3, pos4, pos1);
                    for (int k = 0; k < 60; k++)
                        for (int i = 0; i < 4; i++) {
                            double yaw = switch (i) {
                                case 0 -> 0;
                                case 1 -> Math.PI / 2;
                                case 2 -> Math.PI;
                                case 3 -> Math.PI / 2;
                                default -> 0;
                            };
                            var firstPos = new Vector3d(posList.get(i).x, k * 4, posList.get(i).y);
                            var secondPos = new Vector3d(posList.get(i + 1).x, k * 4, posList.get(i + 1).y);
                            for (float j = 0.0f; j * 2 <= r; j++) {
                                var currentPos = new Vector3d(firstPos).scale((j * 2) / r).add(new Vector3d(secondPos).scale(1 - ((j * 2) / r)));
    //                            ParticleUtil.spawnParticleEffect("Block_Top_Glow", currentPos, store);
    //                            var refs = world.getPlayerRefs();
                                if (currentPos.distanceTo(pos) > 20)
                                    continue;

                                ParticleUtil.spawnParticleEffect("Block_Top_Glow", currentPos.x, currentPos.y, currentPos.z, (float) yaw, 0f, 0f, 2f, (Color) null, null, List.of(ref), store);
                            }
                        }


                });
            }
        }
    }
    private static int hurtLevel(com.hypixel.hytale.math.vector.Vector2i center, int radius, Vector3d pos) {
        var center3d = new Vector3d(center.x, 0, center.y);
        center3d.subtract(pos);
        if (Math.abs(center3d.x) > radius || Math.abs(center3d.z) > radius) {
            return 4;
        } else if (Math.abs(center3d.x) > radius + 2 || Math.abs(center3d.z) > radius + 2) {
            return 3;
        } else if (Math.abs(center3d.x) > radius - 3 || Math.abs(center3d.z) > radius - 3) {
            return 2;
        } else if (Math.abs(center3d.x) > radius - 16 || Math.abs(center3d.z) > radius - 16) {
            return 1;
        }
        return 0;
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Query.and(RealBorders.ENFORCE_BORDER_COMP);
    }
}
