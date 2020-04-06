public class ServerRunner {
	/* Starts server on port given by user */
    public static void main(String args[]) { 
    	// if(args.length == 0 || args.length > 1) {
     //        System.out.println("Usage: java ServerRunner [port]");
     //        return;
     //    }
        //int port = Integer.parseInt(args[0]);
        Server server = new Server(50505);
        /* starts server thread which starts the serverHelper */
        server.start();
    }
}