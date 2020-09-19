package me.zopac.freemanatee.command.syntax.parsers;


import me.zopac.freemanatee.command.syntax.SyntaxChunk;
import me.zopac.freemanatee.command.syntax.SyntaxParser;

public abstract class AbstractParser implements SyntaxParser {

    @Override
    public abstract String getChunk(SyntaxChunk[] chunks, SyntaxChunk thisChunk, String[] values, String chunkValue);
    protected String getDefaultChunk(SyntaxChunk chunk){
        return (chunk.isHeadless() ? "" : chunk.getHead()) + (chunk.isNecessary() ? "<" : "[") + chunk.getType() + (chunk.isNecessary() ? ">" : "]");
    }

}
