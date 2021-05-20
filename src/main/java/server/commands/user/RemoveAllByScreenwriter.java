package server.commands.user;

import server.commands.abstracts.UserCommand;
import shared.serializable.Pair;
import shared.serializable.User;

/**
 * Команда, удаляющая элементы по сценаристу.
 */
public class RemoveAllByScreenwriter extends UserCommand {

    public RemoveAllByScreenwriter() {
        super("remove_all_by_screenwriter", "удалить из коллекции все элементы, значение поля screenwriter которого эквивалентно заданному", false, true);
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String response;
        boolean isDeleted;
        synchronized (getCollectionStorage()) {
            isDeleted = getCollectionStorage().streamRemoveByScreenwriter(arg.trim());
        }
        if (!isDeleted) {
            response = "В коллекции не было фильмов сценариста с подобным именем.";
        } else {
            response = "Фильмы заданного сценариста успешно удалены";
        }

        return new Pair<>(true, response);

    }
}
