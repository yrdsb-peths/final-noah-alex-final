import greenfoot.*;
import java.util.List;

public class Naobito extends Actor
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

    // Projection Sorcery states
    private final int PS_NONE       = 0;
    private final int PS_FROZEN     = 1; 
    private final int PS_TRACING    = 2; 
    private final int PS_CONFIRM    = 3; 
    private final int PS_EXECUTING  = 4; 
    private final int PS_LOCKED     = 5; 
    private int psState = PS_NONE;

    private int psTimer = 0;
    private final int FREEZE_DURATION = 180; 
    private final int LOCKED_DURATION = 120; 

    private java.util.ArrayList<int[]> tracedPath = new java.util.ArrayList<>();
    private int pathIndex = 0;
    private int firstMoveDir = -1; 

    private Actor frozenEnemy = null;
    private int frozenTimer = 0;

    private GreenfootImage normalBg = null;
    private boolean greyedOut = false;

    // --- Constructor (Matches Hero's loop style exactly) ---
    // --- Constructor (Cleans up the first frame for all directions) ---
    public Naobito()
    {
        idleFrames = new GreenfootImage[4];
        upFrames = new GreenfootImage[4];
        leftFrames = new GreenfootImage[4];
        rightFrames = new GreenfootImage[4];

        for (int i = 0; i < 4; i++)
        {
            // If it's the first frame (i == 0), leave the name clean. Otherwise, append 2, 3, or 4.
            String suffix = (i == 0) ? "" : Integer.toString(i + 1);
            
            idleFrames[i] = new GreenfootImage("naobito" + suffix + ".png");
            idleFrames[i].scale(50, 60);
            
            upFrames[i] = new GreenfootImage("naobito-up" + suffix + ".png");
            upFrames[i].scale(50, 60);
            
            leftFrames[i] = new GreenfootImage("naobito-left" + suffix + ".png");
            leftFrames[i].scale(50, 60);
            
            rightFrames[i] = new GreenfootImage("naobito-right" + suffix + ".png");
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
        
        if (dashCooldown > 0)
        {
            dashCooldown--;
            if (dashCooldown % 60 == 0 && dashIcon != null)
            {
                dashIcon.updateIcon((dashCooldown / 60) + (dashCooldown % 60 > 0 ? 1 : 0));
            }
        }

        handleProjectionSorcery(mouse);

        if (psState == PS_FROZEN || psState == PS_TRACING ||
            psState == PS_CONFIRM || psState == PS_EXECUTING || psState == PS_LOCKED)
        {
            if (psState == PS_LOCKED)
            {
                getImage().setTransparency(psTimer % 6 < 3 ? 60 : 255);
            }
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
    }

    private void handleProjectionSorcery(MouseInfo mouse)
    {
        if (Greenfoot.isKeyDown("q") && psState == PS_NONE && laserCooldown == 0)
        {
            Actor target = getEnemyInAimLine(mouse);

            if (target != null)
            {
                frozenEnemy = target;
                frozenTimer = 180; 
                setLocation(target.getX(), target.getY() - 40);
                psState = PS_NONE; 
                laserCooldown = 30;

                if (target.getImage() != null)
                    target.getImage().setTransparency(150);
            }
            else
            {
                psState = PS_FROZEN;
                psTimer = FREEZE_DURATION;
                tracedPath.clear();
                firstMoveDir = -1;
                tracedPath.add(new int[]{getX(), getY()});
                applyGreyOut(true);
            }
        }

        if (frozenEnemy != null)
        {
            frozenTimer--;
            if (frozenTimer <= 0 || frozenEnemy.getWorld() == null)
            {
                if (frozenEnemy.getWorld() != null)
                    frozenEnemy.getImage().setTransparency(255);
                frozenEnemy = null;
            }
            else if (Greenfoot.isKeyDown("space"))
            {
                killFrozenEnemy();
            }
        }

        if (psState == PS_FROZEN || psState == PS_TRACING)
        {
            psTimer--;

            int tx = getX(), ty = getY();
            boolean moved = false;
            int dir = -1;

            if (Greenfoot.isKeyDown("a")) { tx -= 4; dir = 2; moved = true; }
            if (Greenfoot.isKeyDown("d")) { tx += 4; dir = 3; moved = true; }
            if (Greenfoot.isKeyDown("w")) { ty -= 4; dir = 0; moved = true; }
            if (Greenfoot.isKeyDown("s")) { ty += 4; dir = 1; moved = true; }

            if (moved)
            {
                psState = PS_TRACING;
                if (firstMoveDir == -1) firstMoveDir = dir;
                setLocation(tx, ty);
                if (psTimer % 5 == 0)
                    tracedPath.add(new int[]{tx, ty});
            }

            if (psTimer <= 0)
            {
                psState = PS_CONFIRM;
                psTimer = 90; 
                applyGreyOut(false);
                if (tracedPath.size() > 0)
                    setLocation(tracedPath.get(0)[0], tracedPath.get(0)[1]);
            }
        }

        if (psState == PS_CONFIRM)
        {
            psTimer--;

            int pressedDir = -1;
            if (Greenfoot.isKeyDown("w")) pressedDir = 0;
            if (Greenfoot.isKeyDown("s")) pressedDir = 1;
            if (Greenfoot.isKeyDown("a")) pressedDir = 2;
            if (Greenfoot.isKeyDown("d")) pressedDir = 3;

            if (pressedDir != -1)
            {
                if (pressedDir == firstMoveDir)
                {
                    psState = PS_EXECUTING;
                    pathIndex = 0;
                    psTimer = tracedPath.size() * 2;
                }
                else
                {
                    psState = PS_LOCKED;
                    psTimer = LOCKED_DURATION;
                }
            }

            if (psTimer <= 0)
            {
                psState = PS_LOCKED;
                psTimer = LOCKED_DURATION;
            }
        }

        if (psState == PS_EXECUTING)
        {
            if (pathIndex < tracedPath.size())
            {
                int[] point = tracedPath.get(pathIndex);
                setLocation(point[0], point[1]);
                pathIndex++;
                invincibilityTimer = 5; 
            }
            else
            {
                psState = PS_NONE;
                laserCooldown = 60;
            }
        }

        if (psState == PS_LOCKED)
        {
            psTimer--;
            if (psTimer <= 0) psState = PS_NONE;
        }
    }

    private Actor getEnemyInAimLine(MouseInfo mouse)
    {
        if (mouse == null) return null;

        double aimRad = Math.atan2(mouse.getY() - getY(), mouse.getX() - getX());
        List<Fish> fish = getWorld().getObjects(Fish.class);
        for (Fish f : fish)
        {
            double angleToEnemy = Math.atan2(f.getY() - getY(), f.getX() - getX());
            double diff = Math.abs(aimRad - angleToEnemy);
            if (diff < 0.15) return f; 
        }
        List<Pufferfish> puffers = getWorld().getObjects(Pufferfish.class);
        for (Pufferfish p : puffers)
        {
            double angleToEnemy = Math.atan2(p.getY() - getY(), p.getX() - getX());
            double diff = Math.abs(aimRad - angleToEnemy);
            if (diff < 0.15) return p;
        }
        List<Crab> crabs = getWorld().getObjects(Crab.class);
        for (Crab c : crabs)
        {
            double angleToEnemy = Math.atan2(c.getY() - getY(), c.getX() - getX());
            double diff = Math.abs(aimRad - angleToEnemy);
            if (diff < 0.15) return c;
        }
        return null;
    }

    private void killFrozenEnemy()
    {
        if (frozenEnemy == null || frozenEnemy.getWorld() == null) return;
        if (frozenEnemy instanceof Fish) ((Fish) frozenEnemy).takeDamage(99);
        else if (frozenEnemy instanceof Pufferfish) ((Pufferfish) frozenEnemy).takeDamage(99);
        else if (frozenEnemy instanceof Crab) ((Crab) frozenEnemy).takeDamage(99);
        frozenEnemy = null;
    }

    private void applyGreyOut(boolean grey)
    {
        greyedOut = grey;
        World w = getWorld();
        if (w == null) return;
        if (grey)
        {
            GreenfootImage overlay = new GreenfootImage(w.getWidth(), w.getHeight());
            overlay.setColor(new Color(100, 100, 120, 160));
            overlay.fillRect(0, 0, w.getWidth(), w.getHeight());
            GreenfootImage bg = w.getBackground();
            if (bg != null) bg.drawImage(overlay, 0, 0);
        }
        else
        {
            GreenfootImage bg = new GreenfootImage("beach.jpg");
            bg.scale(w.getWidth(), w.getHeight());
            w.setBackground(bg);
        }
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