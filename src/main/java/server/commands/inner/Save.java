package server.commands.inner;

import server.Server;
import server.commands.abstracts.InnerServerCommand;
import server.util.FileHelper;
import shared.serializable.Pair;
import shared.serializable.User;

public class Save extends InnerServerCommand {

    public Save() {
        super("save", "сохранить коллекцию в файл");
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        boolean result = true;
        /*
        synchronized (getCollectionStorage()) {
            result = FileHelper.fileOutputLoader(getCollectionStorage().getCollection(), getCollectionStorage().getPath());
        }

        Server.logger.info("Выполняется сохранение коллекции в файл");
        */
        return new Pair<>(result, "");
    }
}
