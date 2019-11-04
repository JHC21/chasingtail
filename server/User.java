import java.net.Socket;

public class User extends Thread{

    private int x,y;
    private boolean isReady;
    private char status;

    User(int x, int y){
        this.x = x;
        this.y = y;
        isReady = false;
    }

    public boolean getisReady(){
        return isReady;
    }
}
