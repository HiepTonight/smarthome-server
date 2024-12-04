//package com.hieptran.smarthome_server.Service;
//
//import com.corundumstudio.socketio.SocketIOServer;
//import com.corundumstudio.socketio.listener.ConnectListener;
//import com.corundumstudio.socketio.listener.DataListener;
//import com.corundumstudio.socketio.listener.DisconnectListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SocketIOEvents {
//    public static final String MESSAGE_FROM_SERVER = "messageFromServer";
//
//    private final SocketIOServer server;
//
//    @Autowired
//    public SocketIOEvents(SocketIOServer server) {
//        this.server = server;
//        server.start();
//        server.addConnectListener(onConnected());
//        server.addDisconnectListener(onDisconnected());
//        server.addEventListener("send_message", Message.class, onChatReceived());
//    }
//
//    private DataListener<Message> onChatReceived() {
//        return (client, message, ackSender) -> {
//            System.out.println("Received message: " + message);
//            server.getBroadcastOperations().sendEvent(MESSAGE_FROM_SERVER, message.chatMessage);
//        };
//    }
//
//    private DisconnectListener onDisconnected() {
//        return client -> {
//            System.out.println("Client disconnected: " + client.getSessionId());
//            server.getBroadcastOperations().sendEvent(MESSAGE_FROM_SERVER, "Client disconnected: " + client.getSessionId());
//        };
//    }
//
//    private ConnectListener onConnected() {
//        return client -> {
//            System.out.println("Client connected: " + client.getSessionId());
//            client.joinRoom("room");
//            server.getBroadcastOperations().sendEvent(MESSAGE_FROM_SERVER, "Client connected: " + client.getSessionId());
//        };
//    }
//
//    record Message(String id, String chatMessage, String timestamp) {
//    }
//}
