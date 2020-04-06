import java.util.ArrayList;
import java.util.Date;
public class GUITranslater implements Runnable {
	private SelectionGUI selectionGUI = new SelectionGUI();
	private GUI gui = null;

	@Override
	public void run() {
		while(true) {
			if(selectionGUI.finish()) {
				selectionGUI.setVisible(false);
				gui = new GUI(selectionGUI.getTitle(), selectionGUI.getShips());				
				break;
			}
			selectionGUI.repaint();
		}
		//timer();
		while(gui != null) 
			gui.repaint();
		
	}

	private void timer() {
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0L;

		while (elapsedTime < 2*1000) {
		    elapsedTime = (new Date()).getTime() - startTime;
		}
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