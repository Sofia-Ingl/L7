package server.util;

import server.Server;
import server.commands.abstracts.InnerServerCommand;
import server.commands.abstracts.UserCommand;
import shared.serializable.ClientRequest;
import shared.serializable.Pair;
import shared.serializable.ServerResponse;
import shared.util.CommandExecutionCode;

public class RequestProcessor {

    private final CommandWrapper commandWrapper;

    public RequestProcessor(CommandWrapper commandWrapper) {
        this.commandWrapper = commandWrapper;
    }

    public ServerResponse processTechnicalRequests(ClientRequest request) {
        InnerServerCommand innerServerCommand = commandWrapper.getAllInnerCommands().get(request.getCommand());
        Pair<Boolean, String> commandResult = innerServerCommand.execute(request.getCommandArgument(), request.getCreatedObject());
        CommandExecutionCode code = commandResult.getFirst() ? CommandExecutionCode.SUCCESS : CommandExecutionCode.ERROR;
        return new ServerResponse(code, commandResult.getSecond());
    }

    /*
    public ServerResponse processAuthentication(ClientRequest request) {
        Server.logger.info("Попытка аутентификации пользователя...");
        InnerServerCommand innerServerCommand = commandWrapper.getAllInnerCommands().get(request.getCommand());
        Pair<Boolean, String> commandResult = innerServerCommand.execute(request.getCommandArgument(), request.getCreatedObject());
        CommandExecutionCode code = commandResult.getFirst() ? CommandExecutionCode.SUCCESS : CommandExecutionCode.ERROR;
        if (commandResult.getFirst()) {
            Server.logger.info("Аутентификация успешно пройдена!");
        } else {
            Server.logger.info("Пользователь не прошел аутентификацию");
        }
        return new ServerResponse(code, commandResult.getSecond());
    }
     */

    public ServerResponse processRequest(ClientRequest request) {

        Server.logger.info("Исполняется команда {}", request.getCommand());
        UserCommand userCommand = commandWrapper.getAllCommandsAvailable().get(request.getCommand());
        CommandExecutionCode code = CommandExecutionCode.SUCCESS;
        Pair<Boolean, String> commandResult = userCommand.execute(request.getCommandArgument(), request.getCreatedObject());

        if (!commandResult.getFirst()) {
            code = CommandExecutionCode.ERROR;
        } else {
            synchronized (commandWrapper.getHistory()) {
                commandWrapper.updateHistory(userCommand);
            }
        }

        if (request.getCommand().equals("exit")) {
            code = CommandExecutionCode.EXIT;
        }
        return new ServerResponse(code, commandResult.getSecond());
    }

    public CommandWrapper getCommandWrapper() {
        return commandWrapper;
    }
}
