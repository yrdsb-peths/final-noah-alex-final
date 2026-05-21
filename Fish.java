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
    private int fishHp = 3; // Takes 3 hits to die now!
    private GreenfootImage baseFishImage;
    
    public Fish()
    {
        // Replace "fish.png" with whatever your actual fish image filename is!
        baseFishImage = new GreenfootImage("fish.png");
        
        // Scale your base image right here so it's small!
        baseFishImage.scale(30, 30);
        
        // Dynamic drawing step to attach the full green health bar initially
        updateFishAppearance();
    }
    
    public void act()
    {
        
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
            
            // Deduct 1 health point from this specific fish
            fishHp--;
            
            if (fishHp <= 0)
            {
                // If health runs out, the fish dies
                getWorld().removeObject(this);
            }
            else
            {
                // If it survives, redraw its health bar to show the lower HP
                updateFishAppearance();
            }
        }
    }
    private void updateFishAppearance()
    {
        int spriteWidth = baseFishImage.getWidth();
        int spriteHeight = baseFishImage.getHeight();
        
        // 1. Create a larger transparent canvas to fit both the fish and its HP bar overhead
        int barHeight = 6;
        int spacing = 4;
        GreenfootImage canvas = new GreenfootImage(spriteWidth, spriteHeight + barHeight + spacing);
        
        // 2. Draw the base fish sprite at the bottom of our canvas
        canvas.drawImage(baseFishImage, 0, barHeight + spacing);
        
        // 3. Draw the background of the mini health bar (Dark Gray/Black background)
        canvas.setColor(Color.BLACK);
        canvas.fillRect(0, 0, spriteWidth, barHeight);
        
        // 4. Calculate the width of the remaining health segment
        int healthBarWidth = (int)(((double)fishHp / 3) * (spriteWidth - 2));
        if (healthBarWidth < 0) healthBarWidth = 0;
        
        // 5. Change color based on health remaining (Green for healthy, Red for low health)
        if (fishHp > 1) {
            canvas.setColor(Color.GREEN);
        } else {
            canvas.setColor(Color.RED);
        }
        
        // 6. Draw the foreground health level
        canvas.fillRect(1, 1, healthBarWidth, barHeight - 2);
        
        // Assign this combined custom graphic to the actor
        setImage(canvas);
    }
}
