package me.zeroeightsix.kami.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.command.syntax.ChunkBuilder;
import me.zeroeightsix.kami.module.modules.hidden.Pathfind;
import net.minecraft.pathfinding.PathPoint;

/**
 * Created by 086 on 25/01/2018.
 */
public class PathCommand extends Command {
    int x = Integer.MIN_VALUE;
    int y = Integer.MIN_VALUE;
    int z = Integer.MIN_VALUE;

    public PathCommand() {
        super("path", new ChunkBuilder().append("x").append("y").append("z").build());
    }

    @Override
    public void call(String[] args) {
        if (args[0] != null && args[0].equalsIgnoreCase("retry")) {
            if (x != Integer.MIN_VALUE) {
                PathPoint end = new PathPoint(x, y, z);
                Pathfind.createPath(end);
                if (!Pathfind.points.isEmpty()) {
                    Command.sendChatMessage("Path created!");
                }
                return;
            } else {
                Command.sendChatMessage("No location to retry pathfinding to.");
                return;
            }
        }
        if (args.length <= 3) {
            Command.sendChatMessage("&cMissing arguments: x, y, z");
            return;
        }
        try {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
            z = Integer.parseInt(args[2]);

            PathPoint end = new PathPoint(x, y, z);
            Pathfind.createPath(end);
            if (!Pathfind.points.isEmpty()) {
                Command.sendChatMessage("Path created!");
            }
        } catch (NumberFormatException e) {
            Command.sendChatMessage(ChatFormatting.RED.toString() + "Error: input must be numerical");
            return;
        }
    }
}
