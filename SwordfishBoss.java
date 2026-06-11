import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Write a description of class SwordfishBoss here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SwordfishBoss extends Actor
{
    /**
     * Act - do whatever the SwordfishBoss wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    GreenfootSound slash = new GreenfootSound("swordfish.mp3");
    // Boss Core Stats
    private int bossHp = 6;
    private GreenfootImage baseBossImage;
    
    // State Machine 
    private final int TRACKING = 0;
    private final int CHARGING = 1;
    private final int DASHING  = 2;
    private final int STUCK    = 3;
    private int currentState = TRACKING;
    
    // Timers 
    private int stateTimer = 80; // 2 seconds to track initially
    private int dashAngle = 0;    // Locks the angle before charging/dashing
    
    public SwordfishBoss()
    {
        baseBossImage = new GreenfootImage("swordfish.png");
        baseBossImage.scale(80, 40); // Made a bit bigger since it's a boss
        
        updateBossAppearance(false);
    }
    
    public void act()
    {
        // 1. Run behavior based on current state
        switch (currentState)
        {
            case TRACKING:
                handleTrackingState();
                break;
            case CHARGING:
                handleChargingState();
                break;
            case DASHING:
                handleDashingState();
                break;
            case STUCK:
                handleStuckState();
                break;
        }
        
        // 2. Check if a laser hits the boss
        checkLaserCollision();
    }
    
    private void handleTrackingState()
    {
        updateBossAppearance(true); // Draw image WITH the red targeting line
        
        List<Hero> heroes = getWorld().getObjects(Hero.class);
        if (!heroes.isEmpty())
        {
            Hero hero = heroes.get(0);
            turnTowards(hero.getX(), hero.getY());
        }
        
        stateTimer--;
        if (stateTimer <= 0)
        {
            // Transition to charging: Save the angle, stop tracking, set 1.5s timer
            dashAngle = getRotation();
            currentState = CHARGING;
            stateTimer = 30;
        }
    }
    
    
    private void handleChargingState()
    {
        setRotation(dashAngle); // Keep looking exactly at the targeted path
        
        // Flash transparency
        if (stateTimer % 6 < 3) {
            getImage().setTransparency(80);
        } else {
            getImage().setTransparency(255);
        }
        
        stateTimer--;
        if (stateTimer <= 0)
        {
            getImage().setTransparency(255); // Reset transparency
            updateBossAppearance(false);    // Turn off targeting line
            currentState = DASHING;
        }
    }
    
    private void handleDashingState()
    {
        setRotation(dashAngle);
        slash.play();
        move(30); // Rush forward fast
        
        checkHeroCollision();
        
        // If it impacts the world boundaries, smash into it and get stuck
        if (isAtEdge())
        {
            currentState = STUCK;
            stateTimer = 90; // Stuck for 1.5 seconds
        }
    }
    
    private void handleStuckState()
    {
        // Stand completely still
        stateTimer--;
        if (stateTimer <= 0)
        {
            // Reset loop back to tracking the player
            currentState = TRACKING;
            stateTimer = 30; // 2 seconds tracking
        }
    }

    private void checkLaserCollision()
    {
        // 1. Get a list of ALL lasers currently in the world
        List<Lazer> lasers = getWorld().getObjects(Lazer.class);
        
        // 2. Loop through the lasers to check their actual distance from the boss's core
        for (int i = 0; i < lasers.size(); i++)
        {
            Lazer currentLaser = lasers.get(i);
            
            // Calculate horizontal and vertical distance
            int dx = currentLaser.getX() - this.getX();
            int dy = currentLaser.getY() - this.getY();
            
            // Use standard distance formula (or check bounding radius)
            if (Math.abs(dx) < 30 && Math.abs(dy) < 30)
            {
                // Valid physical hit! Remove the laser safely
                getWorld().removeObject(currentLaser);
                
                bossHp--;
                
                if (bossHp <= 0)
                {
                    MyWorld world = (MyWorld) getWorld();
                    world.notifyBossDefeated();
                    getWorld().removeObject(this);
                    return; // Stop running the method immediately since the boss is dead
                }
                else
                {
                    updateBossAppearance(currentState == TRACKING);
                }
                
                break; // Exit the loop since we handled this frame's hit
            }
        }
    }

    /**
     * Draws the composite graphic including Boss Sprite, HP bar, and optional Target line
     */
    private void updateBossAppearance(boolean drawTargetLine)
    {
        int spriteWidth = baseBossImage.getWidth();
        int spriteHeight = baseBossImage.getHeight();
        int barHeight = 8;
        int spacing = 6;
        
        // If drawing targeting laser, make an ultra-long canvas stretching rightward out of its nose
        int canvasWidth = drawTargetLine ? 1000 : spriteWidth;
        
        GreenfootImage canvas = new GreenfootImage(canvasWidth, spriteHeight + barHeight + spacing);
        
        // Draw the base boss body centered horizontally if tracking line isn't extended
        canvas.drawImage(baseBossImage, 0, barHeight + spacing);
        
        // Draw Boss HP bar frame directly over its head
        canvas.setColor(Color.BLACK);
        canvas.fillRect(0, 0, spriteWidth, barHeight);
        
        int healthBarWidth = (int)(((double)bossHp / 6) * (spriteWidth - 2));
        if (healthBarWidth < 0) healthBarWidth = 0;
        
        // Color shifts from Green -> Yellow -> Red as boss loses health
        if (bossHp > 6) canvas.setColor(Color.GREEN);
        else if (bossHp > 3) canvas.setColor(Color.YELLOW);
        else canvas.setColor(Color.RED);
        
        canvas.fillRect(1, 1, healthBarWidth, barHeight - 2);
        
        //DRAW THE TELEGRAPH TARGET LINE 
        if (drawTargetLine)
        {
            canvas.setColor(new Color(255, 0, 0, 130)); // Semi-transparent Red
            // Draws a straight targeting vector projecting forward from the nose
            canvas.fillRect(spriteWidth, (spriteHeight / 2) + barHeight + spacing - 1, 1000, 3);
        }
        
        setImage(canvas);
    }
    
    private void checkHeroCollision()
    {
        // Look for an overlapping Hero object
        Hero target = (Hero) getOneIntersectingObject(Hero.class);
        
        if (target != null)
        {
            // Deliver the massive 3-damage strike directly via the public method!
            target.takeDamage(3);
        }
    }
    
    public void takeDamage(int amount)
    {
        bossHp -= amount;
        if (bossHp <= 0)
        {
            MyWorld world = (MyWorld) getWorld();
            world.notifyBossDefeated();
            getWorld().removeObject(this);
        }
        else
        {
            updateBossAppearance(currentState == TRACKING);
        }
    }
    
}
