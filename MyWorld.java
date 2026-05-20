import greenfoot.*;

public class MyWorld extends World {
    public MyWorld() {
        super(600, 400, 1);
        
        Hero al = new Hero();
        addObject(al, 300, 300);
        
        spawnFish();
    }
    
    public void act()
    {
        // Constantly check how many fish are currently alive in the world
        int fishCount = getObjects(Fish.class).size();
        
        // If there is 1 or 0 fish left, spawn a new one randomly
        if (fishCount < 2)
        {
            spawnFish();
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
}
