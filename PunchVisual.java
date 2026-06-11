import greenfoot.*;

public class PunchVisual extends Actor
{
    private int lifetime = 10; // Extended slightly for maximum range extension
    private int speed = 15;     // Swift, snappier jab to outrange enemy advance
    
    public PunchVisual(int angle)
    {
        setRotation(angle);
        GreenfootImage img = new GreenfootImage(70, 35);
        
        // Outer glowing Projection Sorcery gold streak energy aura
        img.setColor(new Color(255, 215, 0, 160));
        img.fillOval(0, 0, 70, 35);
        
        // Inner bright white core of the high-speed strike
        img.setColor(new Color(255, 255, 255, 230));
        img.fillOval(10, 5, 50, 25);
        
        setImage(img);
    }

    public void act()
    {
        if (getWorld() == null) return;

        // Move forward in the direction of the angle
        move(speed);
        
        // Check for collisions and damage enemies during flight
        checkMeleeHit();
        
        if (getWorld() == null) return;
        
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
                // --- FIXED: NAOBITO CAN NOW HURT TURTLES ---
                else if (enemy instanceof Turtle) {
                    ((Turtle) enemy).takeDamage(1);
                }
                else if (enemy instanceof Dagon) {
                    ((Dagon) enemy).takeDamage(1);
                }
                
                // Remove this punch visual after a successful hit
                getWorld().removeObject(this); 
            }
        }
    }
}