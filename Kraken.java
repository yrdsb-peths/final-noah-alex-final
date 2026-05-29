import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class Kraken extends Actor
{
    private int maxHp = 35;
    private int krakenHp = 35;
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
    
    // Wall Configuration variables (Scaled for 800x600 World)
    private int attackSide = 0;       // 0 = Top, 1 = Bottom, 2 = Left, 3 = Right
    private int wallThickness = 420;  // Covers 70% of a 600px high screen height (or 800px width dynamically)
    private TentacleWall activeWall;

    public Kraken()
    {
        straightTentacleImage = new GreenfootImage("octopus_tentacle_straight.png"); 
        krakenHeadImage = new GreenfootImage("octopus_head.png"); 
        krakenHeadImage.scale(90, 90); // Slightly scaled up head to match larger world resolution
        
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
                stateTimer = 25; // Adjusted slightly from 20 to 25 to accommodate traveling a longer distance
                spawnGiantTentacleWall();
                updateKrakenAppearance(); // Invisible root anchor body
                break;
                
            case STATE_RETRACTING:
                stateTimer = 45; // Adjusted slightly for the larger arena frame layout
                break;
                
            case STATE_VULNERABLE:
                stateTimer = 240; // 4 full seconds to shoot the head in the center!
                setLocation(400, 300); // Snap to exact center of 800x600 world
                updateKrakenAppearance();
                break;
        }
    }

    private void pickRandomAttackSide()
    {
        attackSide = Greenfoot.getRandomNumber(4); // 0, 1, 2, or 3
        
        // Lock positioning deep off-screen relative to 800x600 limits
        if (attackSide == 0) setLocation(400, -100);  // Top
        else if (attackSide == 1) setLocation(400, 700); // Bottom
        else if (attackSide == 2) setLocation(-100, 300); // Left
        else if (attackSide == 3) setLocation(900, 300); // Right
    }

    private void spawnGiantTentacleWall()
    {
        int width = (attackSide == 0 || attackSide == 1) ? 800 : wallThickness;
        int height = (attackSide == 0 || attackSide == 1) ? wallThickness : 600;
        GreenfootSound clash = new GreenfootSound("tentacles.mp3");
        clash.play();
        GreenfootImage wallImg = new GreenfootImage(width, height);
        
        // Fill the wall with a solid color to match the warning zone perfectly
        wallImg.setColor(new Color(150, 20, 20));
        wallImg.fillRect(0, 0, width, height); 
        
        activeWall = new TentacleWall(wallImg);
        
        // --- SCALED STARTING POSITIONS ---
        int startX = 400;
        int startY = 300;
        
        if (attackSide == 0) // Warning is at TOP. Wall spawns ABOVE ceiling, rushes DOWN
        {
            startX = 400;
            startY = -(height / 2);
        }
        else if (attackSide == 1) // Warning is at BOTTOM. Wall spawns BELOW floor, rushes UP
        {
            startX = 400;
            startY = 600 + (height / 2);
        }
        else if (attackSide == 2) // Warning is on LEFT. Wall spawns past LEFT edge, rushes RIGHT
        {
            startX = -(width / 2);
            startY = 300;
        }
        else if (attackSide == 3) // Warning is on RIGHT. Wall spawns past RIGHT edge, rushes LEFT
        {
            startX = 800 + (width / 2);
            startY = 300;
        }
        
        getWorld().addObject(activeWall, startX, startY);
    }

    private void handleLaunchingState()
    {
        if (activeWall == null) return;

        // Drive the wall forward until it perfectly fills the red warning box, then STOP IT
        // Speed slightly increased (+25) to compensate for larger spatial distance coverage requirements
        if (attackSide == 0) // Moving DOWN to fill the top slice
        {
            if (activeWall.getY() < (wallThickness / 2)) {
                activeWall.setLocation(activeWall.getX(), activeWall.getY() + 25);
            }
        }
        else if (attackSide == 1) // Moving UP to fill the bottom slice
        {
            if (activeWall.getY() > 600 - (wallThickness / 2)) {
                activeWall.setLocation(activeWall.getX(), activeWall.getY() - 25);
            }
        }
        else if (attackSide == 2) // Moving RIGHT to fill the left slice
        {
            if (activeWall.getX() < (wallThickness / 2)) {
                activeWall.setLocation(activeWall.getX() + 25, activeWall.getY());
            }
        }
        else if (attackSide == 3) // Moving LEFT to fill the right slice
        {
            if (activeWall.getX() > 800 - (wallThickness / 2)) {
                activeWall.setLocation(activeWall.getX() - 25, activeWall.getY());
            }
        }
        
        checkWallDamage();
    }
    
    private void handleRetractingState()
    {
        // Pull it back out of bounds backwards
        if (attackSide == 0) activeWall.setLocation(activeWall.getX(), activeWall.getY() - 15);
        else if (attackSide == 1) activeWall.setLocation(activeWall.getX(), activeWall.getY() + 15);
        else if (attackSide == 2) activeWall.setLocation(activeWall.getX() - 15, activeWall.getY());
        else if (attackSide == 3) activeWall.setLocation(activeWall.getX() + 15, activeWall.getY());
        
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

    public void takeDamage(int amount)
    {
        krakenHp -= amount;
        if (krakenBar != null) krakenBar.updateBar(krakenHp);
        if (krakenHp <= 0) die();
    }

    public boolean isVulnerable()
    {
        return currentState == STATE_VULNERABLE;
    }

    private void checkWallDamage()
{
    if (activeWall != null && activeWall.getWorld() != null)
    {
        // Tick down the wall's internal damage block timer
        if (activeWall.damageCooldown > 0) {
            activeWall.damageCooldown--;
        }

        Hero h = activeWall.getTouchingHero();
        // Only deal damage if the hero is touching it AND the wall's cooldown is 0
        if (h != null && activeWall.damageCooldown == 0) 
        {
            h.takeDamage(4); 
            activeWall.damageCooldown = 100; // Lock this specific wall from dealing damage for 45 frames!
        }
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
            
            // Hitbox threshold slightly increased to match expanded head scaling bounds (70 -> 90)
            if (Math.abs(dx) < 45 && Math.abs(dy) < 45)
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
        if (activeWall != null && activeWall.getWorld() != null) getWorld().removeObject(activeWall);
        getWorld().removeObject(this);
        Greenfoot.setWorld(new CutsceneWorld());
    }

    private void updateKrakenAppearance()
    {
        if (currentState == STATE_VULNERABLE)
        {
            setImage(krakenHeadImage);
        }
        else if (currentState == STATE_AIMING)
        {
            // Create a canvas covering the updated 800x600 viewable window frame
            GreenfootImage canvas = new GreenfootImage(800, 600);
            canvas.setColor(new Color(255, 0, 0, 95)); // Soft semi-transparent crimson tell
            
            // Draw a thick block matching the upcoming attack side zone
            if (attackSide == 0) canvas.fillRect(0, 0, 800, wallThickness); // Top slice
            else if (attackSide == 1) canvas.fillRect(0, 600 - wallThickness, 800, wallThickness); // Bottom slice
            else if (attackSide == 2) canvas.fillRect(0, 0, wallThickness, 600); // Left slice
            else if (attackSide == 3) canvas.fillRect(800 - wallThickness, 0, wallThickness, 600); // Right slice
            
            setImage(canvas);
            setLocation(400, 300); // Anchor canvas dead center to layout warning perfectly over 800x600
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
    /**
     * Inner class helper managing the independent massive collision block mask
     */
    private class TentacleWall extends Actor {
        // ADDED: Each wall spawned keeps track of its own unique damage cooldown frame state
        public int damageCooldown = 0; 
        
        public TentacleWall(GreenfootImage customImg) {
            setImage(customImg);
        }
        public Hero getTouchingHero() {
            return (Hero) getOneIntersectingObject(Hero.class);
        }
    }
}