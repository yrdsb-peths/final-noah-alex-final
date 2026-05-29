import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Hero extends Actor
{
    private int laserCooldown = 0;
    private int hp = 10;
    private int invincibilityTimer = 0; 
    private HpBar healthBar;
    
    //trident
    private Trident activeTrident = null;
    private boolean hasTrident = false;
    
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
        
        // Toggle trident mode with E
        // Toggle trident mode with E
        // Show trident on hero's back when carrying it
        if (hasTrident)
        {
            // Offset slightly so it appears beside the hero
            if (activeTrident != null && activeTrident.isStuck())
            {
                // ignore, it's on the wall
            }
            else
            {
                // Keep a visual trident stuck to hero — handled by Trident class below
            }
        }
    
        // Pick up stuck trident by walking to it
        if (activeTrident != null && activeTrident.isStuck())
        {
            int dx = Math.abs(activeTrident.getX() - getX());
            int dy = Math.abs(activeTrident.getY() - getY());
            if (dx < 25 && dy < 25)
            {
                getWorld().removeObject(activeTrident);
                activeTrident = null;
                hasTrident = true;
            }
        }

        // E to throw trident
        // E to throw trident
        if (Greenfoot.isKeyDown("e") && hasTrident && mouse != null)
        {
            turnTowards(mouse.getX(), mouse.getY());
            int angle = getRotation();
            setRotation(0);
        
            // If there's a carried trident, launch it directly instead of making a new one
            if (activeTrident != null)
            {
                activeTrident.launch(angle);
            }
            else
            {
                activeTrident = new Trident();
                getWorld().addObject(activeTrident, getX(), getY());
                activeTrident.launch(angle);
            }
            
            hasTrident = false;
        }

        // Normal laser with mouse click, always available
        if (Greenfoot.mousePressed(null) && laserCooldown == 0 && mouse != null)
        {
            turnTowards(mouse.getX(), mouse.getY());
            int angleToMouse = getRotation();
            setRotation(0);
        
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            laser.setRotation(angleToMouse);
            laserCooldown = 20;
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
        
        // --- DOMAIN EXPANSION TRIGGER ---
        if (Greenfoot.isKeyDown("g"))
        {
            // Only activate if a Domain isn't already running
            if (getWorld().getObjects(DomainExpansion.class).isEmpty())
            {
                DomainExpansion domain = new DomainExpansion();
                // Spawn it out of view at (0,0); it manages the screen effects internally
                getWorld().addObject(domain, 0, 0); 
            }
        }
        
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