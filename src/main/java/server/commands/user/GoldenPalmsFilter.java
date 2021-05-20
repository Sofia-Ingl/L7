package server.commands.user;

import server.commands.abstracts.UserCommand;
import shared.serializable.Pair;
import shared.serializable.User;

public class GoldenPalmsFilter extends UserCommand {

    public GoldenPalmsFilter() {
        super("filter_greater_than_golden_palm_count","вывести элементы, значение поля goldenPalmCount которых больше заданного", false, true);
    }

    @Override
    public Pair<Boolean, String> execute(String arg, Object obj, User user) {

        String errorString;

        try {
            if (arg.isEmpty()) {
                throw new IllegalArgumentException("Неверное число аргументов при использовании команды " + this.getName());
            }
            if (!arg.trim().matches("\\d+")) {
                throw new IllegalArgumentException("Неправильный тип аргумента к команде!");
            } else {
                long goldenPalms = Long.parseLong(arg.trim());

                String result;
                synchronized (getCollectionStorage()) {
                    result = getCollectionStorage().streamReturnGreaterThanGoldenPalms(goldenPalms);
                }
                return new Pair<>(true, result);

            }

        } catch (NumberFormatException e) {
            errorString = "Неправильно введен аргумент!";
        } catch (IllegalArgumentException e) {
            errorString = e.getMessage();
        } catch (NullPointerException e) {
            errorString = "Команда не привязана к хранилищу коллекции!";
        }
        return new Pair<>(false, errorString);
    }
}

