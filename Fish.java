import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Write a description of class Fish here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Fish extends Actor
{
    /**
     * Act - do whatever the Fish wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        GreenfootImage image = new GreenfootImage("fihs.jpeg");
        image.scale(30, 30);
        setImage(image);
        // Add your action code here.
        // 1. Move towards the Hero
        moveTowardsHero();
        
        // 2. Check if hit by a laser
        checkLaserCollision();
        
    }
    
    private void moveTowardsHero()
    {
        // Find the Hero in the world
        List<Hero> heroes = getWorld().getObjects(Hero.class);
        
        // If the Hero exists, turn towards them and move at a speed of 5
        if (!heroes.isEmpty())
        {
            Hero alligator = heroes.get(0);
            turnTowards(alligator.getX(), alligator.getY());
            move(1);
        }
    }
    
    private void checkLaserCollision()
    {
        // Check if a Lazer object is overlapping with this fish
        Actor laser = getOneIntersectingObject(Lazer.class);
        
        if (laser != null)
        {
            // Remove the laser so it doesn't pierce through multiple enemies
            getWorld().removeObject(laser);
            
            // Remove this fish from the world
            getWorld().removeObject(this);
        }
    }
}
