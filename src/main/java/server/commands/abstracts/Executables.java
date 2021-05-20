package server.commands.abstracts;

import shared.serializable.Pair;
import shared.serializable.User;

public interface Executables {
    Pair<Boolean, String> execute(String arg, Object obj, User user);
}
