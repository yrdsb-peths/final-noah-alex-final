import greenfoot.*;

public class RatioIndicator extends Actor
{
    private int barWidth = 100;
    private int barHeight = 12;
    private int sliderX = 0;       // Current horizontal position of the moving tick line
    private int sliderSpeed = 3;   // How fast the line slides back and forth
    private boolean movingRight = true;

    public RatioIndicator()
    {
        updateImage();
    }

    public void act()
    {
        // Slide the timing marker back and forth inside the bar width boundaries
        if (movingRight) {
            sliderX += sliderSpeed;
            if (sliderX >= barWidth) {
                sliderX = barWidth;
                movingRight = false;
            }
        } else {
            sliderX -= sliderSpeed;
            if (sliderX <= 0) {
                sliderX = 0;
                movingRight = true;
            }
        }
        
        updateImage();
    }

    private void updateImage()
    {
        GreenfootImage img = new GreenfootImage(barWidth + 2, barHeight + 2);
        
        // 1. Draw Background Frame (Dark gray background)
        img.setColor(new Color(50, 50, 50));
        img.fillRect(0, 0, barWidth, barHeight);
        
        // 2. Draw the 7:3 Ratio Sweet Spot (Gold target zone placed near the 70% mark)
        // Spans from index 65 to 75 to give the player a fair timing window
        img.setColor(new Color(255, 215, 0)); 
        img.fillRect(65, 0, 10, barHeight);
        
        // 3. Draw the active slider tick line (Bright Red/White marker)
        img.setColor(Color.WHITE);
        img.drawRect(sliderX, 0, 2, barHeight);
        img.setColor(Color.RED);
        img.fillRect(sliderX + 1, 1, 1, barHeight - 1);
        
        // 4. Draw outer border line
        img.setColor(Color.BLACK);
        img.drawRect(0, 0, barWidth, barHeight);
        
        setImage(img);
    }

    /**
     * Checks if the slider line is currently sitting inside the Gold target zone.
     * Returns true for a Critical Strike, false for a normal swing.
     */
    public boolean checkTimingSuccess()
    {
        // If the tick line is between 65% and 75% of the bar, it's a perfect 7:3 hit!
        return (sliderX >= 65 && sliderX <= 75);
    }
}   