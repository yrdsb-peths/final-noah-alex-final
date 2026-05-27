import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class DomainExpansion extends Actor
{
    private int lifetime = 300;      // 5 seconds at normal 60fps
    private int damageInterval = 30; // Cleanses everything every 0.5 seconds
    private int damageTimer = 0;
    
    private GreenfootImage originalBackground;
    private boolean domainInitialized = false;

    public void act()
    {
        // 1. First frame initialization: Tint the map red
        if (!domainInitialized)
        {
            originalBackground = new GreenfootImage(getWorld().getBackground());
            
            GreenfootImage redBg = new GreenfootImage(originalBackground);
            redBg.setColor(new Color(255, 0, 0, 85)); // Sukuna's blood-red atmosphere tint
            redBg.fillRect(0, 0, redBg.getWidth(), redBg.getHeight());
            getWorld().setBackground(redBg);
            
            domainInitialized = true;
        }

        // 2. Spawn random rapid-fire Dismantle cuts around the screen frame
        if (Greenfoot.getRandomNumber(10) < 6) 
        {
            spawnDismantleCut();
        }

        // 3. Tick and apply continuous domain damage
        damageTimer++;
        if (damageTimer >= damageInterval)
        {
            damageTimer = 0;
            executeCleaveAndDismantle();
        }

        // 4. Timer clean up loop
        lifetime--;
        if (lifetime <= 0)
        {
            // Dismantle the shrine and restore the normal world view
            getWorld().setBackground(originalBackground);
            getWorld().removeObject(this);
        }
    }

    private void spawnDismantleCut()
    {
        DismantleVisual slash = new DismantleVisual();
        int rx = Greenfoot.getRandomNumber(getWorld().getWidth());
        int ry = Greenfoot.getRandomNumber(getWorld().getHeight());
        
        getWorld().addObject(slash, rx, ry);
        slash.setRotation(Greenfoot.getRandomNumber(360)); // Chaotic multi-angle slices
    }

    private void executeCleaveAndDismantle()
    {
        // Hit standard Fish
        List<Fish> allFish = getWorld().getObjects(Fish.class);
        for (Fish f : allFish) f.takeDamage(2);

        // Hit Pufferfish
        List<Pufferfish> allPuffers = getWorld().getObjects(Pufferfish.class);
        for (Pufferfish p : allPuffers) p.takeDamage(2);

        // Hit Swordfish Bosses
        List<SwordfishBoss> allSwordfish = getWorld().getObjects(SwordfishBoss.class);
        for (SwordfishBoss s : allSwordfish) s.takeDamage(2);
        
        // Hit the Kraken Boss (This will now run completely error-free!)
        List<Kraken> allKraken = getWorld().getObjects(Kraken.class);
        for (Kraken k : allKraken) k.takeDamage(2);
    }
}