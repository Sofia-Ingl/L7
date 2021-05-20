package server.commands.user;

import server.commands.abstracts.UserCommand;
import shared.data.Movie;
import shared.serializable.Pair;
import shared.serializable.User;

import java.sql.SQLException;

/**
 * Команда, обновляющая значение элемента с заданным айди.
 */
public class Update extends UserCommand {

    public Update() {
        super("update", "обновить значение элемента коллекции, id которого равен заданному", true, true, true);
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String response;
        try {
            if (!arg.trim().matches("\\d+")) {
                throw new IllegalArgumentException("Неправильный тип аргумента к команде!");
            } else {
                int id = Integer.parseInt(arg.trim());
                Movie m2 = (Movie) obj;

                getDatabaseCollectionHandler().updateMovieById(id, m2, user);

                Movie m1 = getCollectionStorage().getByUserAndId(id, user);
                if (m1 != null) {
                    m1.setName(m2.getName());
                    m1.setCoordinates(m2.getCoordinates());
                    m1.setGenre(m2.getGenre());
                    m1.setOscarsCount(m2.getOscarsCount());
                    m1.setGoldenPalmCount(m2.getGoldenPalmCount());
                    m1.setTagline(m2.getTagline());
                    m1.setScreenwriter(m2.getScreenwriter());
                    response = "Элемент успешно обновлен";
                } else {
                    response = "Нет принадлежащего пользователю элемента с таким значением id!";
                }

            }

            return new Pair<>(true, response);

        } catch (NumberFormatException e) {
            response = "Неправильно введен аргумент!";
        } catch (IllegalArgumentException e) {
            response = e.getMessage();
        } catch (SQLException e) {
            response = "Произошла ошибка при обращении к базе данных";
        }
        return new Pair<>(false, response);
    }
}