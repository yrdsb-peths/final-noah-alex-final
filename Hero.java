import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Hero extends Actor
{
    private int laserCooldown = 0;
    
    // image
    private GreenfootImage idleImage;
    private GreenfootImage upImage;
    private GreenfootImage leftImage;
    private GreenfootImage rightImage;

    // COONSTRUCTORR
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
        
        // set idle
        setImage(idleImage);
    }

    public void act()
    {
        //mouse track
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null) 
        {
            turnTowards(mouse.getX(), mouse.getY());
            setRotation(getRotation() + 90);
        }
        
        //Track if any key is pressed to reset to idle later
        boolean keyIsPressed = false;

        //movement + directional sprite maps
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
            setImage(idleImage); // Uses regular baseguy for down
            keyIsPressed = true;
        }
        
        //default to baseguy
        if (!keyIsPressed) 
        {
            setImage(idleImage);
        }
        
        //Laser cooldown
        if (laserCooldown > 0) {
            laserCooldown--; 
        }
        
        //Shooting controls
        if (Greenfoot.mousePressed(null) && laserCooldown == 0)
        {
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            laser.setRotation(getRotation()-90); 
            laserCooldown = 20; 
        }
    }
}
