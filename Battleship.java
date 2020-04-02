/*
	* ask for usernames:
	* 2 players
	* show user the grid and their ships
	* prompt player 1 to choose their locations for each ship:
		* 1x Aircraft Carrier 	5
		* 1x Battleship 		4
		* 1x Cruiser 	 		3
		* 2x Destroyer			3
		* 1x Submarine 			2
	* must be within 10x10
	* prompt player 2 to choose within 10x10
	* 
	* starts player 1 first,
		* next round starts winner player
	* if a miss, 
		player 1 is informed on the miss and that its player 2's turn
	  	
	  	player 2 is informed to go next

	* else 
		player 2 is hit, player 1 is informed on the hit and
		that its player 2's turn
		if player sunk a ship, player 1 is informed

		
		player 2 is informed on the hit and plays next turn

	* if a player loses all of its ships, then the player loses and 
	gets prompted a status








*/

public class Battleship {
	// players can have out of two statuses

	public static void main(String[] args) {
	//	start();
	}

	public static void start() {
		//createGrid();

		//Scanner input = new Scanner(System.in);
		
		/* send to server for both players, for now we will linearly do it */
		//System.out.println("Welcome to Battlefield!");
		//System.out.print("Please type in your name to start playing: ");
		//String name = input.nextLine();
		//Player player = new Player(name);

		/* may be done concurrently */
		//layoutShips(player);
		
		//if(player.getName != null)
		//	wait();

		// gonna need to count each player to differentiate in the server to allow player 1 to go first, then player 2.
		// afterwards, figure out how to draw on screen a grid with text on the side from the server telling the current game status...
		// keeps track of missles sent over turn based.
		// keep track of ships and their coordinates
	}
}