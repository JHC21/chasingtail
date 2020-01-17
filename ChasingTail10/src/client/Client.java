package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

public class Client {
	private Socket socket;
	private int port;
	private String server;
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	static GUI myGUI;

	int id, myRoomNo, myX, myY;
	Vector<int[]> userData;
	boolean isGame, isInit;

	static boolean isInput;
	
	Client(){
		server = "127.0.0.1";
		port = 8900;
	}
	
	Client(int port){
		server = "127.0.0.1";
		this.port = port;
	}
	
	Client(String address, int port) {
		this.server = address;
		this.port = port;
	}

	public boolean setup() {
		try {
			socket = new Socket(server,port);
		} catch(Exception s) {
			return false;
		}
		
		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch(IOException io) {
			return false;
		}
		
		new Receiver().start();
		
		userData = new Vector<int[]>();
		isGame = false;
		isInput = false;
		isInit = false;
		
		myGUI = new GUI(this,out);
		
		id = -1;
		
		return true;
	}
	

	private void closeConnection() {
		try {
			if(in != null) in.close();
		} catch(Exception e) { }
		try {
			if(out != null) out.close();
		} catch(Exception e) { }
		try {
			if(socket != null) socket.close();
		} catch(Exception e) { }
	}
	
	public int getRoomUserSize() {
		if(isGame) return userData.size();
		else return -1;
	}
	
	public static void main(String[] args) {
		int port = 8900;
		String address = "127.0.0.1";
		
		Client client = new Client(address,port);
		
		if(!client.setup()) {
			System.out.println("Fail to reach Server");
			return;
		}
		
		myGUI.start();
		
	}
	
	
	class Receiver extends Thread {	
		public void run() {
			while(true) {
				try {
					String getString = (String) in.readObject();
					String[] getData = getString.split(" ");
					System.out.println("in: " + getString);
					
					if(!isGame) {
						if(getData[1].equals("Enter")) {
							if(getData[0].equals("c")) {
								// fail to enter
								myGUI.enterRoom(false);
							} else {
								isGame = true;
								id = Integer.parseInt(getData[0]);
							}
							
						}else if (getData[1].equals("Exit")) {
							// exit from server
							myGUI.exitGame(true);
							closeConnection();
							return;
						}
					}
					else {
						 if (getData[1].equals("Exit")) {
							// exit from game
							id = -1;
							myGUI.exitRoom(true);
							isGame = false;
							continue;
						}

						// get update
						int size = Integer.parseInt(getData[1]);
						int id_g, index=0, rmList[];
						rmList = new int [size];
								
						for(int i=0; i<size; i++) {
							id_g = Integer.parseInt(getData[i*4+2]);
							try {
								if(userData.get(i)[0] == id_g) {
									if(getData[4*i+3].equals("Alive") || getData[4*i+3].equals("Enter")) {
										// update x,y
										userData.get(i)[1] = Integer.parseInt(getData[i*4+4]);
										userData.get(i)[2] = Integer.parseInt(getData[i*4+5]);
									} else if(getData[4*i+3].equals("Dead")) {
										// get killer
										myGUI.display("[Client " + getData[i*4+4] + "] \n>Kill [Client " + id_g +"]\n");
										rmList[index] = i;
										index++;
									} else if(getData[4*i+3].equals("Exit")) {
										// user exit game
										myGUI.display("[Client " + id_g + "] \n>Exit\n");
										rmList[index] = i;
										index++;
									}
								}
							}catch(IndexOutOfBoundsException e) {
								for(int j=i; j<size; j++) {
									id_g = Integer.parseInt(getData[j*4+2]);
									int[] newData = new int[3];
									newData[0] = id_g;
									newData[1] = Integer.parseInt(getData[j*4+4]);
									newData[2] = Integer.parseInt(getData[j*4+5]);
									
									userData.add(newData);
									System.out.println("[Client "+ newData[0] + "] Enter");
									myGUI.display("[Client "+ newData[0] + "] Enter\n");
								}
							}
						}
								
						// check for user change
						for(int i=0; i <userData.size(); i++) {
							if(userData.get(i)[0] == id) {
								try {
									if(getData[4*i+3].equals("Alive")) {
										myX = userData.get(i)[1];
										myY = userData.get(i)[2];	
									} else if(getData[4*i+3].equals("Dead")) {
										// get killer
										// you are killed by __
										myGUI.stopGame();
									} else if (getData[4*i+3].equals("Enter")) {
										myX = userData.get(i)[1];
										myY = userData.get(i)[2];
										
										myGUI.enterRoom(true);
										isInit = true;
									}
								}catch (IndexOutOfBoundsException e3) {}
								break;
							}
						}
						
						for(int i=index-1; i>=0; i--) {
							userData.remove(rmList[i]);
						}
						
						isInput = true;
					}										
				}catch (SocketException e2) {
					System.exit(0);
				} catch (IOException e) {
					System.out.println("Data receive Error");
					e.printStackTrace();
					break;
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} 
			}
		}
	}
}
