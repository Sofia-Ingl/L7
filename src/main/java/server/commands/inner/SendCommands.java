package server.commands.inner;

import server.commands.abstracts.InnerServerCommand;
import shared.serializable.Pair;
import shared.serializable.User;

public class SendCommands extends InnerServerCommand {

    public SendCommands() {
        super("send_available_commands", "Послать пользователю список доступных команд");
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {
        return new Pair<>(true, "Высылается список команд");
    }
}
