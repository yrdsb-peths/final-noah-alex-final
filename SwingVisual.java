import greenfoot.*;
import java.util.List;

public class SwingVisual extends Actor
{
    private int lifetime = 6;            
    private final int TOTAL_ARC = 90;     
    private int degreesPerFrame;          
    private int currentOffsetAngle;       
    private int baseAngle;                
    private boolean isCritical;
    private Nanami owner;                 

    public SwingVisual(Nanami owner, int angle, boolean isCritical)
    {
        this.owner = owner;
        this.baseAngle = angle;
        this.isCritical = isCritical;
        
        this.currentOffsetAngle = -45; 
        this.degreesPerFrame = TOTAL_ARC / lifetime; 
        
        setRotation(baseAngle + currentOffsetAngle);
        
        GreenfootImage img = new GreenfootImage(110, 55);
        
        if (isCritical) {
            img.setColor(new Color(0, 100, 255, 200));
            img.fillOval(0, 5, 110, 45);
            img.setColor(new Color(150, 240, 255, 245));
            img.fillOval(15, 12, 80, 30);
        } else {
            img.setColor(new Color(230, 210, 150, 220));
            img.fillOval(10, 10, 90, 35);
            img.setColor(new Color(255, 255, 255, 255));
            img.drawOval(10, 10, 89, 34);
        }
        setImage(img);
    }

    public void act()
    {
        if (owner == null || owner.getWorld() == null) {
            getWorld().removeObject(this);
            return;
        }

        currentOffsetAngle += degreesPerFrame;
        setRotation(baseAngle + currentOffsetAngle);
        
        // Follows Nanami's coordinates smoothly in real-time
        double rad = Math.toRadians(getRotation());
        int radiusOffset = 55; 
        
        int targetX = owner.getX() + (int)(Math.cos(rad) * radiusOffset);
        int targetY = owner.getY() + (int)(Math.sin(rad) * radiusOffset);
        setLocation(targetX, targetY);
        
        // Scan for hits throughout the sweep
        checkMeleeHit();
        
        if (getWorld() == null) return;
        
        lifetime--;
        if (lifetime <= 0) {
            getWorld().removeObject(this);
        }
    }
    
    public boolean isCritical()
    {
        return this.isCritical;
    }
    
    private void checkMeleeHit()
    {
        if (getWorld() == null) return;
        
        List<Actor> targets = getIntersectingObjects(Actor.class);
        
        for (Actor enemy : targets)
        {
            if (enemy != owner && !(enemy instanceof RatioBar) && !(enemy instanceof SwingVisual))
            {
                if (enemy.getWorld() != null)
                {
                    int damageDealt = isCritical ? 3 : 1;
                    String enemyClassName = enemy.getClass().getSimpleName();
                    
                    // Route damage safely to any enemy types
                    if (enemyClassName.equals("Fish")) {
                        try {
                            enemy.getClass().getMethod("takeDamage", int.class).invoke(enemy, damageDealt);
                        } catch(Exception e) {}
                    }
                    else if (enemy instanceof Pufferfish) {
                        ((Pufferfish) enemy).takeDamage(damageDealt);
                    }
                    else if (enemy instanceof Crab) {
                        ((Crab) enemy).takeDamage(damageDealt);
                    }
                    else if (enemy instanceof Turtle) {
                        ((Turtle) enemy).takeDamage(damageDealt);
                    }
                    else if (enemy instanceof Dagon) {
                        ((Dagon) enemy).takeDamage(damageDealt);
                    }
                }
            }
        }
    }
}