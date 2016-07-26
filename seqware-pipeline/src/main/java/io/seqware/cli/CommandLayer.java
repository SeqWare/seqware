package io.seqware.cli;

import com.google.common.base.Joiner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.seqware.cli.Main.invalid;
import static io.seqware.cli.Main.isHelp;
import static io.seqware.cli.Main.out;

/**
 * Describes a command with nested commands underneath it.
 */
abstract class CommandLayer implements CommandLeaf{

    CommandLayer(CommandLeaf ... nestedCommands){
        for(CommandLeaf leaf : nestedCommands){
            commands.put(leaf.getCommand(), leaf);
        }
    }

    LinkedHashMap<String, CommandLeaf> commands = new LinkedHashMap<>();

    public LinkedHashMap<String, CommandLeaf> getCommands() {
        return commands;
    }

    public void setCommands(LinkedHashMap<String, CommandLeaf> commands) {
        this.commands = commands;
    }

    public String bashOpts(){
        return Joiner.on(' ').join(getCommands().keySet());
    }

    @Override
    public void invoke(List<String> args){

        int keyLength = 0;
        int valueLength = 0;
        for(Map.Entry<String, CommandLeaf> entry : this.commands.entrySet()){
            keyLength = Math.max(keyLength, entry.getKey().length());
            valueLength = Math.max(valueLength, entry.getValue().displayOneLineDescription().length());
        }

        // get some whitespace
        keyLength += 2;
        valueLength += 2;

        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware "+getCommand()+" [--help]");
            out("       seqware "+getCommand()+" <sub-command> [--help]");
            out("");
            out("Description:");
            out("  " + displayOneLineDescription());
            out("");
            out("Sub-commands:");
            for(Map.Entry<String, CommandLeaf> entry : this.commands.entrySet()){
                out("    %-"+keyLength+"s%-"+valueLength+"s", entry.getKey(), entry.getValue().displayOneLineDescription());
            }
            out("");
        } else {
            String cmd = args.remove(0);
            if (null != cmd && this.commands.containsKey(cmd)){
                this.commands.get(cmd).invoke(args);
            } else if (null != cmd){
                invalid(getCommand(), cmd);
            }
        }
    }
}
