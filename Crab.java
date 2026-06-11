import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class Crab extends Actor
{
    private int crabHp = 1; 
    private GreenfootImage baseCrabImage;
    
    public Crab()
    {
        baseCrabImage = new GreenfootImage("crab.png");
        baseCrabImage.scale(40, 30);
        updateFishAppearance();
    }
    
    public void act()
    {
        if (getWorld() == null) return;
        
        // specifically for naobito and throwing them
        if (getWorld() instanceof BeachWorld) {
            BeachWorld bw = (BeachWorld) getWorld();
            if (bw.isTimeFrozen() || bw.getFrozenEnemy() == this) {
                return; // stops doing anything
            }
        }
        
        // move towards whatever hero is alive
        moveTowardsHero();
        if (getWorld() == null) return;
        
        // attack
        checkHeroContact();
        if (getWorld() == null) return;
        
        // laser hit
        checkLaserCollision();
    }
    
    private void moveTowardsHero()
    {
        Actor target = getActiveHero();
        if (target != null)
        {
            turnTowards(target.getX(), target.getY());
            move(4); 
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
        if (target != null && isTouching(target.getClass()))
        {
            if (target instanceof Hero) { ((Hero)target).takeDamage(1); }
            else if (target instanceof Maki) { ((Maki)target).takeDamage(1); }
            else if (target instanceof Naobito) { ((Naobito)target).takeDamage(1); }
            else if (target instanceof Nanami) { ((Nanami)target).takeDamage(1); }
        }
    }
    
    private void checkLaserCollision()
    {
        Actor laser = getOneIntersectingObject(Lazer.class);
        if (laser != null)
        {
            getWorld().removeObject(laser);
            takeDamage(1);
        }
    }
    
    private void updateFishAppearance()
    {
        //this specifically deals with the hp bar above the sprite
        int spriteWidth = baseCrabImage.getWidth();
        int spriteHeight = baseCrabImage.getHeight();
        int barHeight = 4;
        int spacing = 2;
        
        GreenfootImage canvas = new GreenfootImage(spriteWidth, spriteHeight + barHeight + spacing);
        canvas.drawImage(baseCrabImage, 0, barHeight + spacing);
        
        canvas.setColor(Color.BLACK);
        canvas.fillRect(0, 0, spriteWidth, barHeight);
        
        int healthBarWidth = (int)(((double)crabHp / 1) * (spriteWidth - 2));
        if (healthBarWidth < 0) healthBarWidth = 0;
        
        canvas.setColor(crabHp > 1 ? Color.GREEN : Color.RED);
        canvas.fillRect(1, 1, healthBarWidth, barHeight - 2);
        setImage(canvas);
    }
    
    public void takeDamage(int amount)
    {
        crabHp -= amount;
        if (crabHp <= 0) handleDeath();
        else updateFishAppearance();
    }

    private void handleDeath()
    {
        World genericWorld = getWorld();
        if (genericWorld == null) return;

        if (genericWorld instanceof MyWorld)
        {
            MyWorld world = (MyWorld) genericWorld;
            world.increaseScore(); 
            world.notifyNemoKilled();
        }
        else if (genericWorld instanceof BeachWorld)
        {
            BeachWorld world = (BeachWorld) genericWorld;
            world.increaseScore();
        }
        genericWorld.removeObject(this);
    }
}