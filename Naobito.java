import greenfoot.*;
import java.util.List;

public class Naobito extends Actor
{
    private int hp = 10;
    private int laserCooldown = 0;
    private int invincibilityTimer = 0;
    private final int INVINCIBILITY_DURATION = 30;
    private HpBar healthBar;

    private int dashCooldown = 0;
    private int dashDuration = 0;
    private int moveAngle = 0;
    private DashIcon dashIcon;

    // Projection Sorcery states
    private final int PS_NONE       = 0;
    private final int PS_FROZEN     = 1; // time freeze, tracing path
    private final int PS_TRACING    = 2; // actively drawing path points
    private final int PS_CONFIRM    = 3; // waiting for directional key press to execute
    private final int PS_EXECUTING  = 4; // zooming through traced path
    private final int PS_LOCKED     = 5; // punishment for wrong key press
    private int psState = PS_NONE;

    private int psTimer = 0;
    private final int FREEZE_DURATION = 180; // 3 seconds to trace
    private final int LOCKED_DURATION = 120; // 2 seconds frozen as punishment

    // Path tracing
    private java.util.ArrayList<int[]> tracedPath = new java.util.ArrayList<>();
    private int pathIndex = 0;
    private int firstMoveDir = -1; // 0=up 1=down 2=left 3=right (direction of first movement)

    // Frozen enemy (aim-lock teleport)
    private Actor frozenEnemy = null;
    private int frozenTimer = 0;

    // World grey overlay
    private GreenfootImage normalBg = null;
    private boolean greyedOut = false;

    // Sprites
    private GreenfootImage[] idleFrames, upFrames, leftFrames, rightFrames;
    private int animFrame = 0, animTimer = 0;
    private final int ANIM_SPEED = 8;
    
    private int stunTimer = 0; 
        public void getStunned(int frames)
    {
        this.stunTimer = frames;
        //make the hero turn blue/gray when stunned
        getImage().setColor(new Color(0, 150, 255)); 
    }
    
    public Naobito()
    {
        idleFrames  = loadFrames("baseguy",       4);
        upFrames    = loadFrames("baseguy-up",    4);
        leftFrames  = loadFrames("baseguy-left",  4);
        rightFrames = loadFrames("baseguy-right", 4);
        setImage(idleFrames[0]);
    }

    private GreenfootImage[] loadFrames(String base, int count)
    {
        GreenfootImage[] frames = new GreenfootImage[count];
        for (int i = 0; i < count; i++)
        {
            String suffix = (i == 0) ? "" : Integer.toString(i + 1);
            frames[i] = new GreenfootImage(base + suffix + ".png");
            frames[i].scale(50, 50);
        }
        return frames;
    }

