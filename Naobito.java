import greenfoot.*;
import java.util.List;

public class Naobito extends Actor
{
    private GreenfootImage[] idleFrames;
    private GreenfootImage[] upFrames;
    private GreenfootImage[] leftFrames;
    private GreenfootImage[] rightFrames;
    private int animFrame = 0;
    private int animTimer = 0;
    private final int ANIM_SPEED = 8; 

    private GlassPanel playerGlassTrap = null;
    
    private int hp = 10;
    private int punchCooldown = 0; // Replaced laserCooldown with an attack tracker
    private int invincibilityTimer = 0;
    private final int INVINCIBILITY_DURATION = 30;
    private HpBar healthBar;

    private int dashCooldown = 0;
    private int dashDuration = 0;
    private int moveAngle = 0;
    private DashIcon dashIcon;

    // Projection Sorcery states
    private final int PS_NONE       = 0;
    private final int PS_FROZEN     = 1; 
    private final int PS_TRACING    = 2; 
    private final int PS_CONFIRM    = 3; 
    private final int PS_EXECUTING  = 4; 
    private final int PS_LOCKED     = 5; 
    private int psState = PS_NONE;

    private int psTimer = 0;
    private final int FREEZE_DURATION = 180; 
    private final int LOCKED_DURATION = 120; 

    private java.util.ArrayList<int[]> tracedPath = new java.util.ArrayList<>();
    private int pathIndex = 0;
    private int firstMoveDir = -1; 

    private Actor frozenEnemy = null;
    private int frozenTimer = 0;
    private GlassPanel activeGlassPanel = null;
    private ProjectionCursor aimCursor = null;

    private boolean greyedOut = false;

    // --- Constructor ---
    public Naobito()
    {
        idleFrames = new GreenfootImage[4];
        upFrames = new GreenfootImage[4];
        leftFrames = new GreenfootImage[4];
        rightFrames = new GreenfootImage[4];

        for (int i = 0; i < 4; i++)
        {
            String suffix = (i == 0) ? "" : Integer.toString(i + 1);
            
            idleFrames[i] = new GreenfootImage("naobito" + suffix + ".png");
            idleFrames[i].scale(50, 60);
            
            upFrames[i] = new GreenfootImage("naobito-up" + suffix + ".png");
            upFrames[i].scale(50, 60);
            
            leftFrames[i] = new GreenfootImage("naobito-left" + suffix + ".png");
            leftFrames[i].scale(50, 60);
            
            rightFrames[i] = new GreenfootImage("naobito-right" + suffix + ".png");
            rightFrames[i].scale(50, 60);
        }
        
        setImage(idleFrames[0]);
    }

    public void setHpBar(HpBar bar) { this.healthBar = bar; }
    public void setDashIcon(DashIcon icon) { this.dashIcon = icon; }

    public void act()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        
        manageCursorVisuals(mouse);

        if (invincibilityTimer > 0)
        {
            invincibilityTimer--;
            if (invincibilityTimer % 4 == 0) getImage().setTransparency(100);
            else getImage().setTransparency(255);
        }
        else if (getImage() != null) getImage().setTransparency(255);

        if (punchCooldown > 0) punchCooldown--;
        
        if (dashCooldown > 0)
        {
            dashCooldown--;
            if (dashCooldown % 60 == 0 && dashIcon != null)
            {
                dashIcon.updateIcon((dashCooldown / 60) + (dashCooldown % 60 > 0 ? 1 : 0));
            }
        }

        handleProjectionSorcery(mouse);

