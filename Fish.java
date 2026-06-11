import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
public class Fish extends Actor
{
    private int fishHp = 3; // Takes 3 hits to die 
    private GreenfootImage baseFishImage;
    
    public Fish()
    {
        baseFishImage = new GreenfootImage("fish.png");
        baseFishImage.scale(30, 30);
        //updates hp bar
        updateFishAppearance();
    }
    
    public void act()
    {
        moveTowardsHero();
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
                MyWorld world = (MyWorld) getWorld();
                world.increaseScore();
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
        
        // creates a bar above the fish
        int barHeight = 6;
        int spacing = 4;
        GreenfootImage canvas = new GreenfootImage(spriteWidth, spriteHeight + barHeight + spacing);
        
        // fish image below it
        canvas.drawImage(baseFishImage, 0, barHeight + spacing);
        
        // background of the health bar 
        canvas.setColor(Color.BLACK);
        canvas.fillRect(0, 0, spriteWidth, barHeight);
        
        // remaining health segment
        int healthBarWidth = (int)(((double)fishHp / 3) * (spriteWidth - 2));
        if (healthBarWidth < 0) healthBarWidth = 0;
        
        // Change color based on health remaining (Green for healthy, Red for low health)
        if (fishHp > 1) {
            canvas.setColor(Color.GREEN);
        } else {
            canvas.setColor(Color.RED);
        }

        canvas.fillRect(1, 1, healthBarWidth, barHeight - 2);
        setImage(canvas);
    }
    public void takeDamage(int amount)
    {
        fishHp -= amount;
        if (fishHp <= 0)
        {
            MyWorld world = (MyWorld) getWorld();
            if (world != null)
            {
                world.increaseScore(); 
                world.notifyNemoKilled();
            }
            getWorld().removeObject(this);
        }
        else
        {
            updateFishAppearance();
        }
    }
}
