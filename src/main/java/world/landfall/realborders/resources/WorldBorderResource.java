package world.landfall.realborders.resources;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.jspecify.annotations.Nullable;

public class WorldBorderResource implements Resource<ChunkStore> {


    public int blockRadius;
    public com.hypixel.hytale.math.vector.Vector2i center;
    public boolean enabled;
    public static final BuilderCodec<WorldBorderResource> CODEC =
            BuilderCodec.builder(WorldBorderResource.class, WorldBorderResource::create)
                    .append(new KeyedCodec<Integer>("Radius", Codec.INTEGER),
                            (data, value) -> data.blockRadius = value,
                            (data) -> data.blockRadius).add()
                    .append(new KeyedCodec<>("Center", Vector2i.CODEC),
                            (data, value) -> data.center = value,
                            (data) -> data.center).add()
                    .build();

    private WorldBorderResource() {
        blockRadius = 200;
        center = new Vector2i(0, 0);
        enabled = false;
    }

    private WorldBorderResource(WorldBorderResource c) {
        blockRadius = c.blockRadius;
        center = c.center;
        enabled = c.enabled;
    }
    private WorldBorderResource(int blockRadius, com.hypixel.hytale.math.vector.Vector2i center, boolean enabled) {
        this.blockRadius = blockRadius;
        this.center = center;
        this.enabled = enabled;
    }



    public static WorldBorderResource create() {
        return new WorldBorderResource();
    }
    public static WorldBorderResource create(int radius, com.hypixel.hytale.math.vector.Vector2i center, boolean enabled) {
        return new WorldBorderResource(radius, center, enabled);
    }

    @Override
    public @Nullable Resource<ChunkStore> clone() {
        return new WorldBorderResource(this);
    }
}
