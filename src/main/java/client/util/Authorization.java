package client.util;


import shared.serializable.ClientRequest;
import shared.serializable.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Authorization {

    private final Interaction interaction;

    public Authorization(Interaction interaction) {
        this.interaction = interaction;
    }

    public ClientRequest logInSystem() {
        try {
            String register = "register";
            String login = "login";
            String command = (alreadyRegistered()) ? login : register;
            User currentUser = new User(getLogin(), getEncodedPassword(getPassword()));
            interaction.setUser(currentUser);
            return new ClientRequest(command, "", null, currentUser);
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
        String login = "L";
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

    private String getEncodedPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder builder = new StringBuilder();
            String s;
            for (byte b : bytes) {
                s = Integer.toHexString(b);
                try {
                    builder.append(s.substring(s.length() - 2));
                } catch (IndexOutOfBoundsException e) {
                    builder.append("0").append(s);
                }
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ignored) {}
        return password;
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
