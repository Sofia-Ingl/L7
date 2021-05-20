package shared.serializable;

import java.io.Serializable;

public class ClientRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String command;
    private final String commandArgument;
    private final Object createdObject;
    private final User user;

    public ClientRequest(String command, String commandArgument, Object createdObject, User user) {
        this.command = command;
        this.commandArgument = commandArgument;
        this.createdObject = createdObject;
        this.user = user;
    }

    public Object getCreatedObject() {
        return createdObject;
    }

    public String getCommand() {
        return command;
    }

    public String getCommandArgument() {
        return commandArgument;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "ClientRequest{" +
                "command='" + command + '\'' +
                ", commandArgument='" + commandArgument + '\'' +
                ", createdObject=" + createdObject.toString() +
                '}';
    }
}
