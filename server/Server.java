package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class MainServer {
    private int port;
    private ServerSocket servSock = null;

    public MainServer(int port){
        this.port = port;
        this.openServSock();
    }

    private void openServSock(){
        try{
            servSock = new ServerSocket(this.port);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Socket acceptSock(){
        Socket clntSock = null;

        try{
            clntSock = this.servSock.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clntSock;
    }



    public static void main(String[] args) throws SocketException {
        MainServer serv = new MainServer(9000);
        Lobby l = new Lobby();

        l.run();

        while(true){
            Socket client = serv.acceptSock();


        }
    }
}
