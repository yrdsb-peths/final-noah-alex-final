import greenfoot.*;

public class BeachWorld extends World
{
    private String technique;
    
    // --- Add these variables at the top of your BeachWorld class ---
private boolean isTimeFrozen = false;
private Actor frozenEnemyObject = null;



public void setTimeFreeze(boolean freeze) {
    this.isTimeFrozen = freeze;
}

public boolean isTimeFrozen() {
    return this.isTimeFrozen;
}

public void setFrozenEnemy(Actor enemy) {
    this.frozenEnemyObject = enemy;
}

public Actor getFrozenEnemy() {
    return this.frozenEnemyObject;
}

    public BeachWorld(String technique)
    {
        super(800, 600, 1);
        this.technique = technique;

        GreenfootImage beachBg = new GreenfootImage("beach.jpg");
        beachBg.scale(800, 600);
        setBackground(beachBg);

        // Spawn the correct hero class based on chosen technique
        HpBar bar = new HpBar();
        DashIcon dIcon = new DashIcon();

        if (technique.equals("MAKI"))
        {
            Maki player = new Maki();
            addObject(player, 400, 300);
            addObject(bar, 90, 570);
            addObject(dIcon, 210, 570);
            player.setHpBar(bar);
            player.setDashIcon(dIcon);

            // Technique label
            Label techLabel = new Label("NO TECHNIQUE  |  Maki Zenin", 20);
            techLabel.setLineColor(new Color(200, 80, 80));
            addObject(techLabel, 250, 30);
        }
        else if (technique.equals("NAOBITO"))
        {
            Naobito player = new Naobito();
            addObject(player, 400, 300);
            addObject(bar, 90, 570);
            addObject(dIcon, 210, 570);
            player.setHpBar(bar);
            player.setDashIcon(dIcon);

            Label techLabel = new Label("PROJECTION SORCERY  |  Naobito Zenin", 20);
            techLabel.setLineColor(new Color(80, 140, 220));
            addObject(techLabel, 300, 30);

            Label hint = new Label("Q = aim lock / time freeze  |  SPACE = throw frozen enemy", 14);
            hint.setLineColor(new Color(150, 180, 255));
            addObject(hint, 320, 55);
        }
        else if (technique.equals("NANAMI"))
        {
            Nanami player = new Nanami();
            addObject(player, 400, 300);
            addObject(bar, 90, 570);
            addObject(dIcon, 210, 570);
            player.setHpBar(bar);
            player.setDashIcon(dIcon);

            Label techLabel = new Label("7:3 RATIO  |  Kento Nanami", 20);
            techLabel.setLineColor(new Color(220, 180, 40));
            addObject(techLabel, 250, 30);

            Label hint = new Label("E = engage nearest enemy  |  time the red zone for massive damage", 14);
            hint.setLineColor(new Color(255, 220, 100));
            addObject(hint, 330, 55);
        }

        // Score label
        Label scoreLabel = new Label("Score: 0", 30);
        scoreLabel.setLineColor(Color.WHITE);
        addObject(scoreLabel, 80, 570);

        // Your partner's enemy spawning goes here
        // spawnCrab(), spawnShark(), etc.
    }
    
    private int spawnDelay = 0; // To prevent spawning a million fish at once

public void act()
{
    // Check if "p" is pressed and our delay is ready
    if (Greenfoot.isKeyDown("p") && spawnDelay <= 0)
    {
        spawnTestFish();
        spawnDelay = 20; // Wait 20 frames before allowing another spawn
    }
    
    if (spawnDelay > 0) spawnDelay--;
    // Check if "p" is pressed and our delay is ready
    if (Greenfoot.isKeyDown("b") && spawnDelay <= 0)
    {
        spawnTestTurtle();
        spawnDelay = 20; // Wait 20 frames before allowing another spawn
    }
    
    if (spawnDelay > 0) spawnDelay--;
}


    private void spawnTestFish()
    {
        // Spawns a fish at a random edge location
        int x = Greenfoot.getRandomNumber(getWidth());
        int y = Greenfoot.getRandomNumber(getHeight());
        
        // You can replace 'Fish' with 'Shark' or 'Crab' depending on your classes
        addObject(new Crab(), x, y); 
    }
    
    private void spawnTestTurtle()
    {
        // Spawns a fish at a random edge location
        int x = Greenfoot.getRandomNumber(getWidth());
        int y = Greenfoot.getRandomNumber(getHeight());
        
        // You can replace 'Fish' with 'Shark' or 'Crab' depending on your classes
        addObject(new Turtle(), x, y); 
    }

    // Default constructor for compatibility if called without technique
    public BeachWorld()
    {
        this("MAKI");
    }
}