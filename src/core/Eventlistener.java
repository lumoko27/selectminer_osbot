package core;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.input.mouse.BotMouseListener;


public class Eventlistener {

    private Main main;  // Reference to the Main class

    public Eventlistener(Main main) {
        this.main = main;
    }

    BotKeyListener botKeyListener = new BotKeyListener() {
        @Override
        public void checkKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_F1) {
                main.log("F1 pressed: select nodes mode active");
                main.currentState = Main.SELECTION_STATE;
                keyEvent.consume();
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_F2) {
                main.log("F2 pressed: resume gathering");
                main.currentState = Main.GATHERING_STATE;
                keyEvent.consume();
            }
        }
    };
	
	BotMouseListener botMouseListener = new BotMouseListener() {
	    
	    @Override
	    public void checkMouseEvent(MouseEvent mouseEvent) {
	        
	    	if (main.currentState == Main.SELECTION_STATE) {
		    	if (mouseEvent.getButton() == 1) { // Left-click
		            // Get the position of the clicked object
		        	Point mousePosition = main.mouse.getPosition();

		            // Convert screen coordinates to in-game tile coordinates
		            Position clickedPosition = getPositionUnderMouse(mousePosition);
		            
		            if (clickedPosition != null && !main.mineArray.contains(clickedPosition)) {
			            
		            	// check if position contains the desired object
			            try {
			            	RS2Object node = main.getObjects().get(clickedPosition.getX(), clickedPosition.getY()).get(0);
    			            
						    if (node.getName().equals(main.target)) {
						        // Add position to array
						        main.mineArray.add(clickedPosition);

							    main.log("Left click on object at in-game position: " + clickedPosition);
							    mouseEvent.consume();
							    }
			            } catch (Exception e) {}
		            }
		
		        } else if (mouseEvent.getButton() == 3) {
		            // Get the position of the clicked object
		        	Point mousePosition = main.mouse.getPosition();

		            // Convert screen coordinates to in-game tile coordinates
		            Position clickedPosition = getPositionUnderMouse(mousePosition);


		            if (main.mineArray.contains(clickedPosition)) {
			            // Remove the position in the ArrayList
		            	main.mineArray.remove(clickedPosition);
			            
			            main.log("Removed from array object at in-game position: " + clickedPosition);
			            mouseEvent.consume();
		            } else {
		            	
		            	main.log("Array dont contain clicked object at in-game position " + clickedPosition);
		            	mouseEvent.consume();
		            }
		        }
		    	mouseEvent.consume();
	    	}
	    }
	};
	
	private Position getPositionUnderMouse(Point mousePosition) {
		
		for(int x = 0; x < 104; x++) {
			for(int y = 0; y < 104; y++) {
				Position pos = new Position(main.getMap().getBaseX()+x, main.getMap().getBaseY()+y, main.getMap().getPlane());
				if(pos.isVisible(main.getBot()) && pos.getPolygon(main.getBot()).contains(mousePosition)) {
					return pos;
				}
			}
		}
		
		return null;
	}
}
