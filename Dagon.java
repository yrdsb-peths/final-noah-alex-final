import greenfoot.*;

public class Dagon extends Actor
{
    private int hp = 35;
    private int maxHp = 35;
    private HpBar bossHpBar;

    private int activeStance = 1;  // Stance 1 = Corner Blast, Stance 2 = Center Crossbars
    private int actionTimer = 0;   
    private boolean moving = false;
    private boolean isAttacking = false; 
    private int targetX, targetY;
    private int lastCorner = -1;

    private int[][] corners = { {700, 100}, {100, 100}, {100, 500}, {700, 500} };

    public Dagon()
    {
        GreenfootImage img = new GreenfootImage("dagon_normal.png");
        img.scale(90, 90);
        setImage(img);
    }

    public void setHpBar(HpBar bar)
    {
        this.bossHpBar = bar;
        this.bossHpBar.setMaxHp(maxHp);
        this.bossHpBar.updateBar(hp);
    }

    public void act()
    {
        if (getWorld() == null) return;

        // Skip movement/attack loops if Naobito locks him inside a frame
        if (getWorld() instanceof BeachWorld && ((BeachWorld)getWorld()).isTimeFrozen()) return;

        // --- FIXED: Checked every single frame regardless of stance/attack states ---
        checkJujutsuStrikes();
        if (getWorld() == null) return; // Terminate execution branch safely if dead

        if (moving)
        {
            processMovement();
        }
        else 
        {
            // Advance his attack clock even while charging to keep hitboxes vulnerable
            actionTimer++;
            runAttackAI();
        }
    }
    
    private void checkJujutsuStrikes()
    {
        // Double-check world presence initially to block premature coordinate trace lookups
        if (getWorld() == null) return;

        // 1. Naobito's Close Range Jab punches
        Actor punch = getOneIntersectingObject(PunchVisual.class);
        if (punch != null)
        {
            // FIX: Remove projectile asset FIRST while world reference is guaranteed
            getWorld().removeObject(punch); 
            takeDamage(1); 
            if (getWorld() == null) return; // Terminate early if Dagon died here
        }
        
        // 2. Maki's physical weapon slices
        Actor makiSwing = getOneIntersectingObject(MakiSwing.class);
        if (makiSwing != null)
        {
            getWorld().removeObject(makiSwing);
            takeDamage(1); 
            if (getWorld() == null) return; 
        }
        
        // 3. Nanami's 7:3 Ratio Blunt Strike slashes
        Actor nanamiSlash = getOneIntersectingObject(SwingVisual.class);
        if (nanamiSlash != null)
        {
            boolean isCrit = ((SwingVisual)nanamiSlash).isCritical();
            
            // FIX: Clear the slice asset out of the world space FIRST
            getWorld().removeObject(nanamiSlash);
            
            if (isCrit)
            {
                takeDamage(5); 
                Greenfoot.playSound("glassbreak.mp3"); 
            }
            else
            {
                takeDamage(2); 
            }
            if (getWorld() == null) return; 
        }
        
        // 4. Naobito's Launched Glass Panels 
        Actor glassBlock = getOneIntersectingObject(GlassPanel.class);
        if (glassBlock != null)
        {
            getWorld().removeObject(glassBlock);
            takeDamage(4); 
            if (getWorld() == null) return; 
        }
    }
    
    private void runAttackAI()
    {
        // Execute attack from current location every 2.5 seconds (150 frames)
        if (actionTimer >= 150)
        {
            actionTimer = 0;
            isAttacking = true; 

            if (activeStance == 1)
            {
                // STANCE 1: Corner Wedge Blast
                int angleToCenter = (int) Math.toDegrees(Math.atan2(300 - getY(), 400 - getX()));
                DagonAttack blast = new DagonAttack("CORNER", angleToCenter, 0);
                getWorld().addObject(blast, getX(), getY());
            }
            else if (activeStance == 2)
            {
                // STANCE 2: Center Rotating Laser Grid (spins slowly at 1 degree per frame)
                DagonAttack beams = new DagonAttack("CENTER", Greenfoot.getRandomNumber(360), 1);
                getWorld().addObject(beams, getX(), getY());
            }
            
            setRotation(0); // Keep boss sprite perfectly upright
        }
    }

    public void unlockMovementAfterAttack()
    {
        this.isAttacking = false;
        
        // --- RANDOM STANCE SELECTOR ENGINE ---
        // Roll a dice from 0 to 9 to determine his next strategy behavior dynamically!
        int randomRoll = Greenfoot.getRandomNumber(10);
        
        if (randomRoll < 7) 
        {
            // 70% Chance to pick a corner attack position
            activeStance = 1;
            int nextCorner = Greenfoot.getRandomNumber(4);
            while (nextCorner == lastCorner)
            {
                nextCorner = Greenfoot.getRandomNumber(4);
            }
            lastCorner = nextCorner;
            navigateTo(corners[nextCorner][0], corners[nextCorner][1]);
        }
        else 
        {
            // 30% Chance to roll a surprise center map ambush positioning!
            activeStance = 2;
            lastCorner = -1;
            navigateTo(400, 300); // Teleport to exact center coordinates
        }
    }

    private void navigateTo(int x, int y)
    {
        this.targetX = x;
        this.targetY = y;
        this.moving = true;
    }

    private void processMovement()
    {
        turnTowards(targetX, targetY);
        move(10); // Swift traveling speed so transitions don't stall game flow

        if (Math.hypot(getX() - targetX, getY() - targetY) < 12)
        {
            setLocation(targetX, targetY);
            setRotation(0); 
            moving = false;
        }
    }

    public void takeDamage(int amount)
    {
        hp -= amount;
        if (bossHpBar != null) bossHpBar.updateBar(hp);

        if (hp <= 0)
        {
            Label winLabel = new Label("DOMAIN COLLAPSED - VICTORY!", 40);
            winLabel.setLineColor(Color.GREEN);
            getWorld().addObject(winLabel, 400, 300);
            
            // --- NEW: Wipes his specific HP bar container off the screen ---
            if (bossHpBar != null && bossHpBar.getWorld() != null)
            {
                getWorld().removeObject(bossHpBar);
            }
            
            getWorld().removeObject(this);
        }
    }
}