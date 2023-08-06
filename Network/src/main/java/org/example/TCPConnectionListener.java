package org.example;

public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection tcpConnection);
    void receiveString(TCPConnection tcpConnection, String value);
    void onDisconnect(TCPConnection tcpConnection);
    void onException(TCPConnection tcpConnection, Exception exception);
}
