package world.landfall.realborders.resources;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldBorderResource implements Resource<ChunkStore> {
    public static class WorldBorderSafeZone {
        public int blockRadius;
        public Vector2i center;
        public String name;
        public static final BuilderCodec<WorldBorderSafeZone> CODEC =
                BuilderCodec.builder(WorldBorderSafeZone.class, WorldBorderSafeZone::create)
                        .append(new KeyedCodec<Integer>("Radius", Codec.INTEGER),
                                (data, value) -> data.blockRadius = value,
                                (data) -> data.blockRadius).add()
                        .append(new KeyedCodec<>("Center", Vector2i.CODEC),
                                (data, value) -> data.center = value,
                                (data) -> data.center).add()
                        .append(new KeyedCodec<>("Name", Codec.STRING),
                                (data, value) -> data.name = value,
                                (data) -> data.name).add()
                        .build();
        private WorldBorderSafeZone() {
            blockRadius = 200;
            center = Vector2i.ZERO;
            name = "";
        }
        private WorldBorderSafeZone(WorldBorderSafeZone c) {
            blockRadius = c.blockRadius;
            center = c.center;
            name = c.name;
        }
        public static WorldBorderSafeZone create() {
            return new WorldBorderSafeZone();
        }
        public static WorldBorderSafeZone create(int blockRadius, Vector2i center, String name) {
            var zone = new WorldBorderSafeZone();
            zone.blockRadius = blockRadius;
            zone.center = center;
            zone.name = name;
            return zone;
        }
        public static WorldBorderSafeZone create(WorldBorderSafeZone c) {
            return new WorldBorderSafeZone(c);
        }

    }

//    public int blockRadius;
//    public Vector2i center;
//    public boolean enabled;
    public List<WorldBorderSafeZone> activeSafeZones;
    public static final BuilderCodec<WorldBorderResource> CODEC =
            BuilderCodec.builder(WorldBorderResource.class, WorldBorderResource::create)
                    .append(new KeyedCodec<WorldBorderSafeZone[]>("ActiveSafeZones", new ArrayCodec<WorldBorderSafeZone>(WorldBorderSafeZone.CODEC, WorldBorderSafeZone[]::new)),
                            (data, value) -> data.activeSafeZones = Arrays.stream(value).toList(),
                            (data) -> {
                                var array = new WorldBorderSafeZone[data.activeSafeZones.size()];
                                for (int i = 0; i < data.activeSafeZones.size(); i++) {
                                    array[i] = data.activeSafeZones.get(i);
                                }
                                return array;
                            }).add()
                    .build();

    private WorldBorderResource() {
        activeSafeZones = new ArrayList<>();
    }

    private WorldBorderResource(WorldBorderResource c) {
        activeSafeZones = c.activeSafeZones;
    }
    private WorldBorderResource(List activeSafeZones) {
        this.activeSafeZones = activeSafeZones;
    }



    public static WorldBorderResource create() {
        return new WorldBorderResource();
    }
    public static WorldBorderResource create(List activeSafeZones) {
        return new WorldBorderResource(activeSafeZones);
    }

    @Override
    public @Nullable Resource<ChunkStore> clone() {
        return new WorldBorderResource(this);
    }
}
