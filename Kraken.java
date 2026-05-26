import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class Kraken extends Actor
{
    private int maxHp = 20;
    private int krakenHp = 20;
    private HpBar krakenBar; 
    
    // Core Base Images
    private GreenfootImage straightTentacleImage;
    private GreenfootImage krakenHeadImage;
    
    // State Machine States
    private final int STATE_AIMING     = 0; // Huge thick red warning box appears
    private final int STATE_LAUNCHING  = 1; // Tentacle spawns and rushes across
    private final int STATE_RETRACTING = 2; // Tentacle pulls back out of bounds
    private final int STATE_VULNERABLE = 3; // Head appears in center for counter-attack
    private int currentState = STATE_AIMING;
    
    private int stateTimer = 90; // 90 frames = 1.5 seconds warning time
    
    // Wall Configuration variables
    private int attackSide = 0;       // 0 = Top, 1 = Bottom, 2 = Left, 3 = Right
    private int wallThickness = 280;  // Covers 70% of a 400px high screen height
    private TentacleWall activeWall;

    public Kraken()
    {
        straightTentacleImage = new GreenfootImage("octopus_tentacle_straight.png"); 
        krakenHeadImage = new GreenfootImage("octopus_head.png"); 
        krakenHeadImage.scale(70, 70); 
        
        // Pick a random side for the very first attack
        pickRandomAttackSide();
    }
    
    public void setHpBar(HpBar bar)
    {
        this.krakenBar = bar;
    }

    public void act()
    {
        switch (currentState)
        {
            case STATE_AIMING:
                updateKrakenAppearance(); // Constantly handles drawing the telegraph warning
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
                if (stateTimer <= 0) changeState(STATE_AIMING); // Reset loop
                break;
        }
    }
    
    private void changeState(int nextState)
    {
        currentState = nextState;
        
        if (krakenBar != null)
        {
            if (currentState == STATE_VULNERABLE) krakenBar.getImage().setTransparency(255);
            else krakenBar.getImage().setTransparency(0);
        }

        switch (nextState)
        {
            case STATE_AIMING:
                pickRandomAttackSide();
                stateTimer = 90; // 1.5 seconds to dodge to the safe zone!
                updateKrakenAppearance();
                break;
                
            case STATE_LAUNCHING:
                stateTimer = 20; // Rushes across screen in 1/3rd of a second
                spawnGiantTentacleWall();
                updateKrakenAppearance(); // Invisible root anchor body
                break;
                
            case STATE_RETRACTING:
                stateTimer = 40; // Takes a little bit longer to slide backwards out of bounds
                break;
                
            case STATE_VULNERABLE:
                stateTimer = 240; // 4 full seconds to shoot the head in the center!
                setLocation(300, 200); // Snap to exact center of world
                updateKrakenAppearance();
                break;
        }
    }

    private void pickRandomAttackSide()
    {
        attackSide = Greenfoot.getRandomNumber(4); // 0, 1, 2, or 3
        
        // Lock positioning off-screen during targeting telegraph so player only looks at the warning red overlay
        if (attackSide == 0) setLocation(300, -50);  // Top
        else if (attackSide == 1) setLocation(300, 450); // Bottom
        else if (attackSide == 2) setLocation(-50, 200); // Left
        else if (attackSide == 3) setLocation(650, 200); // Right
    }

    private void spawnGiantTentacleWall()
    {
        // Dimensions to cleanly swallow up 70% of the game space lengthways
        int width = (attackSide == 0 || attackSide == 1) ? 600 : wallThickness;
        int height = (attackSide == 0 || attackSide == 1) ? wallThickness : 400;
        
        GreenfootImage wallImg = new GreenfootImage(width, height);
        
        // Stretch your tentacle sprite texture to fill up the massive obstacle boundary frame
        wallImg.drawImage(straightTentacleImage, 0, 0); 
        // Polish step: Tile or paint it solid crimson so it matches a massive eldritch strike zone
        wallImg.setColor(new Color(150, 20, 20));
        wallImg.fillRect(0, 0, width, height); 
        
        activeWall = new TentacleWall(wallImg);
        getWorld().addObject(activeWall, getX(), getY());
    }

    private void handleLaunchingState()
    {
        // Drive the wall forward into the map based on attack vector
        if (attackSide == 0) activeWall.setLocation(activeWall.getX(), activeWall.getY() + 15); // Downwards
        else if (attackSide == 1) activeWall.setLocation(activeWall.getX(), activeWall.getY() - 15); // Upwards
        else if (attackSide == 2) activeWall.setLocation(activeWall.getX() + 15, activeWall.getY()); // Rightwards
        else if (attackSide == 3) activeWall.setLocation(activeWall.getX() - 15, activeWall.getY()); // Leftwards
        
        checkWallDamage();
    }
    
    private void handleRetractingState()
    {
        // Pull it back out of bounds backwards
        if (attackSide == 0) activeWall.setLocation(activeWall.getX(), activeWall.getY() - 10);
        else if (attackSide == 1) activeWall.setLocation(activeWall.getX(), activeWall.getY() + 10);
        else if (attackSide == 2) activeWall.setLocation(activeWall.getX() - 10, activeWall.getY());
        else if (attackSide == 3) activeWall.setLocation(activeWall.getX() + 10, activeWall.getY());
        
        checkWallDamage();
        if (stateTimer == 1 && activeWall != null && activeWall.getWorld() != null)
        {
            getWorld().removeObject(activeWall);
            activeWall = null;
        }
    }
    
    private void handleVulnerableState()
    {
        checkLaserCollision();
    }

    private void checkWallDamage()
    {
        if (activeWall != null && activeWall.getWorld() != null)
        {
            Hero h = activeWall.getTouchingHero();
            if (h != null) h.takeDamage(2); // Taking a wall to the face deals double damage!
        }
    }

    private void checkLaserCollision()
    {
        List<Lazer> lasers = getWorld().getObjects(Lazer.class);
        for (int i = 0; i < lasers.size(); i++)
        {
            Lazer currentLaser = lasers.get(i);
            int dx = currentLaser.getX() - this.getX();
            int dy = currentLaser.getY() - this.getY();
            
            if (Math.abs(dx) < 35 && Math.abs(dy) < 35)
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
        Label win = new Label("KRAKEN DEFEATED!", 60);
        win.setLineColor(Color.GREEN);
        getWorld().addObject(win, getWorld().getWidth()/2, getWorld().getHeight()/2);
        
        if (activeWall != null && activeWall.getWorld() != null) getWorld().removeObject(activeWall);
        getWorld().removeObject(this);
    }

    private void updateKrakenAppearance()
    {
        if (currentState == STATE_VULNERABLE)
        {
            setImage(krakenHeadImage);
        }
        else if (currentState == STATE_AIMING)
        {
            // Create a canvas covering the entire 600x400 viewable window frame
            GreenfootImage canvas = new GreenfootImage(600, 400);
            canvas.setColor(new Color(255, 0, 0, 95)); // Soft semi-transparent crimson tell
            
            // Draw a thick block matching the upcoming attack side zone
            if (attackSide == 0) canvas.fillRect(0, 0, 600, wallThickness); // Top slice
            else if (attackSide == 1) canvas.fillRect(0, 400 - wallThickness, 600, wallThickness); // Bottom slice
            else if (attackSide == 2) canvas.fillRect(0, 0, wallThickness, 400); // Left slice
            else if (attackSide == 3) canvas.fillRect(600 - wallThickness, 0, wallThickness, 400); // Right slice
            
            setImage(canvas);
            setLocation(300, 200); // Anchor canvas dead center to center warning perfectly
        }
        else
        {
            // During layout active state phases, hide the anchor node completely
            setImage(new GreenfootImage(1, 1)); 
        }
    }
    
    /**
     * Inner class helper managing the independent massive collision block mask
     */
    private class TentacleWall extends Actor {
        public TentacleWall(GreenfootImage customImg) {
            setImage(customImg);
        }
        public Hero getTouchingHero() {
            return (Hero) getOneIntersectingObject(Hero.class);
        }
    }
}