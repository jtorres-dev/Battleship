import java.util.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/*
TODO: 
- ~paint server chat~
- host on server
- waits for both players. Prints "Awaiting other player ..." while another player isnt connected
- Welcome players once both players connect
- then says "Player _ is choosing their name" to their opponent
- Ask for player to type in Name with dialog box
- once player enters name, if other player hasn't typed in, server doesn't continue
- if both players entered a name, ~paint setupboat screen~:
	- has rotate button
	- has ready button
	- **has reset button
	
	- has boats aligned on left side to choose from
	- lets user click and drag a location on the grid
- once user selects ready,
	- paints new screen with opponents current ship and your current ship vertically

*/
public class GUI extends JFrame {
	private int spacing = 1; /* for lines in grid */
	private int mouseX, mouseY, click;
	private int draggedX, draggedY;
	private static String name;

	private List<Image> ships = new ArrayList<Image>();
	private List<Image> ships2 = new ArrayList<Image>();


	private static ArrayList<Coord> shipLocations;


	private final int LEFT_BORDER = 774;
	private final int RIGHT_BORDER = 851;
	private final int TOP_BORDER = 76;
	private final int BOTTOM_BORDER = 155;

	private final int MINI_LEFT_BORDER = 59;
	private final int MINI_RIGHT_BORDER = 85;
	private final int MINI_TOP_BORDER = 77;
	private final int MINI_BOTTOM_BORDER = 102;
 

	public GUI(String name, ArrayList<Coord> shipLocations) {
		this.name = name;
		this.shipLocations = shipLocations;

		ships.add(0, getToolkit().getImage("./imgs/ships/airship/0.png"));
		ships.add(1, getToolkit().getImage("./imgs/ships/battleship/0.png"));
		ships.add(2, getToolkit().getImage("./imgs/ships/cruiser/0.png"));
		ships.add(3, getToolkit().getImage("./imgs/ships/destroyer/0.png"));
		ships.add(4, getToolkit().getImage("./imgs/ships/submarine/0.png"));

		ships.add(5, getToolkit().getImage("./imgs/ships/airship/1.png"));
		ships.add(6, getToolkit().getImage("./imgs/ships/battleship/1.png"));
		ships.add(7, getToolkit().getImage("./imgs/ships/cruiser/1.png"));
		ships.add(8, getToolkit().getImage("./imgs/ships/destroyer/1.png"));
		ships.add(9, getToolkit().getImage("./imgs/ships/submarine/1.png"));
		
		ships.add(10, getToolkit().getImage("./imgs/ships/airship/2.png"));
		ships.add(11, getToolkit().getImage("./imgs/ships/battleship/2.png"));
		ships.add(12, getToolkit().getImage("./imgs/ships/cruiser/2.png"));
		ships.add(13, getToolkit().getImage("./imgs/ships/destroyer/2.png"));
		ships.add(14, getToolkit().getImage("./imgs/ships/submarine/2.png"));
		
		ships.add(15, getToolkit().getImage("./imgs/ships/airship/3.png"));
		ships.add(16, getToolkit().getImage("./imgs/ships/battleship/3.png"));
		ships.add(17, getToolkit().getImage("./imgs/ships/cruiser/3.png"));
		ships.add(18, getToolkit().getImage("./imgs/ships/destroyer/3.png"));
		ships.add(19, getToolkit().getImage("./imgs/ships/submarine/3.png"));

		setTitle("Battleship");
		setSize(1600, 900);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setContentPane(new InGame());
		addMouseMotionListener(new Move());
		addMouseListener(new MouseAction());

	}

	public class InGame extends JPanel {
		boolean moved = false;

		public void paintComponent(Graphics graphic) {

			/* main background color (navyish) */
			graphic.setColor(new Color(40, 69, 114));
		
			graphic.fillRect(0, 0, 1600, 900);

			produceGrid(graphic);
			produceAxisTitles(graphic);	
			produceLayout(graphic);
			produceMiniMap(graphic);
			produceChat(graphic);
			pOneShips(graphic);
			pTwoShips(graphic);


		}

