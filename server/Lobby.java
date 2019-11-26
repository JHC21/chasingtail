package Server;

import java.util.List;

public class Lobby extends Thread{
    private Room r[];
    List<User> list;




    Lobby(){
        r = new Room[3];
        for(int i=0; i<3;i++){
            r[i] = new Room();
        }
    }
    
}
