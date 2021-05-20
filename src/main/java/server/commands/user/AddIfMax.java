package server.commands.user;

import server.commands.abstracts.UserCommand;
import shared.data.Movie;
import shared.serializable.Pair;
import shared.serializable.User;

import java.sql.SQLException;

public class AddIfMax extends UserCommand {

    public AddIfMax() {
        super("add_if_max", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции", true, false, true);
    }


    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String response;
        Movie newMovie = (Movie) obj;

        try {

            if (getCollectionStorage().getMaxMovie() == null || getCollectionStorage().getMaxMovie().compareTo(newMovie) < 0) {

                newMovie = getDatabaseCollectionHandler().addNewMovie(newMovie, user);
                getCollectionStorage().addMovie(newMovie);
                response = "Предложенный вами фильм превосходит максимальный в коллекции => он будет добавлен.";
            } else {
                response = "Предложенный фильм максимальным не является => он не будет добавлен";
            }
            return new Pair<>(true, response);

        } catch (SQLException e) {
            response = "Произошла ошибка при добавлении фильма в базу данных";
        }
        return new Pair<>(false, response);
    }
}
