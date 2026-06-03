import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class Crab extends Actor
{
    private int crabHp = 1; 
    private GreenfootImage baseCrabImage;
    
    public Crab()
    {
        // Uses the exact same fish asset and scaling as your fish class
        baseCrabImage = new GreenfootImage("CRAB.jpg");
        baseCrabImage.scale(30, 30);
        
        updateFishAppearance();
    }
    
    public void act()
    {
        if (getWorld() == null) return;
        
        // 1. Move towards whatever hero is alive
        moveTowardsHero();
        if (getWorld() == null) return;
        
        // 2. NEW: Check if we managed to deliver a claw attack!
        checkHeroContact();
        if (getWorld() == null) return;
        
        // 3. Check if hit by a laser
        checkLaserCollision();
    }
    
    private void moveTowardsHero()
    {
        Actor target = getActiveHero();
        
        if (target != null)
        {
            turnTowards(target.getX(), target.getY());
            move(4); // Nice and fast movement speed!
        }
    }
    
    private Actor getActiveHero()
    {
        if (getWorld() == null) return null;
        if (!getWorld().getObjects(Maki.class).isEmpty()) return getWorld().getObjects(Maki.class).get(0);
        if (!getWorld().getObjects(Naobito.class).isEmpty()) return getWorld().getObjects(Naobito.class).get(0);
        if (!getWorld().getObjects(Nanami.class).isEmpty()) return getWorld().getObjects(Nanami.class).get(0);
        if (!getWorld().getObjects(Hero.class).isEmpty()) return getWorld().getObjects(Hero.class).get(0);
        
        return null;
    }
    
    private void checkHeroContact()
    {
        Actor target = getActiveHero();
        
        // Check if we are physically touching our active tracked target
        if (target != null && isTouching(target.getClass()))
        {
            // Cast down to individual classes to safely apply the stun method
            if (target instanceof Hero) {
                ((Hero)target).getStunned(60); // 60 frames = 1 second freeze lock
                ((Hero)target).takeDamage(1);   // Optional: still hits for 1 damage point
            }
            else if (target instanceof Maki) {
                // ((Maki)target).getStunned(60); // Uncomment these when you build out the JJK classes!
            }
            else if (target instanceof Naobito) {
                // ((Naobito)target).getStunned(60);
            }
            else if (target instanceof Nanami) {
                // ((Nanami)target).getStunned(60);
            }
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
        
        int healthBarWidth = (int)(((double)crabHp / 1) * (spriteWidth - 2));
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
