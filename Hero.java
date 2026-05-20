import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Hero here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Hero extends Actor
{
    /**
     * Act - do whatever the Hero wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    private int laserCooldown = 0;
    private int hp = 5;
    private int invincibilityTimer = 0; // Cooldown after getting hit
    private HpBar healthBar;
    
    public void setHpBar(HpBar bar)
    {
        this.healthBar = bar;
    }
    public void act()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
    
    if (mouse != null) 
    {
        turnTowards(mouse.getX(), mouse.getY());
    }
        
        // Add your action code here.
        GreenfootImage image = new GreenfootImage("baseguy.png");
        image.scale(50, 50);
        setImage(image);
        if (Greenfoot.isKeyDown("a"))
        {
            setLocation(getX() - 5, getY());
        }
        if (Greenfoot.isKeyDown("d"))
        {
            setLocation(getX() + 5, getY());
        }
        if (Greenfoot.isKeyDown("w"))
        {
            setLocation(getX(), getY() - 5);
        }
        if (Greenfoot.isKeyDown("s"))
        {
            setLocation(getX(), getY() + 5);
        }
        
        //laser stuff
        if (laserCooldown > 0) {
            laserCooldown--; // Decrease cooldown every frame
        }
        
        // Shoot when space is pressed and cooldown is ready
        if (Greenfoot.isKeyDown("space") && laserCooldown == 0)
        {
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            
            // Match the laser's rotation to the alligator's mouth orientation
            laser.setRotation(getRotation()); 
            
            laserCooldown = 20; // Wait about 1/3 of a second before shooting again
        }
        
        if (invincibilityTimer > 0)
        {
            invincibilityTimer--;
            
            // Optional Polish: Make the alligator blink or turn translucent while invincible
            if (invincibilityTimer % 4 == 0) {
                getImage().setTransparency(100); // Semi-transparent
            } else {
                getImage().setTransparency(255); // Normal
            }
        }
        else
        {
            getImage().setTransparency(255); // Reset transparency completely
        }
        
        checkEnemyContact();
    }
    
    private void checkEnemyContact()
    {
        // If touching a fish AND not currently invincible
        if (isTouching(Fish.class) && invincibilityTimer == 0)
        {
            hp--; // Take 1 damage instantly
            
            // Give the player 30 frames (0.5 seconds) of invincibility to escape
            invincibilityTimer = 30; 
            
            // Update the health bar UI
            if (healthBar != null)
            {
                healthBar.updateBar(hp);
            }
        }
    }
}
