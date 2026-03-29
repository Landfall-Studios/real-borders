package world.landfall.realborders.commands;

import com.hypixel.hytale.builtin.commandmacro.MacroCommandParameter;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.EnumArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;
import world.landfall.realborders.RealBorders;
import world.landfall.realborders.resources.WorldBorderResource;

public class WorldBorderCommand extends AbstractWorldCommand {

    private final OptionalArg<Integer> radiusArg;
    private final OptionalArg<Vector2i> centerArg;

    public WorldBorderCommand() {
        super("worldborder", "Changes the position and size of the worldborder in the current world");
        radiusArg = withOptionalArg("radius", "Sets the radius of the worldborder", ArgTypes.INTEGER);
        centerArg = withOptionalArg("center", "Sets the center position of the worldborder", ArgTypes.VECTOR2I);
        this.setPermissionGroup(GameMode.Creative);
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull World world, @NonNull Store<EntityStore> store) {
        if (!commandContext.provided(radiusArg) && !commandContext.provided(centerArg)) {
            commandContext.sendMessage(Message.raw("Usage: /worldborder --radius 3 --center 300 20"));
            return;
        }
        if (commandContext.provided(radiusArg)) {
            var radius = commandContext.get(radiusArg);
            world.execute(() -> {
                var border = world.getChunkStore().getStore().getResource(RealBorders.WORLD_BORDER_RES);
                world.getChunkStore().getStore().replaceResource(RealBorders.WORLD_BORDER_RES, WorldBorderResource.create(radius, border.center));
            });
            commandContext.sendMessage(Message.raw("Set border radius to [" + radius + "]"));

        }
        if (commandContext.provided(centerArg)) {
            var center = commandContext.get(centerArg);
            world.execute(() -> {
                var border = world.getChunkStore().getStore().getResource(RealBorders.WORLD_BORDER_RES);
                world.getChunkStore().getStore().replaceResource(RealBorders.WORLD_BORDER_RES, WorldBorderResource.create(border.blockRadius, center));
            });
            commandContext.sendMessage(Message.raw("Set border center to [" + center.toString() + "]"));
        }
    }
}
