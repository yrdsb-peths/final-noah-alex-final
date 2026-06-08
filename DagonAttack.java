import greenfoot.*;

public class DagonAttack extends Actor
{
    private String type;
    private int timer = 0;
    private int maxDuration = 160; // Slightly extended to allow rotation time (2.6 seconds)
    private int rotationSpeed;

    public DagonAttack(String type, int baseRotation, int rotationSpeed)
    {
        this.type = type;
        this.rotationSpeed = rotationSpeed;
        setRotation(baseRotation);
        
        // Wipe class default assets completely
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
            // STRIKE FRAME: Turn solid red and check if hero is hit
            renderAttack(true);
            checkDamage();
        }
        else if (timer >= maxDuration)
        {
            // Release Dagon's movement lock freeze before deleting
            notifyDagonAttackFinished();
            getWorld().removeObject(this);
        }
    }

    private void renderAttack(boolean dangerous)
    {
        // Enormous canvas workspace map to keep rotations completely smooth without clipping bounds
        GreenfootImage img = new GreenfootImage(1600, 1600);
        Color attackColor = dangerous ? new Color(255, 0, 0, 200) : new Color(230, 0, 0, 85);
        img.setColor(attackColor);

        if (type.equals("CORNER"))
        {
            // Colossal Screen Flooding Wedge (Centered at 800, 800)
            int[] xWedge = { 800, 1600, 1600, 0, 0 };
            int[] yWedge = { 800, 0, 1600, 1600, 300 };
            img.fillPolygon(xWedge, yWedge, 5);
        }
        else if (type.equals("CENTER"))
        {
            // --- NEW: SCREEN-CLEARING ROTATING RAY BEAMS ---
            // Draws heavy thick intersecting rectangular lines passing straight through center point (800, 800)
            // This leaves clean triangle gaps between the spokes that act as shifting safe zones!
            for (int angle = 0; angle < 180; angle += 45)
            {
                GreenfootImage line = new GreenfootImage(1600, 65); // 65 pixels thick death beams
                line.setColor(attackColor);
                line.fill();
                
                // Overlay multiple rotated spokes onto our main central canvas
                img.drawImage(line, 0, 800 - 32); 
                img.rotate(45);
            }
        }

        setImage(img);
    }

    private void checkDamage()
    {
        Actor hero = getActiveHero();
        // Since the canvas handles rotation matrix automatically, checking standard overlap intersection 
        // works perfectly for catching players standing on top of the red pixels!
        if (hero != null && isTouching(hero.getClass()))
        {
            damageHero(hero);
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