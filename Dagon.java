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

        // naobito stuff
        if (getWorld() instanceof BeachWorld && ((BeachWorld)getWorld()).isTimeFrozen()) return;

        checkJujutsuStrikes();
        if (getWorld() == null) return; //returns so it doesnt crash

        if (moving)
        {
            processMovement();
        }
        else 
        {
            actionTimer++;
            runAttackAI();
        }
    }
    
    private void checkJujutsuStrikes()
    {
        //litterally all the attacks from different heros 
        if (getWorld() == null) return;

        // naobito punches
        Actor punch = getOneIntersectingObject(PunchVisual.class);
        if (punch != null)
        {
            getWorld().removeObject(punch); 
            takeDamage(1); 
            if (getWorld() == null) return; 
        }
        
        // maki attacks
        Actor makiSwing = getOneIntersectingObject(MakiSwing.class);
        if (makiSwing != null)
        {
            getWorld().removeObject(makiSwing);
            takeDamage(1); 
            if (getWorld() == null) return; 
        }
        
        // nanami attacks
        Actor nanamiSlash = getOneIntersectingObject(SwingVisual.class);
        if (nanamiSlash != null)
        {
            boolean isCrit = ((SwingVisual)nanamiSlash).isCritical();
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
        
        // naobito freeze
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
        // attacks every 150 frames
        if (actionTimer >= 150)
        {
            actionTimer = 0;
            isAttacking = true; 

            if (activeStance == 1)
            {
                // 1st stance is corner attack
                int angleToCenter = (int) Math.toDegrees(Math.atan2(300 - getY(), 400 - getX()));
                DagonAttack blast = new DagonAttack("CORNER", angleToCenter, 0);
                getWorld().addObject(blast, getX(), getY());
            }
            else if (activeStance == 2)
            {
                // 2nd stance is spinning attack
                DagonAttack beams = new DagonAttack("CENTER", Greenfoot.getRandomNumber(360), 1);
                getWorld().addObject(beams, getX(), getY());
            }
            
            setRotation(0); // keeps image upright
        }
    }

    public void unlockMovementAfterAttack()
    {
        this.isAttacking = false;
        
        //random attack based on rolls 
        int randomRoll = Greenfoot.getRandomNumber(10);
        
        if (randomRoll < 7) 
        {
            // 70% Chance to go to corner and attack
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
            // 30% chance for wide map attack
            activeStance = 2;
            lastCorner = -1;
            navigateTo(400, 300); 
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
        move(10);

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
            Label winLabel = new Label("VICTORY!", 40);
            winLabel.setLineColor(Color.GREEN);
            getWorld().addObject(winLabel, 400, 300);
            
            // when he dies get rid of his bossbar
            if (bossHpBar != null && bossHpBar.getWorld() != null)
            {
                getWorld().removeObject(bossHpBar);
            }
            
            getWorld().removeObject(this);
        }
    }
}