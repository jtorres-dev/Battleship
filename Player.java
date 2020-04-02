public class Player {

	private final String name;

	public Player(String name) {
		this.name = name;
	}

	public void gameStatus(boolean youWin) {
		if(youWin) 
			System.out.println("You won! Great job!");
		else
			System.out.println("You lost... Better luck next time.");
	}

}