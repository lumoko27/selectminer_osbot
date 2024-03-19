package core;

import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;


@ScriptManifest(name = "Selection miner", author = "me", version = 1.0, info = "", logo = "") 
public class Main extends Script {

	private Eventlistener eventListener;
	
	public ArrayList <Position> mineArray = new ArrayList <Position>();
	public String target = "Coal rocks";
	public String interaction = "Mine";
	
	public static final int SELECTION_STATE = 0;
	public static final int GATHERING_STATE = 1;
	public static final int BANKING_STATE = 2;
	
	public int currentState = SELECTION_STATE;
	
	private long startTime;
	
	Sleep sleep = new Sleep(() -> myPlayer().isAnimating(), 4000, 1000);
	

    @Override
    public void onStart() throws InterruptedException {

    	log("Script started!");
    	
    	eventListener = new Eventlistener(this);
    	
    	getBot().addMouseListener(eventListener.botMouseListener); //Adds the MouseListener
    	getBot().addKeyListener(eventListener.botKeyListener); //Adds the KeyListener
    	
    	
    	startTime = System.currentTimeMillis();
    	    	
    	getBot().getScriptExecutor().pause();
    	
    }

    @Override
    public void onExit() {

    	log("Script finished!");
    	
    }

    @Override
    public int onLoop() throws InterruptedException {
    	
    	switch(currentState) {
    	case SELECTION_STATE:
    		getBot().getScriptExecutor().pause();
    		break;
    	case GATHERING_STATE:
    		sleep.sleep();
    		gatherNode();
			break;
    	case BANKING_STATE:
    		bankInventory();
    		break;
    	}
    	
        return random(1000,2000); //The amount of time in milliseconds before the loop starts over
    }


    @Override


    public void onPaint(Graphics2D g) {
    	
    	final long runTime = System.currentTimeMillis() - startTime;
    	
    	g.setColor(Color.GREEN);
    	
    	g.drawString("Current Runtime: " + formatTime(runTime), 5, 300);
    	g.drawString("CurrentState: " + getStateName(currentState), 5, 320);
    	g.drawString("Number of nodes being gathered: " + mineArray.size(), 5 , 340);
    	
    	if(!mineArray.isEmpty()) {
    		for (Position p : mineArray) {
        		if (p.isVisible(getBot())) {
        			g.draw(p.getPolygon(getBot()).getBounds());
        		}
        	}
    	}
    }
	
	void gatherNode() throws InterruptedException {	
		
			if (getInventory().isFull()) {
				currentState = BANKING_STATE;
				}
			
			for (Position p : mineArray) {
				
				RS2Object node = getObjects().closest(object -> object.getName().equals(target) && object.getPosition().equals(new Position(p)));
				
				if (node != null) {
					node.interact(interaction);
					log("Interacting with object at in-game position: " + p);
					
					break;
					}
				}
			}
	
	void bankInventory() throws InterruptedException {
		
		final Area[] BANKS = {
				Banks.VARROCK_EAST, Banks.VARROCK_WEST, new Area(3011, 9720, 3014, 9717), Banks.LUMBRIDGE_UPPER 
		};
		
		getWalking().webWalk(BANKS);
		
		getBank().open();
		
	    if (getBank().isOpen()) {
	        getBank().depositAll();
	        
	        if (!getInventory().isFull()) {
	        	Area nodeArea = new Area(mineArray.get(0).getX() - 10, mineArray.get(0).getY() - 10, mineArray.get(0).getX(), mineArray.get(0).getY());
	        	
	        	while (!nodeArea.contains(myPosition())) {
	        		getWalking().webWalk(nodeArea);
	        		
	        	}
	        	
	            currentState = GATHERING_STATE;
	        }
	    }
	}
	
	String getStateName(int state) {
        switch (state) {
            case SELECTION_STATE:
                return "SELECTION_STATE";
            case GATHERING_STATE:
                return "GATHERING_STATE";
            case BANKING_STATE:
                return "BANKING_STATE";
            default:
                return "UNKNOWN_STATE";
        }
    }
    
	public final String formatTime(final long ms){
	    long s = ms / 1000, m = s / 60, h = m / 60;
	    s %= 60; m %= 60; h %= 24;
	    return String.format("%02d:%02d:%02d", h, m, s);
	}
	
	
}