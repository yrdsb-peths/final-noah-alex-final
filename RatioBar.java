import greenfoot.*;

public class RatioBar extends Actor
{
    private final int BAR_W = 80;
    private final int BAR_H = 14;

    // The sliding circle position (0.0 to 1.0)
    private double circlePos = 0.0;
    private double circleSpeed = 0.02;
    private boolean goingRight = true;

    // Red zone is at the 7/10 mark (7:3 ratio point)
    private final double RED_ZONE_CENTER = 0.7;
    private final double RED_ZONE_HALF   = 0.08; // red zone spans 0.62 to 0.78

    private int lifetime = 180; // 3 seconds before auto-cancel
    private boolean expired = false;

    public RatioBar()
    {
        redraw();
    }

    // FIX: This method must exist for Nanami.java to call it upon Left-Click!
    public boolean checkRatioTiming()
    {
        double lowerBound = RED_ZONE_CENTER - RED_ZONE_HALF;
        double upperBound = RED_ZONE_CENTER + RED_ZONE_HALF;
        return (circlePos >= lowerBound && circlePos <= upperBound);
    }

    public boolean isExpired() {
        return expired;
    }

    public void act()
    {
        lifetime--;
        if (lifetime <= 0) { expired = true; return; }

        // Bounce the circle back and forth
        if (goingRight)
        {
            circlePos += circleSpeed;
            if (circlePos >= 1.0) { circlePos = 1.0; goingRight = false; }
        }
        else
        {
            circlePos -= circleSpeed;
            if (circlePos <= 0.0) { circlePos = 0.0; goingRight = true; }
        }

        redraw();
    }

    private void redraw()
    {
        GreenfootImage img = new GreenfootImage(BAR_W + 4, BAR_H + 4);

        // Background bar
        img.setColor(new Color(20, 20, 20, 200));
        img.fillRect(2, 2, BAR_W, BAR_H);

        // Grey zone (most of bar)
        img.setColor(new Color(120, 120, 120));
        img.fillRect(2, 2, BAR_W, BAR_H);

        // Red zone at 7:3 ratio point
        int redStart = (int)((RED_ZONE_CENTER - RED_ZONE_HALF) * BAR_W);
        int redEnd   = (int)((RED_ZONE_CENTER + RED_ZONE_HALF) * BAR_W);
        img.setColor(new Color(220, 40, 40));
        img.fillRect(2 + redStart, 2, redEnd - redStart, BAR_H);

        // White label "7" at the red zone to signal the ratio
        img.setColor(Color.WHITE);
        img.setFont(new Font("Arial", true, false, 9));
        img.drawString("7:3", 2 + redStart - 2, BAR_H - 1);

        // Sliding white circle marker
        int circleX = (int)(circlePos * BAR_W);
        img.setColor(Color.WHITE);
        img.fillOval(2 + circleX - 4, 2 + (BAR_H / 2) - 4, 8, 8);

        setImage(img);
    }
}