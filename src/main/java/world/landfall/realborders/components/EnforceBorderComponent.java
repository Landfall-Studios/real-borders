package world.landfall.realborders.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.Nullable;

public class EnforceBorderComponent implements Component<EntityStore> {
    @Override
    public @Nullable Component<EntityStore> clone() {
        return new EnforceBorderComponent();
    }
}
