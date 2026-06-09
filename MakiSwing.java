import greenfoot.*;
import java.util.List;

public class MakiSwing extends Actor
{
    private int lifetime = 6;            
    private final int TOTAL_ARC = 90;     
    private int degreesPerFrame;          
    private int currentOffsetAngle;       
    private int baseAngle;                
    private Maki owner;                 
    private int damage = 5; // Balanced with MakiCloud's damage values

    public MakiSwing(Maki owner, int angle)
    {
        this.owner = owner;
        this.baseAngle = angle;
        
        this.currentOffsetAngle = -45; 
        this.degreesPerFrame = TOTAL_ARC / lifetime; 
        
        setRotation(baseAngle + currentOffsetAngle);
        
        GreenfootImage img = new GreenfootImage("cloudarc.png");
        img.scale(75, 75);
        setImage(img);
    }

    public void act()
    {
        if (getWorld() == null || owner == null || owner.getWorld() == null) {
            if (getWorld() != null) getWorld().removeObject(this);
            return;
        }
        
        // PIVOT LOCK: Lock onto Maki's coordinates
        setLocation(owner.getX(), owner.getY());
        
        currentOffsetAngle += degreesPerFrame;
        setRotation(baseAngle + currentOffsetAngle);
        
        // EXTRA LONG RANGE RADIUS OFFSET: Projects the swing arc far out from Maki's body center
        double rad = Math.toRadians(getRotation());
        int radiusOffset = 75; 
        setLocation(getX() + (int)(Math.cos(rad) * radiusOffset), getY() + (int)(Math.sin(rad) * radiusOffset));
        
        // FIXED DAMAGE ENGINE: Checks everything caught in the swing footprint
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
        
        // Grab all overlapping entities in the crescent path
        List<Actor> targets = getIntersectingObjects(Actor.class);
        
        for (Actor enemy : targets)
        {
            if (enemy != null && !(enemy instanceof Maki) && !(enemy instanceof MakiCloud) && !(enemy instanceof DashIcon) && !(enemy instanceof HpBar))
            {
                if (enemy.getWorld() != null)
                {
                    if (enemy instanceof Fish) { 
                        ((Fish) enemy).takeDamage(damage); 
                    }
                    else if (enemy instanceof Pufferfish) { 
                        ((Pufferfish) enemy).takeDamage(damage); 
                    }
                    else if (enemy instanceof Crab) { 
                        ((Crab) enemy).takeDamage(damage); 
                    }
                    else if (enemy instanceof SwordfishBoss) { 
                        ((SwordfishBoss) enemy).takeDamage(damage); 
                    }
                    else if (enemy instanceof Kraken) { 
                        ((Kraken) enemy).takeDamage(damage); 
                    }
                }
            }
        }
    }
}