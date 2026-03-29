package world.landfall.realborders;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import world.landfall.realborders.commands.WorldBorderCommand;
import world.landfall.realborders.components.EnforceBorderComponent;
import world.landfall.realborders.resources.WorldBorderResource;
import world.landfall.realborders.systems.EnforceBorderSystem;
import world.landfall.realborders.systems.PlayerJoinSystem;

public class RealBorders extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static ComponentType<EntityStore, EnforceBorderComponent> ENFORCE_BORDER_COMP;
    public static ResourceType<ChunkStore, WorldBorderResource> WORLD_BORDER_RES;

    public RealBorders(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        WORLD_BORDER_RES = this.getChunkStoreRegistry().registerResource(WorldBorderResource.class, WorldBorderResource::create);
        ENFORCE_BORDER_COMP = this.getEntityStoreRegistry().registerComponent(EnforceBorderComponent.class, EnforceBorderComponent::new);
        getEntityStoreRegistry().registerSystem(new EnforceBorderSystem());
        getEntityStoreRegistry().registerSystem(new PlayerJoinSystem());

        getCommandRegistry().registerCommand(new WorldBorderCommand());

    }
}
