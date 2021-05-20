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
        ResponseWrapper responseWrapper;
        Thread requestProcessingThread;

        boolean technicalInteraction = true;
        boolean listOfCommandsToSend = false;

        try {

            do {

                byte[] b = new byte[65536];
                socket.getInputStream().read(b);
                clientRequest = (ClientRequest) Serialization.deserialize(b);
                if (technicalInteraction) {

                    responseWrapper = new ResponseWrapper(clientRequest, server.getRequestProcessor(), true);
                    requestProcessingThread = new Thread(responseWrapper);
                    requestProcessingThread.start();
                    requestProcessingThread.join();

                    serverResponse = responseWrapper.getComputedResponse();

                    //serverResponse = server.getRequestProcessor().processTechnicalRequests(clientRequest);

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
                }

            } while (serverResponse.getCode() != CommandExecutionCode.EXIT);

        } catch (ClassNotFoundException e) {
            Server.logger.warn("Ошибка при чтении данных");
        } catch (IOException e) {
            Server.logger.warn("Соединение разорвано");
        } catch (InterruptedException e) {
            Server.logger.warn("Ошибка многопоточности при обслуживании клиентского соединения");
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
            } catch (IOException e) {
                Server.logger.info("Ошибка соединения при отправке ответа клиенту");
            }
        });
        responseThread.start();
    }
}
