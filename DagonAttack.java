import greenfoot.*;

public class DagonAttack extends Actor
{
    private String type;
    private int timer = 0;
    private int maxDuration = 110; 
    private int rotationSpeed;
    private int spawnedRotation; // Remembers the initial direction pointing to center

    public DagonAttack(String type, int baseRotation, int rotationSpeed)
    {
        this.type = type;
        this.rotationSpeed = rotationSpeed;
        this.spawnedRotation = baseRotation;
        setRotation(baseRotation);
        
        setImage(new GreenfootImage(1, 1)); 
        renderAttack(false); 
    }

    public void act()
    {
        timer++;
        
        // Both attacks now utilize rotation vectors beautifully
        setRotation(getRotation() + rotationSpeed);

        // Flashing telegraphing phase
        if (timer < 90)
        {
            if (timer % 10 == 0) renderAttack(timer % 20 == 0);
        }
        else if (timer == 90)
        {
            // Frame 90: The real attack triggers and deals damage instantly
            renderAttack(true);
            checkDamage();
        }
        else if (timer >= maxDuration)
        {
            // Frame 110: Clean up the arena and only THEN let Dagon relocate
            notifyDagonAttackFinished();
            getWorld().removeObject(this);
        }
    }

    private void renderAttack(boolean dangerous)
    {
        // Sized bounding box to perfectly contain our attack assets
        GreenfootImage img = new GreenfootImage(1600, 1600);
        Color attackColor = dangerous ? new Color(255, 0, 0, 200) : new Color(230, 0, 0, 85);
        img.setColor(attackColor);

        if (type.equals("CORNER"))
        {
            // Center anchor point of drawing mapping space is at (800, 800)
            int[] xWedge = { 800, 1600, 1400, 1050 };
            int[] yWedge = { 800, 500, 1100, 1300 };
            img.fillPolygon(xWedge, yWedge, 4);
        }
        else if (type.equals("CENTER"))
        {
            for (int angle = 0; angle < 180; angle += 45)
            {
                GreenfootImage line = new GreenfootImage(1600, 65); 
                line.setColor(attackColor);
                line.fill();
                
                img.drawImage(line, 0, 800 - 32); 
                img.rotate(45);
            }
        }

        setImage(img);
    }

    private void checkDamage()
    {
        Actor hero = getActiveHero();
        if (hero == null) return;

        // Calculate accurate distance distance vectors to the center anchor point
        double dx = hero.getX() - getX();
        double dy = hero.getY() - getY();
        double distance = Math.hypot(dx, dy);

        if (type.equals("CORNER"))
        {
            // Find the player's angle relative to Dagon (-180 to 180 degrees)
            int playerAngle = (int) Math.toDegrees(Math.atan2(dy, dx));
            
            // Normalize the angles to prevent wrapping bugs near 180/-180 boundary zones
            int diff = playerAngle - spawnedRotation;
            while (diff < -180) diff += 360;
            while (diff > 180)  diff -= 360;

            // --- ACCURATE HITBOX DETECTION ---
            // If the player is within 650 pixels of Dagon, and caught inside his custom 
            // 45-degree wedge zone, they take a hit! Anywhere else is completely safe.
            if (distance <= 650 && diff >= -15 && diff <= 45)
            {
                damageHero(hero);
            }
        }
        else if (type.equals("CENTER"))
        {
            // For rotating lines in the center, checking image intersection works 
            // perfectly because the lines spin around the map natively
            if (isTouching(hero.getClass()))
            {
                damageHero(hero);
            }
        }
    }

    private Actor getActiveHero()
    {
        if (getWorld() == null) return null;
        if (!getWorld().getObjects(Maki.class).isEmpty()) return getWorld().getObjects(Maki.class).get(0);
        if (!getWorld().getObjects(Naobito.class).isEmpty()) return getWorld().getObjects(Naobito.class).get(0);
        if (!getWorld().getObjects(Nanami.class).isEmpty()) return getWorld().getObjects(Nanami.class).get(0);
        return null;
    }

    private void damageHero(Actor hero)
    {
        if (hero instanceof Maki) ((Maki)hero).takeDamage(2);
        else if (hero instanceof Naobito) ((Naobito)hero).takeDamage(2);
        else if (hero instanceof Nanami) ((Nanami)hero).takeDamage(2);
    }

    private void notifyDagonAttackFinished()
    {
        java.util.List<Dagon> bosses = getWorld().getObjects(Dagon.class);
        if (!bosses.isEmpty())
        {
            bosses.get(0).unlockMovementAfterAttack();
        }
    }
}