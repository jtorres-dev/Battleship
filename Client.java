import java.io.OutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.net.Socket;

public class Client {
    /* host connection */
    private final String host;
    
    /* port for server */
    private final int port;
    
    /* socket for server */
    private Socket socket;

    /* used to write to server */
    private OutputStream serverOut;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        if(args.length == 0 || args.length < 2) {
            System.out.println("Usage: java Client [host] [port]");
            return;
        }

        Scanner input = new Scanner(System.in);

        /* user is forced to put in username */
        System.out.print("Enter your username: ");
        String username = input.nextLine();
        
        while(username.length() == 0) {
            System.out.print("Enter your username: ");
            username = input.nextLine();
        }
            
        /* constructs a client with the host and port*/
        Client client = new Client(args[0], Integer.parseInt(args[1]));
        /* starts connection */
        client.start(username);
    }

    /*the connection is started with the server by connecting to the host and port */
    private void start(String username) throws IOException {
        /* connect starts a thread that attempts to connect to the server */
        if (connect()) {
            Scanner input = new Scanner(System.in);
            String text;

            /*writes to servers output stream */
            sendToServer("/login " + username + " " + "pw\n");
            
            /* always listens for another input, whatever the input is, let the server handle it */
            while (true) {
                text = input.nextLine();
                if ("/logout".equalsIgnoreCase(text) || "/quit".equalsIgnoreCase(text)) {
                    sendToServer(text);
                    break;
                }
                sendToServer(text);
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
            socket = new Socket(host, port);
            serverOut = socket.getOutputStream();

            Thread listener = new Thread(new ServerListener(socket));
            
            listener.start();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* sends any text to server from client */
    private void sendToServer(String text) throws IOException {
        /* special case where a lineSeparator is needed to detect \n. */
        serverOut.write((text + System.lineSeparator()).getBytes());
        /* used to clear buffer, this fixes the issues of text not being sent properly. It forces the buffer to send and clears it out. */
        serverOut.flush();
    }
}