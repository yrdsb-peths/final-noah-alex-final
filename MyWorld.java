import greenfoot.*;

public class MyWorld extends World {
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
    
    int x, y;
    int edge = Greenfoot.getRandomNumber(4); // 0=top, 1=bottom, 2=left, 3=right
    
    switch (edge)
    {
        case 0: // Top edge
            x = Greenfoot.getRandomNumber(getWidth());
            y = 0;
            break;
        case 1: // Bottom edge
            x = Greenfoot.getRandomNumber(getWidth());
            y = getHeight() - 1;
            break;
        case 2: // Left edge
            x = 0;
            y = Greenfoot.getRandomNumber(getHeight());
            break;
        default: // Right edge
            x = getWidth() - 1;
            y = Greenfoot.getRandomNumber(getHeight());
            break;
    }
    
    addObject(enemy, x, y);
}
}
