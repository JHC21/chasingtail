package Server;

import java.net.Socket;

public class Room extends Thread{
    private char clntNum;;
    User clnt[];
    boolean isStart;

    Map field;

    Room(){
        clntNum = 0;
        clnt = new User[4];
        isStart = false;
        for(int i=0; i<4; i++){
            clnt[i] = null;
        }
    }

    public void setup(User masterClient){
        clnt[0] = masterClient;
        clntNum++;
    }

    public boolean addClient(Socket userSock){
        for(int i=0; i<clntNum; i++){
            if(clnt[i] == null){
                clnt[i] = new User(userSock);
                clntNum++;
                return true;
            }
        }
        return false;
    }

    public boolean removeClient(Socket userSock){
        for(int i=0; i<clntNum; i++){
            if(userSock == clnt[i].getSock()){
                clnt[i] = null;
                for(int j=i; j<clntNum-1; j++){
                    clnt[j] = clnt[j+1];
                }
                clntNum--;
                return true;
            }
        }
        return false;
    }

    public boolean isRunnable(){
        for(int i=0; i<clntNum; i++){
            if(!clnt[i].getisReady()){
                return false;
            }
        }
        return true;
    }

    public void start(){
        field = new Map(5000,5000);

    }
}

class Map{
    private double sizeX, sizeY;

    Map(double sizeX, double sizeY){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public double getSizeX(){
        return sizeX;
    }

    public double getSizeY(){
        return sizeY;
    }
}
