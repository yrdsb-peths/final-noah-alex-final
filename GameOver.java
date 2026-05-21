import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class GameOver here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class GameOver extends World
{

    /**
     * Constructor for objects of class GameOver.
     * 
     */
    public GameOver()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1); 
        // Add a giant red Game Over label in the center
        Label gameOverLabel = new Label("GAME OVER", 70);
        gameOverLabel.setLineColor(Color.RED);
        addObject(gameOverLabel, getWidth() / 2, 160);

        // Add a smaller instruction label underneath
        Label restartLabel = new Label("Press Space to Try Again", 30);
        addObject(restartLabel, getWidth() / 2, 260);
    }
    
    public void act()
    {
        // If the player presses space, send them back to the game world
        if (Greenfoot.isKeyDown("space"))
        {
            Greenfoot.setWorld(new MyWorld());
        }
    }
}
