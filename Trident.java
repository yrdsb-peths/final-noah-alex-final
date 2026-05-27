import greenfoot.*;
import java.util.List;

public class Trident extends Actor
{
    private boolean flying = false;
    private boolean stuck = false;
    private boolean carried = false;

    public Trident()
    {
        GreenfootImage img = new GreenfootImage("trident.png"); // add a trident image to your images folder
        img.scale(100, 100);
        setImage(img);
    }

    public void setCarried(boolean c) { carried = c; }
    
    public void act()
{
    if (carried)
    {
        // Follow hero position with a small offset
        List<Hero> heroes = getWorld().getObjects(Hero.class);
        if (!heroes.isEmpty())
        {
            Hero hero = heroes.get(0);
            setLocation(hero.getX() + 15, hero.getY() + 15);
        }
    }
    else if (flying)
    {
        move(15);
        pierceEnemies();
        if (isAtEdge())
        {
            flying = false;
            stuck = true;
        }
    }
}

    private void pierceEnemies()
    {
        // Damage Fish (pierces, does NOT remove trident)
        List<Fish> fish = getObjectsInRange(15, Fish.class);
        for (Fish f : fish)
        {
            f.takeDamage(5);
        }

        // Damage Pufferfish
        List<Pufferfish> puffers = getObjectsInRange(15, Pufferfish.class);
        for (Pufferfish p : puffers)
        {
            p.takeDamage(5); // you'll need to add takeDamage to Pufferfish (see below)
        }

        // Damage Boss
        List<SwordfishBoss> bosses = getObjectsInRange(15, SwordfishBoss.class);
        for (SwordfishBoss b : bosses)
        {
            b.takeDamage(5); // you'll need to add takeDamage to SwordfishBoss (see below)
        }
    }

    public void launch(int angle)
{
    carried = false;
    setRotation(angle);
    flying = true;
    stuck = false;
}

    public boolean isStuck() { return stuck; }
    public boolean isFlying() { return flying; }
}