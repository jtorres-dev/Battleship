import java.io.OutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.net.Socket;
import java.util.ArrayList;


public class Battleship {

    /* socket for server */
    private Socket socket;

    /* used to write to server */
    private OutputStream serverOut;
	private GUITranslater gui;
	private boolean working = true;

    public Battleship() {
    	gui = new GUITranslater();
    }

    public static void main(String[] args) throws IOException {
        /* constructs a client with the host and port*/
        new Battleship().start();
        /* starts connection */
        // player.start();

    }

    /*the connection is started with the server by connecting to the host and port */
    private void start() throws IOException {
    	//Scanner in = new Scanner(System.in);
    	String text;
        /* connect starts a thread that attempts to connect to the server */
        if (connect()) {
    		System.out.printf("Running: status -> %b working -> %b%n", gui.selectionStatus(), working);
        	while(true) {
        		//without print statement, server doesnt do anything ...?
        		// I assume its because it slows down the execution for if? idk

        		if(gui.selectionStatus() && working) {

        			System.out.println("Sending name: " + gui.playerName());
        			sendName(gui.playerName());
		        	
		        	System.out.println("Sending orientations: " + gui.orientations().toString());
		        	sendOrientation(gui.orientations());

        			System.out.println("Sending coordinates: " + gui.shipCoords().toString());
		        	sendShipCoordinates(gui.shipCoords());
		        	
		        	working = false;
		    		System.out.printf("Running: status -> %b working -> %b%n", gui.selectionStatus(), working);
        		}
				else /* send filler */
					sendToServer("incoming");


        		//text = in.nextLine();
                // if ("/logout".equalsIgnoreCase("text") || "/quit".equalsIgnoreCase("text")) 
                     //sendToServer("incoming");
                //     break;
                // }
                 //	sendToServer(" ");
			}

        }
        else
            System.err.println("Login failed");

        /*closes sockets safely */
        serverOut.close();
        socket.close();
    }

    /* attempts to connect to host and port, if it succeeds, a thread is started as a listener waiting for the server and returns true */
    private boolean connect() {
        try {
            socket = new Socket("localhost", 50505);
            serverOut = socket.getOutputStream();

			new Thread(gui).start();
            /* Server Listener for incoming info */
            new Thread(new ServerListener(socket)).start();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendAttack(String coords) throws IOException {
    	/* sends to server a message before sending attack */
		serverOut.write("/atk ".getBytes());
		serverOut.write(("A1\n" + System.lineSeparator()).getBytes() );    	
		//serverOut.flush();
    
    }

    private void sendOrientation(ArrayList<String> orientations)  throws IOException {
    	// send vertical state to server
    	serverOut.write("/ori ".getBytes());

    	for(int i = 0; i < 5; i++) 
    		serverOut.write((orientations.get(i) + " ").getBytes());
    	//orientations.clear();

		serverOut.write(("/done" + System.lineSeparator()).getBytes());
		serverOut.flush();
    }

    private void sendShipCoordinates(ArrayList<String> shipCoordinates) throws IOException {
    	/* sends to server a message before sending array */
		serverOut.write("/array ".getBytes());
    	
    	/* writes array to server and reassembles in server */
    	for (int i = 0; i < 5; i++) 
    		serverOut.write((shipCoordinates.get(i) + " ").getBytes());
    	shipCoordinates.clear();

		serverOut.write(("/done" + System.lineSeparator()).getBytes());
		serverOut.flush();

    }

    /* sends name to server */
    private void sendName(String name) throws IOException {
    	/* sends to server a message before sending name */
		serverOut.write(("/name ").getBytes());
		serverOut.write((name + System.lineSeparator()).getBytes());
		serverOut.flush();

    }
    /* sends any text to server from client */
    private void sendToServer(String text) throws IOException {
        /* special case where a lineSeparator is needed to detect \n. */
        serverOut.write((text + System.lineSeparator()).getBytes());
        /* used to clear buffer, this fixes the issues of text not being sent properly. It forces the buffer to send and clears it out. */
        serverOut.flush();
    }
}