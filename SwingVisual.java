import greenfoot.*;

public class SwingVisual extends Actor
{
    private int lifetime = 6;            // Total frames the swing lasts
    private final int TOTAL_ARC = 90;     // The quarter-circle swing arc (in degrees)
    private int degreesPerFrame;          // How much it rotates each frame
    private int currentOffsetAngle;       // Current relative angle of the swing
    private int baseAngle;                // The initial angle towards the mouse pointer
    private boolean isCritical;
    private Nanami owner;                 // Reference to Nanami to lock onto his position

    public SwingVisual(Nanami owner, int angle, boolean isCritical)
    {
        this.owner = owner;
        this.baseAngle = angle;
        this.isCritical = isCritical;
        
        // Start the swing 45 degrees to one side, so it sweeps past the cursor perfectly
        this.currentOffsetAngle = -45; 
        this.degreesPerFrame = TOTAL_ARC / lifetime; // Smoothly cover 90 degrees over its lifetime
        
        // Set initial facing direction
        setRotation(baseAngle + currentOffsetAngle);
        
        // LONGER RANGE: Increased visual dimensions from (80, 40) to (110, 55) for a massive blade size
        GreenfootImage img = new GreenfootImage(110, 55);
        
        if (isCritical) {
            // CURSED ENERGY BLUE: Deep blue outer flame with a bright cyan core
            img.setColor(new Color(0, 50, 255, 240)); 
            img.fillOval(0, 0, 110, 55);
            img.setColor(new Color(0, 240, 255, 255));
            img.fillOval(25, 0, 85, 55); 
        } else {
            img.setColor(new Color(220, 220, 220, 190));
            img.fillOval(0, 0, 100, 45);
            img.setColor(new Color(0, 0, 0, 0)); // Masks the back to form a crescent blade
            img.fillOval(25, 0, 75, 45); 
        }
        
        setImage(img);
    }

    public void act()
    {
        // Safety check: if Nanami or the world is missing, vanish immediately
        if (getWorld() == null || owner == null || owner.getWorld() == null) {
            if (getWorld() != null) getWorld().removeObject(this);
            return;
        }
        
        // PIVOT LOCK: Constantly snap the corner of the swing directly to Nanami's center point
        setLocation(owner.getX(), owner.getY());
        
        // Sweep the angle forward across the quarter-circle arc
        currentOffsetAngle += degreesPerFrame;
        setRotation(baseAngle + currentOffsetAngle);
        
        // LONGER RANGE OFFSET: Increased radiusOffset from 35 to 55 to push the swing arc further out from Nanami's body
        double rad = Math.toRadians(getRotation());
        int radiusOffset = 55; 
        setLocation(getX() + (int)(Math.cos(rad) * radiusOffset), getY() + (int)(Math.sin(rad) * radiusOffset));
        
        // Detect cuts throughout the entire sweeping path
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
        if (enemy != null && !(enemy instanceof Nanami) && !(enemy instanceof RatioBar))
        {
            if (enemy.getWorld() != null)
            {
                int damageDealt = isCritical ? 3 : 1;
                
                if (enemy instanceof Fish) {
                    ((Fish) enemy).takeDamage(damageDealt);
                }
                else if (enemy instanceof Pufferfish) {
                    ((Pufferfish) enemy).takeDamage(damageDealt);
                }
                else if (enemy instanceof Crab) {
                    ((Crab) enemy).takeDamage(damageDealt);
                }
            }
        }
    }
}