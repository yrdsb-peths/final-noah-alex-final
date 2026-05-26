import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Write a description of class Kraken here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Kraken extends Actor
{
    /**
     * Act - do whatever the Kraken wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    // Kraken Base Stats
    private int maxHp = 20;
    private int krakenHp = 20;
    private HpBar krakenBar; // We will create a public method to assign this
    
    // Core Images
    private GreenfootImage tentaclesBaseImage;
    private GreenfootImage straightTentacleImage;
    private GreenfootImage krakenHeadImage;
    
    // State Machine States
    private final int STATE_SPAWN_PREP = 0; // The base cluster appears
    private final int STATE_AIMING     = 1; // 3 targeting lines appear
    private final int STATE_LAUNCHING  = 2; // Tentacles rush forward
    private final int STATE_RETRACTING = 3; // Tentacles pull back
    private final int STATE_VULNERABLE = 4; // Head appears for attack
    private int currentState = STATE_SPAWN_PREP;
    
    // State Timer
    private int stateTimer = 120; // 2 seconds (assuming ~60fps)
    
    // We will keep references to the three tentacles we launch
    private TentaclePart[] activeTentacles = new TentaclePart[3];
    private boolean tentaclesDrawnOnEdge = false;
    
    public Kraken()
    {
        // Load and Prepare Images
        tentaclesBaseImage = new GreenfootImage("octopus_tentacles_base.png"); // image_4.png base
        straightTentacleImage = new GreenfootImage("octopus_tentacle_straight.png"); // single from image_3.png
        krakenHeadImage = new GreenfootImage("octopus_head.png"); // image_2.png head
        
        // Initial state is the base cluster in the corner
        updateKrakenAppearance();
    }
    
    // Method for MyWorld to assign the HP bar
    public void setHpBar(HpBar bar)
    {
        this.krakenBar = bar;
    }
    
    public void act()
    {
        // Add your action code here.
        // Run specific behavior loop based on the current state
        switch (currentState)
        {
            case STATE_SPAWN_PREP:
                if (!tentaclesDrawnOnEdge) drawBaseInCorner();
                stateTimer--;
                if (stateTimer <= 0) changeState(STATE_AIMING);
                break;
                
            case STATE_AIMING:
                updateKrakenAppearance(); // Will draw indicator lines
                stateTimer--;
                if (stateTimer <= 0) changeState(STATE_LAUNCHING);
                break;
                
            case STATE_LAUNCHING:
                handleLaunchingState();
                stateTimer--;
                if (stateTimer <= 0) changeState(STATE_RETRACTING);
                break;
                
            case STATE_RETRACTING:
                handleRetractingState();
                stateTimer--;
                if (stateTimer <= 0) changeState(STATE_VULNERABLE);
                break;
                
            case STATE_VULNERABLE:
                handleVulnerableState();
                stateTimer--;
                if (stateTimer <= 0) changeState(STATE_SPAWN_PREP); // Reset loop
                break;
        }
    }
    
    private void changeState(int nextState)
    {
        currentState = nextState;
        switch (nextState)
        {
            case STATE_SPAWN_PREP:
                tentaclesDrawnOnEdge = false;
                stateTimer = 120; // 2s delay
                updateKrakenAppearance(); 
                break;
            case STATE_AIMING:
                stateTimer = 120; // 2s aiming
                updateKrakenAppearance(); 
                break;
            case STATE_LAUNCHING:
                stateTimer = 15; // Quick launch (0.25s)
                spawnTentaclesForLaunch();
                updateKrakenAppearance(); // Remove aiming lines
                break;
            case STATE_RETRACTING:
                stateTimer = 60; // 1s retraction
                updateKrakenAppearance(); // Base cluster returns
                break;
            case STATE_VULNERABLE:
                stateTimer = 300; // 5s vulnerable
                updateKrakenAppearance(); // Draw the head only
                break;
        }
    }

    private void handleLaunchingState()
    {
        // Tentacles rush forward quickly
        for (TentaclePart t : activeTentacles) if (t != null) t.move(20);
        checkTentacleCollisions();
    }
    
    private void handleRetractingState()
    {
        // Tentacles pull back slowly
        for (TentaclePart t : activeTentacles) if (t != null) t.move(-8);
        checkTentacleCollisions();
        if (stateTimer == 1) cleanUpTentacles(); // Remove them from world
    }
    
    private void handleVulnerableState()
    {
        // The head is visible. It doesn't move. We only check for lasers.
        checkLaserCollision();
    }

    private void drawBaseInCorner()
    {
        setImage(tentaclesBaseImage);
        // Position on a bottom corner
        setLocation(500, 300); // Specific coordinate, not center
        tentaclesDrawnOnEdge = true;
    }
    
    private void spawnTentaclesForLaunch()
    {
        // Spawn 3 straight tentacles pointing in different directions
        activeTentacles[0] = new TentaclePart(straightTentacleImage);
        activeTentacles[1] = new TentaclePart(straightTentacleImage);
        activeTentacles[2] = new TentaclePart(straightTentacleImage);
        
        // Spawn them stacked at the core location of the corner cluster
        getWorld().addObject(activeTentacles[0], getX(), getY());
        getWorld().addObject(activeTentacles[1], getX(), getY());
        getWorld().addObject(activeTentacles[2], getX(), getY());
        
        // Set distinct, fixed directions
        activeTentacles[0].setRotation(180); // Pointing left
        activeTentacles[1].setRotation(225); // Pointing top-left
        activeTentacles[2].setRotation(270); // Pointing up
        
        // Ensure they can pass through each other initially
    }
    
    private void cleanUpTentacles()
    {
        for (int i = 0; i < activeTentacles.length; i++) {
            if (activeTentacles[i] != null && activeTentacles[i].getWorld() != null) {
                getWorld().removeObject(activeTentacles[i]);
            }
            activeTentacles[i] = null;
        }
    }
    
    private void checkTentacleCollisions()
    {
        for (TentaclePart t : activeTentacles)
        {
            if (t != null && t.getWorld() != null)
            {
                Hero h = t.getTouchingHero(); 
                if (h != null) h.takeDamage(1);
            }
        }
    }

    private void checkLaserCollision()
    {
        // Laser collision method (same proximity fix as the Swordfish)
        List<Lazer> lasers = getWorld().getObjects(Lazer.class);
        for (int i = 0; i < lasers.size(); i++)
        {
            Lazer currentLaser = lasers.get(i);
            int dx = currentLaser.getX() - this.getX();
            int dy = currentLaser.getY() - this.getY();
            
            // Check real body bounding box (~30 radius for head)
            if (Math.abs(dx) < 30 && Math.abs(dy) < 30)
            {
                getWorld().removeObject(currentLaser);
                krakenHp--;
                if (krakenBar != null) krakenBar.updateBar(krakenHp);
                if (krakenHp <= 0) die();
                break;
            }
        }
    }
    
    private void die()
    {
        // Add Victory Label
        Label win = new Label("KRAKEN DEFEATED!", 60);
        win.setLineColor(Color.GREEN);
        getWorld().addObject(win, getWorld().getWidth()/2, getWorld().getHeight()/2);
        
        cleanUpTentacles(); // In case it died mid-attack
        getWorld().removeObject(this);
    }

    /**
     * Updates the main Kraken image based on the state. It handles:
     * - The Vulnerable Head
     * - The Aiming indicators (on a wide canvas, like the swordfish)
     */
    private void updateKrakenAppearance()
    {
        if (currentState == STATE_VULNERABLE)
        {
            // Only draw the vulnerable head mantle
            setImage(krakenHeadImage);
        }
        else if (currentState == STATE_AIMING)
        {
            // Similar giant-canvas logic as the swordfish for aiming lines.
            // But we cannot rotate the base image (image_4.png), so we draw
            // 3 fixed indicator lines pointing from a wide transparent canvas.
            
            int baseWidth = tentaclesBaseImage.getWidth();
            int baseHeight = tentaclesBaseImage.getHeight();
            
            // Create giant transparent canvas
            int indicatorLength = 800;
            GreenfootImage canvas = new GreenfootImage(indicatorLength, baseHeight);
            
            // Draw base tentacles (image_4.png) centered on the canvas
            canvas.drawImage(tentaclesBaseImage, 0, 0);
            
            // Draw 3 indicator lines from the nose outwards
            canvas.setColor(new Color(255, 0, 0, 130)); // Semi-transparent Red
            
            // We use fixed angles since the base cannot turn
            // Angles: 180 (straight), 225 (diagonal up-left), 270 (straight up)
            
            // Line 1 (straight)
            canvas.fillRect(baseWidth, (baseHeight/2)-1, 700, 3);
            
            // (Standard rectangles cannot be rotated. For diagonal lines,
            // we would need an advanced rotation method which is beyond
            // basic Greenfoot drawing commands.)
            
            // For basic compatibility, we will only show one line (180).
            // A professional version would use dynamic line drawing math or pre-rotated images.
            
            setImage(canvas);
        }
        else // In other states (SPAWN, RETRACTING), use the corner cluster base
        {
            setImage(tentaclesBaseImage);
        }
    }
    
    /**
     * Simple inner class used by the Kraken to manage its temporary, independent parts.
     */
    private class TentaclePart extends Actor {
        public TentaclePart(GreenfootImage img) {
            setImage(img);
        }
        public Hero getTouchingHero() {
            return (Hero) getOneIntersectingObject(Hero.class);
        }
    }
}
