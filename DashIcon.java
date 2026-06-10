import greenfoot.*;  

/**
 * Write a description of class DashIcon here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DashIcon extends Actor
{
    private final int RADIUS = 40;

    public DashIcon()
    {
        updateIcon(0); 
    }
    public void updateIcon(int secondsLeft)
    {
        GreenfootImage img = new GreenfootImage(RADIUS, RADIUS);
        
        if (secondsLeft > 0)
        {
            //dark circle with a white outline
            img.setColor(new Color(50, 50, 50));
            img.fillOval(0, 0, RADIUS - 1, RADIUS - 1);
            img.setColor(Color.WHITE);
            img.drawOval(0, 0, RADIUS - 1, RADIUS - 1);
            
            //cooldown text
            img.setFont(new Font("Arial", true, false, 20));
            img.drawString(Integer.toString(secondsLeft), 14, 27);
        }
        else
        {
            // ready state 
            img.setColor(new Color(0, 200, 255, 200));
            img.fillOval(0, 0, RADIUS - 1, RADIUS - 1);
            img.setColor(Color.WHITE);
            img.drawOval(0, 0, RADIUS - 1, RADIUS - 1);
            
            // so the player knows to press r when its ready
            img.setFont(new Font("Arial", true, false, 18));
            img.drawString("R", 14, 26);
        }
        
        setImage(img);
    }
}
