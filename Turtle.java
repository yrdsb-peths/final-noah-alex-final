import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class Turtle extends Actor
{
    private int crabHp = 6; // Takes multiple hits to die!
    private GreenfootImage baseCrabImage;
    
    public Turtle()
    {
        baseCrabImage = new GreenfootImage("turtle.jpg");
        baseCrabImage.scale(100, 100);
        
        updateFishAppearance();
    }
    
    public void act()
    {
        if (getWorld() == null) return;
        
        // 1. Move towards whatever hero is alive
        moveTowardsHero();
        if (getWorld() == null) return;
        
        // 2. Check if we managed to deliver a bite attack!
        checkHeroContact();
        if (getWorld() == null) return;
        
        // 3. Check if hit by a laser
        checkLaserCollision();
    }
    
    private void checkHeroContact()
    {
        if (getWorld() == null) return;
        
        Actor target = null;
        if (!getWorld().getObjects(Maki.class).isEmpty()) target = getWorld().getObjects(Maki.class).get(0);
        else if (!getWorld().getObjects(Naobito.class).isEmpty()) target = getWorld().getObjects(Naobito.class).get(0);
        else if (!getWorld().getObjects(Nanami.class).isEmpty()) target = getWorld().getObjects(Nanami.class).get(0);
        else if (!getWorld().getObjects(Hero.class).isEmpty()) target = getWorld().getObjects(Hero.class).get(0);
        
        // Check if we are directly intersecting our active target actor
        if (target != null && intersects(target))
        {
            if (target instanceof Hero) { 
                ((Hero)target).takeDamage(2); 
            }
            else if (target instanceof Maki) {
                Maki m = (Maki) target;
                m.takeDamage(2);
            }
            else if (target instanceof Naobito) {
                Naobito n = (Naobito) target;
                n.takeDamage(2);
            }
            else if (target instanceof Nanami) {
                Nanami n = (Nanami) target;
                n.takeDamage(2);
            }
        }
    }
    
    private void moveTowardsHero()
    {
        if (getWorld() == null) return;
        Actor target = null;
        
        if (!getWorld().getObjects(Maki.class).isEmpty()) {
            target = getWorld().getObjects(Maki.class).get(0);
        } else if (!getWorld().getObjects(Naobito.class).isEmpty()) {
            target = getWorld().getObjects(Naobito.class).get(0);
        } else if (!getWorld().getObjects(Nanami.class).isEmpty()) {
            target = getWorld().getObjects(Nanami.class).get(0);
        } else if (!getWorld().getObjects(Hero.class).isEmpty()) {
            target = getWorld().getObjects(Hero.class).get(0);
        }
        
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
        }

        genericWorld.removeObject(this);
    }
}