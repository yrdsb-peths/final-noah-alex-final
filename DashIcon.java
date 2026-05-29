import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DashIcon here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DashIcon extends Actor
{
    /**
     * Act - do whatever the DashIcon wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private final int RADIUS = 40;

    public DashIcon()
    {
        updateIcon(0); // Starts completely ready
    }
    public void updateIcon(int secondsLeft)
    {
        GreenfootImage img = new GreenfootImage(RADIUS, RADIUS);
        
        if (secondsLeft > 0)
        {
            // Cooldown State: Dark circle with a white outline
            img.setColor(new Color(50, 50, 50));
            img.fillOval(0, 0, RADIUS - 1, RADIUS - 1);
            img.setColor(Color.WHITE);
            img.drawOval(0, 0, RADIUS - 1, RADIUS - 1);
            
            // Draw the countdown text exactly in the middle
            img.setFont(new Font("Arial", true, false, 20));
            img.drawString(Integer.toString(secondsLeft), 14, 27);
        }
        else
        {
            // Ready State: Bright cyan/blue circle icon
            img.setColor(new Color(0, 200, 255, 200));
            img.fillOval(0, 0, RADIUS - 1, RADIUS - 1);
            img.setColor(Color.WHITE);
            img.drawOval(0, 0, RADIUS - 1, RADIUS - 1);
            
            // Label it with an 'R' to show the hotkey mapping
            img.setFont(new Font("Arial", true, false, 18));
            img.drawString("R", 14, 26);
        }
        
        setImage(img);
    }
    public void act()
    {
        // Add your action code here.
    }
}
