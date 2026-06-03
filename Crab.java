import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class Crab extends Actor
{
    private int fishHp = 3; // Takes 3 hits to die!
    private GreenfootImage baseFishImage;
    
    public Crab()
    {
        // Uses the exact same fish asset and scaling as your fish class
        baseFishImage = new GreenfootImage("fish.png");
        baseFishImage.scale(30, 30);
        
        // Dynamic drawing step to attach the full green health bar initially
        updateFishAppearance();
    }
    
    public void act() 
    {
        // Safety check to ensure the actor wasn't just deleted by takeDamage()
        if (getWorld() == null) return;

        // --- GLOBAL STATE CHECK ---
        if (getWorld() instanceof BeachWorld) {
            BeachWorld world = (BeachWorld) getWorld();
            
            // Halt if Naobito is actively time-freezing with Q
            if (world.isTimeFrozen()) return;
            
            // Halt if THIS specific entity is still locked inside the active glass panel
            if (world.getFrozenEnemy() == this) return;
        }

        // --- RESUME RUNNING NATIVE ENGINE AI ---
        // If the above conditions are false, this code runs automatically every single frame!
        moveTowardsHero(); 
        
        if (getWorld() == null) return;
        checkLaserCollision();
    }
    
    private void moveTowardsHero()
    {
        Actor target = null;
        
        // Universal tracking: Check for JJK heroes or your original legacy Hero class
        if (!getWorld().getObjects(Maki.class).isEmpty()) {
            target = getWorld().getObjects(Maki.class).get(0);
        } else if (!getWorld().getObjects(Naobito.class).isEmpty()) {
            target = getWorld().getObjects(Naobito.class).get(0);
        } else if (!getWorld().getObjects(Nanami.class).isEmpty()) {
            target = getWorld().getObjects(Nanami.class).get(0);
        } else if (!getWorld().getObjects(Hero.class).isEmpty()) {
            target = getWorld().getObjects(Hero.class).get(0);
        }
        
        // If an active target is found, turn towards them and step forward
        if (target != null)
        {
            turnTowards(target.getX(), target.getY());
            move(1);
        }
    }
    
    private void checkLaserCollision()
    {
        Actor laser = getOneIntersectingObject(Lazer.class);
        
        if (laser != null)
        {
            getWorld().removeObject(laser);
            fishHp--;
            
            if (fishHp <= 0)
            {
                handleDeath();
            }
            else
            {
                updateFishAppearance();
            }
        }
    }
    
    private void updateFishAppearance()
    {
        int spriteWidth = baseFishImage.getWidth();
        int spriteHeight = baseFishImage.getHeight();
        
        int barHeight = 6;
        int spacing = 4;
        GreenfootImage canvas = new GreenfootImage(spriteWidth, spriteHeight + barHeight + spacing);
        
        canvas.drawImage(baseFishImage, 0, barHeight + spacing);
        
        canvas.setColor(Color.BLACK);
        canvas.fillRect(0, 0, spriteWidth, barHeight);
        
        int healthBarWidth = (int)(((double)fishHp / 3) * (spriteWidth - 2));
        if (healthBarWidth < 0) healthBarWidth = 0;
        
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
            handleDeath();
        }
        else
        {
            updateFishAppearance();
        }
    }

    /**
     * Handles points allocation dynamically without crashing BeachWorld
     */
    private void handleDeath()
    {
        World genericWorld = getWorld();
        if (genericWorld == null) return;

        // If we are playing inside the original test world, update its unique score systems
        if (genericWorld instanceof MyWorld)
        {
            MyWorld world = (MyWorld) genericWorld;
            world.increaseScore(); 
            world.notifyNemoKilled();
        }
        // If inside BeachWorld, it won't force cast to MyWorld anymore! 
        else if (genericWorld instanceof BeachWorld)
        {
            BeachWorld world = (BeachWorld) genericWorld;
            // If you add a scoring system to BeachWorld later, you can add it here safely:
            // world.increaseScore();
        }

        genericWorld.removeObject(this);
    }
}