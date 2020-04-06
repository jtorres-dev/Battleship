import java.lang.Thread;
import java.lang.InterruptedException;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.Socket;



public class ServerHelper extends Thread {
	/* main server */
	private final Server server;

	/* keeps client sockets unique and immutable */
	private final Socket csocket;
	
	/* Output stream from server helper to communicate with client */
	private OutputStream sendOut;

	/* keeps track of clients username */
	private String username;

	private ArrayList<String> p1Ships;
	private ArrayList<String> p2Ships;
	private ArrayList<Boolean> p1Orientations;
	private ArrayList<Boolean> p2Orientations;
	private String[][] p1Locations;

	/* keeps track of player count */
	private int currentPlayer;



							 
	private String commands;							 

	private String welcome = "\n_____________________________________________________________________________________________________" +
							 "\n\nWelcome to the coolest server! This server contains some basic commands designed for cool kids only." +
							 commands +
							 "Use the /help command to display all the commands." +
							 "\n_____________________________________________________________________________________________________\n\n";


	/* constructs an instance of the main server, and the created client socket */
	public ServerHelper(Server server, Socket csocket, int currentPlayer) {
		this.server = server;
		this.csocket = csocket;
		this.currentPlayer = currentPlayer;
		this.username = null;
		
		p1Ships = new ArrayList<String>();
		p2Ships = new ArrayList<String>();
		
		p1Orientations = new ArrayList<Boolean>();
		p2Orientations = new ArrayList<Boolean>();

		p1Locations = new String[5][5];
	}

  	public String getUser() { 
  		return username;
	}

	/* 
		server helper thread handles what the client types in and translates it to an action based off
		of what the user enters
	*/
	@Override
	public void run() {
		try {
			System.out.println("in run!");
			clientCommunication();
		} 
		catch(IOException e) { e.printStackTrace(); }
		catch(InterruptedException e) { e.printStackTrace(); }
	}

	/* Will assist client with any client inputs. */
	/* Throws InterruptedException for threads status */
	private void clientCommunication() throws IOException, InterruptedException {
  		/* client's output stream */
  		sendOut = csocket.getOutputStream();
    	
		/* takes input stream from client */
		InputStream readInput = csocket.getInputStream();

    	/* takes in client's input stream to read inputs as string tokens */
    	BufferedReader reader = new BufferedReader(new InputStreamReader(readInput));
    	String line;

    	System.out.println("in communication!");
    	try {
    		/* if there is still stuff to read from client */
    		while((line = reader.readLine()) != null) {
    			//System.out.println("checking line!");
	    		if(line.length() > 0) {
	    			/* if a command, do command	*/
	    			if(line.split(" ")[0].charAt(0) == '/') {

		    			String command = line.split(" ")[0];

			    		if("/name".equals(command)) {
			    			System.out.println("receiving name");
			    			login(line.split(" "));
			    		}

			    		else if("/ori".equals(command)) {
			    			if(currentPlayer == 1) {
			    				storeP1ShipOrientation(line.split(" "));
			    			}
			    		}

			    		else if("/array".equals(command)) {
			    			if(currentPlayer == 1) {
			    				storeP1Ships(line.split(" "));
    					    	stretchLocation();
			    			}
			    		}

			    		//else if("/atk".equals(command))
			    		//	sendAttack(line);

			    		else if ("/quit".equalsIgnoreCase(command)) {
			    			logout();
			    			break;
			    		}
			    			    		
			    		else 
				    		sendOut.write((command + " is unknown.\n").getBytes());
					}
					
					//else /* not a command, but a message from a user */ 
					//	allChat(line);
				
				}
			}
			System.out.println("never entered");
			/* safely closes connections */
	    	readInput.close();
	    	sendOut.close();
	    	csocket.close();

		} catch(Exception e) {System.out.println("failed");} //logout(); }

    }

    /* allows user to login to server with a username */
    private void login(String[] tokens) throws IOException {
    	if(tokens.length >= 2) {
    		/* removes trailing ' ' */
    		username = tokens[1].trim();


    		/* sends to user they have logged on successfully.  */
    		//sendOut.write(welcome.getBytes());

    		/* writes to server user has logged on successfully */
			//System.out.println(username + " has finished.");
			//System.out.println(username + "'s playerCount: " + currentPlayer);
			


  			List<ServerHelper> helpers = server.getHelpers();

  			/* for all users excluding this user */
  			for(ServerHelper helper : helpers) {
  				if(!username.equals(helper.getUser())) {
  					/* special case when user is connected to server without a name */ 
  					if(helper.getUser() != null) 
	  					/* sends to every client */
	  					sendToClient(helper.getUser() + " is ready!\n");		
	  			}
  			}

  			/* for current user to receive status from newly logged in user */
  			for(ServerHelper helper : helpers) {
  				if(!username.equals(helper.getUser())) 
  					helper.sendToClient(username + " is ready!\n"); 
  			}

    	} else /* Error has occurred */ {
    		System.err.println("Connection failed for " + username + ".");
    		sendOut.write("Error with Connection.\n".getBytes());
    	}
    }

