import greenfoot.*;
import java.util.List;

public class Nanami extends Actor
{
    private GreenfootImage[] idleFrames;
    private GreenfootImage[] upFrames;
    private GreenfootImage[] leftFrames;
    private GreenfootImage[] rightFrames;
    private int animFrame = 0;
    private int animTimer = 0;
    private final int ANIM_SPEED = 8; // lower = faster animation

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
    private boolean eKeyHeldLastFrame = false; 

    // --- Constructor (Matches Hero's loop style exactly) ---
    // --- Constructor (Cleans up the first frame for all directions) ---
    public Nanami()
    {
        idleFrames = new GreenfootImage[4];
        upFrames = new GreenfootImage[4];
        leftFrames = new GreenfootImage[4];
        rightFrames = new GreenfootImage[4];

        for (int i = 0; i < 4; i++)
        {
            // If it's the first frame (i == 0), leave the name clean. Otherwise, append 2, 3, or 4.
            String suffix = (i == 0) ? "" : Integer.toString(i + 1);
            
            idleFrames[i] = new GreenfootImage("nanami" + suffix + ".png");
            idleFrames[i].scale(50, 60);
            
            upFrames[i] = new GreenfootImage("nanami-up" + suffix + ".png");
            upFrames[i].scale(50, 60);
            
            leftFrames[i] = new GreenfootImage("nanami-left" + suffix + ".png");
            leftFrames[i].scale(50, 60);
            
            rightFrames[i] = new GreenfootImage("nanami-right" + suffix + ".png");
            rightFrames[i].scale(50, 60);
        }
        
        setImage(idleFrames[0]);
    }

    public void setHpBar(HpBar bar) { this.healthBar = bar; }
    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }

    public void act()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();

        if (invincibilityTimer > 0)
        {
            invincibilityTimer--;
            if (invincibilityTimer % 4 == 0) getImage().setTransparency(100);
            else getImage().setTransparency(255);
        }
        else if (getImage() != null) getImage().setTransparency(255);

        if (laserCooldown > 0) laserCooldown--;
        if (ratioCooldown > 0) ratioCooldown--;
        
        if (dashCooldown > 0)
        {
            dashCooldown--;
            if (dashCooldown % 60 == 0 && dashIcon != null)
            {
                dashIcon.updateIcon((dashCooldown / 60) + (dashCooldown % 60 > 0 ? 1 : 0));
            }
        }

        boolean eIsDown = Greenfoot.isKeyDown("e");
        boolean eMouseClicked = eIsDown && !eKeyHeldLastFrame;

        // --- 7:3 RATIO MECHANIC ---
        if (ratioEngaged)
        {
            handleRatioEngaged(eMouseClicked);
            eKeyHeldLastFrame = eIsDown;
            return; 
        }

        if (eMouseClicked && ratioCooldown == 0)
        {
            Actor closest = findClosestEnemy();
            if (closest != null)
            {
                ratioTarget = closest;
                ratioEngaged = true;
                ratioCooldown = 120;

                ratioBar = new RatioBar();
                getWorld().addObject(ratioBar, closest.getX(), closest.getY() - 50);
            }
        }

        // Dash execution
        if (dashDuration > 0)
        {
            dashDuration--;
            invincibilityTimer = 2;
            int currentRotation = getRotation();
            setRotation(moveAngle);
            move(15);
            setRotation(currentRotation);
            checkEnemyContact();
            eKeyHeldLastFrame = eIsDown;
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
        if (animTimer >= ANIM_SPEED)
        {
            animTimer = 0;
            if (keyIsPressed)
            {
                animFrame = (animFrame + 1) % 4;
            }
            else
            {
                animFrame = 0;
            }
        }
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
        eKeyHeldLastFrame = eIsDown;
    }

    private void handleRatioEngaged(boolean eMouseClicked)
    {
        if (ratioTarget == null || ratioTarget.getWorld() == null)
        {
            cancelRatio();
            return;
        }

        if (ratioBar != null && ratioBar.getWorld() != null)
            ratioBar.setLocation(ratioTarget.getX(), ratioTarget.getY() - 50);

        if (eMouseClicked)
        {
            if (ratioBar != null)
            {
                boolean hitRedZone = ratioBar.isInRedZone();
                getWorld().removeObject(ratioBar);
                ratioBar = null;

                if (hitRedZone)
                {
                    dealRatioDamage(8, ratioTarget);
                }
                else
                {
                    dealRatioDamage(2, ratioTarget);
                }
            }
            ratioEngaged = false;
            ratioTarget = null;
        }

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