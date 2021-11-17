package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
	private static int uniqueId;
	private ArrayList<User> userList;
	Room room[];
	
	private int port;
	private boolean keepGoing;
	
	public Server(int port) {
		this.port = port;
		uniqueId = 1;
		userList = new ArrayList<User>();
		room = new Room[3];
		for(int i=0; i<3; i++) {
			room[i] = new Room();
		}
		keepGoing = true;
 	}
	
	public void start() {
		try 
		{
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Start Server:" + serverSocket.getLocalSocketAddress().toString());
			while(true) 
			{
				Socket socket = serverSocket.accept();  	// accept connection

				System.out.println(socket.getInetAddress().toString() +":" +socket.getPort()+ " Client " + uniqueId + " Connected");
				if(!keepGoing)
					break;
				User user = new User(this,socket,uniqueId++);
				userList.add(user);
				user.start();
			}
			try {
				serverSocket.close();
				for(int i = 0; i < userList.size(); ++i) {
					User user = userList.get(i);
					try {
						user.in.close();
						user.out.close();
						user.socket.close();
					}
					catch(IOException ioE) { }
				}
				userList.clear();
			} catch(Exception e) {
				System.out.println("Exception closing the server and clients: " + e);
			}
		} catch (IOException e) { }
	}
	
	synchronized void remove(int id) {
		for(User user: userList) {
			if(user.id == id) {
				userList.remove(user);
				return;
			}
		}
	}
	
	public static void main(String args[]) {
		int port = 8900;
		Server server = new Server(port );
		server.start();
	}
}