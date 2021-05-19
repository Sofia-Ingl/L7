package server.commands.inner;

import server.commands.abstracts.InnerServerCommand;
import shared.serializable.Pair;

public class Register extends InnerServerCommand {

    public Register() {
        super("register", "зарегистрировать пользователя");
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj) {
        return new Pair<>(true, "Регистрация");
    }
}
