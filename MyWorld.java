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
    public MyWorld() {
        super(600, 400, 1);
        setBackground("background.png");
        
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
        // hook for future waves or effects
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
    }

    private void spawnPufferfish()
    {
        Pufferfish puffer = new Pufferfish();
        int randomX = Greenfoot.getRandomNumber(getWidth());
        int randomY = Greenfoot.getRandomNumber(getHeight());
        addObject(puffer, randomX, randomY);
    }
}