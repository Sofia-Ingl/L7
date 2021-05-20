package server.commands.abstracts;

public abstract class InnerServerCommand extends Command {

    public InnerServerCommand(String name, String utility) {
        super(name, utility, false, false, true, false);
    }
}
