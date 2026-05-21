import greenfoot.*;

public class MyWorld extends World {
    private int score = 0;
    private boolean bossSpawned = false;
    public MyWorld() {
        super(600, 400, 1);

        Hero al = new Hero();
        addObject(al, 300, 300);
        
        HpBar bar = new HpBar();
        // Placing it near the bottom-left corner (X: 90, Y: 370)
        addObject(bar, 90, 370);
        al.setHpBar(bar);
        
        spawnFish();
    }
    
    public void act()
    {
        int fishCount = getObjects(Fish.class).size();
        int bossCount = getObjects(SwordfishBoss.class).size();
        
        // If there is 1 or 0 fish left, spawn a new one randomly
        if(!bossSpawned)
        {
           if (fishCount < 2)
            {
                spawnFish();
            } 
        }
    }
    
    public void increaseScore()
    {
        score++;
        
        // If the player defeats 5 fish, unleash the boss!
        if (score >= 5 && !bossSpawned)
        {
            bossSpawned = true;
            spawnBoss();
        }
    }
    
    private void spawnFish()
    {
        Fish enemy = new Fish();
        
        // Pick a random X between 0 and 599, and random Y between 0 and 399
        int randomX = Greenfoot.getRandomNumber(getWidth());
        int randomY = Greenfoot.getRandomNumber(getHeight());
        
        addObject(enemy, randomX, randomY);
    }
    
    private void spawnBoss()
    {
        SwordfishBoss boss = new SwordfishBoss();
        // Spawn the boss right in the top-center of the screen
        addObject(boss, 300, 80);
    }
}
