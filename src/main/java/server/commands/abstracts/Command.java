package server.commands.abstracts;
import server.util.CollectionStorage;
import server.util.CommandWrapper;
import server.util.DatabaseCollectionHandler;
import server.util.UserHandler;

public abstract class Command implements Executables {
    private final String name;
    private final String utility;
    private final boolean isInteractive;
    private final boolean hasStringArg;

    private final boolean needsStorageAccess;
    private final boolean isWrittenToHistory;
    private CommandWrapper commandWrapper = null;
    private CollectionStorage collectionStorage = null;
    private UserHandler userHandler = null;
    private DatabaseCollectionHandler databaseCollectionHandler = null;

    public Command(String name, String utility, boolean isInteractive, boolean hasStringArg, boolean needsCollectionAccess, boolean isWrittenToHistory) {
        this.name = name;
        this.utility = utility;
        this.isInteractive = isInteractive;
        this.hasStringArg = hasStringArg;
        this.needsStorageAccess = needsCollectionAccess;
        this.isWrittenToHistory = isWrittenToHistory;
    }

    public String getName() {
        return name;
    }

    public String getUtility() {
        return utility;
    }

    public void setCommandWrapper(CommandWrapper commandWrapper) {
        this.commandWrapper = commandWrapper;
    }

    public CommandWrapper getCommandWrapper() {
        return commandWrapper;
    }

    public CollectionStorage getCollectionStorage() {
        return collectionStorage;
    }

    public void setCollectionStorage(CollectionStorage collectionStorage) {
        this.collectionStorage = collectionStorage;
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public void setUserHandler(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    public DatabaseCollectionHandler getDatabaseCollectionHandler() {
        return databaseCollectionHandler;
    }

    public void setDatabaseCollectionHandler(DatabaseCollectionHandler databaseCollectionHandler) {
        this.databaseCollectionHandler = databaseCollectionHandler;
    }

    public boolean isInteractive() {
        return isInteractive;
    }

    public boolean hasStringArg() {
        return hasStringArg;
    }

    public boolean isNeedsStorageAccess() {
        return needsStorageAccess;
    }

    public boolean isWrittenToHistory() {
        return isWrittenToHistory;
    }
}

