package server.commands.user;


import server.commands.abstracts.UserCommand;
import shared.data.Movie;
import shared.serializable.Pair;
import shared.serializable.User;

import java.sql.SQLException;


public class RemoveGreater extends UserCommand {

    public RemoveGreater() {
        super("remove_greater", "удалить из коллекции все элементы, превышающие заданный", true, false, true);
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String response;
        Movie movieToCompareWith = (Movie) obj;
        boolean isDeleted;

        try {
            getDatabaseCollectionHandler().removeGreaterMovies(movieToCompareWith, user, getCollectionStorage());
            isDeleted = getCollectionStorage().removeGreater(movieToCompareWith, user);

            if (!isDeleted) {
                response = "В коллекции не было принадлежащих пользователю элементов, которые превосходят заданный";
            } else {
                response = "Принадлежащие пользователю элементы больше заданного успешно удалены из коллекции";
            }
            return new Pair<>(true, response);
        } catch (SQLException e) {
            response = "Возникла ошибка при обращении к базе данных";
        }
        return new Pair<>(false, response);
    }
}