		private void produceGrid(Graphics graphic) {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {
					graphic.setColor(new Color(122, 189, 255));

					if(mouseX >= (i * 80) + LEFT_BORDER && mouseX < (i * 80) + RIGHT_BORDER &&
					   mouseY >= (j * 80) + TOP_BORDER  && mouseY < (j * 80) + BOTTOM_BORDER)
						graphic.setColor(Color.gray);
				
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
				/* Image img = getToolkit().getImage("node.jpg"); */
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
				aThruJ.add(new ImageIcon("./imgs/coordinates/"+ (char)( 'A' + i ) +".png").getImage());
			
			int box = 0;
			for(Image img : aThruJ) {
				if(img.getWidth(null) <= 21)
					graphic.drawImage(img, 746, (spacing + box * 80 + 70), 4, 24, null);
				else 
					graphic.drawImage(img, 738, (spacing + box * 80 + 70), 16, 24, null);
				box++;
			}
		}

		private void produceLayout(Graphics graphic) {
			for(int i = 0;  i < shipLocations.size(); i++)
				graphic.drawImage(ships.get(i), shipLocations.get(i).shipsX() , shipLocations.get(i).shipsY(), null);
		}

		private void produceMiniMap(Graphics graphic) {
			produceMiniAxisTitles(graphic);
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {
					graphic.setColor(new Color(122, 189, 255));

					if(mouseX >= (i * 27) + MINI_LEFT_BORDER && mouseX < (i * 27) + MINI_RIGHT_BORDER &&
					   mouseY >= (j * 27) + MINI_TOP_BORDER  && mouseY < (j * 27) + MINI_BOTTOM_BORDER)
						graphic.setColor(Color.gray);
				
					graphic.fillRect((spacing + i * 27 + 50), (spacing + j * 27 + 45), 25, 25);
				}
			}
		}

		private void produceMiniAxisTitles(Graphics graphic) {
			produceMiniXAxis(graphic);
			produceMiniYAxis(graphic);
		}

		private void produceMiniXAxis(Graphics graphic) {
			List<Image> oneThruTen = new ArrayList<Image>();

			for(int i = 1; i <= 10; i++) 
				oneThruTen.add(new ImageIcon("./imgs/coordinates/"+i+".png").getImage());
			
			int box = 0;
			for(Image img : oneThruTen) {
				if(img.getWidth(null) >= 130)
					graphic.drawImage(img, (spacing + box * 27 + 53), (spacing + 28), 20, 10, null);
				else if(img.getWidth(null) <= 21)
					graphic.drawImage(img, (spacing + box * 27 + 55), (spacing + 28), 8, 10, null);
				else
					graphic.drawImage(img, (spacing + box * 27 + 55), (spacing + 28), 10, 10, null);
				box++;
			}
		}
		
		private void produceMiniYAxis(Graphics graphic) {
			List<Image> aThruJ = new ArrayList<Image>();
			
			for(int i = 0; i < 10; i++) 
				aThruJ.add(new ImageIcon("./imgs/coordinates/"+ (char)( 'A' + i ) +".png").getImage());
			
			int box = 0;
			for(Image img : aThruJ) {
				if(img.getWidth(null) <= 21)
					graphic.drawImage(img, 37, (spacing + box * 27 + 52), 2, 10, null);
				else 
					graphic.drawImage(img, 30, (spacing + box * 27 + 52), 12, 10, null);
				box++;
			}
		}



		private void produceChat(Graphics graphic) {
			graphic.setColor(Color.white);
			graphic.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

			char[] title = "Chat".toCharArray();
			graphic.drawChars(title, 0, 4, 400, 210);
			graphic.setColor(Color.gray);
			graphic.fillRect(400,215,280,630);
			graphic.clearRect(405, 220, 270, 620);
		}



