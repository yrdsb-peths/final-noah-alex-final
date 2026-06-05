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
    private final int INVINCIBILITY_DURATION = 30; // <-- FIXED: Added missing constant for safety i-frames!

    // Dash Variables (Perfectly matched to Hero's logic)
    private int dashCooldown = 0;      // Ticks down from 180 (3 seconds)
    private int dashDuration = 0;      // Ticks down from 10 frames during active burst
    private int moveAngle = 0;         // Stores movement vector angle

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
        getImage().setColor(new Color(0, 150, 255));
    }

    public Nanami()
    {
        idleFrames = new GreenfootImage[4];
        upFrames = new GreenfootImage[4];
        leftFrames = new GreenfootImage[4];
        rightFrames = new GreenfootImage[4];

        for (int i = 0; i < 4; i++)
        {
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

    public int getInvincibilityTimer()
    {
        return this.invincibilityTimer;
    }

    public void setHpBar(HpBar bar) { this.healthBar = bar; }
    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }

    public void act()
    {
        if (stunTimer > 0)
        {
            stunTimer--;
            if (stunTimer == 0) getImage().setColor(new Color(255, 255, 255, 255));
            return; // Blocks the 7:3 ratio bar and blunt strikes
        }
        // 1. Invincibility Countdown
        if (invincibilityTimer > 0) 
        {
            invincibilityTimer--;
        }

        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (attackCooldown > 0) attackCooldown--;

        // 2. Dash Cooldown Clock Updater (Hero Logic)
        if (dashCooldown > 0)
        {
            dashCooldown--;
            // Every 60 frames (1 second), update the visual number on the icon
            if (dashCooldown % 60 == 0 && dashIcon != null)
            {
                dashIcon.updateIcon((dashCooldown / 60) + (dashCooldown % 60 > 0 ? 1 : 0));
            }
        }

        // 3. Dash Movement Execution Overrides Regular Controls (Hero Logic)
        if (dashDuration > 0)
        {
            dashDuration--;
            invincibilityTimer = 2; // Lock invulnerability completely while zooming
            
            int currentRotation = getRotation();
            setRotation(moveAngle); 
            move(15);               
            setRotation(currentRotation); // Restore original look direction
            return; // Skip standard inputs while dashing
        }

        // Auto-cancel if the player waits too long and the indicator expires
        if (ratioActive && ratioBar != null && ratioBar.isExpired())
        {
            getWorld().removeObject(ratioBar);
            ratioBar = null;
            ratioActive = false;
            
            BeachWorld currentWorld = null;
            if (getWorld() instanceof BeachWorld) {
                currentWorld = (BeachWorld) getWorld();
            }
            if (currentWorld != null) {
                currentWorld.setTimeFreeze(false);
            }
        }

        // PRIME TECHNIQUE: Pressing E spawns the RatioBar
        if (Greenfoot.isKeyDown("e") && !ratioActive && attackCooldown == 0)
        {
            ratioActive = true;
            ratioBar = new RatioBar();
            getWorld().addObject(ratioBar, getX(), getY() - 50);
            
            BeachWorld currentWorld = null;
            if (getWorld() instanceof BeachWorld) {
                currentWorld = (BeachWorld) getWorld();
            }
            if (currentWorld != null) {
                currentWorld.setTimeFreeze(true);
            }
        }

        if (ratioActive && ratioBar != null && ratioBar.getWorld() != null)
        {
            ratioBar.setLocation(getX(), getY() - 50);
        }

        // 4. KEYBOARD MOVEMENT & DASH HANDLING (Now unrestricted by ratioActive!)
        int dx1 = 0;
        int dy1 = 0;
        boolean keyIsPressed = false;
        GreenfootImage[] currentFrames = idleFrames;

        if (Greenfoot.isKeyDown("a")) { setLocation(getX() - 4, getY()); currentFrames = leftFrames;  keyIsPressed = true; dx1 = -1; }
        if (Greenfoot.isKeyDown("d")) { setLocation(getX() + 4, getY()); currentFrames = rightFrames; keyIsPressed = true; dx1 = 1;  }
        if (Greenfoot.isKeyDown("w")) { setLocation(getX(), getY() - 4); currentFrames = upFrames;    keyIsPressed = true; dy1 = -1; }
        if (Greenfoot.isKeyDown("s")) { setLocation(getX(), getY() + 4); currentFrames = idleFrames;  keyIsPressed = true; dy1 = 1;  }

        // Check for Dash Activation Input (Hero Logic)
        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx1 != 0 || dy1 != 0))
        {
            dashDuration = 10;    // Active movement frame windows
            dashCooldown = 180;   // 3 seconds total reset clock
            
            // Convert current WASD directions to an operational angle
            moveAngle = (int) Math.toDegrees(Math.atan2(dy1, dx1)); 
            
            if (dashIcon != null) dashIcon.updateIcon(3); // Start UI clock text immediately at 3
        }

        // Handle sprite cycling
        animTimer++;
        if (animTimer >= ANIM_SPEED)
        {
            animTimer = 0;
            if (keyIsPressed) animFrame = (animFrame + 1) % 4;
            else animFrame = 0;
        }
        
        // Update his graphic dynamically depending on movement state
        if (keyIsPressed) {
            setImage(currentFrames[animFrame]);
        } else {
            setImage(idleFrames[0]);
        }

        // 5. MOUSE CLICK: Attack directionally or strike the timing bar
        if (Greenfoot.mousePressed(null) && attackCooldown == 0 && mouse != null)
        {
            double angleRad = Math.atan2(mouse.getY() - getY(), mouse.getX() - getX());
            int angleDeg = (int) Math.toDegrees(angleRad);
            
            if (ratioActive && ratioBar != null)
            {
                boolean hitSweetSpot = ratioBar.isInRedZone();
                
                if (hitSweetSpot) {
                    executeBluntStrike(angleDeg, angleRad, true);
                    attackCooldown = 40;
                } else {
                    executeBluntStrike(angleDeg, angleRad, false);
                    attackCooldown = 20;
                }
                
                getWorld().removeObject(ratioBar);
                ratioBar = null;
                ratioActive = false;
                
                BeachWorld currentWorld = null;
                if (getWorld() instanceof BeachWorld) {
                    currentWorld = (BeachWorld) getWorld();
                }
                if (currentWorld != null) {
                    currentWorld.setTimeFreeze(false);
                }
            }
            else
            {
                executeBluntStrike(angleDeg, angleRad, false);
                attackCooldown = 15;
            }
        }
    }

    private void executeBluntStrike(int angleDeg, double angleRad, boolean isCritical)
    {
        if (getWorld() == null) return;
        
        // Spawns right on Nanami's body center point because the swing class handles its own locked tracking now!
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
                // Check if we are currently fighting in BeachWorld
                boolean isBeach = (getWorld() instanceof BeachWorld);
                Greenfoot.setWorld(new GameOver(isBeach));
            }
        }
    }
}