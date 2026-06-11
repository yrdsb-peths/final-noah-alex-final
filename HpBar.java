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
    private int maxHp = 10;
    private int currentHp = 10;
    private int barWidth = 150;
    private int barHeight = 20;
    private Color barColor = Color.RED;

    public HpBar()
    {
        updateBar(maxHp);
    }
    
    public void setMaxHp(int newMax)
    {
        this.maxHp = newMax;
        this.currentHp = newMax;
        updateBar(currentHp);
    }
    
    public void setBarDimensions(int width, int height)
    {
        this.barWidth = width;
        this.barHeight = height;
        updateBar(currentHp); 
    }
    
    public void setLineColor(Color newColor)
    {
        this.barColor = newColor;
        updateBar(currentHp);
    }
    
    public void updateBar(int hp)
    {
        this.currentHp = hp;
        
        // Create a blank image to act as our canvas
        GreenfootImage image = new GreenfootImage(barWidth, barHeight);
        
        // Draw the white background/outline frame
        image.setColor(Color.WHITE);
        image.drawRect(0, 0, barWidth - 1, barHeight - 1);
        
        // Prevent errors if HP goes below 0
        if (currentHp < 0) {
            currentHp = 0;
        }
        
        // Calculate how wide the inner rectangle should be
        int fillWidth = (int)(((double)currentHp / maxHp) * (barWidth - 4));
        
        // Fill in the meter 
        if (fillWidth > 0)
        {
            image.setColor(barColor);
            image.fillRect(2, 2, fillWidth, barHeight - 4);
        }
        setImage(image);
    }
}
