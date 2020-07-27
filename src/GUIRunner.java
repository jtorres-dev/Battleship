public class GUIRunner implements Runnable {
	SelectionGUI selectionGUI = new SelectionGUI();
	GUI gui = null;

	public static void main(String[] args) {
		new Thread(new GUIRunner()).start();
	}

	@Override
	public void run() {
		while(selectionGUI != null) {
			if(selectionGUI.finish()) {
				selectionGUI.setVisible(false);
				gui = new GUI(selectionGUI.getPlayer(), selectionGUI.getShips());				
				selectionGUI = null;
				break;
			}
			selectionGUI.repaint();
		}
		while(gui != null)
			gui.repaint();
	}
}