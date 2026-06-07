import greenfoot.*;

public class BeachWorld extends World
{
    private String technique;
    
    // --- Hook variables for Projection Sorcery / Mechanics ---
    private boolean isTimeFrozen = false;
    private Actor frozenEnemyObject = null;

    // --- SCORE AND WAVE ENGINE TRACKING ---
    private int score = 0;
    private Label scoreLabel;
    private int spawnDelay = 0; 

    // --- CHANGED TO PUBLIC STATIC: ALLOWS OTHER WORLDS TO SHUT IT DOWN ---
    public static GreenfootSound beachBgm = new GreenfootSound("delirious.mp3");

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

    // --- GREENFOOT LIFECYCLE HOOKS FOR AUDIO MANAGEMENT ---
    @Override
    public void started()
    {
        beachBgm.playLoop();
    }

    @Override
    public void stopped()
    {
        beachBgm.pause();
    }

    public BeachWorld(String technique, int startingScore)
    {
        super(800, 600, 1);
        this.technique = technique;
        this.score = startingScore;
    
        // --- FIXED BGM SETTINGS (Comfortable 40% Volume) ---
        beachBgm.setVolume(40); 
        beachBgm.playLoop();   

        GreenfootImage beachBg = new GreenfootImage("beach.png");
        beachBg.scale(800, 600);
        setBackground(beachBg);

        // Score Label Setup
        scoreLabel = new Label("Score: 0", 30);
        scoreLabel.setLineColor(Color.WHITE);
        addObject(scoreLabel, 80, 30);

        // Spawn UI Components
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

            Label techLabel = new Label("NO TECHNIQUE  |  Maki Zenin", 20);
            techLabel.setLineColor(new Color(200, 80, 80));
            addObject(techLabel, 250, 75); 
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
            addObject(techLabel, 340, 75);

            Label hint = new Label("Q = aim lock / time freeze  |  SPACE = throw frozen enemy", 14);
            hint.setLineColor(new Color(150, 180, 255));
            addObject(hint, 340, 105);
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
            addObject(techLabel, 250, 75);

            Label hint = new Label("E = engage nearest enemy  |  time the red zone for massive damage", 14);
            hint.setLineColor(new Color(255, 220, 100));
            addObject(hint, 330, 105);
        }

        // Spawn initial waves
        spawnCrabWave();
    }

    public BeachWorld(String technique)
    {
        this(technique, 0); 
    }
    
    public void act()
    {
        // Continuous wave manager loop
        if (getObjects(Crab.class).isEmpty() && !isTimeFrozen)
        {
            spawnCrabWave();
        }

        // Manual Spawning Cheats
        if (Greenfoot.isKeyDown("p") && spawnDelay <= 0)
        {
            spawnMultipleCrabs(1);
            spawnDelay = 20; 
        }
        if (Greenfoot.isKeyDown("b") && spawnDelay <= 0)
        {
            spawnMultipleTurtles(1);
            spawnDelay = 20; 
        }
        
        if (spawnDelay > 0) spawnDelay--;
    }

    public void increaseScore()
    {
        score++;
        if (scoreLabel != null)
        {
            scoreLabel.setValue("Score: " + score);
        }
        
        if (score > 0 && score % 7 == 0)
        {
            spawnMultipleTurtles(1);
        }
    }

    private void spawnCrabWave()
    {
        int totalCrabsToSpawn = 2 + (score / 3);
        spawnMultipleCrabs(totalCrabsToSpawn);
    }

    private void spawnMultipleCrabs(int count)
    {
        for (int i = 0; i < count; i++)
        {
            int edge = Greenfoot.getRandomNumber(4);
            int x = 0, y = 0;
            
            if (edge == 0) { x = Greenfoot.getRandomNumber(getWidth()); y = 15; }                  
            else if (edge == 1) { x = Greenfoot.getRandomNumber(getWidth()); y = getHeight() - 15; } 
            else if (edge == 2) { x = 15; y = Greenfoot.getRandomNumber(getHeight()); }              
            else { x = getWidth() - 15; y = Greenfoot.getRandomNumber(getHeight()); }               
            
            addObject(new Crab(), x, y);
        }
    }

    private void spawnMultipleTurtles(int count)
    {
        for (int i = 0; i < count; i++)
        {
            int x = Greenfoot.getRandomNumber(2) == 0 ? 30 : getWidth() - 30;
            int y = 50 + Greenfoot.getRandomNumber(getHeight() - 100);
            addObject(new Turtle(), x, y);
        }
    }

    public BeachWorld()
    {
        this("MAKI");
    }
}