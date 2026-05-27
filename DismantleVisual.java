import greenfoot.*;

public class DismantleVisual extends Actor
{
    private int visibleFrames = 12; // Flashes on screen for 1/5th of a second

    public DismantleVisual()
    {
        GreenfootImage img = new GreenfootImage("dismantle.png");
        // Scale it down to fit nicely across your 600x400 map
        img.scale(160, 35); 
        setImage(img);
    }

    public void act()
    {
        visibleFrames--;
        
        if (visibleFrames > 0)
        {
            // Linear alpha fadeout modifier
            getImage().setTransparency(visibleFrames * 21);
        }
        else
        {
            getWorld().removeObject(this);
        }
    }
}