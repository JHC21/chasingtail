package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {

    public static void main(String[] args) throws SocketException {

        int port;
        Lobby room[];

        if(args.length != 2){
            return;
        } else {
            port = Integer.valueOf(args[1]);
            room = new Lobby[3];
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            Socket listenSocket = serverSocket.accept();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

//https://lktprogrammer.tistory.com/64
