import greenfoot.*;

public class Nanami extends Actor
{
    private int attackCooldown = 0;
    
    // 7:3 Ratio Technique States
    private boolean ratioActive = false;
    private RatioBar ratioBar = null;
    
    // UI Setup Links
    private HpBar healthBar;
    private DashIcon dashIcon;
    private int hp = 10;
    private int invincibilityTimer = 0;
    private final int INVINCIBILITY_DURATION = 30;

    // Dash Variables
    private int dashCooldown = 0;      
    private int dashDuration = 0;      
    private int moveAngle = 0;         

    // Movement & Animation Variables
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
        if (getImage() != null) {
            getImage().setColor(new Color(0, 150, 255));
        }
    }

    public void setHpBar(HpBar bar) { this.healthBar = bar; }
    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }
    public int getInvincibilityTimer() { return invincibilityTimer; }

    public Nanami()
    {
        idleFrames  = new GreenfootImage[4];
        upFrames    = new GreenfootImage[4];
        leftFrames  = new GreenfootImage[4];
        rightFrames = new GreenfootImage[4];

        // Your exact preferred sprite filename layouts
        String[] downFiles  = {"nanami.png", "nanami2.png", "nanami3.png", "nanami4.png"};
        String[] upFiles    = {"nanami-up.png", "nanami-up2.png", "nanami-up3.png", "nanami-up4.png"};
        String[] leftFiles  = {"nanami-left.png", "nanami-left2.png", "nanami-left3.png", "nanami-left4.png"};
        String[] rightFiles = {"nanami-right.png", "nanami-right2.png", "nanami-right3.png", "nanami-right4.png"};

        for (int i = 0; i < 4; i++)
        {
            idleFrames[i]  = new GreenfootImage(downFiles[i]);  idleFrames[i].scale(50, 50);
            upFrames[i]    = new GreenfootImage(upFiles[i]);    upFrames[i].scale(50, 50);
            leftFrames[i]  = new GreenfootImage(leftFiles[i]);  leftFrames[i].scale(50, 50);
            rightFrames[i] = new GreenfootImage(rightFiles[i]); rightFrames[i].scale(50, 50);
        }

        setImage(idleFrames[0]);
    }

    public void act()
    {
        if (getWorld() == null) return;

        // Follow Nanami dynamically if the bar is active while moving
        if (ratioActive && ratioBar != null)
        {
            if (ratioBar.isExpired())
            {
                getWorld().removeObject(ratioBar);
                ratioBar = null;
                ratioActive = false;
            }
            else
            {
                ratioBar.setLocation(getX(), getY() - 45);
            }
        }

        // Handle stun mechanics
        if (stunTimer > 0)
        {
            stunTimer--;
            if (stunTimer == 0 && getImage() != null) {
                getImage().setColor(new Color(255, 255, 255, 255));
            }
            return;
        }

        // Run general counters
        if (invincibilityTimer > 0)
        {
            invincibilityTimer--;
            if (getImage() != null) getImage().setTransparency(invincibilityTimer % 4 == 0 ? 100 : 255);
        }
        else if (getImage() != null) getImage().setTransparency(255);

        if (attackCooldown > 0) attackCooldown--;
        if (dashCooldown > 0)
        {
            dashCooldown--;
            if (dashCooldown % 60 == 0 && dashIcon != null)
                dashIcon.updateIcon(dashCooldown / 60);
        }

        // Dash core execution
        if (dashDuration > 0)
        {
            dashDuration--;
            invincibilityTimer = 2;
            int prevRotation = getRotation();
            setRotation(moveAngle);
            move(16);
            setRotation(prevRotation);
            return;
        }

        // Process attacks and inputs
        handleCombatInputs();

        // FIXED: Removed the return block so Nanami can run around while handling 'e' triggers!
        boolean moving = false;
        int dx = 0, dy = 0;
        GreenfootImage[] currentFrames = idleFrames;

        if (Greenfoot.isKeyDown("a")) { setLocation(getX() - 5, getY()); currentFrames = leftFrames;  moving = true; dx = -1; }
        else if (Greenfoot.isKeyDown("d")) { setLocation(getX() + 5, getY()); currentFrames = rightFrames; moving = true; dx =  1; }
        
        if (Greenfoot.isKeyDown("w")) { setLocation(getX(), getY() - 5); currentFrames = upFrames;    moving = true; dy = -1; }
        else if (Greenfoot.isKeyDown("s")) { setLocation(getX(), getY() + 5); currentFrames = idleFrames;  moving = true; dy =  1; }

        animTimer++;
        if (animTimer >= ANIM_SPEED)
        {
            animTimer = 0;
            animFrame = moving ? (animFrame + 1) % 4 : 0;
        }
        setImage(currentFrames[animFrame]);

        // Dash trigger
        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx != 0 || dy != 0))
        {
            dashDuration = 10;
            dashCooldown = 180;
            moveAngle = (int) Math.toDegrees(Math.atan2(dy, dx));
            if (dashIcon != null) dashIcon.updateIcon(3);
        }
    }

    private void handleCombatInputs()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        
        // 1. Press 'e' to load the bar safely in real time without stopping
        if (Greenfoot.isKeyDown("e") && attackCooldown == 0 && !ratioActive)
        {
            ratioActive = true;
            ratioBar = new RatioBar();
            getWorld().addObject(ratioBar, getX(), getY() - 45); 
        }

        // 2. Left click strikes
        if (mouse != null && Greenfoot.mousePressed(null) && mouse.getButton() == 1 && attackCooldown == 0)
        {
            double dx = mouse.getX() - getX();
            double dy = mouse.getY() - getY();
            double angleRad = Math.atan2(dy, dx);
            int angleDeg = (int) Math.toDegrees(angleRad);

            if (ratioActive && ratioBar != null)
            {
                boolean isCritical = ratioBar.checkRatioTiming();
                executeBluntStrike(angleDeg, angleRad, isCritical);
                
                attackCooldown = 40; 
                getWorld().removeObject(ratioBar);
                ratioBar = null;
                ratioActive = false;
            }
            else if (!ratioActive)
            {
                executeBluntStrike(angleDeg, angleRad, false);
                attackCooldown = 15;
            }
        }
    }

    private void executeBluntStrike(int angleDeg, double angleRad, boolean isCritical)
    {
        if (getWorld() == null) return;
        SwingVisual slash = new SwingVisual(this, angleDeg, isCritical);
        getWorld().addObject(slash, getX(), getY());
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
}