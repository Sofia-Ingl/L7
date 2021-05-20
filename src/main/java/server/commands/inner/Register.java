package server.commands.inner;

import server.commands.abstracts.InnerServerCommand;
import shared.serializable.Pair;
import shared.serializable.User;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

public class Register extends InnerServerCommand {

    public Register() {
        super("register", "зарегистрировать пользователя");
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String answer;
        try {
            boolean successfullyInserted = getUserHandler().insertUser(user);
            if (!successfullyInserted) {
                throw new AccessDeniedException("Пользователь с таким именем уже есть в базе!");
            }
            return new Pair<>(true, "Пользователь успешно зарегистрирован");
        } catch (SQLException e) {
            answer = "Ошибка при обращении к базе данных, регистрация не осуществлена";
        } catch (AccessDeniedException e) {
            answer = e.getMessage();
        }
        return new Pair<>(false, answer);
    }
}
