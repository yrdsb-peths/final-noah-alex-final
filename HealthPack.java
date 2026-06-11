import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class HealthPack here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class HealthPack extends Actor
{
    /**
     * Act - do whatever the HealthPack wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public HealthPack()
    {
        GreenfootImage img = new GreenfootImage("medkit.png");
        img.scale(30, 30);
        setImage(img);
    }
    public void act()
    {
        // Check if the Hero touches the health pack
        Hero hero = (Hero) getOneIntersectingObject(Hero.class);
        if (hero != null)
        {
            // Heal the hero by 1 HP 
            hero.heal(3);
            
            // Remove the health pack from the world
            getWorld().removeObject(this);
        }
    }
}
