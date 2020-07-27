public class Ship {
	private boolean isAlive;
	private final String name;
	/*[start location, *, *, end location]*/
	private String[] location;

	public Ship(String name) {
		this.name = name;
		isAlive = true;
	}

	public void setLocation(String ... coordinates) {
		location = new String[coordinates.length];
		
		for(int i = 0; i < coordinates.length; i++)
			location[i] = coordinates[i];
	}

	public boolean attacked(String location) {
		for(int i = 0; i < this.location.length; i++) {
			if(location == this.location[i]) {
				this.location[i] += 'X';
				return true;
			}
		}
		return false;
	}

	/* seems implemented */
	public boolean alive() {
		
		for(int i = 0; i < location.length; i++) {
			if(location[i].charAt(location[i].length() - 1) != 'X') {
				isAlive = true;
				break;
			}
			else
				isAlive = false;
		}

		return isAlive;
	}

}