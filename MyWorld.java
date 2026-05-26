import greenfoot.*;
public class MyWorld extends World {
    private int score = 0;
    private boolean bossSpawned = false;
    private boolean bossDefeated = false;
    private int nemoKillsAfterBoss = 0;
    private boolean pufferWaveSpawned = false;
    private int spawnTimer = 0;
    private int nemoSpawnCount = 0; // how many nemos have been spawned in puffer wave
    private Label scoreLabel;
    private int phase2EndScore = -1;
    public MyWorld() {
        super(600, 400, 1);
        GreenfootImage bg = new GreenfootImage("background.png");
        bg.scale(600, 400); // match your world dimensions
        setBackground(bg);
        
        Hero al = new Hero();
        addObject(al, 300, 300);

        HpBar bar = new HpBar();
        addObject(bar, 90, 370);
        al.setHpBar(bar);
        
        scoreLabel = new Label("Score: 0", 30);
        scoreLabel.setLineColor(Color.WHITE);
        addObject(scoreLabel, 80, 30);
        
        spawnFish();
    }

    public void act()
    {
        int fishCount = getObjects(Fish.class).size();

        // Phase 1: Normal nemo spawning before boss
        if (!bossSpawned && !bossDefeated)
        {
            if (fishCount < 2)
            {
                spawnFish();
            }
        }

        // Phase 2: After boss dies, spawn 4 nemos in intervals + 1 pufferfish
        if (bossDefeated && !pufferWaveSpawned)
        {
            spawnTimer++;
            if (spawnTimer % 60 == 0 && nemoSpawnCount < 4) // one nemo every 60 frames
            {
                spawnFish();
                nemoSpawnCount++;

                // Spawn the pufferfish alongside the first nemo
                if (nemoSpawnCount == 1)
                {
                    spawnPufferfish();
                }
            }

            if (nemoSpawnCount >= 4)
            {
                pufferWaveSpawned = true;
                phase2EndScore = score;
            }
        }
    }

    public void increaseScore()
    {
        score++;
        scoreLabel.setValue("Score: " + score);
        if (score >= 5 && !bossSpawned)
        {
            bossSpawned = true;
            spawnBoss();
        }
    }

    // Called by SwordfishBoss when it dies
    public void notifyBossDefeated()
    {
        bossDefeated = true;
    }

    // Called by Fish when killed during post-boss phase
    public void notifyNemoKilled()
    {
        if (bossDefeated && !pufferWaveSpawned) return; // still spawning, don't count yet
        if (bossDefeated)
        {
            nemoKillsAfterBoss++;
        }
    }

    // Called by Pufferfish when it dies
    public void notifyPufferKilled()
{
    spawnTridentPickup();
}

private void spawnTridentPickup()
{
    TridentPickup pickup = new TridentPickup();
    // Spawns off the left edge, slides right, sticks to right wall
    addObject(pickup, -10, 100 + Greenfoot.getRandomNumber(200));
    pickup.setRotation(0); // slides rightward
}

    private void spawnFish()
    {
        Fish enemy = new Fish();
        int randomX = Greenfoot.getRandomNumber(getWidth());
        int randomY = Greenfoot.getRandomNumber(getHeight());
        addObject(enemy, randomX, randomY);
    }

    private void spawnBoss()
    {
        SwordfishBoss boss = new SwordfishBoss();
        addObject(boss, 300, 80);
        spawnHealthPack();
    }

    private void spawnPufferfish()
    {
        Pufferfish puffer = new Pufferfish();
        int randomX = Greenfoot.getRandomNumber(getWidth());
        int randomY = Greenfoot.getRandomNumber(getHeight());
        addObject(puffer, randomX, randomY);
    }
    
    private void spawnHealthPack()
    {
        HealthPack pack = new HealthPack();
        // Spawns with a safety padding inside the world edges
        int randomX = 40 + Greenfoot.getRandomNumber(getWidth() - 80);
        int randomY = 40 + Greenfoot.getRandomNumber(getHeight() - 110);
        addObject(pack, randomX, randomY);
    }
}