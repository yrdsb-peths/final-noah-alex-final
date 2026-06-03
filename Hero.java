import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Hero extends Actor
{
    private GreenfootImage[] idleFrames;
    private GreenfootImage[] upFrames;
    private GreenfootImage[] leftFrames;
    private GreenfootImage[] rightFrames;
    private int animFrame = 0;
    private int animTimer = 0;
    private final int ANIM_SPEED = 8; // lower = faster animation
        
    private int laserCooldown = 0;
    private int hp = 10;
    private int invincibilityTimer = 0;
    private final int INVINCIBILITY_DURATION = 30; // About half a second of safety
    private HpBar healthBar;
    
    GreenfootSound bubble = new GreenfootSound("bubble.mp3");
    GreenfootSound trident = new GreenfootSound("trident.mp3");
    
    private Trident activeTrident = null;
    private boolean hasTrident = false;
    private GreenfootImage idleImage;
    private GreenfootImage upImage;
    private GreenfootImage leftImage;
    private GreenfootImage rightImage;
    
    private int dashCooldown = 0;      // Ticks down from 180 (3 seconds)
    private int dashDuration = 0;      // Ticks down from 10 frames during active burst
    private int moveAngle = 0;         // Stores movement vector angle
    private DashIcon dashIcon;

    private int stunTimer = 0; 
    
    //CRAB CAN FREEZE THE HERO
    public void getStunned(int frames)
    {
        this.stunTimer = frames;
        //make the hero turn blue/gray when stunned
        getImage().setColor(new Color(0, 150, 255)); 
    }
    // --- Constructor (Loads and scales images once at the start) ---
        public Hero() 
    {
        idleFrames = new GreenfootImage[4];
        upFrames = new GreenfootImage[4];
        leftFrames = new GreenfootImage[4];
        rightFrames = new GreenfootImage[4];
    
        for (int i = 0; i < 4; i++)
        {
            String suffix = (i == 0) ? "" : Integer.toString(i + 1);
            
            idleFrames[i] = new GreenfootImage("baseguy" + suffix + ".png");
            idleFrames[i].scale(50, 50);
            
            upFrames[i] = new GreenfootImage("baseguy-up" + suffix + ".png");
            upFrames[i].scale(50, 50);
            
            leftFrames[i] = new GreenfootImage("baseguy-left" + suffix + ".png");
            leftFrames[i].scale(50, 50);
            
            rightFrames[i] = new GreenfootImage("baseguy-right" + suffix + ".png");
            rightFrames[i].scale(50, 50);
        }
        
        setImage(idleFrames[0]);
    }
    
    public void setHpBar(HpBar bar)
    {
        this.healthBar = bar;
    }

    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }
    
    public void act()
    {
        // 1. Tick down the stun lock
        if (stunTimer > 0)
        {
            stunTimer--;
            
            // If stun just ended, restore normal look appearance
            if (stunTimer == 0) {
                setImage(idleImage); 
            }
            
            // CRITICAL: Stop everything else! Skips movement, shooting, and WASD keys completely
            return; 
        }
        
        // 1. Handle Invincibility Frame Visual Countdown
        if (invincibilityTimer > 0) 
        {
            invincibilityTimer--;
            if (invincibilityTimer % 4 == 0) getImage().setTransparency(100); 
            else getImage().setTransparency(255); 
        }
        else if (getImage() != null) 
        {
            getImage().setTransparency(255); 
        }
        
        // 2. Laser Cooldown Ticker
        if (laserCooldown > 0) {
            laserCooldown--; 
        }
        
        // 3. Dash Cooldown Clock Updater
        if (dashCooldown > 0)
        {
            dashCooldown--;
            // Every 60 frames (1 second), update the visual number on the icon
            if (dashCooldown % 60 == 0 && dashIcon != null)
            {
                dashIcon.updateIcon((dashCooldown / 60) + (dashCooldown % 60 > 0 ? 1 : 0));
            }
        }

        // 4. Dash Movement Execution Overrides Regular Controls
        if (dashDuration > 0)
        {
            dashDuration--;
            invincibilityTimer = 2; // Lock invulnerability completely while zooming
            
            // Move fast in whatever direction you were moving when you hit R
            int currentRotation = getRotation();
            setRotation(moveAngle); 
            move(15);               
            setRotation(currentRotation); // Restore original mouse targeting look direction
            
            checkEnemyContact();
            return; // Skip normal WASD input scripts while slicing ahead
        }
        
        // Mouse Tracking 
        MouseInfo mouse = Greenfoot.getMouseInfo();
        
        boolean keyIsPressed = false;
int dx1 = 0;
int dy1 = 0;
GreenfootImage[] currentFrames = idleFrames;

if (Greenfoot.isKeyDown("a"))
{
    setLocation(getX() - 4, getY());
    currentFrames = leftFrames;
    keyIsPressed = true;
    dx1 = -1;
}
if (Greenfoot.isKeyDown("d"))
{
    setLocation(getX() + 4, getY());
    currentFrames = rightFrames;
    keyIsPressed = true;
    dx1 = 1;
}
if (Greenfoot.isKeyDown("w"))
{
    setLocation(getX(), getY() - 4);
    currentFrames = upFrames;
    keyIsPressed = true;
    dy1 = -1;
}
if (Greenfoot.isKeyDown("s"))
{
    setLocation(getX(), getY() + 4);
    currentFrames = idleFrames;
    keyIsPressed = true;
    dy1 = 1;
}

// Advance animation frame on a timer
animTimer++;
if (animTimer >= ANIM_SPEED)
{
    animTimer = 0;
    if (keyIsPressed)
    {
        animFrame = (animFrame + 1) % 4; // cycle through frames while moving
    }
    else
    {
        animFrame = 0; // snap back to frame 1 when idle
    }
}

setImage(currentFrames[animFrame]);
        
        // 6. Check for Dash Activation Input
        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx1 != 0 || dy1 != 0))
        {
            dashDuration = 10;    // Active movement frame windows
            dashCooldown = 180;   // 3 seconds total reset clock
            
            // Convert current WASD directions to an operational angle
            moveAngle = (int) Math.toDegrees(Math.atan2(dy1, dx1)); 
            
            if (dashIcon != null) dashIcon.updateIcon(3); // Start UI clock text immediately at 3
        }
        
        // 7. Trident Retrieval Mechanics
        if (activeTrident != null && activeTrident.isStuck())
        {
            int dx = Math.abs(activeTrident.getX() - getX());
            int dy = Math.abs(activeTrident.getY() - getY());
            if (dx < 25 && dy < 25)
            {
                activeTrident.setCarried(true);
                hasTrident = true;
            }
        }

        // 8. Launch Trident Input Mechanics
        if (Greenfoot.isKeyDown("e") && hasTrident && activeTrident != null && !activeTrident.isFlying() && mouse != null)
        {
            turnTowards(mouse.getX(), mouse.getY());
            int angle = getRotation();
            setRotation(0);
            activeTrident.launch(angle);
            trident.play();
            hasTrident = false;
        }

        // 9. Primary Laser Attack Actions
        if (Greenfoot.mousePressed(null) && laserCooldown == 0 && mouse != null)
        {
            turnTowards(mouse.getX(), mouse.getY());
            int angleToMouse = getRotation();
            setRotation(0); 
            
            bubble.play();
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            laser.setRotation(angleToMouse);
            laserCooldown = 20;
        }
        
        // 10. Check Environmental Collisions
        checkEnemyContact();
    }
    
    private void checkEnemyContact()
    {
        if (isTouching(Fish.class) && invincibilityTimer == 0)
        {
            takeDamage(1); // Normal fish does 1 damage
        }
    }
    
    public void takeDamage(int damageAmount)
    {
        if (invincibilityTimer == 0)
        {
            hp -= damageAmount;
            invincibilityTimer = INVINCIBILITY_DURATION; // 0.5 seconds of safe i-frames
            
            if (healthBar != null)
            {
                healthBar.updateBar(hp);
            }
            
            if (hp <= 0)
            {
                Greenfoot.setWorld(new GameOver());
            }
        }
    }
    
    public void heal(int amount)
    {
        hp += amount;
        if (hp > 10) 
        {
            hp = 10; // Prevent healing past maximum health
        }
        
        // Update the visual health bar layout
        if (healthBar != null)
        {
            healthBar.updateBar(hp);
        }
    }
    
    public void pickUpTrident(Trident t)
    {
        hasTrident = true;
        activeTrident = t;
    }
}