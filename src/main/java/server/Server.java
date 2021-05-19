package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.commands.abstracts.InnerServerCommand;
import server.commands.abstracts.UserCommand;
import server.commands.inner.Login;
import server.commands.inner.Register;
import server.commands.inner.Save;
import server.commands.inner.SendCommands;
import server.commands.user.*;
import server.util.*;
import shared.serializable.Pair;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.Console;
import java.io.IOException;
import java.net.*;
import java.nio.channels.IllegalBlockingModeException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    public final static Logger logger = LoggerFactory.getLogger(Server.class);

    private int port;
    private ServerSocket serverSocket;
    private final RequestProcessor requestProcessor;
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);


    public static void main(String[] args) {

//        try {
//            Signal s = new Signal("TSTP");
//            Signal.handle(s, SignalHandler.SIG_IGN);
//        } catch (IllegalArgumentException ignored) {}

        Pair<Pair<String, String>, Integer> databaseAddrUserAndPort = processArguments(args);
        DatabaseManager databaseManager = new DatabaseManager(databaseAddrUserAndPort.getFirst().getFirst(), databaseAddrUserAndPort.getFirst().getSecond(), readDatabasePass());
        UserHandler userHandler = new UserHandler(databaseManager);
        DatabaseCollectionHandler databaseCollectionHandler = new DatabaseCollectionHandler(databaseManager, userHandler);
        CollectionStorage collectionStorage = new CollectionStorage(databaseCollectionHandler);
        collectionStorage.loadCollectionFromDatabase();

        //CollectionStorage collectionStorage = new CollectionStorage();
        //collectionStorage.loadCollection(databaseAddrAndPort.getFirst());
        InnerServerCommand[] innerServerCommands = {new Save(), new Login(), new Register(), new SendCommands()};
        UserCommand[] userCommands = {new Help(), new History(), new Clear(), new Add(), new Show(), new ExecuteScript(),
                new GoldenPalmsFilter(), new Info(), new AddIfMax(), new PrintAscending(), new RemoveAllByScreenwriter(),
                new RemoveById(), new RemoveGreater(), new Update(), new Exit()};

        Server server = new Server(databaseAddrUserAndPort.getSecond(), new RequestProcessor(new CommandWrapper(collectionStorage, userCommands, innerServerCommands)));
        addShutdownHook(server);

        server.run();
    }


    Server(int port, RequestProcessor requestProcessor) {
        this.port = port;
        this.requestProcessor = requestProcessor;

    }

    @Override
    public void run() {

        logger.info("Сервер запускается...");
        createSocketFactory();
        boolean noServerExitCode = true;

        Thread exitManager = new Thread(new ExitManager());
        exitManager.start();

        while (noServerExitCode) {

            try {

                Socket socket = establishClientConnection();

                //fixedThreadPool.execute(new ClientConnection(this, socket));
                fixedThreadPool.submit(new ClientConnection(this, socket));

//                Thread clientConnection = new Thread(new ClientConnection(this, socket));
//                clientConnection.start();

            } catch (ConnectException e) {
                logger.info(e.getMessage());
                requestProcessor.getCommandWrapper().getAllInnerCommands().get("save").execute("", null);
                noServerExitCode = false;
            } catch (IllegalThreadStateException e) {
                logger.info("Ошибка при запуске потока для обслуживания клиентского соединения");
            }
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
                logger.info("Сервер прекращает работу");
            } catch (IOException e) {
                logger.warn("Сервер прекращает работу с ошибкой");
            }
        }


    }

    private void createSocketFactory() {

        try {
            serverSocket = new ServerSocket(port);
            logger.info("Фабрика сокетов создана");
        } catch (BindException e) {
            logger.warn("Порт недоступен, следует указать другой");
            emergencyExit();
        } catch (IOException | IllegalArgumentException e) {
            logger.warn("Ошибка при инициализации фабрики сокетов");
            emergencyExit();
        }
    }

    private Socket establishClientConnection() throws ConnectException {
        try {
            logger.info("Прослушивается порт {}", port);
            Socket clientSocket = serverSocket.accept();
            logger.info("Соединение с клиентом, находящимся по адресу {}, установлено", clientSocket.getRemoteSocketAddress().toString());
            return clientSocket;
        } catch (IOException | IllegalBlockingModeException | IllegalArgumentException e) {
            throw new ConnectException("Ошибка соединения");
        }
    }


/*
    private void handleRequests(Socket socket) {

        ClientRequest clientRequest;
        ServerResponse serverResponse;

        try {

            do {

                byte[] b = new byte[66666];
                socket.getInputStream().read(b);
                clientRequest = (ClientRequest) Serialization.deserialize(b);
                serverResponse = requestProcessor.processRequest(clientRequest);
                if (clientRequest.getCommand().equals("exit")) {
                    logger.info(serverResponse.getResponseToPrint());
                    requestProcessor.getCommandWrapper().getAllInnerCommands().get("save").execute("", null);
                } else {
                    socket.getOutputStream().write(Serialization.serialize(serverResponse));
                }


            } while (serverResponse.getCode() != CommandExecutionCode.EXIT);

        } catch (IOException | ClassNotFoundException e) {

            logger.warn("Соединение разорвано");
        }

    }
*/

    private static Pair<Pair<String, String>, Integer> processArguments(String[] args) {
        try {

            if (args.length == 4) {
                String databaseHost = args[0];
                String databaseName = args[1];
                String databaseUsername = args[2];
                String databaseAddress = "jdbc:postgresql://" + databaseHost + ":5432/" + databaseName;
                int port = Integer.parseInt(args[3]);

                if (port <= 1024) {
                    throw new IllegalArgumentException("Указан недопустимый порт");
                }
                return new Pair<>(new Pair<>(databaseAddress, databaseUsername), port);

            } else {
                throw new IllegalArgumentException("При запуске jar некорректно указаны аргументы (правильный вариант: java -jar <имя jar> <хост бд> <имя бд> <имя пользователя> <порт сервера>");
            }
        } catch (NumberFormatException e) {
            logger.error("Порт должен быть целым числом");
            emergencyExit();
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            emergencyExit();
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при расшифровке аргументов командной строки");
            emergencyExit();
        }
        return new Pair<>(new Pair<>("", ""), 8234);
    }

    private static String readDatabasePass() {
        System.out.println("Введите пароль от учетной записи в бд:");
        System.out.print(">");
        Console console = System.console();
        if (console != null) {
            return String.valueOf(console.readPassword()).trim();
        } else {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) return scanner.nextLine().trim();
        }
        return "";
    }

    private static void emergencyExit() {
        logger.error("Осуществляется аварийный выход из сервера");
        System.exit(1);
    }

    private static void addShutdownHook(Server server) {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    logger.info("Выполняются действия после сигнала о прекращении работы сервера");
                    server.getRequestProcessor().getCommandWrapper().getAllInnerCommands().get("save").execute("", null);
                }
                ));
    }

    public RequestProcessor getRequestProcessor() {
        return requestProcessor;
    }

}