package server.commands.inner;

import server.commands.abstracts.InnerServerCommand;
import shared.serializable.Pair;

public class Login extends InnerServerCommand {

    public Login() {
        super("login", "обеспечить зарегистрированному пользователю вход в систему");
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj) {
        return new Pair<>(true, "Вход");
    }
}
