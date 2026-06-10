import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Scaled Game Over screen with smart tracking to send you back 
 * to the technique selection if you died in BeachWorld.
 */
public class GameOver extends World
{
    private boolean cameFromBeach;
    private GreenfootSound gameOverBgm = new GreenfootSound("gameover.mp3");
    public GameOver()
    {    
        this(false); 
    }

    /**
     * Main constructor that tracks which world you died in
     */
    public GameOver(boolean cameFromBeach)
    {    
        super(800, 600, 1); 
        this.cameFromBeach = cameFromBeach;

        // stops bgm
        if (BeachWorld.beachBgm != null && BeachWorld.beachBgm.isPlaying()) {
            BeachWorld.beachBgm.stop();
        }
        if (MyWorld.regularBgm != null && MyWorld.regularBgm.isPlaying()) {
            MyWorld.regularBgm.stop();
        }
        if (MyWorld.krakenBgm != null && MyWorld.krakenBgm.isPlaying()) {
            MyWorld.krakenBgm.stop();
        }
        gameOverBgm.setVolume(40);
        gameOverBgm.playLoop();

        // dark bgm
        GreenfootImage bg = new GreenfootImage(800, 600);
        bg.setColor(new Color(15, 5, 5));
        bg.fillRect(0, 0, 800, 600);
        setBackground(bg);

        // red label
        Label gameOverLabel = new Label("GAME OVER", 70);
        gameOverLabel.setLineColor(Color.RED);
        addObject(gameOverLabel, getWidth() / 2, 240);
        Label restartLabel = new Label("Press Space to Try Again", 30);
        addObject(restartLabel, getWidth() / 2, 340);
    }
    
    @Override
    public void started()
    {
        gameOverBgm.playLoop();
    }

    @Override
    public void stopped()
    {
        gameOverBgm.pause();
    }
    
    public void act()
    {
        if (Greenfoot.isKeyDown("space"))
        {
            gameOverBgm.stop();

            if (cameFromBeach)
            {
                Greenfoot.setWorld(new TechniqueSelectWorld());
            }
            else
            {
                Greenfoot.setWorld(new MyWorld());
            }
        }
    }
}