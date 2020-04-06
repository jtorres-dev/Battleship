import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/*
    ListeningToServer waits for the server's response and prints it out to the client as a thread.
*/
public class ServerListener implements Runnable {
    private Socket socket;

    public ServerListener(Socket socket) {
        this.socket = socket;
    }

    /*  reads the chat from the server */
    @Override
    public void run() {
        Scanner input;
        try {
            input = new Scanner(socket.getInputStream());
            /*if socket is not closed and server has a next line*/
            while (!socket.isClosed()) {
                String text = input.nextLine();
                System.out.println(text);
                if(text != "/gameover")
                    System.out.println("YOUWon!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}