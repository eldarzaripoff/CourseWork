package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }
    private final List<TCPConnection> connections = new ArrayList<>();
    private ChatServer() {
        System.out.println("Server is running...");
        //В бесконечном цикле слушаем порт
        try(ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try{
                    //Висим в методе accept, который  при успешном входящем соединении возвращает объект сокета, который с ним связан
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception " + e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client is connected " + tcpConnection);

    }

    @Override
    public synchronized void receiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client is disconnected " + tcpConnection);

    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception exception) {
        System.out.println("TCPConnection exception " +  exception);
    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        for (TCPConnection connection: connections) {
            connection.sendMessage(value);
        }
    }
}
