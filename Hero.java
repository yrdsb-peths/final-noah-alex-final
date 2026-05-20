import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Hero extends Actor
{
    private int laserCooldown = 0;
    private int hp = 5;
    private int invincibilityTimer = 0; 
    private HpBar healthBar;
    
    // --- Friend's Sprite Variables ---
    private GreenfootImage idleImage;
    private GreenfootImage upImage;
    private GreenfootImage leftImage;
    private GreenfootImage rightImage;

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

    public void act()
    {
        // 1. Friend's Mouse Tracking (with rotation correction)
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null) 
        {
            turnTowards(mouse.getX(), mouse.getY());
            setRotation(getRotation() + 90);
        }
        
        // Track if a key is held down to manage idle states
        boolean keyIsPressed = false;

        // 2. Movement & Sprite Swapping (Combined)
        if (Greenfoot.isKeyDown("a"))
        {
            setLocation(getX() - 5, getY());
            setImage(leftImage);
            keyIsPressed = true;
        }
        if (Greenfoot.isKeyDown("d"))
        {
            setLocation(getX() + 5, getY());
            setImage(rightImage);
            keyIsPressed = true;
        }
        if (Greenfoot.isKeyDown("w"))
        {
            setLocation(getX(), getY() - 5);
            setImage(upImage);
            keyIsPressed = true;
        }
        if (Greenfoot.isKeyDown("s"))
        {
            setLocation(getX(), getY() + 5);
            setImage(idleImage); 
            keyIsPressed = true;
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
        if (Greenfoot.mousePressed(null) && laserCooldown == 0)
        {
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            laser.setRotation(getRotation() - 90); 
            laserCooldown = 20; 
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
    
    private void checkEnemyContact()
    {
        if (isTouching(Fish.class) && invincibilityTimer == 0)
        {
            hp--; 
            invincibilityTimer = 30;  
            
            if (healthBar != null)
            {
                healthBar.updateBar(hp);
            }
        }
    }
}