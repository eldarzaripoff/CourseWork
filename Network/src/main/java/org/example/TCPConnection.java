package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

//Данный класс - это одно соединение

public class TCPConnection {
    //Для взаимодействия с сетью необходимы 2 класса - Socket, ServerSocket.
    //ServerSocket - слушает входящее соединение, создавать объект сокета соединения и его отдавать
    //Класс сокет позволяет это соединение устанавливать

    //В одном TCP соединении должен быть:
    //1. Сокет, который с ним связан
    //2. Поток, слушающий входящее соединение
    //3. Поток вывода
    //4. Поток ввода
    private final Socket socket;
    private final Thread listenerThread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private  final TCPConnectionListener eventListener;
    //Первый конструктор рассчитан на то, что сокет придётся создавать внутри
    public TCPConnection(TCPConnectionListener eventListener, String ipAdress, int port) throws IOException {
        this(eventListener, new Socket(ipAdress, port));
    }
    //Второй конструктор рассчитан на то, что сокет уже был создан снаружи
    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        //Поток слушает входящие сообщения
        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!listenerThread.isInterrupted()) {
                        String msg = in.readLine();
                        eventListener.receiveString(TCPConnection.this, msg);
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        listenerThread.start();
    }
    public synchronized void sendMessage(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }
    public synchronized void disconnect() {
        listenerThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection{" +
                "socket=" + socket +
                socket.getInetAddress() +
                socket.getPort() +
                '}';
    }
}
