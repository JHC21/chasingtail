public class Lobby {
    private char num;
    User masterClient;
    User guest[];
    boolean isStart;

    Lobby(User masterClient){
        num = 0;
        this.masterClient = masterClient;
        guest = new User[3];
        isStart = false;
    }

    public int addClient(User newUser){ // return client number
        if(num < 4){
            guest[num] = newUser;
            num++;
            return num;
        } else {
            return -1;
        }
    }

    public boolean isReady(){
        if(num == 0){
            return false;
        } else {
          for(int i=0; i<num;i++){
            if(!guest[i].getisReady())
                return false;
          }
          return true;
        }
    }
}
