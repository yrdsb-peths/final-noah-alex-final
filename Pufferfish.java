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
                die();
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
}