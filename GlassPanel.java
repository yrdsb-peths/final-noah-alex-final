import greenfoot.*;

public class GlassPanel extends Actor
{
    private Actor trappedTarget;
    private boolean isLaunched = false;
    private int velocity = 0;

    public GlassPanel(Actor target)
    {
        this.trappedTarget = target;
        
        // Dynamically creates a stylized glass container outline texture automatically
        GreenfootImage img = new GreenfootImage(65, 75);
        img.setColor(new Color(135, 206, 250, 90)); // Soft translucent blue
        img.fillRect(0, 0, 65, 75);
        img.setColor(new Color(255, 255, 255, 200)); // Bright glass edge
        img.drawRect(0, 0, 64, 74);
        setImage(img);
    }

    public void launchFromActor(int angle, int speed)
    {
        setRotation(angle);
        this.velocity = speed;
        this.isLaunched = true;
    }

    public void act()
    {
        if (!isLaunched)
        {
            // Maintain structural lock anchoring position securely onto the trapped host entity
            if (trappedTarget != null && trappedTarget.getWorld() != null)
            {
                setLocation(trappedTarget.getX(), trappedTarget.getY());
            }
            else
            {
                getWorld().removeObject(this);
            }
        }
        else
        {
            // Handle projectile travel frames
            move(velocity);
            
            if (trappedTarget != null && trappedTarget.getWorld() != null)
            {
                trappedTarget.setLocation(getX(), getY());
            }

            // Check if projectile contacts edge layouts to shatter frame
            if (isAtEdge())
            {
                shatterAndExecute();
            }
        }
    }

    public boolean isAtEdge()
    {
        World w = getWorld();
        if (w == null) return false;
        return (getX() <= 5 || getX() >= w.getWidth() - 5 || getY() <= 5 || getY() >= w.getHeight() - 5);
    }

    private void shatterAndExecute()
    {
        if (getWorld() instanceof BeachWorld) {
            ((BeachWorld) getWorld()).setFrozenEnemy(null);
        }

        if (trappedTarget != null && trappedTarget.getWorld() != null)
        {
            // Restore normal appearance transparency
            trappedTarget.getImage().setTransparency(255);
            
            // Deal damage to the target
            if (trappedTarget instanceof Fish) ((Fish) trappedTarget).takeDamage(2);
            else if (trappedTarget instanceof Pufferfish) ((Pufferfish) trappedTarget).takeDamage(2);
            else if (trappedTarget instanceof Crab) ((Crab) trappedTarget).takeDamage(2);
        }
        
        // Remove the glass panel overlay
        getWorld().removeObject(this);
    }
}