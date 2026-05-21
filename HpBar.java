import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class HpBar here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class HpBar extends Actor
{
    /**
     * Act - do whatever the HpBar wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private final int MAX_HP = 10;
    private final int BAR_WIDTH = 150;
    private final int BAR_HEIGHT = 20;

    public HpBar()
    {
        updateBar(MAX_HP);
    }
    
    public void updateBar(int currentHp)
    {
        // 1. Create a blank image to act as our canvas
        GreenfootImage image = new GreenfootImage(BAR_WIDTH, BAR_HEIGHT);
        
        // 2. Draw the white background/outline frame
        image.setColor(Color.WHITE);
        image.drawRect(0, 0, BAR_WIDTH - 1, BAR_HEIGHT - 1);
        
        // 3. Prevent errors if HP goes below 0
        if (currentHp < 0) {
            currentHp = 0;
        }
        
        // 4. Calculate how wide the red rectangle should be
        // (Current HP / Max HP) * Total Width
        int redWidth = (int)(((double)currentHp / MAX_HP) * (BAR_WIDTH - 4));
        
        // 5. Fill in the red health meter if we have health left
        if (redWidth > 0)
        {
            image.setColor(Color.RED);
            image.fillRect(2, 2, redWidth, BAR_HEIGHT - 4);
        }
        
        // Set the newly drawn image to this actor
        setImage(image);
    }
    public void act()
    {
        // Add your action code here.
        
    }
}
