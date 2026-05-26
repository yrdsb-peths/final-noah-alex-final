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
        // Replace with your actual image file name if different!
        GreenfootImage img = new GreenfootImage("medkit.png");
        img.scale(30, 30);
        setImage(img);
    }
    public void act()
    {
        // Add your action code here.
        // Check if the Hero touches the health pack
        Hero hero = (Hero) getOneIntersectingObject(Hero.class);
        if (hero != null)
        {
            // Heal the hero by 1 HP (or up to your max cap)
            hero.heal(1);
            
            // Remove the health pack from the world
            getWorld().removeObject(this);
        }
    }
}
