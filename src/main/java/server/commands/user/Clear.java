package server.commands.user;

import server.commands.abstracts.UserCommand;
import shared.serializable.Pair;
import shared.serializable.User;

import java.sql.SQLException;

public class Clear extends UserCommand {

    public Clear() {
        super("clear", "очистить коллекцию", false, false, true);
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String errorString;
        try {

            getDatabaseCollectionHandler().deleteAllMoviesBelongToUser(user);
            getCollectionStorage().deleteElementsByUser(user);

            return new Pair<>(true, "Все объекты, принадлежащие пользователю, удалены из коллекции");
        } catch (SQLException e) {
            errorString = "Произошла ошибка при удалении фильмов, принадлежащих пользователю, из базы данных";
        }
        return new Pair<>(false, errorString);
    }
}
