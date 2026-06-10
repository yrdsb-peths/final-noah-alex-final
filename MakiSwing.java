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
    private int damage = 5; 
    private boolean isReversed = false;

    // Overloaded constructor so it doesn't break any other code scripts
    public MakiSwing(Maki owner, int angle) {
        this(owner, angle, false);
    }

    public MakiSwing(Maki owner, int angle, boolean isReversed)
    {
        this.owner = owner;
        this.baseAngle = angle;
        this.isReversed = isReversed;
        
        // --- REVERSED ANGULAR MATH ---
        if (isReversed) {
            this.currentOffsetAngle = 45; // Start on the positive side
            this.degreesPerFrame = -(TOTAL_ARC / lifetime); // Step backward each frame
        } else {
            this.currentOffsetAngle = -45;
            this.degreesPerFrame = TOTAL_ARC / lifetime;
        }
        
        setRotation(baseAngle + currentOffsetAngle);
        
        GreenfootImage img = new GreenfootImage("cloudarc.png");
        // Optional: Mirror the image visual horizontally if it looks backwards
        if (isReversed) {
            img.mirrorVertically(); 
        }
        img.scale(75, 75);
        setImage(img);
    }

    public void act()
    {
        if (getWorld() == null || owner == null || owner.getWorld() == null) {
            if (getWorld() != null) getWorld().removeObject(this);
            return;
        }
        
        // Pushes the spinning pivot slightly out forward during the swing processing phase
        int forwardProjection = 20;
        double rad = Math.toRadians(baseAngle);
        int targetX = owner.getX() + (int)(forwardProjection * Math.cos(rad));
        int targetY = owner.getY() + (int)(forwardProjection * Math.sin(rad));
        setLocation(targetX, targetY);
        
        currentOffsetAngle += degreesPerFrame;
        setRotation(baseAngle + currentOffsetAngle);
        
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
                    else if (enemy instanceof Turtle) { 
                        ((Turtle) enemy).takeDamage(damage); 
                    }
                    else if (enemy instanceof SwordfishBoss) { 
                        ((SwordfishBoss) enemy).takeDamage(damage); 
                    }
                    else if (enemy instanceof Kraken) { 
                        ((Kraken) enemy).takeDamage(damage); 
                    }
                    else if (enemy instanceof Dagon) {
                        ((Dagon) enemy).takeDamage(damage);
                    }
                }
            }
        }
    }
}