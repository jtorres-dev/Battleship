import java.util.*;

import javax.swing.*;
import javax.swing.ImageIcon;

import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;

import java.util.List;
import java.util.ArrayList;

public class SelectionGUI extends JFrame {
	
	private final int AIRSHIP    = 5;
	private final int BATTLESHIP = 4;
	private final int CRUISER    = 3;
	private final int DESTROYER  = 3;
	private final int SUBMARINE  = 2;

	private final int LEFT_BORDER   = 774;
	private final int RIGHT_BORDER  = 851;
	private final int TOP_BORDER    = 76;
	private final int BOTTOM_BORDER = 155;

	private static ArrayList<Coord> shipLocations = new ArrayList<Coord>();
	private static ArrayList<String> shipCoordinates = new ArrayList<String>();
	private static ArrayList<String> orientations = new ArrayList<String>();
	
	private static int confirmed = 0;
	private static int currentShip = 0;

	private static String title = "";
	private static String name  = "";

	private static boolean selectingAirship    = true;
	private static boolean selectingBattleship = false;
	private static boolean selectingCruiser    = false;
	private static boolean selectingDestroyer  = false;
	private static boolean selectingSubmarine  = false;
	private static boolean confirmedLocation   = false;

	private static String[][] boundaries = new String[5][5];

	private static boolean shipIsVertical = true;	
	private static boolean moved, moved1, moved2, moved3, moved4 = false;
	private static int orientation = 0;

	private int spacing = 1; /* for lines in grid */
	private int mouseX, mouseY, lastX, lastY;
	private int fixedY, fixedX;
	private int draggedX, draggedY;
	private int centerX, centerY;
	private String shipGridX;
	private String shipGridY;
	
	private boolean ifFinished;
	

	private boolean dragging = false;
	private boolean pressed = false;
	private boolean rotateShip = false;

	private List<Image> ships = new ArrayList<Image>();


	public SelectionGUI() {
		ifFinished = false;
		
		setTitle("Battleship");
		setSize(1600, 900);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		storeShips();
		setContentPane(new InGame());
		addMouseMotionListener(new Move());
		addMouseListener(new MouseAction());

		if(name == "") {
			name = JOptionPane.showInputDialog(this, "What is your name?");
			
			while(name.length() <= 1 || name.length() > 10) 
				name = JOptionPane.showInputDialog(this, "Choose a name in between 2-10 characters");
			
			title = "Welcome " + name + "! Pick your locations:";
		}
	}

	public boolean finish() {
		return ifFinished;
	}

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public static ArrayList<Coord> getShips() {
		return shipLocations;
	}
	
	public static ArrayList<String> shipCoord() {
		return shipCoordinates;
	}

	public static ArrayList<String> orientations() {
		return orientations;
	}
	
	private void storeShips() {
		/* vertical up */
		ships.add(0, getToolkit().getImage("./imgs/ships/airship/0.png"));
		ships.add(1, getToolkit().getImage("./imgs/ships/battleship/0.png"));
		ships.add(2, getToolkit().getImage("./imgs/ships/cruiser/0.png"));
		ships.add(3, getToolkit().getImage("./imgs/ships/destroyer/0.png"));
		ships.add(4, getToolkit().getImage("./imgs/ships/submarine/0.png"));
		
		/* horizontal right */
		ships.add(5, getToolkit().getImage("./imgs/ships/airship/1.png"));
		ships.add(6, getToolkit().getImage("./imgs/ships/battleship/1.png"));
		ships.add(7, getToolkit().getImage("./imgs/ships/cruiser/1.png"));
		ships.add(8, getToolkit().getImage("./imgs/ships/destroyer/1.png"));
		ships.add(9, getToolkit().getImage("./imgs/ships/submarine/1.png"));
		
		/* vertical down */
		ships.add(10, getToolkit().getImage("./imgs/ships/airship/2.png"));
		ships.add(11, getToolkit().getImage("./imgs/ships/battleship/2.png"));
		ships.add(12, getToolkit().getImage("./imgs/ships/cruiser/2.png"));
		ships.add(13, getToolkit().getImage("./imgs/ships/destroyer/2.png"));
		ships.add(14, getToolkit().getImage("./imgs/ships/submarine/2.png"));
		
		/* horizontal left */
		ships.add(15, getToolkit().getImage("./imgs/ships/airship/3.png"));
		ships.add(16, getToolkit().getImage("./imgs/ships/battleship/3.png"));
		ships.add(17, getToolkit().getImage("./imgs/ships/cruiser/3.png"));
		ships.add(18, getToolkit().getImage("./imgs/ships/destroyer/3.png"));
		ships.add(19, getToolkit().getImage("./imgs/ships/submarine/3.png"));
	}

