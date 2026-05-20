import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Lazer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Lazer extends Actor
{
    /**
     * Act - do whatever the Lazer wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private int lifeTimer = 120;
    public void act()
    {
        GreenfootImage image = new GreenfootImage("projectile.png");
        image.scale(20, 20);
        setImage(image);
        // Add your action code here.
        move(10);
        
        // 2. Count down the lifetime
        lifeTimer--;
        
        // 3. If time is up, or if it hits the edge of the world, remove it
        if (lifeTimer <= 0 || isAtEdge())
        {
            getWorld().removeObject(this);
        }
    }
}
