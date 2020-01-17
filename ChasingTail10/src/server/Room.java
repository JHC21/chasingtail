 package server;

import java.util.Vector;
import java.util.Random;

public class Room {
	Vector<User> roomUserList;
	Vector<String> updateData;
	Vector<int[]> userLoc;
	Random rand;
	
	Room(){
		roomUserList = new Vector<User>();
		updateData = new Vector<String>();
		userLoc = new Vector<int[]>();
		rand = new Random();
	}
	
	boolean addUser(User user) {
		if(roomUserList.size() < 4) {
			roomUserList.add(user);
			
			int[] newUserLoc = new int[4];
			newUserLoc[0] = user.id;
			newUserLoc[1] = rand.nextInt(500); // initial x
			newUserLoc[2] = rand.nextInt(500); // initial y
			newUserLoc[3] = 1; // canSend (0=false, 1=true)
			
			userLoc.add(newUserLoc);
			updateData.add("");
			
			return true;
		}
		return false;
	}
	
	synchronized void addData(int id,String getString) {
		System.out.println(id+" " + getString);
		String getData[] = getString.split(" ");
		for(int i=0; i<userLoc.size(); i++) {
			if(userLoc.get(i)[0] == id) {
				updateData.set(i, getData[1]);
				
				if(!getData[1].equals("Enter")) {
					userLoc.get(i)[1] = Integer.parseInt(getData[2]);	
					userLoc.get(i)[2] = Integer.parseInt(getData[3]);
				} 				
				userLoc.get(i)[3] = 1;
				break;
			}
		}		
		
		if(canSend()) {
			sendLocs();
		}
	}
	
	boolean canSend() {
		if(userLoc.size() == 0) {
			updateData.clear();
			return false;
		}
		
		for(int i=0; i<userLoc.size(); i++) {
			if(userLoc.get(i)[3] == 0) {
				return false;
			}
		}
		return true;
	}

	synchronized void sendLocs() {
		int size = userLoc.size();
		String sendString = " " + (userLoc.size()) + " ";
		for(int i=0; i<size; i++) {
			sendString += userLoc.get(i)[0] + " " + updateData.get(i) +" " + userLoc.get(i)[1] + " " + userLoc.get(i)[2] + " ";
			userLoc.get(i)[3] = 0;
		}
		
		for(User user:roomUserList) {
			String userSendString = user.id + sendString;
			user.write(userSendString);
		}
		
		for(int i=0; i<size; i++) {
			if(!(updateData.get(i).equals("Alive") || updateData.get(i).equals("Enter"))) {
				userLoc.remove(i);
				updateData.remove(i);
				i--;
				size--;
			}
		}
	}
	
	void exitUser(User user) {
		int index;
		for(index=0; index<roomUserList.size(); index++) {
			if(roomUserList.get(index) == user) {
				String sendString = user.id + " Exit 0 0";
				updateData.add(sendString);
				roomUserList.remove(index);
			}
		}
	}
}
