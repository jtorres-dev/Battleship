public class Coord {
	
	private final int x;
	private final int y;
	private final String gridX;
	private final String gridY;
	private final int orientation;

	public Coord(int x, int y, String gridX, String gridY, int orientation) {
		this.x = x;
		this.y = y;
		this.gridX = gridX;
		this.gridY = gridY;
		this.orientation = orientation;
	}

	public int shipsX() {
		return x;
	}

	public int shipsY() {
		return y;
	}

	public String getGridX() {
		return gridX;
	}

	public String getGridY() {
		return gridY;
	}

	public String getGridLocation() {
		return gridX + gridY;
	}

	public int orientation() {
		return orientation;
	}
	// @Override
	// public boolean equals(Object o) {
	// 	if(o == this) 
	// 		return true;

	// 	if(!(o instanceof Coord))
	// 		return false;

	// 	Coord coord = (Coord) o;

	// 	return Integer.compare
	// }
}