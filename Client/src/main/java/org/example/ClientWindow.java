package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String ipAdress = "46.191.225.204";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientWindow());
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Name");
    private final JTextField fieldInput = new JTextField();
    TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);
        setVisible(true);
        try {
            connection = new TCPConnection(this, ipAdress, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e.getMessage());
            // Добавляем сообщение об ошибке в лог
            printMsg("Failed to establish connection to " + ipAdress + ":" + PORT);
            // Завершаем приложение
            System.exit(1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendMessage(fieldNickName.getText() + ": " + msg);

    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection is ready...");

    }

    @Override
    public void receiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection closed...");

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        printMsg("Connection exception: " + exception);

    }
    private synchronized void printMsg(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });

    }
}
