package server.util;

import server.Server;
import shared.serializable.ClientRequest;
import shared.serializable.ServerResponse;
import shared.util.CommandExecutionCode;
import shared.util.Serialization;

import java.io.IOException;
import java.net.Socket;

public class ClientConnection implements Runnable {

    private final Server server;
    private final Socket socket;

    public ClientConnection(Server generatingServer, Socket client) {
        this.server = generatingServer;
        this.socket = client;
    }

    @Override
    public void run() {

        ClientRequest clientRequest;
        ServerResponse serverResponse;
        boolean technicalInteraction = true;
        boolean listOfCommandsToSend = false;

        try {

            //socket.getOutputStream().write(Serialization.serialize(server.getRequestProcessor().getCommandWrapper().mapOfCommandsToSend()));

            do {

                byte[] b = new byte[65536];
                socket.getInputStream().read(b);
                clientRequest = (ClientRequest) Serialization.deserialize(b);
                if (technicalInteraction) {

                    serverResponse = server.getRequestProcessor().processTechnicalRequests(clientRequest);
                    if (!listOfCommandsToSend) {
                        if (serverResponse.getCode().equals(CommandExecutionCode.SUCCESS)) {
                            listOfCommandsToSend = true;
                            Server.logger.info("Аутентификация пользователя прошла успешно");
                        } else {
                            Server.logger.info("Аутентификация не пройдена");
                        }
                    } else {
                        sendObjectInNewThread(server.getRequestProcessor().getCommandWrapper().mapOfCommandsToSend());
                        Server.logger.info("Список доступных команд отправлен клиенту");
                        technicalInteraction = false;
                    }

                } else {
                    serverResponse = server.getRequestProcessor().processRequest(clientRequest);
                }

                if (clientRequest.getCommand().equals("exit")) {
                    Server.logger.info(serverResponse.getResponseToPrint());
                    server.getRequestProcessor().getCommandWrapper().getAllInnerCommands().get("save").execute("", null, null);
                } else {
                    if (!clientRequest.getCommand().equals("send_available_commands")) {
                        sendObjectInNewThread(serverResponse);
                    }

//                    ServerResponse finalServerResponse = serverResponse;
//                    Thread responseThread = new Thread(() -> {
//                        try {
//                            socket.getOutputStream().write(Serialization.serialize(finalServerResponse));
//                            Server.logger.info("Ответ успешно отправлен");
//                        } catch (IOException e) {
//                            Server.logger.info("Ошибка соединения при отправке ответа клиенту");
//                        }
//                    });
//                    responseThread.start();

                    //socket.getOutputStream().write(Serialization.serialize(serverResponse));
                }

            } while (serverResponse.getCode() != CommandExecutionCode.EXIT);

        } catch (ClassNotFoundException e) {
            Server.logger.warn("Ошика при чтении данных");
        } catch (IOException e) {
            Server.logger.warn("Соединение разорвано");
        } finally {
            try {
                socket.close();
                Server.logger.info("Клиент успешно отключается");
            } catch (IOException e) {
                Server.logger.warn("Ошибка при попытке закрыть соединение с клиентом");
            }
        }

    }


    public void sendObjectInNewThread(Object o) {
        Thread responseThread = new Thread(() -> {
            try {
                socket.getOutputStream().write(Serialization.serialize(o));
                //Server.logger.info("Ответ успешно отправлен");
            } catch (IOException e) {
                Server.logger.info("Ошибка соединения при отправке ответа клиенту");
            }
        });
        responseThread.start();
    }
}
