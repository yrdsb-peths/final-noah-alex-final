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

    // Dash
    private int dashCooldown = 0;
    private int dashDuration = 0;
    private int moveAngle = 0;
    private DashIcon dashIcon;

    // Cloud weapon state
    private MakiCloud orbitCloud = null;   
    private boolean rightHeldLastFrame = false;
    private boolean middleHeldLastFrame = false;

    // Full 4-Directional Animation Arrays
    private GreenfootImage[] downFrames;
    private GreenfootImage[] upFrames;
    private GreenfootImage[] rightFrames;
    private GreenfootImage[] leftFrames;
    
    private int animFrame = 0;
    private int animTimer = 0;
    private final int ANIM_SPEED = 8;

    public Maki()
    {
        downFrames  = new GreenfootImage[4];
        upFrames    = new GreenfootImage[4];
        rightFrames = new GreenfootImage[4];
        leftFrames  = new GreenfootImage[4];

        // Explicit asset assignments using your exact file naming conventions
        String[] downFiles  = {"maki.png", "maki2.png", "maki3.png", "maki4.png"};
        String[] upFiles    = {"maki-up.png", "maki-up2.png", "maki-up3.png", "maki-up4.png"};
        String[] rightFiles = {"maki-right.png", "maki-right2.png", "maki-right3.png", "maki-right4.png"};
        String[] leftFiles  = {"maki-left.png", "maki-left2.png", "maki-left3.png", "maki-left4.png"};

        for (int i = 0; i < 4; i++)
        {
            // 1. Down/Idle Facing
            downFrames[i] = new GreenfootImage(downFiles[i]);
            downFrames[i].scale(50, 50);

            // 2. Up Facing
            upFrames[i] = new GreenfootImage(upFiles[i]);
            upFrames[i].scale(50, 50);

            // 3. Right Facing
            rightFrames[i] = new GreenfootImage(rightFiles[i]);
            rightFrames[i].scale(50, 50);

            // 4. Left Facing (Loading your custom left sprites directly)
            leftFrames[i] = new GreenfootImage(leftFiles[i]);
            leftFrames[i].scale(50, 50);
        }

        // Default layout posture
        setImage(downFrames[0]);
    }

    public void setHpBar(HpBar bar) { this.healthBar = bar; }
    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }

    public void act()
    {
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

        // Dash handling
        if (dashDuration > 0)
        {
            dashDuration--;
            invincibilityTimer = 2;
            int cur = getRotation();
            setRotation(moveAngle);
            move(18); 
            setRotation(cur);
            checkEnemyContact();
            return;
        }

        // Directional state resolution 
        boolean keyIsPressed = false;
        int dx1 = 0, dy1 = 0;
        GreenfootImage[] currentFrames = downFrames; // Default baseline state

        if (Greenfoot.isKeyDown("a")) { setLocation(getX() - 6, getY()); currentFrames = leftFrames;  keyIsPressed = true; dx1 = -1; }
        else if (Greenfoot.isKeyDown("d")) { setLocation(getX() + 6, getY()); currentFrames = rightFrames; keyIsPressed = true; dx1 =  1; }
        
        if (Greenfoot.isKeyDown("w")) { setLocation(getX(), getY() - 6); currentFrames = upFrames;    keyIsPressed = true; dy1 = -1; }
        else if (Greenfoot.isKeyDown("s")) { setLocation(getX(), getY() + 6); currentFrames = downFrames;  keyIsPressed = true; dy1 =  1; }

        // Dynamic State Processing
        animTimer++;
        if (animTimer >= ANIM_SPEED)
        {
            animTimer = 0;
            animFrame = keyIsPressed ? (animFrame + 1) % 4 : 0;
        }
        setImage(currentFrames[animFrame]);

        // Dash processing activation
        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx1 != 0 || dy1 != 0))
        {
            dashDuration = 16;
            dashCooldown = 180;
            moveAngle = (int) Math.toDegrees(Math.atan2(dy1, dx1));
            if (dashIcon != null) dashIcon.updateIcon(3);
        }

        // Combat Engine Input Maps
        if (mouse != null)
        {
            boolean leftClick   = Greenfoot.mousePressed(null) && mouse.getButton() == 1;
            boolean middleClick = mouse.getButton() == 2 && Greenfoot.mousePressed(null);
            boolean rightClick  = mouse.getButton() == 3 && Greenfoot.mousePressed(null);

            if (leftClick && laserCooldown == 0)
            {
                if (orbitCloud != null && orbitCloud.getWorld() != null)
                    getWorld().removeObject(orbitCloud);
                orbitCloud = new MakiCloud("ORBIT", getX(), getY(), 0);
                getWorld().addObject(orbitCloud, getX(), getY());
                laserCooldown = 30;
            }

            if (middleClick && laserCooldown == 0)
            {
                turnTowards(mouse.getX(), mouse.getY());
                int angle = getRotation();
                setRotation(0);
                MakiCloud boomerang = new MakiCloud("BOOMERANG", getX(), getY(), angle);
                getWorld().addObject(boomerang, getX(), getY());
                laserCooldown = 40;
            }

            if (rightClick && laserCooldown == 0)
            {
                turnTowards(mouse.getX(), mouse.getY());
                int angle = getRotation();
                setRotation(0);
                
                MakiSwing VisualSwing = new MakiSwing(this, angle);
                getWorld().addObject(VisualSwing, getX(), getY());
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
        
        if (hp <= 0) 
        {
            // Check if we are currently fighting in BeachWorld
            boolean isBeach = (getWorld() instanceof BeachWorld);
            Greenfoot.setWorld(new GameOver(isBeach));
        }
    }
}

    public void heal(int amount)
    {
        hp = Math.min(10, hp + amount);
        if (healthBar != null) healthBar.updateBar(hp);
    }

}