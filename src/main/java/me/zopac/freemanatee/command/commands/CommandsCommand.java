package me.zopac.freemanatee.command.commands;

import me.zopac.freemanatee.KamiMod;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.command.syntax.SyntaxChunk;

import java.util.Comparator;

/**
 * Created by 086 on 12/11/2017.
 */
public class CommandsCommand extends Command {

    public CommandsCommand() {
        super("commands", SyntaxChunk.EMPTY);
    }

    @Override
    public void call(String[] args) {
        KamiMod.getInstance().getCommandManager().getCommands().stream().sorted(Comparator.comparing(command -> command.getLabel())).forEach(command ->
            Command.sendChatMessage("&7" + Command.getCommandPrefix() + command.getLabel() + "&r ~ &8" + command.getDescription())
        );
    }
}