        // ====================================================================
        // FREEZE BRAKE: Stop all inputs if locked, tracing, or executing
        // ====================================================================
        if (psState == PS_FROZEN || psState == PS_TRACING || psState == PS_LOCKED || psState == PS_EXECUTING)
        {
            if (psState == PS_LOCKED) {
                setImage(idleFrames[0]); 
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
        if (animTimer >= ANIM_SPEED)
        {
            animTimer = 0;
            if (keyIsPressed)
            {
                animFrame = (animFrame + 1) % 4;
            }
            else
            {
                animFrame = 0;
            }
        }
        setImage(currentFrames[animFrame]);

        if (Greenfoot.isKeyDown("r") && dashCooldown == 0 && (dx1 != 0 || dy1 != 0))
        {
            dashDuration = 10; dashCooldown = 180;
            moveAngle = (int) Math.toDegrees(Math.atan2(dy1, dx1));
            if (dashIcon != null) dashIcon.updateIcon(3);
        }

        // ====================================================================
        // NEW MELEE MECHANIC: Quick Jabs / Close Range Punching
        // ====================================================================
        if (Greenfoot.mousePressed(null) && punchCooldown == 0 && mouse != null)
        {
            // Execute physical forward striking detector
            executeCloseRangeJab(mouse);
            punchCooldown = 12; // Fast recovery rate for quick succession inputs
        }

        checkEnemyContact();
    }

    private void executeCloseRangeJab(MouseInfo mouse)
    {
        if (mouse == null || getWorld() == null) return;

        // Calculate the angle towards the cursor
        double angleRad = Math.atan2(mouse.getY() - getY(), mouse.getX() - getX());
        int angleDeg = (int) Math.toDegrees(angleRad);
        
        // Push the spawn points out slightly in front of Naobito's body (20 pixels out)
        int spawnX = getX() + (int)(Math.cos(angleRad) * 20);
        int spawnY = getY() + (int)(Math.sin(angleRad) * 20);
        
        // Punch 1: Left shoulder jab
        int leftX = spawnX + (int)(Math.cos(angleRad + Math.PI/2) * 12);
        int leftY = spawnY + (int)(Math.sin(angleRad + Math.PI/2) * 12);
        int leftAngle = angleDeg + (Greenfoot.getRandomNumber(16) - 8); // slight variance
        PunchVisual jab1 = new PunchVisual(leftAngle);
        getWorld().addObject(jab1, leftX, leftY);
        
        // Punch 2: Right shoulder jab (thrown slightly wider)
        int rightX = spawnX + (int)(Math.cos(angleRad - Math.PI/2) * 12);
        int rightY = spawnY + (int)(Math.sin(angleRad - Math.PI/2) * 12);
        int rightAngle = angleDeg + (Greenfoot.getRandomNumber(16) - 8);
        PunchVisual jab2 = new PunchVisual(rightAngle);
        getWorld().addObject(jab2, rightX, rightY);
    }

    private void handleProjectionSorcery(MouseInfo mouse)
    {
        BeachWorld currentWorld = null;
        if (getWorld() instanceof BeachWorld) {
            currentWorld = (BeachWorld) getWorld();
        }

        // --- PHASE 1: ACTIVATION (PRESSING Q) ---
        if (Greenfoot.isKeyDown("q") && psState == PS_NONE && punchCooldown == 0)
        {
            Actor target = getEnemyInAimLine(mouse);

            if (target != null)
            {
                frozenEnemy = target;
                frozenTimer = 180; 
                
                if (currentWorld != null) {
                    currentWorld.setFrozenEnemy(target);
                }

                setLocation(target.getX(), target.getY() - 60);
                psState = PS_NONE; 
                punchCooldown = 45;

                activeGlassPanel = new GlassPanel(frozenEnemy);
                getWorld().addObject(activeGlassPanel, frozenEnemy.getX(), frozenEnemy.getY());
            }
            else
            {
                psState = PS_FROZEN;
                psTimer = FREEZE_DURATION;
                tracedPath.clear();
                firstMoveDir = -1;
                tracedPath.add(new int[]{getX(), getY()});
                applyGreyOut(true);

                if (currentWorld != null) {
                    currentWorld.setTimeFreeze(true);
                }
            }
        }

        // --- PHASE 2: TIME FREEZE & PATH TRACING ---
        if (psState == PS_FROZEN || psState == PS_TRACING)
        {
            psTimer--;

            int secondsLeft = (psTimer / 60) + 1; 
            
            String directionPrompt = "";
            if (firstMoveDir == 2) directionPrompt = "Keep moving Left! Hold [A] when zone ends!";
            else if (firstMoveDir == 3) directionPrompt = "Keep moving Right! Hold [D] when zone ends!";
            else if (firstMoveDir == 0) directionPrompt = "Keep moving Up! Hold [W] when zone ends!";
            else if (firstMoveDir == 1) directionPrompt = "Keep moving Down! Hold [S] when zone ends!";
            
            String subtitleText = "TIME FREEZE: " + secondsLeft + "... | " + directionPrompt;
            drawFreezeSubtitles(subtitleText);

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
                setLocation(tx, ty);
                if (psTimer % 5 == 0)
                    tracedPath.add(new int[]{tx, ty});
            }

            if (psTimer <= 0)
            {
                psState = PS_CONFIRM;
                psTimer = 90; 
                applyGreyOut(false);
                drawFreezeSubtitles(""); 
                
                if (currentWorld != null) {
                    currentWorld.setTimeFreeze(false);
                }

                if (tracedPath.size() > 0)
                    setLocation(tracedPath.get(0)[0], tracedPath.get(0)[1]);
            }
        }

        // --- PHASE 3: VERIFY CONTINUOUS SPEED RULES ---
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
                    psState = PS_EXECUTING;
                    pathIndex = 0;
                    psTimer = tracedPath.size() * 2;
                }
                else
                {
                    psState = PS_LOCKED;
                    psTimer = LOCKED_DURATION; 
                    
                    playerGlassTrap = new GlassPanel(this);
                    getWorld().addObject(playerGlassTrap, getX(), getY());
                }
            }

            if (psTimer <= 0)
            {
                psState = PS_LOCKED;
                psTimer = LOCKED_DURATION;
                
                playerGlassTrap = new GlassPanel(this);
                getWorld().addObject(playerGlassTrap, getX(), getY());
            }
        }

        // --- PHASE 4: EXECUTION MODE ---
        if (psState == PS_EXECUTING)
        {
            if (pathIndex < tracedPath.size())
            {
                int[] point = tracedPath.get(pathIndex);
                setLocation(point[0], point[1]);
                pathIndex++;
                invincibilityTimer = 5; 
            }
            else
            {
                psState = PS_NONE;
                punchCooldown = 60;
            }
        }

        // --- PHASE 5: FREEZE PENALTY ACTIVE ---
        if (psState == PS_LOCKED)
        {
            psTimer--;
            
            if (playerGlassTrap != null && playerGlassTrap.getWorld() != null) {
                playerGlassTrap.setLocation(getX(), getY());
            }

            if (psTimer <= 0) 
            {
                psState = PS_NONE;
                
                if (playerGlassTrap != null && playerGlassTrap.getWorld() != null) {
                    getWorld().removeObject(playerGlassTrap);
                }
                playerGlassTrap = null; 
            }
        }

        // --- SINGLE GLASS PANEL BEHAVIOR TRACKER ---
        if (frozenEnemy != null)
        {
            frozenTimer--;
            immobilizeTarget(frozenEnemy);

            if (frozenTimer <= 0 || frozenEnemy.getWorld() == null)
            {
                clearActiveGlassTrap();
            }
            else if (Greenfoot.isKeyDown("space") && mouse != null && activeGlassPanel != null)
            {
                int launchAngle = (int) Math.toDegrees(Math.atan2(mouse.getY() - getY(), mouse.getX() - getX()));
                activeGlassPanel.launchFromActor(launchAngle, 12);
                
                activeGlassPanel = null;
                frozenEnemy = null;
                frozenTimer = 0;
            }
        }
    }

    private void manageCursorVisuals(MouseInfo mouse)
    {
        if (getWorld() == null) return;
        
        if (aimCursor == null)
        {
            aimCursor = new ProjectionCursor();
            getWorld().addObject(aimCursor, getX(), getY());
        }
        
        if (mouse != null)
        {
            aimCursor.setLocation(mouse.getX(), mouse.getY());
            Actor potentialTarget = getEnemyInAimLine(mouse);
            aimCursor.updateCursorStyle(potentialTarget != null);
        }
    }

    private void immobilizeTarget(Actor enemy)
    {
        if (enemy == null || enemy.getWorld() == null) return;
    }

    private void freezeAllWorldEnemies(boolean shouldFreeze)
    {
        if (getWorld() == null) return;
        
        List<Actor> entities = getWorld().getObjects(Actor.class);
        for (Actor a : entities)
        {
            if (a instanceof Fish || a instanceof Pufferfish || a instanceof Crab)
            {
                if (shouldFreeze) {
                    // Handled internally by time freeze hooks
                }
            }
        }
    }

    private void clearActiveGlassTrap()
    {
        if (getWorld() instanceof BeachWorld)
        {
            ((BeachWorld) getWorld()).setFrozenEnemy(null);
        }

        if (activeGlassPanel != null && activeGlassPanel.getWorld() != null)
        {
            getWorld().removeObject(activeGlassPanel);
        }
        
        if (frozenEnemy != null && frozenEnemy.getWorld() != null)
        {
            frozenEnemy.getImage().setTransparency(255);
        }
        
        activeGlassPanel = null;
        frozenEnemy = null;
        frozenTimer = 0;
    }
    
    private void drawFreezeSubtitles(String message)
    {
        World w = getWorld();
        if (w == null) return;
        
        GreenfootImage bg = w.getBackground();
        
        int bannerHeight = 40;
        int bannerY = w.getHeight() - 75; 
        
        GreenfootImage cleanPatch = new GreenfootImage("beach.jpg");
        cleanPatch.scale(w.getWidth(), w.getHeight());
        
        GreenfootImage slice = new GreenfootImage(w.getWidth(), bannerHeight);
        slice.drawImage(cleanPatch, 0, -bannerY);
        
        if (greyedOut || psState == PS_FROZEN || psState == PS_TRACING) {
            GreenfootImage tint = new GreenfootImage(w.getWidth(), bannerHeight);
            tint.setColor(new Color(100, 100, 120, 160));
            tint.fillRect(0, 0, w.getWidth(), bannerHeight);
            slice.drawImage(tint, 0, 0);
        }
        
        bg.drawImage(slice, 0, bannerY);
        
        if (message == null || message.isEmpty()) return;

        Font subtitleFont = new Font("Arial", true, false, 24);
        bg.setFont(subtitleFont);
        
        int textWidth = message.length() * 12; 
        int x = (w.getWidth() / 2) - (textWidth / 2) + 40; 
        int y = w.getHeight() - 50;
        
        bg.setColor(Color.BLACK);
        bg.drawString(message, x + 2, y + 2);
        bg.drawString(message, x - 2, y - 2);
        bg.drawString(message, x + 2, y - 2);
        bg.drawString(message, x - 2, y + 2);
        
        bg.setColor(new Color(255, 215, 0));
        bg.drawString(message, x, y);
    }

    private Actor getEnemyInAimLine(MouseInfo mouse)
    {
        if (mouse == null) return null;

        double aimRad = Math.atan2(mouse.getY() - getY(), mouse.getX() - getX());
        List<Fish> fish = getWorld().getObjects(Fish.class);
        for (Fish f : fish)
        {
            double angleToEnemy = Math.atan2(f.getY() - getY(), f.getX() - getX());
            if (Math.abs(aimRad - angleToEnemy) < 0.15 && distanceTo(f) < 250) return f; 
        }
        List<Pufferfish> puffers = getWorld().getObjects(Pufferfish.class);
        for (Pufferfish p : puffers)
        {
            double angleToEnemy = Math.atan2(p.getY() - getY(), p.getX() - getX());
            if (Math.abs(aimRad - angleToEnemy) < 0.15 && distanceTo(p) < 250) return p;
        }
        List<Crab> crabs = getWorld().getObjects(Crab.class);
        for (Crab c : crabs)
        {
            double angleToEnemy = Math.atan2(c.getY() - getY(), c.getX() - getX());
            if (Math.abs(aimRad - angleToEnemy) < 0.15 && distanceTo(c) < 250) return c;
        }
        return null;
    }

    private double distanceTo(Actor target)
    {
        return Math.hypot(target.getX() - getX(), target.getY() - getY());
    }

    private void applyGreyOut(boolean grey)
    {
        World w = getWorld();
        if (w == null) return;
        
        this.greyedOut = grey; 
        
        GreenfootImage bg = new GreenfootImage("beach.jpg");
        bg.scale(w.getWidth(), w.getHeight());
        
        if (grey)
        {
            GreenfootImage overlay = new GreenfootImage(w.getWidth(), w.getHeight());
            overlay.setColor(new Color(100, 100, 120, 160));
            overlay.fillRect(0, 0, w.getWidth(), w.getHeight());
            bg.drawImage(overlay, 0, 0);
        }
        
        w.setBackground(bg);
    }

    private void checkEnemyContact()
    {
        if (invincibilityTimer == 0 && psState == PS_NONE)
        {
            Actor immuneEnemy = null;
            if (getWorld() instanceof BeachWorld) {
                immuneEnemy = ((BeachWorld) getWorld()).getFrozenEnemy();
            }

            Actor hitCrab = (Actor) getOneIntersectingObject(Crab.class);
            if (hitCrab != null && hitCrab != immuneEnemy) {
                takeDamage(1);
                return;
            }

            Actor hitFish = (Actor) getOneIntersectingObject(Fish.class);
            if (hitFish != null && hitFish != immuneEnemy) {
                takeDamage(1);
                return;
            }

            Actor hitPuffer = (Actor) getOneIntersectingObject(Pufferfish.class);
            if (hitPuffer != null && hitPuffer != immuneEnemy) {
                takeDamage(1);
                return;
            }
        }
    }

    public void takeDamage(int amount)
    {
        if (invincibilityTimer == 0 && psState == PS_NONE)
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