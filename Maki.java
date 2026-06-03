import greenfoot.*;
import java.util.List;

public class Maki extends Actor
{
    // Stats
    private int hp = 10;
    private int laserCooldown = 0;
    private int invincibilityTimer = 0;
    private final int INVINCIBILITY_DURATION = 30;
    private HpBar healthBar;

    // Dash (longer than Hero's)
    private int dashCooldown = 0;
    private int dashDuration = 0;
    private int moveAngle = 0;
    private DashIcon dashIcon;

    // Cloud weapon state
    private MakiCloud orbitCloud = null;   // left click cloud
    private boolean rightHeldLastFrame = false;
    private boolean middleHeldLastFrame = false;

    // Sprites (reuse hero sprites - swap if you have Maki sprites)
    private GreenfootImage[] idleFrames;
    private GreenfootImage[] upFrames;
    private GreenfootImage[] leftFrames;
    private GreenfootImage[] rightFrames;
    private int animFrame = 0;
    private int animTimer = 0;
    private final int ANIM_SPEED = 8;
    private int stunTimer = 0; 
        public void getStunned(int frames)
    {
        this.stunTimer = frames;
        //make the hero turn blue/gray when stunned
        getImage().setColor(new Color(0, 150, 255)); 
    }
    
    public Maki()
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
        if (stunTimer > 0)
        {
            stunTimer--;
            
            // If stun just ended, restore normal look appearance
            if (stunTimer == 0) {
                //setImage(frames); 
            }
            
            // CRITICAL: Stop everything else! Skips movement, shooting, and WASD keys completely
            return; 
        }
        MouseInfo mouse = Greenfoot.getMouseInfo();

        // Timers
        if (invincibilityTimer > 0)
        {
            invincibilityTimer--;
            getImage().setTransparency(invincibilityTimer % 4 == 0 ? 100 : 255);
        }
        else if (getImage() != null) getImage().setTransparency(255);

        if (laserCooldown > 0) laserCooldown--;

        if (dashCooldown > 0)
        {
            dashCooldown--;
            if (dashCooldown % 60 == 0 && dashIcon != null)
                dashIcon.updateIcon(dashCooldown / 60);
        }

        // Dash execution (longer than Hero - 16 frames instead of 10)
        if (dashDuration > 0)
        {
            dashDuration--;
            invincibilityTimer = 2;
            int cur = getRotation();
            setRotation(moveAngle);
            move(18); // slightly faster dash speed too
            setRotation(cur);
            checkEnemyContact();
            return;
        }

        // Movement
        boolean keyIsPressed = false;
        int dx1 = 0, dy1 = 0;
        GreenfootImage[] currentFrames = idleFrames;

        if (Greenfoot.isKeyDown("a")) { setLocation(getX() - 6, getY()); currentFrames = leftFrames;  keyIsPressed = true; dx1 = -1; }
        if (Greenfoot.isKeyDown("d")) { setLocation(getX() + 6, getY()); currentFrames = rightFrames; keyIsPressed = true; dx1 =  1; }
        if (Greenfoot.isKeyDown("w")) { setLocation(getX(), getY() - 6); currentFrames = upFrames;    keyIsPressed = true; dy1 = -1; }
        if (Greenfoot.isKeyDown("s")) { setLocation(getX(), getY() + 6); currentFrames = idleFrames;  keyIsPressed = true; dy1 =  1; }

        // Animation
        animTimer++;
        if (animTimer >= ANIM_SPEED)
        {
            animTimer = 0;
            animFrame = keyIsPressed ? (animFrame + 1) % 4 : 0;
        }
        setImage(currentFrames[animFrame]);

        // Dash activation (longer: 16 frames)
        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx1 != 0 || dy1 != 0))
        {
            dashDuration = 16;
            dashCooldown = 180;
            moveAngle = (int) Math.toDegrees(Math.atan2(dy1, dx1));
            if (dashIcon != null) dashIcon.updateIcon(3);
        }

        // --- CLOUD WEAPON ---
        if (mouse != null)
        {
            boolean leftClick   = Greenfoot.mousePressed(null) && mouse.getButton() == 1;
            boolean middleClick = mouse.getButton() == 2 && Greenfoot.mousePressed(null);
            boolean rightClick  = mouse.getButton() == 3 && Greenfoot.mousePressed(null);

            // Left click: spawn orbiting cloud (one at a time)
            if (leftClick && laserCooldown == 0)
            {
                if (orbitCloud != null && orbitCloud.getWorld() != null)
                    getWorld().removeObject(orbitCloud);
                orbitCloud = new MakiCloud("ORBIT", getX(), getY(), 0);
                getWorld().addObject(orbitCloud, getX(), getY());
                laserCooldown = 30;
            }

            // Middle click: boomerang arc throw
            if (middleClick && laserCooldown == 0)
            {
                turnTowards(mouse.getX(), mouse.getY());
                int angle = getRotation();
                setRotation(0);
                MakiCloud boomerang = new MakiCloud("BOOMERANG", getX(), getY(), angle);
                getWorld().addObject(boomerang, getX(), getY());
                laserCooldown = 40;
            }

            // Right click: forward curve shot
            if (rightClick && laserCooldown == 0)
            {
                turnTowards(mouse.getX(), mouse.getY());
                int angle = getRotation();
                setRotation(0);
                MakiCloud curveShot = new MakiCloud("CURVE", getX(), getY(), angle);
                getWorld().addObject(curveShot, getX(), getY());
                laserCooldown = 25;
            }
        }

        checkEnemyContact();
    }

    private void checkEnemyContact()
    {
        if (isTouching(Fish.class) && invincibilityTimer == 0)
            takeDamage(1);
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