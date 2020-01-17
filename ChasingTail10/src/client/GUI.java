package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.*;

import client.GUI.GameField.MovingGUI;

public class GUI {	
	private LobbyField lobby = null;
	private GameField game = null;
	private Client client;
	private Color[] palete = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.ORANGE};
	
	private ObjectOutputStream out;
	
	GUI(Client client, ObjectOutputStream out){
		this.client = client;
		this.out = out;
	}
	
	public void start() {
		lobby = new LobbyField(client);
	}
	
	void enterRoom(boolean success) {
		if(success) {
			if(game == null) {
				lobby.dispose();;
				game = new GameField(client);
			}
			((MovingGUI) game.map).run();
			
		} else {
			lobby.FailToEnterRoom();
		}
	}
	
	void exitRoom(boolean success) {
		if(success) {
			game.dispose();
			lobby.setVisible(true);
			lobby.EnableRoom();
		}
	}
	
	void exitGame(boolean success) {
		if(success) {
			System.exit(0);
		}
	}
	
	void display(String s) {
		if(game != null) {
			game.display(s);
		}
	}
	
	void stopGame() {
		if(game != null) {
			game.stop();
		}
	}
	
	private void sendRequest(String sendData) {
		try {
			System.out.println("out: " + sendData);
			out.writeObject(sendData);
		} catch(IOException io) {
			
		}
	}
	
	class LobbyField extends JFrame{
		
		Client client;
		JButton roomButton[];
		JButton exitButton;
		JLabel gameName, explain, explain1;
		
		LobbyField(Client client){
			this.client = client;
			this.setBounds(200,100,500,600);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setLayout(null);
			
			gameName = new JLabel("Chasing Tail");
			gameName.setBounds(200,100,200,80);
			gameName.setFont(new Font("Serif",Font.BOLD,20));
			this.add(gameName);
			
			roomButton = new JButton[3];
			for(int i=0; i<3; i++) {
				roomButton[i] = new JButton("Room " + (i+1));
				roomButton[i].setBounds(200,200+50*i,100,30);
				roomButton[i].addMouseListener(new ActionAdapter());
				this.add(roomButton[i]);
			}
			explain = new JLabel("Select Room");
			explain1 = new JLabel("");
			explain.setBounds(320,320,100,80);
			explain1.setBounds(320,410,100,80);
			this.add(explain);
			
			exitButton = new JButton("Exit");
			exitButton.setBounds(200,400,100,30);
			exitButton.addMouseListener(new ActionAdapter());
			this.add(exitButton);
			
			this.setVisible(true);
		}
		
		void EnableRoom() {
			for(int j=0; j<3; j++) {
				roomButton[j].setEnabled(true);
			}
		}
		
		void FailToEnterRoom() {
			explain.setText("Fail to Enter room.");
			explain1.setText("Please Select another room.");
			
			EnableRoom();
		}
			
		class ActionAdapter extends MouseAdapter{
			@Override
	        public void mouseEntered(MouseEvent arg0) {
	            // TODO Auto-generated method stub
	            JButton btn = (JButton)arg0.getSource();
	            for(int i=0; i<3; i++) {
					if(btn.getText().equals("Room "+(i+1))) {
						explain.setText("Enter Room "+(i+1));
					} else if (btn.getText().equals("Exit")) {
						explain.setText("Exit");
					}
				}
	        }
	        @Override
	        public void mouseExited(MouseEvent arg0) {
	        	explain.setText("Select Room");
	        	explain1.setText("");
	        }
	        @Override
	        public void mouseReleased(MouseEvent arg0) {
	            // TODO Auto-generated method stub
	        	JButton btn = (JButton)arg0.getSource();
	            for(int i=0; i<3; i++) {
					if(btn.getText().equals("Room "+(i+1))) {
						// send Enter Request
						String sendString ="";
						sendString = "c Enter " + i;
						
						sendRequest(sendString);
						client.myRoomNo = i;
						for(int j=0; j<3; j++) {
							roomButton[j].setEnabled(false);
						}
						return;
					}
				}
	            if (btn.getText().equals("Exit")) {
					explain.setText("Close window");
					
					String sendString ="";
					sendString = "c Exit";
					sendRequest(sendString); 
				}
	        }
		}
	}

	class GameField extends JFrame{
		Client client;
		JButton exitButton;
		JLabel gameName, myName, myRoomName, explain1, explain2;
		JTextArea updates;
		JPanel map;
		int boundX = 600, boundY = 600, targetX, targetY;
		boolean isRun;
		
		GameField(Client client){
			this.client = client;
			this.setBounds(200, 100, 750, 600);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setLayout(null);
			this.setBackground(Color.GRAY);
			this.addWindowListener(new WinEvent());

			gameName = new JLabel("Chasing Tail");
			gameName.setBounds(620,-10,200,80);
			gameName.setFont(new Font("Serif",Font.BOLD,20));
			this.add(gameName);
			
			myRoomName = new JLabel("Room " + (client.myRoomNo+1));
			myRoomName.setBounds(650,20,100,60);
			this.add(myRoomName);
			
			myName = new JLabel("Client " + client.id);
			myName.setBounds(650,40,100,60);
			this.add(myName);
			
			updates = new JTextArea();
			updates.setBounds(620,80,100,400);
			this.add(updates);
			
			exitButton = new JButton("Exit");
			exitButton.setBounds(620,500,100,30);
			exitButton.addMouseListener(new exitAdapter());
			
			explain1 = new JLabel("");
			explain1.setFont(new Font("Serif",Font.BOLD,15));
			explain1.setBounds(250,250,100,60);
			explain2 = new JLabel("");
			explain2.setBounds(200,300,100,50);
			
			map = new MovingGUI();
			map.setBounds(0,0,600,600);
			map.addMouseListener(new clickAdapter());
			
			map.add(explain1);
			map.add(explain2);
			
			this.add(map);
			((MovingGUI) map).run();
			
			
			this.add(exitButton);
			this.setVisible(true);
		}


		class exitAdapter extends MouseAdapter{
			@Override
	        public void mouseEntered(MouseEvent arg0) {	}
	        @Override
	        public void mouseExited(MouseEvent arg0) { }
	        @Override
	        public void mouseReleased(MouseEvent arg0) {
	            // TODO Auto-generated method stub
	        	JButton btn = (JButton)arg0.getSource();
	        	if (btn.getText().equals("Exit")) {
					// send exit Signal
					sendRequest(client.id + " Exit 0 0");
				}
	        }
		}
		
		class clickAdapter implements MouseListener{
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {	
				targetX = e.getX();
				targetY = e.getY();
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {	}
			
		}
		
		class WinEvent implements WindowListener{
			@Override
			public void windowOpened(WindowEvent e) { }

			@Override
			public void windowClosing(WindowEvent e) {
				// End Connection
				isRun = false;
			}

			@Override
			public void windowClosed(WindowEvent e) { System.exit(0); }

			@Override
			public void windowIconified(WindowEvent e) { }
			@Override
			public void windowDeiconified(WindowEvent e) { }
			@Override
			public void windowActivated(WindowEvent e) { }
			@Override
			public void windowDeactivated(WindowEvent e) { }
			
		}
		
		public void stop() {
			explain1.setText("You Died!");
			explain2.setText("Press Exit button to exit");
			((MovingGUI) map).stop();
		}
		
		void display(String s) {
			updates.append(s);
		}
		class Snake{
			private int id;
	    	private Body[] me;
	    	private Color myColor;
	    	
	    	Snake(int ID, int x, int y){
	    		id = ID;
	    		me = new Body[8];
	    		int myX = x;
	    		int myY = y;
	    		myColor = palete[(id+1)%6];
	        	for(int i=0; i<8; i++) {
	        		me[i] = new Body();
	        		me[i].set(myX, myY);
	        		me[i].setSize(30 - 3*i);
	        	}
	    	}    	
	    	Snake(int[] arr){
	    		id = arr[0];
	    		me = new Body[8];
	    		int myX = arr[1];
	    		int myY = arr[2];
	    		myColor = palete[(id+1)%6];
	        	for(int i=0; i<8; i++) {
	        		me[i] = new Body();
	        		me[i].set(myX, myY);
	        		me[i].setSize(30 - 3*i);
	        	}
	    	}
	    	int getID() { return id;}
	    	int getX(int i) { return me[i].x; }
	    	int getY(int i) { return me[i].y; }
	    	int getSize(int i) { return me[i].size; }
	    	
	    	void addBody(int updateX, int updateY) {
	        	for(int i=6; i>=0; i--) {
	        		me[i+1].set(me[i].x,me[i].y);
	        	}
	        	if(updateX > 600) updateX = 599;
	        	else if (updateX < 0) updateX = 1;
	        	if(updateY > 600) updateY = 599;
	        	else if (updateY < 0) updateY = 1;
	        	me[0].set(updateX, updateY);
	        	
	        	/*
	        	for(int i=0; i<8; i++) {
	        		System.out.print("["+(i) + ": "+me[i].x+me[i].y+"] ");
	        	}System.out.println("");
	        	*/
	        }
	        
	    	private class Body{
	        	private int x,y,size;
	        	
	        	void set(int x, int y) {
	        		this.x = x;
	        		this.y = y;
	        	}
	        	
	        	void setSize(int size) {
	        		this.size = size;
	        	}
	        }
	    }
		
		class MovingGUI extends JPanel {
			Snake me;
			Vector<Snake> users;
			
			MovingGUI() {
				this.setBackground(Color.white);
			}
			
			void addSnake(int[] arr) {
				users.add(new Snake(arr));
			}
			
			void stop() {
				isRun = false;
			}
			
			public void run() {
				isRun = true;
				me = new Snake(client.id, client.myX, client.myY);
				users = new Vector<Snake>();
				targetX = client.myX;
				targetY = client.myY;
				
				new Thread(){
					public void run(){
						while(isRun){
							// get mouse input

							int moveX = targetX - me.getX(0);
							if(moveX > 20) moveX = 20;
							else if (moveX < -20) moveX = -20;
							moveX += me.getX(0);
									
							int moveY = targetY - me.getY(0);
							if(moveY > 20) moveY = 20;
							else if (moveY < -20) moveY = -20;
							moveY += me.getY(0);
					
							me.addBody(moveX, moveY);
							sendRequest(me.id + " Alive " + moveX + " " + moveY);
				
							update();							
							repaint();
							
							if(!isRun) break;
							
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) { }
						}
					}
				}.start();
			}

			void update() {
				//sync
				System.out.println("snake: "+ users.size() + ", client: " + client.userData.size());
				if(users.size() > client.userData.size()) {
					// some user left room
					for(int i=0; i<users.size(); i++) {
						try {
							if(users.get(i).id != client.userData.get(i)[0]) {
								users.remove(i);
							}
						} catch(IndexOutOfBoundsException oie) {
							users.remove(i);
						}
						
					}
				} else if(users.size() < client.userData.size()) {
					// some user enter room
					users.add(new Snake(client.userData.get(users.size())));
				}
				if(client.isInput) {
					for(int i=0; i<users.size(); i++) {
						Snake s = users.get(i);
						if(s.id != me.id) {
							if((s.getX(0) - me.getX(6) < 8) && (s.getX(0) - me.getX(6) > -8)) {
								if((s.getY(0) - me.getY(6) < 8) && (s.getY(0) - me.getY(6) > -8)) {
									// killed by s
									String sendString = me.id + " Dead " + s.id + " 0";
									sendRequest(sendString);
										
									client.isInput = false;
									
									return;
								}
							}
							s.addBody(client.userData.get(i)[1],client.userData.get(i)[2]);
						}
					}
					client.isInput = false;
				}
			}
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D)g;
				
				// draw me
				g2d.setColor(me.myColor);
				for(int i=0; i<7; i++) {
					g2d.fillOval(me.getX(i), me.getY(i), me.getSize(i), me.getSize(i));
				}
				
				//draw other users
				for(int i=0; i<users.size(); i++) {
					for(int j=0; j<7; j++) {
						if(users.get(i).id != me.id) {
							g2d.setColor(users.get(i).myColor);
							g2d.fillOval(users.get(i).getX(j), users.get(i).getY(j), users.get(i).getSize(j), users.get(i).getSize(j));
						}
					}
				}
			}
		}
	}
}