    public void setHpBar(HpBar bar) { this.healthBar = bar; }
    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }

    public void act()
    {
        if (stunTimer > 0)
        {
            stunTimer--;
            
            // If stun just ended, restore normal look appearance
            if (stunTimer == 0) {
                //setImage(idleFrames); 
            }
            
            // CRITICAL: Stop everything else! Skips movement, shooting, and WASD keys completely
            return; 
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
        if (dashCooldown > 0) dashCooldown--;

        // --- PROJECTION SORCERY HANDLING ---
        handleProjectionSorcery(mouse);

        // Block normal movement during sorcery states
        if (psState == PS_FROZEN || psState == PS_TRACING ||
            psState == PS_CONFIRM || psState == PS_EXECUTING || psState == PS_LOCKED)
        {
            if (psState == PS_LOCKED)
            {
                // Flash red to show punishment
                getImage().setTransparency(psTimer % 6 < 3 ? 60 : 255);
            }
            return;
        }

        // Normal movement
        boolean keyIsPressed = false;
        int dx1 = 0, dy1 = 0;
        GreenfootImage[] currentFrames = idleFrames;

        if (Greenfoot.isKeyDown("a")) { setLocation(getX() - 4, getY()); currentFrames = leftFrames;  keyIsPressed = true; dx1 = -1; }
        if (Greenfoot.isKeyDown("d")) { setLocation(getX() + 4, getY()); currentFrames = rightFrames; keyIsPressed = true; dx1 =  1; }
        if (Greenfoot.isKeyDown("w")) { setLocation(getX(), getY() - 4); currentFrames = upFrames;    keyIsPressed = true; dy1 = -1; }
        if (Greenfoot.isKeyDown("s")) { setLocation(getX(), getY() + 4); currentFrames = idleFrames;  keyIsPressed = true; dy1 =  1; }

        animTimer++;
        if (animTimer >= ANIM_SPEED) { animTimer = 0; animFrame = keyIsPressed ? (animFrame + 1) % 4 : 0; }
        setImage(currentFrames[animFrame]);

        // Dash
        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx1 != 0 || dy1 != 0))
        {
            dashDuration = 10; dashCooldown = 180;
            moveAngle = (int) Math.toDegrees(Math.atan2(dy1, dx1));
            if (dashIcon != null) dashIcon.updateIcon(3);
        }

        // Normal laser
        if (Greenfoot.mousePressed(null) && laserCooldown == 0 && mouse != null)
        {
            turnTowards(mouse.getX(), mouse.getY());
            int angle = getRotation();
            setRotation(0);
            Lazer laser = new Lazer();
            getWorld().addObject(laser, getX(), getY());
            laser.setRotation(angle);
            laserCooldown = 20;
        }

        checkEnemyContact();
    }

    private void handleProjectionSorcery(MouseInfo mouse)
    {
        // Q pressed - activate sorcery
        if (Greenfoot.isKeyDown("q") && psState == PS_NONE && laserCooldown == 0)
        {
            // Check if any enemy is in a straight line of current aim
            Actor target = getEnemyInAimLine(mouse);

            if (target != null)
            {
                // Freeze and teleport to enemy
                frozenEnemy = target;
                frozenTimer = 180; // enemy stays frozen for 3 seconds
                setLocation(target.getX(), target.getY() - 40);
                psState = PS_NONE; // not a full freeze, just teleport
                laserCooldown = 30;

                // Make enemy visually frozen
                if (target.getImage() != null)
                    target.getImage().setTransparency(150);
            }
            else
            {
                // No enemy in line — enter time freeze + path trace
                psState = PS_FROZEN;
                psTimer = FREEZE_DURATION;
                tracedPath.clear();
                firstMoveDir = -1;
                tracedPath.add(new int[]{getX(), getY()});
                applyGreyOut(true);
            }
        }

        // Handle frozen enemy
        if (frozenEnemy != null)
        {
            frozenTimer--;
            if (frozenTimer <= 0 || frozenEnemy.getWorld() == null)
            {
                // Unfreeze
                if (frozenEnemy.getWorld() != null)
                    frozenEnemy.getImage().setTransparency(255);
                frozenEnemy = null;
            }
            else if (Greenfoot.isKeyDown("space"))
            {
                // Throw frozen enemy to nearest wall — kills it
                killFrozenEnemy();
            }
        }

        // PATH TRACING STATE
        if (psState == PS_FROZEN || psState == PS_TRACING)
        {
            psTimer--;

            // Record path as player moves (WASD during freeze)
            int tx = getX(), ty = getY();
            boolean moved = false;
            int dir = -1;

            if (Greenfoot.isKeyDown("a")) { tx -= 4; dir = 2; moved = true; }
            if (Greenfoot.isKeyDown("d")) { tx += 4; dir = 3; moved = true; }
            if (Greenfoot.isKeyDown("w")) { ty -= 4; dir = 0; moved = true; }
            if (Greenfoot.isKeyDown("s")) { ty += 4; dir = 1; moved = true; }

            if (moved)
            {
                psState = PS_TRACING;
                if (firstMoveDir == -1) firstMoveDir = dir;
                setLocation(tx, ty); // Ghost-move during trace
                // Record waypoint every 10 frames of movement
                if (psTimer % 5 == 0)
                    tracedPath.add(new int[]{tx, ty});
            }

            if (psTimer <= 0)
            {
                // Time up — now wait for direction confirmation
                psState = PS_CONFIRM;
                psTimer = 90; // 1.5 seconds to press the right key
                applyGreyOut(false);
                // Reset to start of traced path
                if (tracedPath.size() > 0)
                    setLocation(tracedPath.get(0)[0], tracedPath.get(0)[1]);
            }
        }

        // CONFIRM STATE — player must press correct directional key
        if (psState == PS_CONFIRM)
        {
            psTimer--;

            int pressedDir = -1;
            if (Greenfoot.isKeyDown("w")) pressedDir = 0;
            if (Greenfoot.isKeyDown("s")) pressedDir = 1;
            if (Greenfoot.isKeyDown("a")) pressedDir = 2;
            if (Greenfoot.isKeyDown("d")) pressedDir = 3;

            if (pressedDir != -1)
            {
                if (pressedDir == firstMoveDir)
                {
                    // Correct! Execute the path
                    psState = PS_EXECUTING;
                    pathIndex = 0;
                    psTimer = tracedPath.size() * 2;
                }
                else
                {
                    // Wrong direction — get frozen as punishment
                    psState = PS_LOCKED;
                    psTimer = LOCKED_DURATION;
                }
            }

            if (psTimer <= 0)
            {
                // Ran out of time — punished
                psState = PS_LOCKED;
                psTimer = LOCKED_DURATION;
            }
        }

        // EXECUTE STATE — zoom through recorded path
        if (psState == PS_EXECUTING)
        {
            if (pathIndex < tracedPath.size())
            {
                int[] point = tracedPath.get(pathIndex);
                setLocation(point[0], point[1]);
                pathIndex++;
                invincibilityTimer = 5; // invincible while dashing path
            }
            else
            {
                psState = PS_NONE;
                laserCooldown = 60;
            }
        }

        // LOCKED punishment state
        if (psState == PS_LOCKED)
        {
            psTimer--;
            if (psTimer <= 0) psState = PS_NONE;
        }
    }

    private Actor getEnemyInAimLine(MouseInfo mouse)
    {
        if (mouse == null) return null;

        // Check all enemies to see if any are within a narrow band along the aim direction
        double aimRad = Math.atan2(mouse.getY() - getY(), mouse.getX() - getX());
        List<Fish> fish = getWorld().getObjects(Fish.class);
        for (Fish f : fish)
        {
            double angleToEnemy = Math.atan2(f.getY() - getY(), f.getX() - getX());
            double diff = Math.abs(aimRad - angleToEnemy);
            if (diff < 0.15) return f; // within ~8 degrees
        }
        List<Pufferfish> puffers = getWorld().getObjects(Pufferfish.class);
        for (Pufferfish p : puffers)
        {
            double angleToEnemy = Math.atan2(p.getY() - getY(), p.getX() - getX());
            double diff = Math.abs(aimRad - angleToEnemy);
            if (diff < 0.15) return p;
        }
        List<Crab> crabs = getWorld().getObjects(Crab.class);
for (Crab c : crabs)
{
    double angleToEnemy = Math.atan2(c.getY() - getY(), c.getX() - getX());
    double diff = Math.abs(aimRad - angleToEnemy);
    if (diff < 0.15) return c;
}
        return null;
    }

    private void killFrozenEnemy()
    {
        if (frozenEnemy == null || frozenEnemy.getWorld() == null) return;
        // Throw to nearest wall edge and destroy
        if (frozenEnemy instanceof Fish) ((Fish) frozenEnemy).takeDamage(99);
        else if (frozenEnemy instanceof Pufferfish) ((Pufferfish) frozenEnemy).takeDamage(99);
        
        else if (frozenEnemy instanceof Crab) {
    ((Crab) frozenEnemy).takeDamage(99);
}
        frozenEnemy = null;
    }

    private void applyGreyOut(boolean grey)
    {
        greyedOut = grey;
        // Tint the world background grey during freeze
        World w = getWorld();
        if (w == null) return;
        if (grey)
        {
            GreenfootImage overlay = new GreenfootImage(w.getWidth(), w.getHeight());
            overlay.setColor(new Color(100, 100, 120, 160));
            overlay.fillRect(0, 0, w.getWidth(), w.getHeight());
            // Draw over background
            GreenfootImage bg = w.getBackground();
            if (bg != null) bg.drawImage(overlay, 0, 0);
        }
        else
        {
            // Restore background
            GreenfootImage bg = new GreenfootImage("beach.jpg");
            bg.scale(w.getWidth(), w.getHeight());
            w.setBackground(bg);
        }
    }

    private void checkEnemyContact()
    {
        if (isTouching(Fish.class) && invincibilityTimer == 0) takeDamage(1);
    }

    public void takeDamage(int amount)
    {
        if (invincibilityTimer == 0)
        {
            hp -= amount;
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
}