	public class InGame extends JPanel {
		
		public void paintComponent(Graphics graphic) {
			/* main background color (navyish) */
			graphic.setColor(new Color(40, 69, 114));
		
			graphic.fillRect(0, 0, 1600, 900);

			produceGrid(graphic);
			produceAxisTitles(graphic);

			if(name != "") {
				showName(graphic);
				
				if(currentShip < 5) {
					chooseLocations(graphic);
					confirmedLocation();
				}
				
				lockShips(graphic);
				makeRotateButton(graphic);
				makeConfirmButton(graphic);
			}
		}

		private void produceGrid(Graphics graphic) {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {
					graphic.setColor(new Color(122, 189, 255));
					graphic.fillRect((spacing + i * 80 + 765), (spacing + j * 80 + 45), 78, 78);
				}
			}
		}

		private void produceAxisTitles(Graphics graphic) {
			produceXAxis(graphic);
			produceYAxis(graphic);
		}

		private void produceXAxis(Graphics graphic) {
			List<Image> oneThruTen = new ArrayList<Image>();

			for(int i = 1; i <= 10; i++) 
				oneThruTen.add(getToolkit().getImage("./imgs/coordinates/"+i+".png"));
			
			int box = 0;
			for(Image img : oneThruTen) {
				if(img.getWidth(null) >= 130)
					graphic.drawImage(img, (spacing + box * 80 + 795), (spacing + 8), 20, 24, null);
				else if(img.getWidth(null) <= 21)
					graphic.drawImage(img, (spacing + box * 80 + 800), (spacing + 8), 8, 24, null);
				else
					graphic.drawImage(img, (spacing + box * 80 + 800), (spacing + 8), 12, 24, null);
				box++;
			}
		}
		
		private void produceYAxis(Graphics graphic) {
			List<Image> aThruJ = new ArrayList<Image>();
			
			for(int i = 0; i < 10; i++) 
				aThruJ.add(getToolkit().getImage("./imgs/coordinates/" + (char)( 'A' + i ) + ".png"));
			
			int box = 0;
			for(Image img : aThruJ) {
				if(img.getWidth(null) <= 21)
					graphic.drawImage(img, 746, (spacing + box * 80 + 70), 4, 24, null);
				else 
					graphic.drawImage(img, 738, (spacing + box * 80 + 70), 16, 24, null);
				box++;
			}
		}

