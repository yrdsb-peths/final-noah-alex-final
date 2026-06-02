import greenfoot.*;
import java.util.List;

public class Nanami extends Actor
{
    private int hp = 10;
    private int laserCooldown = 0;
    private int invincibilityTimer = 0;
    private final int INVINCIBILITY_DURATION = 30;
    private HpBar healthBar;

    private int dashCooldown = 0;
    private int dashDuration = 0;
    private int moveAngle = 0;
    private DashIcon dashIcon;

    // 7:3 Ratio mechanic
    private boolean ratioEngaged = false;
    private Actor ratioTarget = null;
    private RatioBar ratioBar = null;
    private int ratioCooldown = 0;
    
    // 💥 FIX FIELD: Tracks if 'E' was pressed down in the previous frame
    private boolean eKeyHeldLastFrame = false; 

    // Sprites
    private GreenfootImage[] idleFrames, upFrames, leftFrames, rightFrames;
    private int animFrame = 0, animTimer = 0;
    private final int ANIM_SPEED = 8;

    public Nanami()
    {
        idleFrames  = loadFrames("baseguy",       4);
        upFrames    = loadFrames("baseguy-up",    4);
        leftFrames  = loadFrames("baseguy-left",  4);
        rightFrames = loadFrames("baseguy-right", 4);
        setImage(idleFrames[0]);
    }

    private GreenfootImage[] loadFrames(String base, int count)
    {
        GreenfootImage[] frames = new GreenfootImage[count];
        for (int i = 0; i < count; i++)
        {
            String suffix = (i == 0) ? "" : Integer.toString(i + 1);
            frames[i] = new GreenfootImage(base + suffix + ".png");
            frames[i].scale(50, 50);
        }
        return frames;
    }

