import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class Turtle extends Actor
{
    private int crabHp = 6; // Takes 3 hits to die!
    private GreenfootImage baseCrabImage;
    
    public Turtle()
    {
        // Uses the exact same fish asset and scaling as your fish class
        baseCrabImage = new GreenfootImage("turtle.jpg");
        baseCrabImage.scale(100, 100);
        
        // Dynamic drawing step to attach the full green health bar initially
        updateFishAppearance();
    }
    
    public void act()
    {
        // EMERGENCY BRAKE: If deleted earlier in this frame loop, stop instantly
        if (getWorld() == null) return;
        
        // 1. Move towards whatever hero is alive in the active world
        moveTowardsHero();
        if (getWorld() == null) return;
        
        // 2. Check if hit by a laser
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
            crabHp--;
            
            if (crabHp <= 0)
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
        int spriteWidth = baseCrabImage.getWidth();
        int spriteHeight = baseCrabImage.getHeight();
        
        int barHeight = 6;
        int spacing = 4;
        GreenfootImage canvas = new GreenfootImage(spriteWidth, spriteHeight + barHeight + spacing);
        
        canvas.drawImage(baseCrabImage, 0, barHeight + spacing);
        
        canvas.setColor(Color.BLACK);
        canvas.fillRect(0, 0, spriteWidth, barHeight);
        
        int healthBarWidth = (int)(((double)crabHp / 6) * (spriteWidth - 2));
        if (healthBarWidth < 0) healthBarWidth = 0;
        
        if (crabHp > 1) {
            canvas.setColor(Color.GREEN);
        } else {
            canvas.setColor(Color.RED);
        }
        
        canvas.fillRect(1, 1, healthBarWidth, barHeight - 2);
        setImage(canvas);
    }
    
    public void takeDamage(int amount)
    {
        crabHp -= amount;
        if (crabHp <= 0)
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