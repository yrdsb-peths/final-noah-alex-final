import greenfoot.*;

public class Hero extends Actor
{
    private int laserCooldown = 0;
    private int hp = 5;
    private int invincibilityTimer = 0;
    private HpBar healthBar;

    private GreenfootImage idleImage;
    private GreenfootImage upImage;
    private GreenfootImage leftImage;
    private GreenfootImage rightImage;

    // Platformer physics
    private int velocityY = 0;
    private boolean onGround = false;
    private static final int JUMP_STRENGTH = -15;

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
        setImage(idleImage);
    }

    public void setHpBar(HpBar bar) { this.healthBar = bar; }

    public void act()
    {
        applyGravity();
        handleMovement();
        handleShooting();
        handleInvincibility();
        checkEnemyContact();
    }

    private void applyGravity()
    {
        int floorY = MyWorld.FLOOR_Y;

        // Apply gravity
        velocityY += MyWorld.GRAVITY;
        int newY = getY() + velocityY;

        if (newY >= floorY)
        {
            newY = floorY;
            velocityY = 0;
            onGround = true;
        }
        else
        {
            onGround = false;
        }

        setLocation(getX(), newY);
    }

    private void handleMovement()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null)
        {
            turnTowards(mouse.getX(), mouse.getY());
            setRotation(getRotation() + 90);
        }

        int dx = 0;

        if (Greenfoot.isKeyDown("a"))
        {
            dx = -5;
            setImage(leftImage);
        }
        else if (Greenfoot.isKeyDown("d"))
        {
            dx = 5;
            setImage(rightImage);
        }
        else
        {
            setImage(idleImage);
        }

        // Jump
        if ((Greenfoot.isKeyDown("w") || Greenfoot.isKeyDown("space")) && onGround)
        {
            velocityY = JUMP_STRENGTH;
            onGround = false;
            setImage(upImage);
        }

        // Scroll world horizontally
        if (dx != 0)
        {
            MyWorld world = (MyWorld) getWorld();
            world.scrollWorld(dx);
        }
    }

    private void handleShooting()
    {
        if (laserCooldown > 0) laserCooldown--;

        if (Greenfoot.mousePressed(null) && laserCooldown == 0)
        {
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            laser.setRotation(getRotation() - 90);
            laserCooldown = 20;
        }
    }

    private void handleInvincibility()
    {
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
            if (getImage() != null) getImage().setTransparency(255);
        }
    }

    public void takeDamage(int amount)
    {
        if (invincibilityTimer == 0)
        {
            hp -= amount;
            invincibilityTimer = 30;
            if (healthBar != null) healthBar.updateBar(hp);
            if (hp <= 0) Greenfoot.setWorld(new GameOver());
        }
    }

    private void checkEnemyContact()
    {
        if (isTouching(Fish.class) && invincibilityTimer == 0) takeDamage(1);
        if (isTouching(Pufferfish.class) && invincibilityTimer == 0) takeDamage(2);
    }
}