package server.commands.user;

import server.commands.abstracts.UserCommand;
import shared.serializable.Pair;
import shared.serializable.User;

import java.sql.SQLException;

public class RemoveAllByScreenwriter extends UserCommand {

    public RemoveAllByScreenwriter() {
        super("remove_all_by_screenwriter", "удалить из коллекции все элементы, значение поля screenwriter которого эквивалентно заданному", false, true, true);
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String response;
        boolean isDeleted;
        try {
            getDatabaseCollectionHandler().removeMoviesByScreenwriter(arg.trim(), user);
            isDeleted = getCollectionStorage().removeByScreenwriter(arg.trim(), user);

            if (!isDeleted) {
                response = "В коллекции не было фильмов сценариста с подобным именем, принадлежащих пользователю";
            } else {
                response = "Фильмы заданного сценариста, принадлежащие пользователю, успешно удалены";
            }
            return new Pair<>(true, response);
        } catch (SQLException e) {
            response = "Ошибка при удалении элементов из базы данных";
        }
        return new Pair<>(false, response);
    }
}
