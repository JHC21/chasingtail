package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class User extends Thread{	
	Server server;
	
	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;
	
	Room myRoom;
	int id;
	boolean isConnected, isExit,isGame;
	
	User(Server server, Socket socket, int id){
		this.server = server;
		this.socket = socket;
		this.id = id;
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		}catch (IOException e) {
			System.out.println("fail to create Stream To Client");
			return;
		}
		
		isConnected = true;
		isGame = false;
		isExit = false;
	}

	public void run() {
		while(isConnected) {
			try{
				String getString = (String) in.readObject();
				String[] getData = getString.split(" ");
				System.out.println("in: " + getString);
				
				if(isGame) {
					if(getData[1].equals("Exit")) {
						myRoom.exitUser(this);
						isGame = false;
						String sendString = "c Exit";
						write(sendString); // ack
					}
					// update my Data to Room
					myRoom.addData(id,getString);
					continue;
				}
				
				
				if (getData[1].equals("Enter")) {
					// enter room
					int roomNum = Integer.parseInt(getData[2]);
					
					// if enter sucess, isGame = true
					if (server.room[roomNum].addUser(this)) {
						System.out.println("Client "+id+": Success enter Room "+(roomNum+1));
						myRoom = server.room[roomNum];
						isGame = true;
						String sendString = id + " Enter -1 -1";
						write(sendString);
						server.room[roomNum].addData(id,sendString);
					}else {
						// enter fail, send received msg
						write(getString);
					}			
				} else if (getData[1].equals("Exit")) {
					// close connection
					write(getString);
					isExit = true;
					
					while(isExit) {}
				}	
				
			}catch(IOException e) {
				System.out.println("Cleint " + id + ": Connection Close");
				
				break;
			} catch (ClassNotFoundException e) { }
		}
		System.out.println("Client "+ id +" left Server");
		myRoom.exitUser(this);
		myRoom = null;
		server.remove(id);
		close();
	}
	
	private void close() {
		try {
			if(out != null) out.close();
		} catch(Exception e) {}
		try {
			if(in != null) in.close();
		} catch(Exception e) {};
		try {
			if(socket != null) socket.close();
		} catch (Exception e) {}
	}
	
	public boolean write(String sendString) {
		if(socket.isClosed()) {
			close();
			return false;
		}
		try {
			System.out.println("out: "+sendString);
			out.writeObject(sendString);
			
			if(isExit) {
				isExit = false;
			}
		} catch(IOException e) {
			System.out.println("Fail to send to client");
			return false;
		}
		return true;
	}
}