		private void showName(Graphics graphic) {
			graphic.setColor(Color.white);
			graphic.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 35));
			graphic.drawChars(title.toCharArray(), 0, title.length(), 10, 50);
		}
		
		public void placeInGrid() {
			if(shipIsVertical) {
				verticalShips();
			}
			else {
				horizontalShips();
			}
		}

		private void verticalShips() {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {		
					if (draggedX >= (i * 80) + LEFT_BORDER && draggedX < (i * 80) + RIGHT_BORDER &&
						draggedY >= (j * 80) + TOP_BORDER && draggedY < (j * 80) + BOTTOM_BORDER) {
						
						if(j < 6 && currentShip == 0) {	
							centerX = ((i * 80) + RIGHT_BORDER) - 64;
							centerY = ((j * 80) + BOTTOM_BORDER) - 47;
						}
						
						else if(j < 7 && currentShip == 1 && notInAirship(BATTLESHIP)) {								
							centerX = ((i * 80) + RIGHT_BORDER) - 64;
							centerY = ((j * 80) + BOTTOM_BORDER) - 47;			
						}
						
						else if(j < 8 && currentShip == 2 && notInAirship(CRUISER) && notInBattleship(CRUISER)) {
							centerX = ((i * 80) + RIGHT_BORDER) - 64;
							centerY = ((j * 80) + BOTTOM_BORDER) - 47;			
						}
						
						else if(j < 8 && currentShip == 3 && notInAirship(DESTROYER) && notInBattleship(DESTROYER) && notInCruiser(DESTROYER)) {	
							centerX = ((i * 80) + RIGHT_BORDER) - 64;
							centerY = ((j * 80) + BOTTOM_BORDER) - 47;			
						}
						
						else if(j < 9 && currentShip == 4 && notInAirship(SUBMARINE) && notInBattleship(SUBMARINE) && notInCruiser(SUBMARINE) && notInDestroyer(SUBMARINE)) {
							centerX = ((i * 80) + RIGHT_BORDER) - 64;
							centerY = ((j * 80) + BOTTOM_BORDER) - 47;			
						}
					}
				}
			}
		}

		private void horizontalShips() {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {		
					if (draggedX >= (i * 80) + LEFT_BORDER && draggedX < (i * 80) + RIGHT_BORDER &&
						draggedY >= (j * 80) + TOP_BORDER  && draggedY < (j * 80) + BOTTOM_BORDER) {
						
						if(i < 6 && currentShip == 0) {	
							centerX = ((i * 80) + RIGHT_BORDER) - 24;
							centerY = ((j * 80) + BOTTOM_BORDER) - 85;			
						}
						
						else if(i < 7 && currentShip == 1 && notInAirship(BATTLESHIP)) {
							//System.out.println("current location!: " + shipLocations.get(0).getGridLocation());
							//System.out.println("TESTING COORDSSS: " + (char) (shipGridX) + "" + (Integer.valueOf(shipGridY) + 3));								
							centerX = ((i * 80) + RIGHT_BORDER) - 24;
							centerY = ((j * 80) + BOTTOM_BORDER) - 85;			
						}
						
						else if(i < 8 && currentShip == 2 && notInAirship(CRUISER) && notInBattleship(CRUISER)) {
							centerX = ((i * 80) + RIGHT_BORDER) - 24;
							centerY = ((j * 80) + BOTTOM_BORDER) - 85;			
						}
						
						else if(i < 8 && currentShip == 3 && notInAirship(DESTROYER) && notInBattleship(DESTROYER) && notInCruiser(DESTROYER)) {	
							centerX = ((i * 80) + RIGHT_BORDER) - 24;
							centerY = ((j * 80) + BOTTOM_BORDER) - 85;			
						}
						
						else if(i < 9 && currentShip == 4 && notInAirship(SUBMARINE) && notInBattleship(SUBMARINE) && notInCruiser(SUBMARINE) && notInDestroyer(SUBMARINE)) {
							centerX = ((i * 80) + RIGHT_BORDER) - 85;
							centerY = ((j * 80) + BOTTOM_BORDER) - 25;			
						}
					}
				}
			}
		}

		private boolean notInAirship(int ship) {
			char bottomOfShip;
			char maxHeight;
			char minHeight;
			//shipGridX is a string, so to represent it as a character it must be used as .charAt(0)
			if(ship == BATTLESHIP) {
				bottomOfShip = (char)(shipGridX.charAt(0) + 3);
				maxHeight = (char) (boundaries[0][0].charAt(0) - 3);
				minHeight = boundaries[0][0].charAt(0);

				// System.out.println("maxHeight: " + maxHeight);
				// System.out.println("minHeight: " + minHeight);
			}
			else if(ship == CRUISER) {
				bottomOfShip = (char)(shipGridX.charAt(0) + 2);
				maxHeight = (char) (boundaries[0][0].charAt(0) - 2);
				minHeight = boundaries[0][0].charAt(0);
			
			}
			else if(ship == DESTROYER) {
				bottomOfShip = (char)(shipGridX.charAt(0) + 2);
				maxHeight = (char) (boundaries[0][0].charAt(0) - 2);
				minHeight = boundaries[0][0].charAt(0);
			}
			else {
				bottomOfShip = (char)(shipGridX.charAt(0) + 1);
				maxHeight = (char) (boundaries[0][0].charAt(0) - 1);
				minHeight = boundaries[0][0].charAt(0);
			}
			
			/* if ship is vertical */
			if(orientations.get(0).equals("true")) {
				for(int i = 0; i < 5; i++) {
					if(shipGridX.charAt(0) == boundaries[0][i].charAt(0) && shipGridY.charAt(0) == boundaries[0][0].charAt(1) ||
					  (boundaries[0][i].charAt(0) == bottomOfShip && shipGridY.charAt(0) == boundaries[0][0].charAt(1)))
						return false;
				}
			}
			else { /* else ship is horizontal, returns false if the ship overlaps */
				for(int i = 0; i < 5; i++) {
					if(shipGridX.charAt(0) == boundaries[0][0].charAt(0) && shipGridY.charAt(0) == boundaries[0][i].charAt(1) ||
					  (shipGridX.charAt(0) >= maxHeight && shipGridX.charAt(0) <= minHeight && shipGridY.charAt(0) == boundaries[0][i].charAt(1)))
						return false;
				}
			}
			return true;
		}

		private boolean notInBattleship(int ship) {
			char bottomOfShip;
			char maxHeight;
			char minHeight;

			if(ship == CRUISER) {
				bottomOfShip = (char)(shipGridX.charAt(0) + 2);
				maxHeight = (char) (boundaries[1][0].charAt(0) - 2);
				minHeight = boundaries[1][0].charAt(0);
			
			}
			else if(ship == DESTROYER) {
				bottomOfShip = (char)(shipGridX.charAt(0) + 2);
				maxHeight = (char) (boundaries[1][0].charAt(0) - 2);
				minHeight = boundaries[1][0].charAt(0);
			}
			else {
				bottomOfShip = (char)(shipGridX.charAt(0) + 1);
				maxHeight = (char) (boundaries[1][0].charAt(0) - 1);
				minHeight = boundaries[1][0].charAt(0);
			}
			
			/* if ship is vertical */
			if(orientations.get(1).equals("true")) {
				for(int i = 0; i < 4; i++) {
					if(shipGridX.charAt(0) == boundaries[1][i].charAt(0) && shipGridY.charAt(0) == boundaries[1][0].charAt(1) ||
					  (boundaries[1][i].charAt(0) == bottomOfShip && shipGridY.charAt(0) == boundaries[1][0].charAt(1)))
						return false;
				}
			}
			else { /* else ship is horizontal, returns false if the ship overlaps */
				for(int i = 0; i < 4; i++) {
					if(shipGridX.charAt(0) == boundaries[1][0].charAt(0) && shipGridY.charAt(0) == boundaries[1][i].charAt(1) ||
					  (shipGridX.charAt(0) >= maxHeight && shipGridX.charAt(0) <= minHeight && shipGridY.charAt(0) == boundaries[1][i].charAt(1)))
						return false;
				}
			}
			return true;
		}
		
		private boolean notInCruiser(int ship) {
			char bottomOfShip;
			char maxHeight;
			char minHeight;

			if(ship == DESTROYER) {
				bottomOfShip = (char)(shipGridX.charAt(0) + 2);
				maxHeight = (char) (boundaries[2][0].charAt(0) - 2);
				minHeight = boundaries[2][0].charAt(0);
			}
			else {
				bottomOfShip = (char)(shipGridX.charAt(0) + 1);
				maxHeight = (char) (boundaries[2][0].charAt(0) - 1);
				minHeight = boundaries[2][0].charAt(0);
			}
			
			/* if ship is vertical */
			if(orientations.get(2).equals("true")) {
				for(int i = 0; i < 3; i++) {
					if(shipGridX.charAt(0) == boundaries[2][i].charAt(0) && shipGridY.charAt(0) == boundaries[2][0].charAt(1) ||
					  (boundaries[2][i].charAt(0) == bottomOfShip && shipGridY.charAt(0) == boundaries[2][0].charAt(1)))
						return false;
				}
			}
			else { /* else ship is horizontal, returns false if the ship overlaps */
				for(int i = 0; i < 3; i++) {
					if(shipGridX.charAt(0) == boundaries[2][0].charAt(0) && shipGridY.charAt(0) == boundaries[2][i].charAt(1) ||
					  (shipGridX.charAt(0) >= maxHeight && shipGridX.charAt(0) <= minHeight && shipGridY.charAt(0) == boundaries[2][i].charAt(1)))
						return false;
				}
			}
			return true;
		}
		
		private boolean notInDestroyer(int ship) {
			char bottomOfShip = (char)(shipGridX.charAt(0) + 1);
			char maxHeight = (char) (boundaries[3][0].charAt(0) - 1);
			char minHeight = boundaries[3][0].charAt(0);
			
			/* if ship is vertical */
			if(orientations.get(3).equals("true")) {
				for(int i = 0; i < 3; i++) {
					if(shipGridX.charAt(0) == boundaries[3][i].charAt(0) && shipGridY.charAt(0) == boundaries[3][0].charAt(1) ||
					  (boundaries[3][i].charAt(0) == bottomOfShip && shipGridY.charAt(0) == boundaries[3][0].charAt(1)))
						return false;
				}
			}
			else { /* else ship is horizontal, returns false if the ship overlaps */
				for(int i = 0; i < 3; i++) {
					if(shipGridX.charAt(0) == boundaries[3][0].charAt(0) && shipGridY.charAt(0) == boundaries[3][i].charAt(1) ||
					  (shipGridX.charAt(0) >= maxHeight && shipGridX.charAt(0) <= minHeight && shipGridY.charAt(0) == boundaries[3][i].charAt(1)))
						return false;
				}
			}
			return true;
		}

		private void chooseLocations(Graphics graphic) {
			int i;
			switch(currentShip) {
				case 0:
					i = 0;
					
					if(rotateShip)
						i = rotate();

					if( mouseX >= 331 && mouseX < 410 && 
						mouseY >= 105 && mouseY < 501 && 
						dragging) {
						
						// fixing blinking problem
						if(draggedX != 0 && draggedY != 0) {
							graphic.drawImage(ships.get(i * 5), draggedX - 60, draggedY - 65 , null);
						}
						// has been clicked
						moved = true;
					}
					
					else if(moved && draggedX >= LEFT_BORDER && draggedX < RIGHT_BORDER*10 &&
									  draggedY >= TOP_BORDER  && draggedY < BOTTOM_BORDER*10) {
						placeInGrid();
						// draws in the last spot dragged to locked in place
						graphic.drawImage(ships.get(i * 5), centerX - 60, centerY - 65, null);
					}

					else 
						graphic.drawImage(ships.get(i * 5), 280, 70, null);	
					break;

				case 1:	
					i = 0;
					if(rotateShip)
						i = rotate();

					if( mouseX >= 331 && mouseX < 410 && 
						mouseY >= 105 && mouseY < 501 && 
						dragging) {
						
						// fixing blinking problem
						if(draggedX != 0 && draggedY != 0)
							graphic.drawImage(ships.get((i * 5) + 1), draggedX - 60, draggedY - 65 , null);
						// has been clicked
						moved1 = true;
					}

					else if(moved1 && draggedX >= LEFT_BORDER && draggedX < RIGHT_BORDER*10 &&
									  draggedY >= TOP_BORDER  && draggedY < BOTTOM_BORDER*10) {
						placeInGrid();
						// draws in the last spot dragged to locked in place
						graphic.drawImage(ships.get((i * 5) + 1), centerX - 60, centerY - 65, null);
					}
					else
						graphic.drawImage(ships.get((i * 5) + 1), 280, 140, null);
					break;


				case 2:
					i = 0;
					if(rotateShip)
						i = rotate();

					if( mouseX >= 331 && mouseX < 410 && 
						mouseY >= 105 && mouseY < 501 && 
						dragging) {

						// fixing blinking problem
						if(draggedX != 0 && draggedY != 0)
							graphic.drawImage(ships.get((i * 5) + 2), draggedX - 60, draggedY - 65 , null);
						// has been clicked
						moved2 = true;
					}

					else if(moved2 && draggedX >= LEFT_BORDER && draggedX < RIGHT_BORDER*10 &&
									  draggedY >= TOP_BORDER  && draggedY < BOTTOM_BORDER*10) {
						placeInGrid();
						// draws in the last spot dragged to locked in place
						graphic.drawImage(ships.get((i * 5) + 2), centerX - 63, centerY - 65, null);
					}

					else
						graphic.drawImage(ships.get((i * 5) + 2), 280, 230, null);	
					
					break;


				case 3:
					i = 0;
					if(rotateShip)
						i = rotate();

					if( mouseX >= 331 && mouseX < 410 && 
						mouseY >= 105 && mouseY < 501 && 
						dragging) {

						// fixing blinking problem
						if(draggedX != 0 && draggedY != 0)
							graphic.drawImage(ships.get((i * 5) + 3), draggedX - 60, draggedY - 65 , null);
						// has been clicked
						moved3 = true;
					}

					else if(moved3 && draggedX >= LEFT_BORDER && draggedX < RIGHT_BORDER*10 &&
									  draggedY >= TOP_BORDER  && draggedY < BOTTOM_BORDER*10) {
						placeInGrid();
						// draws in the last spot dragged to locked in place
						graphic.drawImage(ships.get((i * 5) + 3), centerX - 65, centerY - 62, null);
					}

					else
						graphic.drawImage(ships.get((i * 5) + 3), 280, 210, null);	
					
					break;


				case 4:
					i = 0;
					if(rotateShip)
						i = rotate();

					if( mouseX >= 331 && mouseX < 410 && 
						mouseY >= 105 && mouseY < 501 && 
						dragging) {

						// fixing blinking problem
						if(draggedX != 0 && draggedY != 0)
							graphic.drawImage(ships.get((i * 5) + 4), draggedX - 20, draggedY - 30, null);
						// has been clicked
						moved4 = true;
					}

					else if(moved4 && draggedX >= LEFT_BORDER && draggedX < RIGHT_BORDER*10 &&
									  draggedY >= TOP_BORDER  && draggedY < BOTTOM_BORDER*10) {
						placeInGrid();
						// draws in the last spot dragged to locked in place
						graphic.drawImage(ships.get((i * 5) + 4), centerX, centerY - 60, null);
					}

					else
						graphic.drawImage(ships.get((i * 5) + 4), 330, 290, null);
				break;
			}
		}

		public void confirmedLocation() {
			if(confirmed == 1 && currentShip == 0) {
				shipLocations.add(0, new Coord(centerX - 60, centerY - 65, shipGridX, shipGridY, orientation));
				shipCoordinates.add(0, shipGridX + shipGridY);
				
				if(shipIsVertical)
					for(int i = 0; i < 5; i++) 				
						boundaries[0][i] = "" + (char)(shipGridX.charAt(0) + i) + shipGridY; 
				else 
					for(int i = 0; i < 5; i++) 				
						boundaries[0][i] = shipGridX  + ( "" + (char)(shipGridY.charAt(0) + i)); 
					
				orientations.add(0, String.valueOf(shipIsVertical));
				//resets x and y so that the new ship does go under the original ship locked in
				centerX = 340; // resets to outside the grid for the user to grab
				centerY = 205; // resets to outside the grid for the user to grab
				orientation = 0;
				rotateShip = false;
				shipIsVertical = true;
				selectingAirship = false;
				selectingBattleship = true;
				currentShip++;
			}
			
			else if(confirmed == 2 && currentShip == 1) {
				shipLocations.add(1, new Coord(centerX - 60, centerY - 65, shipGridX,shipGridY, orientation));
				shipCoordinates.add(1, shipGridX + shipGridY); 

				if(shipIsVertical)
					for(int i = 0; i < 5; i++) 				
						boundaries[1][i] = "" + (char)(shipGridX.charAt(0) + i) + shipGridY; 
				else 
					for(int i = 0; i < 5; i++) 				
						boundaries[1][i] = shipGridX  + ( "" + (shipGridY.charAt(0) + i));
		
				orientations.add(1, String.valueOf(shipIsVertical));
				centerX = 0;
				centerY = 0;
				orientation = 0;
				rotateShip = false;
				shipIsVertical = true;
				selectingBattleship = false;
				selectingCruiser = true;
				currentShip++;
			}
			
			else if(confirmed == 3 && currentShip == 2) {
				shipLocations.add(2, new Coord(centerX - 63, centerY - 65, shipGridX, shipGridY, orientation)); 
				shipCoordinates.add(2, shipGridX + shipGridY);

				if(shipIsVertical)
					for(int i = 0; i < 5; i++) 				
						boundaries[2][i] = "" + (char)(shipGridX.charAt(0) + i) + shipGridY; 
				else 
					for(int i = 0; i < 5; i++) 				
						boundaries[2][i] = shipGridX  + ( "" + (shipGridY.charAt(0) + i));
		
				orientations.add(2, String.valueOf(shipIsVertical));
				centerX = 0;
				centerY = 0;
				orientation = 0;
				rotateShip = false;
				shipIsVertical = true;
				selectingCruiser = false;
				selectingDestroyer = true;
				currentShip++;
			}
			
			else if(confirmed == 4 && currentShip == 3) {
				shipLocations.add(3, new Coord(centerX - 65, centerY - 62, shipGridX, shipGridY, orientation)); 
				shipCoordinates.add(3, shipGridX + shipGridY);

				if(shipIsVertical)
					for(int i = 0; i < 5; i++) 				
						boundaries[3][i] = "" + (char)(shipGridX.charAt(0) + i) + shipGridY; 
				else 
					for(int i = 0; i < 5; i++) 				
						boundaries[3][i] = shipGridX  + ( "" + (shipGridY.charAt(0) + i));
		
				orientations.add(3, String.valueOf(shipIsVertical));
				centerX = 0;
				centerY = 0;
				orientation = 0;
				rotateShip = false;
				shipIsVertical = true;
				selectingDestroyer = false;
				selectingSubmarine = true;
				currentShip++;
			}
			
			else if(confirmed == 5 && currentShip == 4) {
				shipLocations.add(4, new Coord(centerX, centerY - 60, shipGridX, shipGridY, orientation)); 
				shipCoordinates.add(4, shipGridX + shipGridY);

				if(shipIsVertical)
					for(int i = 0; i < 5; i++) 				
						boundaries[4][i] = "" + (char)(shipGridX.charAt(0) + i) + shipGridY; 
				else 
					for(int i = 0; i < 5; i++) 				
						boundaries[4][i] = shipGridX  + ( "" + (shipGridY.charAt(0) + i));
		
				orientations.add(4, String.valueOf(shipIsVertical));
				centerX = 0;
				centerY = 0;
				rotateShip = false;
				shipIsVertical = true;
				selectingSubmarine = false;
				currentShip++;
				ifFinished = true;
			}
		}

		private void lockShips(Graphics graphic) {
			if(!selectingAirship) {
				int layout = shipLocations.get(0).orientation();
				graphic.drawImage(ships.get(layout * 5), shipLocations.get(0).shipsX() , shipLocations.get(0).shipsY(), null);
			}
			
			if (currentShip >= 1 && confirmed >= 2 && !selectingBattleship) {
				int layout = shipLocations.get(1).orientation();
				graphic.drawImage(ships.get((layout * 5) + 1), shipLocations.get(1).shipsX() , shipLocations.get(1).shipsY(), null);
			}
			
			if (currentShip >= 2 && confirmed >= 3 && !selectingCruiser) {
				int layout = shipLocations.get(2).orientation();
				graphic.drawImage(ships.get((layout * 5) + 2), shipLocations.get(2).shipsX() , shipLocations.get(2).shipsY(), null);
			}
			
			if (currentShip >= 3 && confirmed >= 4 && !selectingDestroyer) {
				int layout = shipLocations.get(3).orientation();
				graphic.drawImage(ships.get((layout * 5) + 3), shipLocations.get(3).shipsX() , shipLocations.get(3).shipsY(), null);
			}

			if (currentShip >= 4 && confirmed >= 5 && !selectingSubmarine) {
				int layout = shipLocations.get(4).orientation();
				graphic.drawImage(ships.get((layout * 5) + 4), shipLocations.get(4).shipsX() , shipLocations.get(4).shipsY(), null);
			}
		}

		private void makeRotateButton(Graphics graphic) {
			Image rotate = getToolkit().getImage("./imgs/buttons/rotate.png");
			graphic.drawImage(rotate, 375, 500, null);
		}

		private int rotate() {
			return orientation;
		}

		private void makeConfirmButton(Graphics graphic) {
			Image check = getToolkit().getImage("./imgs/buttons/check.png");
			Image greenCheck = getToolkit().getImage("./imgs/buttons/check(green).png");
			if(confirmedLocation) 
				graphic.drawImage(greenCheck, 260, 500, null);
			else
				graphic.drawImage(check, 260, 500, null);
		}

	}


	public class Move implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent event) {
			dragging = true;
			draggedX = event.getX();
			draggedY = event.getY();

			if(!firstCoord().equals("-1") &&  !secondCoord().equals("-1")) {
				shipGridX = firstCoord();
				shipGridY = secondCoord();
			//	System.out.println("Ship X: " + shipGridX + " Ship Y: " + shipGridY);
			}

			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent event) {
			mouseX = event.getX();
			mouseY = event.getY();
			//System.out.println("mouseX: " + mouseX + " mouseY: " + mouseY);
		}


		//used for last dragged image coords
		private String firstCoord() {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {

					if(draggedX >= (i * 80) + LEFT_BORDER && draggedX < (i * 80) + RIGHT_BORDER &&
					   draggedY >= (j * 80) + TOP_BORDER  && draggedY < (j * 80) + BOTTOM_BORDER)
						return "" + (char)('A' + j);
				}
			}
			return "-1";
		}

		//used for last dragged image coords
		private String secondCoord() {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {

					if(draggedX >= (i * 80) + LEFT_BORDER && draggedX < (i * 80) + RIGHT_BORDER &&
					   draggedY >= (j * 80) + TOP_BORDER  && draggedY < (j * 80) + BOTTOM_BORDER)
						return i == 10 ? String.valueOf(10) : String.valueOf(i + 1);
				}
			}
			return "-1";
		}
	}

	public class MouseAction implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent event) {
			//if(firstCoord() != '0' &&  secondCoord() != -1)
				//System.out.println("In box: [" + firstCoord() + ", " + secondCoord() + "]");
			if(mouseOnRotate()) {
				if(orientation % 2 == 0)
					shipIsVertical = false;
				else
					shipIsVertical = true;

				//System.out.println("ITS TRUE");
				rotateShip = true;
				orientation = (orientation + 1) % 4;
			}
		}

		@Override
		public void mouseEntered(MouseEvent event) {

		}

		@Override
		public void mouseExited(MouseEvent event) {

		}

		@Override
		public void mousePressed(MouseEvent event) {
			pressed = true;

			if(mouseOnCheck()) {
				confirmed++;
				confirmedLocation = true;
			}


		}

		@Override
		public void mouseReleased(MouseEvent event) {
			pressed = false;
			dragging = false;

			lastX = event.getX();
			lastY = event.getY();


			if(mouseOnCheck())
				confirmedLocation = false;

			if(mouseOnRotate()) {
				//System.out.println("ITS FALSE");
				rotateShip = false;
			}

			//if(mouseOnRotate())
			//	rotateShip = false;
		}

		private char firstCoord() {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {

					if(mouseX >= (i * 80) + LEFT_BORDER && mouseX < (i * 80) + RIGHT_BORDER &&
					   mouseY >= (j * 80) + TOP_BORDER  && mouseY < (j * 80) + BOTTOM_BORDER)
						return (char) ('A' + j);
				}
			}
			return '0';
		}

		private int secondCoord() {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {

					if(mouseX >= (i * 80) + LEFT_BORDER && mouseX < (i * 80) + RIGHT_BORDER &&
					   mouseY >= (j * 80) + TOP_BORDER  && mouseY < (j * 80) + BOTTOM_BORDER)
						return i == 10 ? 10 : i + 1;
				}
			}
			return -1;
		}

		public boolean mouseOnCheck() {
			if(mouseX >= 268 && mouseX < 335 && mouseY >= 531 && mouseY < 596) 
				return true;
			return false;
		}

		public boolean mouseOnRotate() {
			if(mouseX >= 368 && mouseX < 443 && mouseY >= 537 && mouseY < 589)
				return true;
			return false;
		}
	}
}
