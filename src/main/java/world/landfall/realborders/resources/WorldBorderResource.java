package world.landfall.realborders.resources;

import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;

public class WorldBorderResource implements Resource<ChunkStore> {
    public final int blockRadius;
    public final Vector2i center;

    private WorldBorderResource() {
        blockRadius = 50;
        center = new Vector2i(0, 0);
    }

    private WorldBorderResource(WorldBorderResource c) {
        blockRadius = c.blockRadius;
        center = c.center;
    }
    private WorldBorderResource(int blockRadius, Vector2i center) {
        this.blockRadius = blockRadius;
        this.center = center;
    }



    public static WorldBorderResource create() {
        return new WorldBorderResource();
    }
    public static WorldBorderResource create(int radius, Vector2i center) {
        return new WorldBorderResource(radius, center);
    }

    @Override
    public @Nullable Resource<ChunkStore> clone() {
        return new WorldBorderResource(this);
    }
}
