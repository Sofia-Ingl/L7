package server.commands.inner;

import server.commands.abstracts.InnerServerCommand;
import server.util.DatabaseCollectionHandler;
import shared.serializable.Pair;
import shared.serializable.User;

import javax.security.sasl.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.security.AccessControlException;
import java.sql.SQLException;

public class Login extends InnerServerCommand {

    public Login() {
        super("login", "обеспечить зарегистрированному пользователю вход в систему");
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String answer;
        try {
            if (getUserHandler().findUserByNameAndPass(user)) {
                answer = "Пользователь был найден в базе данных, пароль верный";
            } else {
                throw new AccessDeniedException("Неверно указано имя или пароль");
            }
            return new Pair<>(true, answer);
        } catch (SQLException e) {
            answer = "Произошла ошибка при обращении к базе данных";
        } catch (AccessDeniedException e) {
            answer = e.getMessage();
        }
        return new Pair<>(false, answer);
    }
}