		private void pOneShips(Graphics graphic) {
			graphic.setColor(Color.white);
			graphic.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 35));

			char[] title = "Player 1:".toCharArray();
			graphic.drawChars(title, 0, 9, 5, 385);



			
			// if(mouseX >= 38 && mouseX < 76 && mouseY >= 433 && mouseY < 591 && moved == false) {
			// 	
			// 	moved = true;
			// } 
			// else if(moved && draggedX != 0 && draggedY != 0) 
			// 	graphic.drawImage(ships.get(0), draggedX - 60, draggedY - 65, 100, 160, null);
			// else 
			// 	graphic.drawImage(ships.get(0),  0, 400, 100, 160, null);
			
			graphic.drawImage(ships.get(0), 0, 400, 100, 160, null);
			graphic.drawImage(ships.get(1),  80, 422, 80, 140,  null);
			graphic.drawImage(ships.get(2), 145, 440, 80, 130,  null);
			graphic.drawImage(ships.get(3), 210, 442, 80, 120,  null);
			graphic.drawImage(ships.get(4), 305, 472, 20, 90,  null);
		}

		private void pTwoShips(Graphics graphic) {
			graphic.setColor(Color.white);
			graphic.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 35));

			char[] title = "Player 2:".toCharArray();
			graphic.drawChars(title, 0, 9, 5, 635);

			ships2.add(getToolkit().getImage("./imgs/ships/airship/airship_carrier(o).png"));
			ships2.add(getToolkit().getImage("./imgs/ships/battleship/battleship(o).png"));
			ships2.add(getToolkit().getImage("./imgs/ships/cruiser/cruiser(o).png"));
			ships2.add(getToolkit().getImage("./imgs/ships/destroyer/destroyer(o).png"));
			ships2.add(getToolkit().getImage("./imgs/ships/submarine/submarine(o).png"));

			graphic.drawImage(ships2.get(0),  0, 650, 100, 160, null);
			graphic.drawImage(ships2.get(1),  80, 672, 80, 140,  null);
			graphic.drawImage(ships2.get(2), 145, 690, 80, 130,  null);
			graphic.drawImage(ships2.get(3), 210, 692, 80, 120,  null);
			graphic.drawImage(ships2.get(4), 305, 722, 20, 90,  null);
		}

	}

	public class Move implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent event) {
			draggedX = event.getX();
			draggedY = event.getY();
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent event) {
			mouseX = event.getX();
			mouseY = event.getY();
			System.out.println("mouseX: " + mouseX + " mouseY: " + mouseY);
		}
	}

	public class MouseAction implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent event) {
			if(firstCoord() != -1 &&  secondCoord() != -1)
				System.out.println("In box: [" + (char) firstCoord() + ", " + secondCoord() + "]");
		}

		@Override
		public void mouseEntered(MouseEvent event) {

		}

		@Override
		public void mouseExited(MouseEvent event) {

		}

		@Override
		public void mousePressed(MouseEvent event) {

		}

		@Override
		public void mouseReleased(MouseEvent event) {

		}

		public int firstCoord() {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {

					if(mouseX >= (i * 80) + LEFT_BORDER && mouseX < (i * 80) + RIGHT_BORDER &&
					   mouseY >= (j * 80) + TOP_BORDER  && mouseY < (j * 80) + BOTTOM_BORDER)
						return 'A' + j;
				}
			}
			return -1;
		}

		public int secondCoord() {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {

					if(mouseX >= (i * 80) + LEFT_BORDER && mouseX < (i * 80) + RIGHT_BORDER &&
					   mouseY >= (j * 80) + TOP_BORDER  && mouseY < (j * 80) + BOTTOM_BORDER)
						return i == 10 ? 10 : i + 1;
				}
			}
			return -1;
		}
	}


}
