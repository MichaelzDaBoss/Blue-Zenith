package cat.module;

import cat.command.Command;
import cat.module.value.Value;
import cat.module.value.types.*;

import java.util.regex.Pattern;

public class ModuleCommand extends Command {
    private final Module parent;
    public ModuleCommand(Module parent, String... pref) {
        super(parent.getName(), "Auto-generated command.", ".module valuename valueparameter", pref);
        this.parent = parent;
    }
    @Override
    public void execute(String[] args){
        if(args.length > 1){
            Value<?> value = parent.getValue(args[1]);
            if(value == null){
                chat("Invalid value!");
                return;
            }
            if (value instanceof BooleanValue) {
                BooleanValue v = (BooleanValue) value;
                v.next();
                chat("Set "+v.name+" to "+v.get());
                changedSound();
            }else if(args.length > 2){
                if(value instanceof ModeValue) {
                    ModeValue v = (ModeValue) value;
                    String result = v.find(args[2]);
                    if(result != null) {
                        v.set(result);
                        changedSound();
                    } else {
                        chat("Illegal argument: " + args[2] + " is not in the " + value.name + " possible values range.");
                    }
                }else if(value instanceof StringValue){
                    StringValue v = (StringValue) value;
                    v.set(args[2]);
                    chat("Set "+v.name+" to "+v.get());
                    changedSound();
                }else if(!Pattern.matches("[a-zA-Z]+", args[2])){
                    if(value instanceof FloatValue){
                        FloatValue v = (FloatValue) value;
                        v.set(Float.parseFloat(args[2]));
                        chat("Set "+v.name+" to "+v.get());
                        changedSound();
                    }else if(value instanceof IntegerValue){
                        IntegerValue v = (IntegerValue) value;
                        v.set(Integer.parseInt(args[2]));
                        chat("Set "+v.name+" to "+v.get());
                        changedSound();
                    }
                }else{
                    chat("Syntax: $m <setting> <value>".replace("$m", name.toLowerCase()));
                }
            }else{
                chat("Syntax: $m <setting> <value>".replace("$m", name.toLowerCase()));
            }
        }else{
            chat("Syntax: $m <setting> <value>".replace("$m", name.toLowerCase()));
        }
    }
}
