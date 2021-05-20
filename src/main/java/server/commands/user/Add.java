package server.commands.user;

import server.commands.abstracts.UserCommand;
import server.util.DatabaseCollectionHandler;
import shared.data.Movie;
import shared.serializable.Pair;
import shared.serializable.User;

import java.sql.SQLException;

public class Add extends UserCommand {

    public Add() {
        super("add", "добавить новый элемент в коллекцию", true, false, true);
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String errorString;

        try {

            Movie movie = getDatabaseCollectionHandler().addNewMovie((Movie) obj, user);
            getCollectionStorage().addMovie(movie);

            return new Pair<>(true, "Элемент добавлен в коллекцию!");

        } catch (SQLException e) {
            errorString = "Возникла ошибка при добавлении фильма в базу данных";
        }

        return new Pair<>(false, errorString);
    }
}
