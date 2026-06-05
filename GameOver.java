import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Scaled Game Over screen with smart tracking to send you back 
 * to the technique selection if you died in BeachWorld.
 */
public class GameOver extends World
{
    private boolean cameFromBeach;

    /**
     * Default constructor (defaults to normal MyWorld legacy restart)
     */
    public GameOver()
    {    
        this(false); // Call the main constructor with false
    }

    /**
     * Main constructor that tracks which world you died in
     */
    public GameOver(boolean cameFromBeach)
    {    
        // 1. Scaled up to 800x600 to perfectly match the rest of the game worlds!
        super(800, 600, 1); 
        this.cameFromBeach = cameFromBeach;

        // Darkened background layout
        GreenfootImage bg = new GreenfootImage(800, 600);
        bg.setColor(new Color(15, 5, 5));
        bg.fillRect(0, 0, 800, 600);
        setBackground(bg);

        // Giant red Game Over label in the center
        Label gameOverLabel = new Label("GAME OVER", 70);
        gameOverLabel.setLineColor(Color.RED);
        addObject(gameOverLabel, getWidth() / 2, 240);

        // Smaller instruction label underneath
        Label restartLabel = new Label("Press Space to Try Again", 30);
        addObject(restartLabel, getWidth() / 2, 340);
    }
    
    public void act()
    {
        // If the player presses space, check where they came from
        if (Greenfoot.isKeyDown("space"))
        {
            if (cameFromBeach)
            {
                // Send them directly back to pick a technique!
                Greenfoot.setWorld(new TechniqueSelectWorld());
            }
            else
            {
                // Legacy default fallback
                Greenfoot.setWorld(new MyWorld());
            }
        }
    }
}