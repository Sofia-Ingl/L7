package server.util;


import server.Server;
import server.commands.abstracts.Command;
import server.commands.abstracts.UserCommand;
import server.commands.abstracts.InnerServerCommand;
import shared.serializable.ClientRequest;
import shared.serializable.Pair;
import shared.serializable.ServerResponse;
import shared.serializable.User;
import shared.util.CommandExecutionCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс-обертка для набора команд.
 * Привязан к командному режиму и используется в качестве посредника между ним и экземплярами команд.
 */
public class CommandWrapper {

    private final HashMap<String, UserCommand> allCommandsAvailable = new HashMap<>();
    private final HashMap<String, InnerServerCommand> allInnerCommands = new HashMap<>();
    private final ArrayList<Command> lastSixCommands = new ArrayList<>();

    private final ReentrantLock collectionLock = new ReentrantLock();
    private final ReentrantLock historyLock = new ReentrantLock();

    public CommandWrapper(CollectionStorage collectionStorage, DatabaseCollectionHandler databaseCollectionHandler, UserHandler userHandler, UserCommand[] listOfUserCommands, InnerServerCommand[] innerServerCommands) {

        for (UserCommand userCommand : listOfUserCommands) {
            allCommandsAvailable.put(userCommand.getName(), userCommand);
            userCommand.setCommandWrapper(this);
            userCommand.setDatabaseCollectionHandler(databaseCollectionHandler);
            userCommand.setCollectionStorage(collectionStorage);
            userCommand.setUserHandler(userHandler);
        }

        for (InnerServerCommand command : innerServerCommands) {
            allInnerCommands.put(command.getName(), command);
            command.setCommandWrapper(this);
            command.setCollectionStorage(collectionStorage);
            command.setDatabaseCollectionHandler(databaseCollectionHandler);
            command.setUserHandler(userHandler);
        }
    }

    public HashMap<String, UserCommand> getAllCommandsAvailable() {
        return allCommandsAvailable;
    }

    public HashMap<String, InnerServerCommand> getAllInnerCommands() {
        return allInnerCommands;
    }

    public HashMap<String, Pair<String, Pair<Boolean, Boolean>>> mapOfCommandsToSend() {
        HashMap<String, Pair<String, Pair<Boolean, Boolean>>> mapToSend = new HashMap<>();
        UserCommand userCommand;
        for (String commandName : allCommandsAvailable.keySet()) {
            userCommand = allCommandsAvailable.get(commandName);
            mapToSend.put(commandName, new Pair<>(userCommand.getUtility(), new Pair<>(userCommand.isInteractive(), userCommand.hasStringArg())));
        }
        return mapToSend;
    }

    public ServerResponse processRequest(ClientRequest request, boolean isTechnical) {

        Server.logger.info("Исполняется команда {}", request.getCommand());
        Pair<Boolean, String> commandResult = executeCommand(request.getCommand(), isTechnical, request.getCommandArgument(), request.getCreatedObject(), request.getUser());

        CommandExecutionCode code = commandResult.getFirst() ? CommandExecutionCode.SUCCESS : CommandExecutionCode.ERROR;
        if (request.getCommand().equals("exit")) {
            code = CommandExecutionCode.EXIT;
        }

        return new ServerResponse(code, commandResult.getSecond());
    }

    public Pair<Boolean, String> executeCommand(String commandName, boolean isTechnical, String arg, Object obj, User user) {
        Command command = isTechnical ? allInnerCommands.get(commandName) : allCommandsAvailable.get(commandName);
        if (command.isNeedsStorageAccess()) {
            collectionLock.lock();
        }
        if (command.isWrittenToHistory()) {
            historyLock.lock();
        }
        Pair<Boolean, String> result = command.execute(arg, obj, user);
        if (command.isWrittenToHistory() && result.getFirst()) {updateHistory(command);}
        if (historyLock.isLocked()) historyLock.unlock();
        if (collectionLock.isLocked()) collectionLock.unlock();
        return result;
    }

    public void updateHistory(Command command) {
        if (lastSixCommands.size() == 6) {
            lastSixCommands.remove(0);
        }
        lastSixCommands.add(command);
    }

    public ArrayList<Command> getHistory() {
        return lastSixCommands;
    }
}
