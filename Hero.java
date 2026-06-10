import greenfoot.*;  

public class Hero extends Actor
{
    private GreenfootImage[] idleFrames;
    private GreenfootImage[] upFrames;
    private GreenfootImage[] leftFrames;
    private GreenfootImage[] rightFrames;
    private int animFrame = 0;
    private int animTimer = 0;
    private final int ANIM_SPEED = 8; // lower or faster animation
        
    private int laserCooldown = 0;
    private int hp = 10;
    private int invincibilityTimer = 0;
    //how long invinciviiltiy lasts for
    private final int INVINCIBILITY_DURATION = 30; 
    private HpBar healthBar;
    GreenfootSound trident = new GreenfootSound("trident.mp3");
    private Trident activeTrident = null;
    private boolean hasTrident = false;
    private int dashCooldown = 0;      
    private int dashDuration = 0;    
    private int moveAngle = 0; 
    private DashIcon dashIcon;
    private int stunTimer = 0; 
    
    //stun
    public void getStunned(int frames)
    {
        this.stunTimer = frames;
        getImage().setColor(new Color(0, 150, 255)); 
    }

    // constructor
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
    
    public void setHpBar(HpBar bar) { this.healthBar = bar; }
    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }
    
    public void act()
    {
        // stun timer
        if (stunTimer > 0)
        {
            stunTimer--;
            if (stunTimer == 0) {
                setImage(idleFrames[0]); 
                getImage().setColor(new Color(255, 255, 255, 255)); // Reset color
            }
            return; 
        }
        
        // creates a flickering effect for invincibility 
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
        
        // Laser Cooldown 
        if (laserCooldown > 0) {
            laserCooldown--; 
        }
        
        // Dash Cooldown 
        if (dashCooldown > 0)
        {
            dashCooldown--;
            if (dashCooldown % 60 == 0 && dashIcon != null)
            {
                dashIcon.updateIcon((dashCooldown / 60) + (dashCooldown % 60 > 0 ? 1 : 0));
            }
        }

        // Dash Movement 
        if (dashDuration > 0)
        {
            dashDuration--;
            invincibilityTimer = 2; 
            
            int currentRotation = getRotation();
            setRotation(moveAngle); 
            move(15);               
            setRotation(currentRotation); 
            
            checkEnemyContact();
            return; 
        }
        
        MouseInfo mouse = Greenfoot.getMouseInfo();
        
        boolean keyIsPressed = false;
        int dx1 = 0;
        int dy1 = 0;
        GreenfootImage[] currentFrames = idleFrames;

        //movement
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

        animTimer++;
        if (animTimer >= ANIM_SPEED)
        {
            animTimer = 0;
            if (keyIsPressed) animFrame = (animFrame + 1) % 4;
            else animFrame = 0;
        }

        setImage(currentFrames[animFrame]);
        
        // dash
        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx1 != 0 || dy1 != 0))
        {
            dashDuration = 10;
            dashCooldown = 180;
            moveAngle = (int) Math.toDegrees(Math.atan2(dy1, dx1)); 
            if (dashIcon != null) dashIcon.updateIcon(3); 
        }
        
        // Trident Retrieval
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

        // Launch Trident
        if (Greenfoot.isKeyDown("e") && hasTrident && activeTrident != null && !activeTrident.isFlying() && mouse != null)
        {
            turnTowards(mouse.getX(), mouse.getY());
            int angle = getRotation();
            setRotation(0);
            activeTrident.launch(angle);
            trident.play();
            hasTrident = false;
        }

        // m1 lazer atk
        if (Greenfoot.mousePressed(null) && laserCooldown == 0 && mouse != null)
        {
            //goes wherever ur mouse is
            turnTowards(mouse.getX(), mouse.getY());
            int angleToMouse = getRotation();
            setRotation(0); 
            
            //sound
            Greenfoot.playSound("bubble.mp3");
            
            //lazer
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            laser.setRotation(angleToMouse);
            laserCooldown = 20;
        }
        
        checkEnemyContact();
    }
    
    private void checkEnemyContact()
    {
        if (isTouching(Fish.class) && invincibilityTimer == 0)
        {
            takeDamage(1); 
        }
    }
    
    public void takeDamage(int damageAmount)
    {
        if (invincibilityTimer == 0)
        {
            hp -= damageAmount;
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
    
    public void pickUpTrident(Trident t)
    {
        hasTrident = true;
        activeTrident = t;
    }
}