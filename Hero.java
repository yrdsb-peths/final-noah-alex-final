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
    private int damageTimer = 60; // 60 frames = 1 second at normal speed
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
        checkEnemyContact();
    }
    
    private void checkEnemyContact()
    {
        // If a Fish is currently touching the Alligator
        if (isTouching(Fish.class))
        {
            damageTimer--; // Count down towards 1 second
            
            if (damageTimer <= 0)
            {
                hp--; // Deal 1 damage
                damageTimer = 60; // Reset the 1-second timer
                
                // Update the visual health bar if it exists
                if (healthBar != null)
                {
                    healthBar.updateBar(hp);
                }
            }
        }
        else
        {
            // If the fish steps away, reset the timer so they can't land 
            // a rapid hit by stepping on and off your hitbox.
            damageTimer = 60; 
        }
    }
}