    public void setHpBar(HpBar bar) { this.healthBar = bar; }
    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }

    public void act()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();

        if (invincibilityTimer > 0)
        {
            invincibilityTimer--;
            getImage().setTransparency(invincibilityTimer % 4 == 0 ? 100 : 255);
        }
        else if (getImage() != null) getImage().setTransparency(255);

        if (laserCooldown > 0) laserCooldown--;
        if (ratioCooldown > 0) ratioCooldown--;
        if (dashCooldown > 0) dashCooldown--;

        // Capture current 'E' key state
        boolean eIsDown = Greenfoot.isKeyDown("e");
        // An 'E Click' means the key is down now, but WAS NOT down on the previous frame loop
        boolean eMouseClicked = eIsDown && !eKeyHeldLastFrame;

        // --- 7:3 RATIO MECHANIC ---
        if (ratioEngaged)
        {
            handleRatioEngaged(eMouseClicked);
            eKeyHeldLastFrame = eIsDown; // Store keyboard history before exiting early
            return; // No movement while timing
        }

        // E Click to engage closest enemy
        if (eMouseClicked && ratioCooldown == 0)
        {
            Actor closest = findClosestEnemy();
            if (closest != null)
            {
                ratioTarget = closest;
                ratioEngaged = true;
                ratioCooldown = 120;

                // Spawn the timing bar above the enemy
                ratioBar = new RatioBar();
                getWorld().addObject(ratioBar, closest.getX(), closest.getY() - 50);
            }
        }

        // Dash execution
        if (dashDuration > 0)
        {
            dashDuration--;
            invincibilityTimer = 2;
            int cur = getRotation();
            setRotation(moveAngle);
            move(15);
            setRotation(cur);
            checkEnemyContact();
            eKeyHeldLastFrame = eIsDown; // Store keyboard history
            return;
        }

        // Normal movement
        boolean keyIsPressed = false;
        int dx1 = 0, dy1 = 0;
        GreenfootImage[] currentFrames = idleFrames;

        if (Greenfoot.isKeyDown("a")) { setLocation(getX() - 4, getY()); currentFrames = leftFrames;  keyIsPressed = true; dx1 = -1; }
        if (Greenfoot.isKeyDown("d")) { setLocation(getX() + 4, getY()); currentFrames = rightFrames; keyIsPressed = true; dx1 =  1; }
        if (Greenfoot.isKeyDown("w")) { setLocation(getX(), getY() - 4); currentFrames = upFrames;    keyIsPressed = true; dy1 = -1; }
        if (Greenfoot.isKeyDown("s")) { setLocation(getX(), getY() + 4); currentFrames = idleFrames;  keyIsPressed = true; dy1 =  1; }

        animTimer++;
        if (animTimer >= ANIM_SPEED) { animTimer = 0; animFrame = keyIsPressed ? (animFrame + 1) % 4 : 0; }
        setImage(currentFrames[animFrame]);

        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx1 != 0 || dy1 != 0))
        {
            dashDuration = 10; dashCooldown = 180;
            moveAngle = (int) Math.toDegrees(Math.atan2(dy1, dx1));
            if (dashIcon != null) dashIcon.updateIcon(3);
        }

        // Normal laser
        if (Greenfoot.mousePressed(null) && laserCooldown == 0 && mouse != null)
        {
            turnTowards(mouse.getX(), mouse.getY());
            int angle = getRotation();
            setRotation(0);
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            laser.setRotation(angle);
            laserCooldown = 20;
        }

        checkEnemyContact();
        
        // Save current frame state to compare with the next frame
        eKeyHeldLastFrame = eIsDown;
    }

    private void handleRatioEngaged(boolean eMouseClicked)
    {
        // Target died or left world
        if (ratioTarget == null || ratioTarget.getWorld() == null)
        {
            cancelRatio();
            return;
        }

        // Keep bar tracked right above target
        if (ratioBar != null && ratioBar.getWorld() != null)
            ratioBar.setLocation(ratioTarget.getX(), ratioTarget.getY() - 50);

        // Player presses E a SECOND separate time to strike
        if (eMouseClicked)
        {
            if (ratioBar != null)
            {
                boolean hitRedZone = ratioBar.isInRedZone();
                getWorld().removeObject(ratioBar);
                ratioBar = null;

                if (hitRedZone)
                {
                    // PERFECT — 7:3 hit, massive damage (deals 8 points)
                    dealRatioDamage(8, ratioTarget);
                }
                else
                {
                    // Missed the red zone, faint glancing blow (deals 2 points)
                    dealRatioDamage(2, ratioTarget);
                }
            }
            ratioEngaged = false;
            ratioTarget = null;
        }

        // Auto-cancel if bar times out
        if (ratioBar != null && ratioBar.isExpired())
        {
            cancelRatio();
        }
    }

    private void dealRatioDamage(int amount, Actor target)
    {
        if (target instanceof Fish)              ((Fish) target).takeDamage(amount);
        else if (target instanceof Pufferfish)   ((Pufferfish) target).takeDamage(amount);
        else if (target instanceof SwordfishBoss) ((SwordfishBoss) target).takeDamage(amount);
        else if (target instanceof Kraken)       ((Kraken) target).takeDamage(amount);
        else if (target instanceof Crab)         ((Crab) target).takeDamage(amount);
    }

    private void cancelRatio()
    {
        if (ratioBar != null && ratioBar.getWorld() != null)
            getWorld().removeObject(ratioBar);
        ratioBar = null;
        ratioEngaged = false;
        ratioTarget = null;
    }

    private Actor findClosestEnemy()
    {
        Actor closest = null;
        double closestDist = Double.MAX_VALUE;

        List<Crab> crabs = getWorld().getObjects(Crab.class);
        for (Crab c : crabs)
        {
            double d = distance(c);
            if (d < closestDist) { closestDist = d; closest = c; }
        }
        List<Fish> fish = getWorld().getObjects(Fish.class);
        for (Fish f : fish)
        {
            double d = distance(f);
            if (d < closestDist) { closestDist = d; closest = f; }
        }
        List<Pufferfish> puffers = getWorld().getObjects(Pufferfish.class);
        for (Pufferfish p : puffers)
        {
            double d = distance(p);
            if (d < closestDist) { closestDist = d; closest = p; }
        }
        List<SwordfishBoss> bosses = getWorld().getObjects(SwordfishBoss.class);
        for (SwordfishBoss b : bosses)
        {
            double d = distance(b);
            if (d < closestDist) { closestDist = d; closest = b; }
        }
        List<Kraken> krakens = getWorld().getObjects(Kraken.class);
        for (Kraken k : krakens)
        {
            double d = distance(k);
            if (d < closestDist) { closestDist = d; closest = k; }
        }
        return closest;
    }

    private double distance(Actor a)
    {
        int dx = a.getX() - getX();
        int dy = a.getY() - getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void checkEnemyContact()
    {
        if (invincibilityTimer == 0)
        {
            if (isTouching(Crab.class) || isTouching(Fish.class) || isTouching(Pufferfish.class))
            {
                takeDamage(1);
            }
        }
    }

    public void takeDamage(int amount)
    {
        if (invincibilityTimer == 0)
        {
            hp -= amount;
            invincibilityTimer = INVINCIBILITY_DURATION;
            if (healthBar != null) healthBar.updateBar(hp);
            if (hp <= 0) Greenfoot.setWorld(new GameOver());
        }
    }

    public void heal(int amount)
    {
        hp = Math.min(10, hp + amount);
        if (healthBar != null) healthBar.updateBar(hp);
    }
}