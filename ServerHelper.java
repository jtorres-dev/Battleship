import java.lang.Thread;
import java.lang.InterruptedException;

import java.util.List;
import java.util.HashSet;
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

	/*TODO*/
	/* a list of all topics the client is listed in */
	private HashSet<String> topics;
							 
	private String commands ="\n\n====>   /quit or /logout will log you out of the server." +
							   "\n====>	/w or /whisper will let you whisper to another user. Ex: /w l33thacker Hey friend!" +
							   "\n====>   /allusers, /users, or /all will show you all users that are online.\n\n";							 

	private String welcome = "\n_____________________________________________________________________________________________________" +
							 "\n\nWelcome to the coolest server! This server contains some basic commands designed for cool kids only." +
							 commands +
							 "Use the /help command to display all the commands." +
							 "\n_____________________________________________________________________________________________________\n\n";


	/* constructs an instance of the main server, and the created client socket */
	public ServerHelper(Server server, Socket csocket) {
		this.server = server;
		this.csocket = csocket;
		this.username = null;
		topics = new HashSet<>();
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

    	try {
    		/* if there is still stuff to read from client */
    		while((line = reader.readLine()) != null) {

	    		if(line.length() > 0) {

	    			/* if a command, do command	*/
	    			if(line.split(" ")[0].charAt(0) == '/') {

		    			String command = line.split(" ")[0];
			    		
			    		if("/login".equalsIgnoreCase(command))
			    			login(line.split(" "));
			    		
			    		else if ("/quit".equalsIgnoreCase(command) || "/logout".equalsIgnoreCase(command)) {
							/* server tells user goodbye */
							sendOut.write(("Goodbye " + username + "!\n").getBytes());
			    			logout();
			    			break;
			    		}
			    		
			    		else if("/w".equalsIgnoreCase(command) || "/whisper".equalsIgnoreCase(command))
			    			/* special case when body doesn't need to be parsed i.e: 
			    			["/w", "john", "hello my friend, i have to tell u something..."] */
			    			whisper(line.split(" ", 3));
			    		
			    		else if("/join".equalsIgnoreCase(command)) 
			    			joinTopic(line.split(" "));

			    		else if("/leave".equalsIgnoreCase(command))
			    			leaveTopic(line.split(" "));

			    		else if("/users".equalsIgnoreCase(command) || "/allusers".equalsIgnoreCase(command) || "/all".equalsIgnoreCase(command))
							listOnlineUsers();			    		
			    		
			    		else if("/help".equalsIgnoreCase(command))
			    			sendOut.write(commands.getBytes());
			    		else 
				    		sendOut.write((command + " is unknown.\n").getBytes());
					}
					
					else /* not a command, but a message from a user */ 
						allChat(line);
				
				}
			}
			/* safely closes connections */
	    	readInput.close();
	    	sendOut.close();
	    	csocket.close();

		} catch(Exception e) { logout(); }

    }

    /* allows user to login to server with a username */
    private void login(String[] tokens) throws IOException {
    	if(tokens.length == 3) {
    		/* removes trailing ' ' */
    		username = tokens[1].trim();
    		String password = tokens[2].trim();

    		/* to check if user is online or offline */
    		String status; 

    		/* sends to user they have logged on successfully.  */
    		sendOut.write(welcome.getBytes());

    		/* writes to server user has logged on successfully */
			System.out.println(username + " has logged on successfully.");

  			List<ServerHelper> helpers = server.getHelpers();

  			/* for all users excluding this user */
  			for(ServerHelper helper : helpers) {
  				if(!username.equals(helper.getUser())) {
  					/* special case when user is connected to server without a name */ 
  					if(helper.getUser() != null) 
	  					/* sends to every client */
	  					sendToClient(helper.getUser() + " is online\n");		
	  			}
  			}

  			/* for current user to receive status from newly logged in user */
  			for(ServerHelper helper : helpers) {
  				if(!username.equals(helper.getUser())) 
  					helper.sendToClient(username + " is online\n"); 
  			}

    	} else /* Error has occurred */ {
    		System.err.println("Login failed for " + username + ".");
    		sendOut.write("Error logging in.\n".getBytes());
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

    /* allows a user to direct message another user that is online with the /w command or /whisper.
    i.e: /w john the secret i wanted to tell you is very important.. */
    private void whisper(String[] tokens) throws IOException {
    	String recipient = tokens[1];
    	String body = tokens[2];

    	/*TODO*/
    	/*boolean msgForTopic = tokens[0] == "/join";*/
    	
    	List<ServerHelper> helpers = server.getHelpers();

		for(ServerHelper helper : helpers) {
			if(recipient.equals(helper.getUser())) 
				helper.sendToClient(helper.getUser() + " whispered: " + body + "\n");
			/*TODO*//*
			if(msgForTopic) {
				String topic = recipient;
				if(helper.isMemberOfTopic(topic)) {
  					String outMsg = "[ " + topic + " ] " + helper.getUser() + ": " + body + "\n";
  					helper.sendToClient(outMsg);
  				}
  			}*/	
		}
    }

    /*TODO: still in progress, will refine later */
    /* checks to see if user is in a topic with someone else */
    public boolean isMemberOfTopic(String topic) {
    	return topics.contains(topic);
    }

    /*TODO: still in progress, will refine later */
    /* user can join unique topics and sendToClient messages to each other in those topics */
    private void joinTopic(String[] tokens) throws IOException {
    	if(tokens.length > 2) {
    		/* format: '/join topic' */
    		String topic = tokens[1];
    		topics.add(topic);

    		/* used whisper to enclose all users in the same topic to receive the message */
    		whisper(tokens); 
    	}
    }

    /*TODO: still in progress, will refine later */
    private void leaveTopic(String[] tokens) {
    	if(tokens.length > 1) {
    		String topic = tokens[1];
    		topics.remove(topic);
    	}
    }

    /* Gives the user the ability to check for all online users */
    private void listOnlineUsers() throws IOException {
    	List<ServerHelper> helpers = server.getHelpers();
    	
	  	sendOut.write("\n".getBytes());		
		/* for all users excluding this user */
  		for(ServerHelper helper : helpers) {
  			if(!username.equals(helper.getUser())) {
  				/* special case when user is connected to server without a name */ 
  				if(helper.getUser() != null) 
	  				/* sends to every client */
			  		sendToClient("ONLINE: " + helper.getUser() + "\n");		
	  		}
  		}
	  	sendOut.write("\n".getBytes());		
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