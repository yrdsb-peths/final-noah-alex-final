import greenfoot.*;

public class ProjectionCursor extends Actor
{
    private GreenfootImage clearImage;
    private GreenfootImage targetedImage;

    public ProjectionCursor()
    {
        // 🟡 Passive state styling (Empty Ring tracking cursor coordinate points)
        clearImage = new GreenfootImage(30, 30);
        clearImage.setColor(new Color(255, 215, 0, 180));
        clearImage.drawOval(0, 0, 28, 28);

        // 🔴 Active line contact confirmed styling
        targetedImage = new GreenfootImage(34, 34);
        targetedImage.setColor(new Color(220, 20, 60, 220));
        targetedImage.drawOval(0, 0, 32, 32);
        targetedImage.fillOval(12, 12, 8, 8);

        setImage(clearImage);
    }

    public void updateCursorStyle(boolean hasTarget)
    {
        setImage(hasTarget ? targetedImage : clearImage);
    }
}