import greenfoot.*;

public class PunchVisual extends Actor
{
    private int lifetime = 6; // How many frames the punch effect stays on screen
    private int speed = 8;     // How fast the punch moves forward
    
    public PunchVisual(int angle)
    {
        setRotation(angle);
        
        // Create a custom fist/swipe graphic using code shapes
        GreenfootImage img = new GreenfootImage(30, 15);
        
        // Inner bright white core of the jab
        img.setColor(new Color(255, 255, 255, 230));
        img.fillOval(5, 2, 20, 10);
        
        // Outer glowing gold streak energy
        img.setColor(new Color(255, 215, 0, 180));
        img.drawOval(0, 0, 28, 14);
        
        setImage(img);
    }

    public void act()
    {
        // 1. CRITICAL SAFETY: If this object was already removed during this frame, 
        // stop executing immediately so we don't call methods on a null world!
        if (getWorld() == null) return;

        // Move forward in the direction of the angle
        move(speed);
        
        // Check for collisions and damage enemies during flight
        checkMeleeHit();
        
        // 2. CRITICAL SAFETY: Check again, because checkMeleeHit() might have 
        // just removed this object if it successfully struck an enemy!
        if (getWorld() == null) return;
        
        // Fade out and self-destruct quickly based on lifetime expiration
        lifetime--;
        if (lifetime <= 0) {
            getWorld().removeObject(this);
        }
    }
    
    private void checkMeleeHit()
    {
        if (getWorld() == null) return; 
        
        Actor enemy = getOneIntersectingObject(Actor.class);
        if (enemy != null && !(enemy instanceof Naobito) && !(enemy instanceof ProjectionCursor) && !(enemy instanceof GlassPanel))
        {
            if (enemy.getWorld() != null) 
            {
                if (enemy instanceof Fish) {
                    ((Fish) enemy).takeDamage(1);
                }
                else if (enemy instanceof Pufferfish) {
                    ((Pufferfish) enemy).takeDamage(1);
                }
                else if (enemy instanceof Crab) {
                    ((Crab) enemy).takeDamage(1);
                }
                
                // Remove this punch visual after a successful hit
                getWorld().removeObject(this); 
            }
        }
    }
}