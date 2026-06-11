import greenfoot.*;

public class BeachWorld extends World
{
    private String technique;
    
    // specifically for Naobito when he freezes time and enemies 
    private boolean isTimeFrozen = false;
    private Actor frozenEnemyObject = null;

    // score
    private int score = 0;
    private Label scoreLabel;
    private int spawnDelay = 0; 
    
    //phases
    private int phaseTimer = 0;
    private final int SPAWN_RATE = 90; // Spawns an enemy every 90 frames 
    
    //bgm
    public static GreenfootSound beachBgm = new GreenfootSound("delirious.mp3");
    public static GreenfootSound victoryBgm = new GreenfootSound("smash.mp3"); // Add this line!

    private boolean dagonSpawned = false; 
    
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

@Override
    public void started()
    {
        if (dagonSpawned && getObjects(Dagon.class).isEmpty() && score >= 35) {
            victoryBgm.play();
        } else {
            beachBgm.playLoop();
        }
    }

    @Override
    public void stopped()
    {
        beachBgm.pause();
        victoryBgm.pause(); 
    }

    public BeachWorld(String technique, int startingScore)
    {
        super(800, 600, 1);
        this.technique = technique;
        this.score = startingScore;
        beachBgm.setVolume(40); 
        beachBgm.playLoop();   

        GreenfootImage beachBg = new GreenfootImage("beach.png");
        beachBg.scale(800, 600);
        setBackground(beachBg);

        // score label
        scoreLabel = new Label("Score: 0", 30);
        scoreLabel.setLineColor(Color.WHITE);
        addObject(scoreLabel, 80, 30);

        // ui 
        HpBar bar = new HpBar();
        DashIcon dIcon = new DashIcon();

        //technique selection
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

        // Spawn enemies
        spawnMultipleCrabs(2);
    }

    public BeachWorld(String technique)
    {
        this(technique, 0); 
    }
    
    public void act()
    {
        //summons enemies w/o dagon
        if (!isTimeFrozen && !dagonSpawned)
        {
            handlePhaseSpawning();
        }

        //cheats
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
        if (Greenfoot.isKeyDown("y") && spawnDelay <= 0 && !dagonSpawned)
        {
            spawnDagonBossInstance();
        }
        if (spawnDelay > 0) spawnDelay--;
    }

    private void handlePhaseSpawning()
    {
        // if dagon exists, stops all minion generation cycles
        if (dagonSpawned) return;

        phaseTimer++;
        if (phaseTimer >= SPAWN_RATE)
        {
            phaseTimer = 0; 

            // phase 1 of crabs
            if (score <= 12)
            {
                if (getObjects(Crab.class).size() < 5)
                {
                    spawnMultipleCrabs(1);
                }
            }
            // phase 2 lots of crabs and some turtles 
            else if (score >= 13 && score <= 25)
            {
                if (Greenfoot.getRandomNumber(10) < 3) 
                {
                    if (getObjects(Turtle.class).size() < 1) 
                    {
                        spawnMultipleTurtles(1);
                    }
                    else
                    {
                        spawnMultipleCrabs(1);
                    }
                }
                else
                {
                    spawnMultipleCrabs(2);
                }
            }
            // phase 3 lots of turtles and crabs
            else if (score >= 26 && score <= 34)
            {
                if (Greenfoot.getRandomNumber(10) < 4) 
                {
                    spawnMultipleTurtles(1);
                }
                else
                {
                    spawnMultipleCrabs(1);
                }
            }
            // phase 4 dagon
            else if (score >= 35)
            {
                spawnDagonBossInstance();
            }
        }
    }
    
    private void spawnDagonBossInstance()
    {
        dagonSpawned = true;
        
        //gets rid of other annoying enemies to make it easier
        removeObjects(getObjects(Crab.class));
        removeObjects(getObjects(Turtle.class));

        //hp
        HpBar dagonBar = new HpBar();
        dagonBar.setBarDimensions(400, 15);
        dagonBar.setLineColor(new Color(139, 0, 0)); 
        addObject(dagonBar, 400, 45);

        //spawns him in the corner 
        Dagon boss = new Dagon();
        addObject(boss, 700, 100);
        boss.setHpBar(dagonBar);
    }
    
    public void increaseScore()
    {
        score++;
        if (scoreLabel != null)
        {
            scoreLabel.setValue("Score: " + score);
        }
    }

    private void spawnCrabWave()
    {
        int totalCrabsToSpawn = 2 + (score / 7);
        spawnMultipleCrabs(totalCrabsToSpawn);
    }

    private void spawnMultipleCrabs(int count)
    {
        //spawns crabs in locations
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

    //default value so it doesnt crash
    public BeachWorld()
    {
        this("MAKI");
    }
}