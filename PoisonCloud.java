import greenfoot.*;
import java.util.List;

public class PoisonCloud extends Actor
{
    private int lifetime = 300;       // 5 seconds at 60fps
    private int damageInterval = 60;  // damages every 1 second
    private int damageTimer = 0;
    private int damageAmount = 1;
    private int radius = 60;
    private GreenfootImage cloudImage;

    public PoisonCloud()
    {
        cloudImage = new GreenfootImage("poison-cloud.png");
        cloudImage.scale(radius * 2, radius * 2);
        setImage(cloudImage);
    }

    public void act()
    {
        lifetime--;
        damageTimer++;

        // Fade out gradually as lifetime runs out
        int alpha = (int)((lifetime / 300.0) * 255);
        getImage().setTransparency(Math.max(alpha, 0));

        if (damageTimer >= damageInterval)
        {
            damageTimer = 0;
            dealDamage();
        }

        if (lifetime <= 0)
        {
            getWorld().removeObject(this);
        }
    }

    private void dealDamage()
    {
        // Damage Hero if in range
        List<Hero> heroes = getObjectsInRange(radius, Hero.class);
        for (Hero h : heroes)
        {
            h.takeDamage(damageAmount);
        }

        // Damage Fish if in range
        List<Fish> fish = getObjectsInRange(radius, Fish.class);
        for (Fish f : fish)
        {
            f.takeDamage(damageAmount);
        }
    }
}