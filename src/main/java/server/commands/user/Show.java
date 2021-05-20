package server.commands.user;

import server.commands.abstracts.UserCommand;
import shared.data.Movie;
import shared.serializable.Pair;
import shared.serializable.User;

public class Show extends UserCommand {

    public Show() {
        super("show", " вывести в стандартный поток вывода все элементы коллекции в строковом представлении", false, false, true);
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        StringBuilder responseString;

        if (getCollectionStorage().getCollection().isEmpty()) {
            responseString = new StringBuilder("Коллекция пуста!");
        } else {
            responseString = new StringBuilder("В настоящий момент в коллекции находятся следующие элементы\n");
            for (Movie movie : getCollectionStorage().getSortedCollection().getSecond()) {
                responseString.append(movie).append("\n");
            }
        }
        return new Pair<>(true, responseString.toString());

    }

}
