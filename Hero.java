import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Hero here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Hero extends Actor
{
    /**
     * Act - do whatever the Hero wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    public void act()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
    
    if (mouse != null) 
    {
        turnTowards(mouse.getX(), mouse.getY());
    }
        
        // Add your action code here.
        GreenfootImage image = new GreenfootImage("alligator.png");
        image.scale(60, 20);
        setImage(image);
        if (Greenfoot.isKeyDown("a"))
        {
            setLocation(getX() - 5, getY());
        }
        if (Greenfoot.isKeyDown("d"))
        {
            setLocation(getX() + 5, getY());
        }
        if (Greenfoot.isKeyDown("w"))
        {
            setLocation(getX(), getY() - 5);
        }
        if (Greenfoot.isKeyDown("s"))
        {
            setLocation(getX(), getY() + 5);
        }
    }
}
