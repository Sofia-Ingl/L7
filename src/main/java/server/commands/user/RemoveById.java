package server.commands.user;

import server.commands.abstracts.UserCommand;
import shared.serializable.Pair;
import shared.serializable.User;

import java.sql.SQLException;

/**
 * Команда, удаляющая элементы по айди.
 */
public class RemoveById extends UserCommand {

    public RemoveById() {
        super("remove_by_id", "удалить элемент из коллекции по его id", false, true, true);
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String response;
        try {
            if (!arg.trim().matches("\\d+")) {
                throw new IllegalArgumentException("Неправильный тип аргумента к команде!");
            }

            int id = Integer.parseInt(arg.trim());
            getDatabaseCollectionHandler().deleteMovieById(id, user);
            boolean wasInCollection = getCollectionStorage().deleteElementForId(id, user);

            if (!wasInCollection) {
                response = "Среди принадлежащих пользователю элементов нет фильма с таким значением id!";
            } else {
                response = "Элемент успешно удален";
            }

            return new Pair<>(true, response);

        } catch (NumberFormatException e) {
            response = "Неправильно введен аргумент!";
        } catch (IllegalArgumentException e) {
            response = e.getMessage();
        } catch (SQLException e) {
            response = "Ошибка при взаимодействии с базой данных";
        }
        return new Pair<>(false, response);
    }
}
