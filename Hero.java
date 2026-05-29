import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Hero extends Actor
{
    private int laserCooldown = 0;
    private int hp = 10;
    private int invincibilityTimer = 0; 
    private HpBar healthBar;
    
    GreenfootSound bubble = new GreenfootSound("bubble.mp3");
    GreenfootSound trident = new GreenfootSound("trident.mp3");
    //trident
    private Trident activeTrident = null;
private boolean hasTrident = false;
    
    // --- Friend's Sprite Variables ---
    private GreenfootImage idleImage;
    private GreenfootImage upImage;
    private GreenfootImage leftImage;
    private GreenfootImage rightImage;
    
        private int dashCooldown = 0;      // Ticks down from 180 (3 seconds)
    private int dashDuration = 0;      // Ticks down from 10 frames during active burst
    private int moveAngle = 0;         // Stores movement vector angle
    private DashIcon dashIcon;

    // --- Constructor (Loads and scales images once at the start) ---
    public Hero() 
    {
        idleImage = new GreenfootImage("baseguy.png");
        idleImage.scale(50, 50);
        
        upImage = new GreenfootImage("baseguy-up.png");
        upImage.scale(50, 50);
        
        leftImage = new GreenfootImage("baseguy-left.png");
        leftImage.scale(50, 50);
        
        rightImage = new GreenfootImage("baseguy-right.png");
        rightImage.scale(50, 50);
        
        // Set the initial appearance
        setImage(idleImage);
    }
    
    public void setHpBar(HpBar bar)
    {
        this.healthBar = bar;
    }

    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }
    
    public void act()
    {
         if (laserCooldown > 0) laserCooldown--; 
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
        
        // --- NEW: COOLDOWN CLOCK CONVERTER ---
        if (dashCooldown > 0)
        {
            dashCooldown--;
            // Every 60 frames (1 second), update the visual number on the icon
            if (dashCooldown % 60 == 0 && dashIcon != null)
            {
                dashIcon.updateIcon((dashCooldown / 60) + (dashCooldown % 60 > 0 ? 1 : 0));
            }
        }

        // 2. Dash Movement Execution Overrides Regular Controls
        if (dashDuration > 0)
        {
            dashDuration--;
            invincibilityTimer = 2; // Lock invulnerability completely while zooming
            
            // Move fast in whatever direction you were moving when you hit R
            // 15 pixels per frame for 10 frames = 150 pixel total distance covered instantly
            int currentRotation = getRotation();
            setRotation(moveAngle); 
            move(15);               
            setRotation(currentRotation); // Restore original mouse targeting look direction
            
            checkEnemyContact();
            return; // Skip normal WASD input scripts while slicing ahead
        }
        
        // Mouse Tracking 
        MouseInfo mouse = Greenfoot.getMouseInfo();
        
        // Track if a key is held down to manage idle states
        boolean keyIsPressed = false;
        
        int dx1 = 0;
        int dy1 = 0;

        // Movement & Sprite Swapping 
        if (Greenfoot.isKeyDown("a"))
        {
            setLocation(getX() - 4, getY());
            setImage(leftImage);
            keyIsPressed = true;
            dx1 = -1;
        }
        if (Greenfoot.isKeyDown("d"))
        {
            setLocation(getX() + 4, getY());
            setImage(rightImage);
            keyIsPressed = true;
            dx1 = 1;
        }
        if (Greenfoot.isKeyDown("w"))
        {
            setLocation(getX(), getY() - 4);
            setImage(upImage);
            keyIsPressed = true;
            dy1 = -1;
        }
        if (Greenfoot.isKeyDown("s"))
        {
            setLocation(getX(), getY() + 4);
            setImage(idleImage); 
            keyIsPressed = true;
            dy1 = 1;
        }
        
        if (!keyIsPressed) 
        {
            setImage(idleImage);
        }
        
        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx1 != 0 || dy1 != 0))
        {
            dashDuration = 10;    // Active movement frame windows
            dashCooldown = 180;   // 3 seconds total reset clock
            
            // Convert current WASD directions to an operational angle
            moveAngle = (int) Math.toDegrees(Math.atan2(dy1, dx1)); 
            
            if (dashIcon != null) dashIcon.updateIcon(3); // Start UI clock text immediately at 3
        }
        
        // Toggle trident mode with E
// Toggle trident mode with E
// Show trident on hero's back when carrying it
// Pick up stuck trident by walking to it
// Pick up stuck trident by walking to it
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

// E to throw trident - only when not already flying
if (Greenfoot.isKeyDown("e") && hasTrident && activeTrident != null && !activeTrident.isFlying() && mouse != null)
{
    turnTowards(mouse.getX(), mouse.getY());
    int angle = getRotation();
    setRotation(0);
    activeTrident.launch(angle);
    trident.play();
    hasTrident = false;
}

// Normal laser with mouse click, always available
if (Greenfoot.mousePressed(null) && laserCooldown == 0 && mouse != null)
{
    turnTowards(mouse.getX(), mouse.getY());
    int angleToMouse = getRotation();
    setRotation(0);
    bubble.play();
    Lazer laser = new Lazer();
    getWorld().addObject(laser, getX(), getY());
    laser.setRotation(angleToMouse);
    laserCooldown = 0;
}
        
        // If no keys are pressed, return to base idle sprite
        if (!keyIsPressed) 
        {
            setImage(idleImage);
        }
        
        // 3. Laser Cooldown Ticker
        if (laserCooldown > 0) {
            laserCooldown--; 
        }
        
        // 4. Friend's Mouse Click Shooting (with matching laser rotation fix)
        if (Greenfoot.mousePressed(null) && laserCooldown == 0 && mouse != null)
        {
            turnTowards(mouse.getX(), mouse.getY());
            int angleToMouse = getRotation();
            setRotation(0); // or whatever default rotation you want
            
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            laser.setRotation(angleToMouse);
            laserCooldown = 00; 
        }
        
        // 5. Your Invincibility Frame Polish
        if (invincibilityTimer > 0)
        {
            invincibilityTimer--;
            if (invincibilityTimer % 4 == 0) {
                getImage().setTransparency(100); 
            } else {
                getImage().setTransparency(255); 
            }
        }
        else
        {
            if (getImage() != null) {
                getImage().setTransparency(255); 
            }
        }
        
        checkEnemyContact();
    }
    
       private void handleInvincibilityFrames()
    {
        invincibilityTimer--;
        if (invincibilityTimer % 4 == 0) getImage().setTransparency(100);
        else getImage().setTransparency(255);
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
            invincibilityTimer = 30; // 0.5 seconds of i-frames
            
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