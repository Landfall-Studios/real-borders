package world.landfall.realborders.commands;

import com.hypixel.hytale.builtin.commandmacro.MacroCommandParameter;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
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
    private final OptionalArg<String> nameArg;
    private final FlagArg listArg;
    private final FlagArg removeArg;

    public WorldBorderCommand() {
        super("worldborder", "Changes the position and size of the worldborder in the current world");
        radiusArg = withOptionalArg("radius", "Sets the radius of the worldborder", ArgTypes.INTEGER);
        centerArg = withOptionalArg("center", "Sets the center position of the worldborder", ArgTypes.VECTOR2I);
        nameArg = withOptionalArg("name", "Which safe zone to edit", ArgTypes.STRING);
        listArg = withFlagArg("list", "Lists all worldborder safe zones");
        removeArg = withFlagArg("remove", "Removes the selected safe zone");
        this.setPermissionGroup(GameMode.Creative);
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull World world, @NonNull Store<EntityStore> store) {
        world.execute(() -> {
            var worldBorderData = world.getChunkStore().getStore().getResource(RealBorders.WORLD_BORDER_RES);
            if (commandContext.get(listArg)) {
                if (worldBorderData.activeSafeZones.isEmpty()) {
                    commandContext.sendMessage(Message.raw("There are no safe zones in the selected world"));
                    return;
                }
                commandContext.sendMessage(Message.raw("Listing safe zones:"));
                worldBorderData.activeSafeZones.forEach(safeZone -> {
                    commandContext.sendMessage(Message.raw("  "+safeZone.name+":"));
                    commandContext.sendMessage(Message.raw("    Center: "+safeZone.center));
                    commandContext.sendMessage(Message.raw("    Radius: "+safeZone.blockRadius));
                });
                return;
            }
            if (!commandContext.provided(nameArg)) {
                commandContext.sendMessage(Message.raw("Usage: /worldborder --name spawn_area --radius 3 --center 300 20"));
                return;
            }
            if (commandContext.provided(removeArg)) {
                worldBorderData.activeSafeZones.removeIf(zone -> zone.name.equals(nameArg.get(commandContext)));
                commandContext.sendMessage(Message.raw("Removed zone ["+nameArg.get(commandContext)+"]"));
                return;
            }
            var name = commandContext.get(nameArg);
            var radius = commandContext.provided(radiusArg) ? commandContext.get(radiusArg) : 200;
            var center = commandContext.provided(centerArg) ? commandContext.get(centerArg) : new Vector2i(0, 0);
            var optionalZone = worldBorderData.activeSafeZones.stream().filter(safeZone -> safeZone.name.equals(name)).findFirst();
            if (optionalZone.isPresent()) {
                var zone = optionalZone.get();
                if (commandContext.provided(radiusArg))
                    zone.blockRadius = radius;
                if (commandContext.provided(centerArg))
                    zone.center = center;
                commandContext.sendMessage(Message.raw("Set zone ["+name+"] to parameters:"));
                commandContext.sendMessage(Message.raw("  Radius: "+zone.blockRadius));
                commandContext.sendMessage(Message.raw("  Center: "+zone.center));
            } else {
                var zone = WorldBorderResource.WorldBorderSafeZone.create(radius, center, name);
                worldBorderData.activeSafeZones.addLast(zone);

                commandContext.sendMessage(Message.raw("Created zone ["+name+"] with parameters:"));
                commandContext.sendMessage(Message.raw("  Radius: "+zone.blockRadius));
                commandContext.sendMessage(Message.raw("  Center: "+zone.center));
            }
        });
//        if (!commandContext.provided(radiusArg) && !commandContext.provided(centerArg) && !commandContext.provided(enabledArg)) {
//            commandContext.sendMessage(Message.raw("Usage: /worldborder --radius 3 --center 300 20"));
//            return;
//        }
//        if (commandContext.provided(radiusArg)) {
//            var radius = commandContext.get(radiusArg);
//            world.execute(() -> {
//                var border = world.getChunkStore().getStore().getResource(RealBorders.WORLD_BORDER_RES);
//                world.getChunkStore().getStore().replaceResource(RealBorders.WORLD_BORDER_RES, WorldBorderResource.create(radius, border.center, border.enabled));
//            });
//            commandContext.sendMessage(Message.raw("Set border radius to [" + radius + "]"));
//
//        }
//        if (commandContext.provided(centerArg)) {
//            var center = commandContext.get(centerArg);
//            world.execute(() -> {
//                var border = world.getChunkStore().getStore().getResource(RealBorders.WORLD_BORDER_RES);
//                world.getChunkStore().getStore().replaceResource(RealBorders.WORLD_BORDER_RES, WorldBorderResource.create(border.blockRadius, center, border.enabled));
//            });
//            commandContext.sendMessage(Message.raw("Set border center to [" + center.toString() + "]"));
//        }
//        if (commandContext.provided(enabledArg)) {
//            var enabled = commandContext.get(enabledArg);
//            world.execute(() -> {
//                var border = world.getChunkStore().getStore().getResource(RealBorders.WORLD_BORDER_RES);
//                world.getChunkStore().getStore().replaceResource(RealBorders.WORLD_BORDER_RES, WorldBorderResource.create(border.blockRadius, border.center, enabled));
//            });
//            commandContext.sendMessage(Message.raw("Set enabled: [" + enabled + "]"));
//        }

    }
}
