package client.util;


import server.util.RequestProcessor;
import shared.serializable.ClientRequest;
import shared.serializable.User;


public class Authorization {

    private final String login = "login";
    private final String register = "register";
    private Interaction interaction;

    public Authorization(Interaction interaction) {
        this.interaction = interaction;
    }

    public ClientRequest logInSystem() {
        try {
            String command = (alreadyRegistered()) ? login : register;
            return new ClientRequest(command, "", new User(getLogin(), getPassword()));
        } catch (Exception e) {
            authFailure();
        }
        return null;
    }

    private boolean alreadyRegistered() {
        try {
            interaction.printlnMessage("Есть ли у вас учетная запись в системе? (yes/no)");
            String answer;
            while (true) {
                interaction.printMessage(">");
                answer = interaction.readLine().trim();
                if (answer.toLowerCase().equals("yes")) return true;
                if (answer.toLowerCase().equals("no")) return false;
                interaction.printlnMessage("Ответ не распознан. Пожалуйста, выберите ответ из предложенных!");
            }
        } catch (Exception e) {
            authFailure();
        }
        return false;
    }

    private String getLogin() {
        String login = "Ffuuu";
        try {
            interaction.printlnMessage("Введите логин:");
            boolean incorrectInput = true;
            while (incorrectInput) {
                interaction.printMessage(">");
                login = interaction.readLine().trim();
                if (login.isEmpty()) {
                    interaction.printlnMessage("Логин не должен быть пустым!");
                }
                else {
                    incorrectInput = false;
                }
            }
        } catch (Exception e) {
            authFailure();
        }
        return login;
    }

    private String getPassword() {
        String password = "oh my...";
        try {
            interaction.printlnMessage("Введите пароль:");
            boolean incorrectInput = true;
            while (incorrectInput) {
                interaction.printMessage(">");
                password = interaction.readLine().trim();
                if (password.isEmpty()) {
                    interaction.printlnMessage("Пароль не должен быть пустым!");
                }
                else {
                    incorrectInput = false;
                }
            }
            return password;
        } catch (Exception e) {
            authFailure();
        }
        return password;

    }

    private void authFailure() {
        System.out.println("Возникла непредвиденная ошибка при запросе учетной записи пользователя");
        System.exit(1);
    }
}
