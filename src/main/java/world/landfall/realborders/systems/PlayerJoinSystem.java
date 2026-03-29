package world.landfall.realborders.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.system.PlayerRefAddedSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.beacons.SpawnBeaconSystems;
import org.jspecify.annotations.NonNull;
import world.landfall.realborders.RealBorders;

public class PlayerJoinSystem extends PlayerRefAddedSystem {
    public PlayerJoinSystem() {
        super(PlayerRef.getComponentType());
    }

    @Override
    public void onEntityAdded(@NonNull Ref<EntityStore> ref, @NonNull AddReason reason, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
//        super.onEntityAdded(ref, reason, store, commandBuffer);
        commandBuffer.ensureComponent(ref, RealBorders.ENFORCE_BORDER_COMP);
    }

    @Override
    public @NonNull Query<EntityStore> getQuery() {
        return Query.and(PlayerRef.getComponentType());
    }
}
