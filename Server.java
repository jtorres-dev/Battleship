import java.lang.Thread;
import java.util.Vector;
import java.util.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread { 
  	/* port for the server */
  	private final int port;

  	/* 
  		Vector for thread safety when accessing data together. 
  		This will keep track of all the helpers created to communicate 
		with clients.
  	*/
  	private Vector<ServerHelper> helpers = new Vector<>();
  	
  	public Server(int port) {
  		this.port = port;
  	}

  	/* returns all seperate instances of a server helper */
  	public List<ServerHelper> getHelpers() {
  		return helpers;
  	}

  	/* removes a helper when user decides to logout */
  	public void removeHelper(ServerHelper helper) {
  		helpers.remove(helper);
  	}
  	
  	@Override
  	public void run() {
		try {
			/* creates socket for server from port initialized by user */
	    	ServerSocket socket = new ServerSocket(port);
	      	
	      	/* accepts clients for the programs lifetime */
	      	while(true) {

	      		/* creates connection between server and client. client socket is returned */	
	      		Socket csocket = socket.accept();
	      		/* initializes a server helper to be able to handle client communication individually */
	      		ServerHelper helper = new ServerHelper(this, csocket);
	      		/* adds this instance as a helper in helpers list to keep track of all helpers */
	      		helpers.add(helper);
	      		/* starts helper thread */
	      		helper.start();
      		}
    	} catch(IOException e) { e.printStackTrace(); }
  	} 
} 