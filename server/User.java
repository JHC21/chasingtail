package Server;

import java.net.Socket;

public class User{

    private int x,y;
    private boolean isReady;
    private char status;
    private Socket sock;

    User(Socket sock){
        this.sock = sock;
        isReady = false;
    }

    public boolean getisReady(){
        return isReady;
    }
    public Socket getSock() { return this.sock; }
}
