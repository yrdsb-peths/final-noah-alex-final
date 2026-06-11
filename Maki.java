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

    // Full 4-Directional Animation Arrays
    private GreenfootImage[] downFrames;
    private GreenfootImage[] upFrames;
    private GreenfootImage[] rightFrames;
    private GreenfootImage[] leftFrames;
    
    private int animFrame = 0;
    private int animTimer = 0;
    private final int ANIM_SPEED = 8;
    
    // Stun Mechanics
    private int stunTimer = 0;

    GreenfootSound swing = new GreenfootSound("makiswing.mp3");
    GreenfootSound arc = new GreenfootSound("makiarc.mp3");
    GreenfootSound strike = new GreenfootSound("makistrike.mp3");
    
    public Maki()
    {
        downFrames  = new GreenfootImage[4];
        upFrames    = new GreenfootImage[4];
        rightFrames = new GreenfootImage[4];
        leftFrames  = new GreenfootImage[4];

        String[] downFiles  = {"maki.png", "maki2.png", "maki3.png", "maki4.png"};
        String[] upFiles    = {"maki-up.png", "maki-up2.png", "maki-up3.png", "maki-up4.png"};
        String[] rightFiles = {"maki-right.png", "maki-right2.png", "maki-right3.png", "maki-right4.png"};
        String[] leftFiles  = {"maki-left.png", "maki-left2.png", "maki-left3.png", "maki-left4.png"};

        for (int i = 0; i < 4; i++)
        {
            downFrames[i] = new GreenfootImage(downFiles[i]);
            downFrames[i].scale(50, 50);

            upFrames[i] = new GreenfootImage(upFiles[i]);
            upFrames[i].scale(50, 50);

            rightFrames[i] = new GreenfootImage(rightFiles[i]);
            rightFrames[i].scale(50, 50);

            leftFrames[i] = new GreenfootImage(leftFiles[i]);
            leftFrames[i].scale(50, 50);
        }

        setImage(downFrames[0]);
    }

    public void getStunned(int frames)
    {
        this.stunTimer = frames;
        if (getImage() != null) {
            getImage().setColor(new Color(0, 150, 255)); // Blue tint for stun status
        }
    }

    public void setHpBar(HpBar bar) { this.healthBar = bar; }
    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }
    public int getInvincibilityTimer() { return invincibilityTimer; }

    public void act()
    {
        // Handle stun countdown and block inputs while stunned
        if (stunTimer > 0)
        {
            stunTimer--;
            if (stunTimer == 0 && getImage() != null) {
                getImage().setColor(new Color(255, 255, 255, 255)); // Clear blue tint
            }
            return; // Exit early so no movement or combat can happen
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
        GreenfootImage[] currentFrames = downFrames;

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
        // LEFT MOUSE CLICK: Orbit Cloud
        if (mouse != null && Greenfoot.mousePressed(null) && mouse.getButton() == 1 && laserCooldown == 0)
        {
            Greenfoot.playSound("makiarc.mp3");
            if (orbitCloud != null && orbitCloud.getWorld() != null)
                getWorld().removeObject(orbitCloud);
            orbitCloud = new MakiCloud("ORBIT", getX(), getY(), 0);
            getWorld().addObject(orbitCloud, getX(), getY());
            laserCooldown = 30;
        }

        // Maki Swing 
        if (Greenfoot.isKeyDown("q") && laserCooldown == 0)
        {
            Greenfoot.playSound("makiarc.mp3");
            int angle = 0;
            if (mouse != null) {
                turnTowards(mouse.getX(), mouse.getY());
                angle = getRotation();
                setRotation(0); 
            }
            
            int handDistance = 48; 
            double cornerRadians = Math.toRadians(angle - 50); 
            
            int spawnX = getX() + (int)(handDistance * Math.cos(cornerRadians));
            int spawnY = getY() + (int)(handDistance * Math.sin(cornerRadians));
            
            MakiSwing visualSwing = new MakiSwing(this, angle, true);
            getWorld().addObject(visualSwing, spawnX, spawnY);
            
            laserCooldown = 25;
        }

        // RIGHT CLICK: Boomerang Cloud
        if (mouse != null && Greenfoot.mousePressed(null) && mouse.getButton() == 3 && laserCooldown == 0)
        {
            Greenfoot.playSound("makistrike.mp3");
            turnTowards(mouse.getX(), mouse.getY());
            int angle = getRotation();
            setRotation(0);
            
            MakiCloud boomerang = new MakiCloud("BOOMERANG", getX(), getY(), angle);
            getWorld().addObject(boomerang, getX(), getY());
            laserCooldown = 40;
        }

        checkEnemyContact();
    }

    private void checkEnemyContact()
    {
        if (invincibilityTimer == 0 && (isTouching(Fish.class) || isTouching(Turtle.class)))
        {
            takeDamage(1);
        }
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