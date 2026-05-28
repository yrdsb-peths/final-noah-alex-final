import greenfoot.*;
import java.util.List;

public class Trident extends Actor
{
    private boolean flying = false;
    private boolean stuck = false;
    private boolean carried = false;

    private java.util.ArrayList<Actor> hitActors = new java.util.ArrayList<>();
    
    public Trident()
    {
        GreenfootImage img = new GreenfootImage("trident.png");
        img.scale(80, 80);
        setImage(img);
    }

    public void setCarried(boolean c)
    {
        carried = c;
        stuck = false;  // clear stuck when picked up
        flying = false; // clear flying when picked up
    }

    public void act()
    {
        if (carried)
        {
            List<Hero> heroes = getWorld().getObjects(Hero.class);
            if (!heroes.isEmpty())
            {
                Hero hero = heroes.get(0);
                setLocation(hero.getX() + 20, hero.getY());

                MouseInfo mouse = Greenfoot.getMouseInfo();
                if (mouse != null)
                {
                    turnTowards(mouse.getX(), mouse.getY());
                }
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
    List<Fish> fish = getObjectsInRange(15, Fish.class);
    for (Fish f : fish)
    {
        if (!hitActors.contains(f)) { f.takeDamage(5); hitActors.add(f); }
    }

    List<Pufferfish> puffers = getObjectsInRange(15, Pufferfish.class);
    for (Pufferfish p : puffers)
    {
        if (!hitActors.contains(p)) { p.takeDamage(5); hitActors.add(p); }
    }

    List<SwordfishBoss> bosses = getObjectsInRange(15, SwordfishBoss.class);
    for (SwordfishBoss b : bosses)
    {
        if (!hitActors.contains(b)) { b.takeDamage(5); hitActors.add(b); }
    }

    List<Kraken> kraken = getObjectsInRange(15, Kraken.class);
    for (Kraken k : kraken)
    {
        if (!hitActors.contains(k) && k.isVulnerable()) { k.takeDamage(5); hitActors.add(k); }
    }
}

    public void launch(int angle)
    {
        carried = false;
        stuck = false;
        setRotation(angle);
        flying = true;
        hitActors.clear();
    }

    public boolean isStuck() { return stuck; }
    public boolean isFlying() { return flying; }
    public boolean isCarried() { return carried; }
}