    private void storeP1Ships(String[] tokens) {
    	//System.out.println("In storep1");
    	for(int i = 0; i < tokens.length; i++) {
    		if(!(tokens[i].equals("/array") || tokens[i].equals("/done")))
    			p1Ships.add(tokens[i]);
    	}
    	System.out.println("storep1: " + p1Ships.toString());

    }

    private void storeP1ShipOrientation(String[] tokens) {
    	for(int i = 0; i < tokens.length; i++) {
    		if(!(tokens[i].equals("/ori") || tokens[i].equals("/done")))
    			p1Orientations.add(Boolean.parseBoolean(tokens[i]));
    	}
    	System.out.println("p1orientations: " + p1Orientations.toString());

    }

    private void stretchLocation() {

    	for(int i = 0; i < p1Ships.size(); i++) 
    		p1Locations[i][0] = p1Ships.get(i);
    	
    	// if true, its horizontal
    	if(!p1Orientations.get(0)) {
	    	for(int i = 1; i < 5; i++) 
	    		p1Locations[0][i] = ((char)(p1Ships.get(0).charAt(0) + i)) + "" +  p1Ships.get(0).charAt(1);
    	} else {
    		for(int i = 1; i < 5; i++) 
	    		p1Locations[0][i] = (p1Ships.get(0).charAt(0) + "" +  (char)(p1Ships.get(0).charAt(1) + i));
    	}

    	if(!p1Orientations.get(1)) {
	    	for(int i = 1; i < 4; i++) 
	    		p1Locations[1][i] = ((char)(p1Ships.get(1).charAt(0) + i)) + "" +  p1Ships.get(1).charAt(1);
    	} else {
    		for(int i = 1; i < 4; i++) 
	    		p1Locations[1][i] = (p1Ships.get(1).charAt(0) + "" +  (char)(p1Ships.get(1).charAt(1) + i));
    	}   

    	if(!p1Orientations.get(2)) {
	    	for(int i = 1; i < 3; i++) 
	    		p1Locations[2][i] = ((char)(p1Ships.get(2).charAt(0) + i)) + "" +  p1Ships.get(2).charAt(1);
    	} else {
    		for(int i = 1; i < 3; i++) 
	    		p1Locations[2][i] = (p1Ships.get(2).charAt(0) + "" +  (char)(p1Ships.get(2).charAt(1) + i));
    	}    	

    	if(!p1Orientations.get(3)) {
	    	for(int i = 1; i < 3; i++) 
	    		p1Locations[3][i] = ((char)(p1Ships.get(3).charAt(0) + i)) + "" +  p1Ships.get(3).charAt(1);
    	} else {
    		for(int i = 1; i < 3; i++) 
	    		p1Locations[3][i] = (p1Ships.get(3).charAt(0) + "" +  (char)(p1Ships.get(3).charAt(1) + i));
    	}    	

    	if(!p1Orientations.get(4)) {
	    	for(int i = 1; i < 2; i++) 
	    		p1Locations[4][i] = ((char)(p1Ships.get(4).charAt(0) + i)) + "" +  p1Ships.get(4).charAt(1);
    	} else {
    		for(int i = 1; i < 2; i++) 
	    		p1Locations[4][i] = (p1Ships.get(4).charAt(0) + "" +  (char)(p1Ships.get(4).charAt(1) + i));
    	}
    

		System.out.println(username + "'s locations: ");
		for(int i = 0; i < p1Locations.length; i++) {
			for(int j = 0; j < p1Locations[i].length; j++) {
				System.out.print(p1Locations[i][j] + ", ");
			}
			System.out.println();
		}
			
    }

    /* removes serverHelper in charge of current user and updates users the current user has logged off */
    private void logout() throws IOException {
  		server.removeHelper(this);
  		System.out.println(username + " has logged off.");

  		List<ServerHelper> helpers = server.getHelpers();
		
		/* sends to all users the  current user is offline */
		for(ServerHelper helper : helpers) {
			if(!username.equals(helper.getUser())) 	
				helper.sendToClient(username + " is offline" + "\n");  
    	}
    	csocket.close();
    }


    /* gives a default output for clients i.e: "currentuser: Hello!" */
    private void allChat(String message) throws IOException {
    	List<ServerHelper> helpers = server.getHelpers();

		for(ServerHelper helper : helpers) {
			String userMessage = username + ": " + message + "\n";
			helper.sendToClient(userMessage);
		}	
    }

    /* server sends any responses to client */
  	public void sendToClient(String message) throws IOException {
		sendOut.write(("> [" + new Date() + "] " + message).getBytes());

  	}
}