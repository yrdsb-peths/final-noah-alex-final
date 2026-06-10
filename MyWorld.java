import greenfoot.*;

public class MyWorld extends World {
    // --- CHANGED TO PUBLIC STATIC: Allows GameOver to find and kill them instantly ---
    public static GreenfootSound regularBgm = new GreenfootSound("spongebob.mp3");
    public static GreenfootSound krakenBgm = new GreenfootSound("spongebobbattle.mp3");
    
    // Non-static sound effect (only plays once, doesn't need global tracking)
    GreenfootSound krakenSpawnSFX = new GreenfootSound("kraken_spawn.mp3");

    private int score = 0;
    private boolean bossSpawned = false;
    private boolean bossDefeated = false;
    private int nemoKillsAfterBoss = 0;
    private boolean pufferWaveSpawned = false;
    private int spawnTimer = 0;
    private int nemoSpawnCount = 0; // how many nemos have been spawned in puffer wave
    private Label scoreLabel;
    private int phase2EndScore = -1;
    private boolean krakenSpawned = false;
    private Label tridentHintTitle;
    private Label tridentHintSubtitle;

    public MyWorld() {
        super(800, 600, 1);
        GreenfootImage bg = new GreenfootImage("background.png");
        bg.scale(800, 600); // match your world dimensions
        setBackground(bg);
        
        // --- FIXED BGM SETTINGS (Comfortable 40% Volume) ---
        regularBgm.setVolume(40);
        krakenBgm.setVolume(40);
        
        // Start playing the standard background music on loop immediately when the world loads
        regularBgm.playLoop();
        
        Hero al = new Hero();
        addObject(al, 300, 300);

        HpBar bar = new HpBar();
        addObject(bar, 90, 570);
        al.setHpBar(bar);
        
        scoreLabel = new Label("Score: 0", 30);
        scoreLabel.setLineColor(Color.WHITE);
        addObject(scoreLabel, 80, 30);
        
        spawnFish();
        
        // ADDED DASH ICON TO THE BOTTOM LEFT 
        DashIcon dIcon = new DashIcon();
        // Positioned at X: 210, Y: 370 (cleanly sitting to the right of your health bar)
        addObject(dIcon, 210, 570);
        al.setDashIcon(dIcon);
    }

    // --- GREENFOOT LIFECYCLE HOOKS FOR AUDIO MANAGEMENT ---
    @Override
    public void started()
    {
        // Safe check to resume whichever background theme was active when paused
        if (krakenSpawned) {
            krakenBgm.playLoop();
        } else {
            regularBgm.playLoop();
        }
    }

    @Override
    public void stopped()
    {
        // Pause both sound slots immediately on runtime pause
        regularBgm.pause();
        krakenBgm.pause();
    }

    public void act()
    {
        int fishCount = getObjects(Fish.class).size();
        int pufferCount = getObjects(Pufferfish.class).size();

        // Phase 1: Normal nemo spawning before boss
        if (!bossSpawned && !bossDefeated)
        {
            if (fishCount < 2)
            {
                spawnFish();
            }
        }
        int swordfishCount = getObjects(SwordfishBoss.class).size();

        // Phase 2: After boss dies, spawn 10 nemos in intervals + 1 pufferfish
        if (bossDefeated && !pufferWaveSpawned)
        {
            spawnTimer++;
            if (spawnTimer % 60 == 0 && nemoSpawnCount < 10) // one nemo every 60 frames
            {
                spawnFish();
                nemoSpawnCount++;

                // Spawn the pufferfish alongside the first nemo
                if (nemoSpawnCount == 1)
                {
                    spawnPufferfish();
                }
            }

            if (nemoSpawnCount >= 10)
            {
                pufferWaveSpawned = true;
                phase2EndScore = score;
            }
        }
        
        // Phase 3: Transition into the Kraken Boss
        if (pufferWaveSpawned && !krakenSpawned)
        {
            if (fishCount == 0 && pufferCount == 0)
            {
                krakenSpawned = true; // Flips safety gate
                
                // Stop the regular background music before starting the battle music
                regularBgm.stop();
                
                krakenSpawnSFX.play();  // Unleash the kraken spawn roar sound effect!
                krakenBgm.playLoop();   // Spin up the intense SpongeBob battle loop!
                
                spawnKrakenBoss();
            }
        }
        if (Greenfoot.isKeyDown("p"))
        {
            bossSpawned = true; 
            spawnBoss();
            Greenfoot.delay(10); 
        }
        if (Greenfoot.isKeyDown("k"))
        {
            // Debug key override: Ensure regular music stops and battle track triggers smoothly
            if (!krakenSpawned) {
                regularBgm.stop();
                krakenBgm.playLoop();
                krakenSpawned = true;
            }
            spawnKrakenBoss();
            Greenfoot.delay(10); 
        }
        if (Greenfoot.isKeyDown("t"))
        {
            spawnTridentPickup();
            Greenfoot.delay(10); 
        }
    }

    public void stopMusic()
    {
        if (regularBgm.isPlaying()) regularBgm.stop();
        if (krakenBgm.isPlaying()) krakenBgm.stop();
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
        if (pufferWaveSpawned && phase2EndScore != -1)
        {
            int currentKillsInPhase3 = score - phase2EndScore;
            if (currentKillsInPhase3 > 0 && currentKillsInPhase3 % 7 == 0)
            {
                spawnHealthPack();
            }
        }
    }

    public void notifyBossDefeated()
    {
        bossDefeated = true;
    }

    public void notifyNemoKilled()
    {
        if (bossDefeated && !pufferWaveSpawned) return; 
        if (bossDefeated)
        {
            nemoKillsAfterBoss++;
        }
    }

    public void notifyPufferKilled()
    {
        spawnTridentPickup();

        tridentHintTitle = new Label("Press E to throw the trident", 26);
        tridentHintTitle.setLineColor(new Color(255, 215, 0)); 
        addObject(tridentHintTitle, 400, 95);

        tridentHintSubtitle = new Label("After you throw it, run back and pick it back up", 18);
        tridentHintSubtitle.setLineColor(Color.WHITE);
        addObject(tridentHintSubtitle, 400, 130);
    }

    private void spawnTridentPickup()
    {
        TridentPickup pickup = new TridentPickup();
        addObject(pickup, -10, 100 + Greenfoot.getRandomNumber(200));
        pickup.setRotation(0); 
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
        int randomX = 40 + Greenfoot.getRandomNumber(getWidth() - 80);
        int randomY = 40 + Greenfoot.getRandomNumber(getHeight() - 110);
        addObject(pack, randomX, randomY);
    }
    
    private void spawnKrakenBoss()
    {
        HpBar krakenBar = new HpBar();
        krakenBar.setBarDimensions(500, 15); 
        krakenBar.setMaxHp(35); 
        krakenBar.setLineColor(new Color(128, 0, 128)); 
        addObject(krakenBar, 400, 100); 
        
        Kraken boss = new Kraken();
        boss.setHpBar(krakenBar);
        addObject(boss, 500, 300);
    }
    
    public int getScore()
    {
        return this.score;
    }
    
    public void clearTridentTutorialText()
    {
        if (tridentHintTitle != null && tridentHintTitle.getWorld() != null)
        {
            removeObject(tridentHintTitle);
        }
        if (tridentHintSubtitle != null && tridentHintSubtitle.getWorld() != null)
        {
            removeObject(tridentHintSubtitle);
        }
    }
}