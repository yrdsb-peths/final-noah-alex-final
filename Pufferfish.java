import greenfoot.*;
import java.util.List;

public class Pufferfish extends Actor
{
    private int targetX, targetY;
    private boolean inflated = false;
    private int inflateTimer = 0;
    private static final int INFLATE_DELAY = 120;
    private int speed = 2;
    private int pufferHp = 3;
    private int contactCooldown = 0; // prevents damage every single frame
    private GreenfootImage normalImage;
    private GreenfootImage inflatedImage;

    public Pufferfish()
    {
        normalImage = new GreenfootImage("pufferfish.png");
        normalImage.scale(35, 35);
        inflatedImage = new GreenfootImage("inflated-pufferfish.png");
        inflatedImage.scale(50, 50);
        setImage(normalImage);
        
        updatePuffAppearance();

        targetX = 50 + Greenfoot.getRandomNumber(500);
        targetY = 50 + Greenfoot.getRandomNumber(300);
    }

    public void act()
    {
        if (!inflated)
        {
            moveToTarget();
        }

        if (contactCooldown > 0) contactCooldown--;

        checkHeroContact();
        checkLaserCollision();
    }

    private void checkHeroContact()
    {
        Hero hero = (Hero) getOneIntersectingObject(Hero.class);
        if (hero != null && contactCooldown == 0)
        {
            hero.takeDamage(2); // direct contact hit
            contactCooldown = 60; // 1 second cooldown between hits
        }
    }

    private void moveToTarget()
    {
        int dx = targetX - getX();
        int dy = targetY - getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < speed + 1)
        {
            setLocation(targetX, targetY);
            inflateTimer++;
            if (inflateTimer >= INFLATE_DELAY)
            {
                inflated = true;
                setImage(inflatedImage);
            }
        }
        else
        {
            setLocation(getX() + (int)(speed * dx / dist),
                        getY() + (int)(speed * dy / dist));
        }
    }

    private void checkLaserCollision()
    {
        Actor laser = getOneIntersectingObject(Lazer.class);
        if (laser != null)
        {
            MyWorld world = (MyWorld) getWorld();
            world.removeObject(laser);
            pufferHp--;

            if (pufferHp <= 0)
            {
                world.increaseScore();
                // If health runs out, the fish dies
                die();
            }
            else
            {
                // If it survives, redraw its health bar to show the lower HP
                updatePuffAppearance();
            }
        }
    }

    public void die()
    {
        MyWorld world = (MyWorld) getWorld();
        world.notifyPufferKilled();

        PoisonCloud cloud = new PoisonCloud();
        world.addObject(cloud, getX(), getY());

        world.removeObject(this);
    }
    
    private void updatePuffAppearance()
    {
        int spriteWidth = inflatedImage.getWidth();
        int spriteHeight = inflatedImage.getHeight();
        
        // 1. Create a larger transparent canvas to fit both the fish and its HP bar overhead
        int barHeight = 6;
        int spacing = 4;
        GreenfootImage canvas = new GreenfootImage(spriteWidth, spriteHeight + barHeight + spacing);
        
        // 2. Draw the base fish sprite at the bottom of our canvas
        canvas.drawImage(normalImage, 0, barHeight + spacing);
        
        // 3. Draw the background of the mini health bar (Dark Gray/Black background)
        canvas.setColor(Color.BLACK);
        canvas.fillRect(0, 0, spriteWidth, barHeight);
        
        // 4. Calculate the width of the remaining health segment
        int healthBarWidth = (int)(((double)pufferHp / 3) * (spriteWidth - 2));
        if (healthBarWidth < 0) healthBarWidth = 0;
        
        // 5. Change color based on health remaining (Green for healthy, Red for low health)
        if (pufferHp > 1) {
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