import java.util.ArrayList;

public class GUITranslater implements Runnable {
	private SelectionGUI selectionGUI = new SelectionGUI();
	private GUI gui = null;

	@Override
	public void run() {
		while(true) {
			selectionGUI.repaint();
			
			if(selectionGUI.finish()) {
				selectionGUI.setVisible(false);
				gui = new GUI(selectionGUI.getTitle(), selectionGUI.getShips(), selectionGUI.orientations());				
				break;
			}
		}
		while(gui != null) 
			gui.repaint();
		
	}


	public ArrayList<String> shipCoords() {
		return selectionGUI.shipCoord();
	}

	public ArrayList<String> orientations() {
		return selectionGUI.orientations();
	}

	public String playerName() {
		return selectionGUI.getName();
	}
	
	public boolean selectionStatus() {
		return selectionGUI.finish();
	}

}