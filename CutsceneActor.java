import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class CutsceneActor here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CutsceneActor extends Actor
{
    /**
     * Act - do whatever the CutsceneActor wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private GreenfootImage originalImage;
    private GreenfootImage silhouetteImage;

    public CutsceneActor(String filename, int width, int height)
    {
        originalImage = new GreenfootImage(filename);
        originalImage.scale(width, height);
        
        // Create the dark shadow silhouette mode frame canvas mapping
        silhouetteImage = new GreenfootImage(originalImage);
        for (int x = 0; x < silhouetteImage.getWidth(); x++)
        {
            for (int y = 0; y < silhouetteImage.getHeight(); y++)
            {
                Color pixelColor = silhouetteImage.getColorAt(x, y);
                // Preserve invisible image backgrounds, make solid parts flat black
                if (pixelColor.getAlpha() > 10)
                {
                    silhouetteImage.setColorAt(x, y, new Color(0, 0, 0, pixelColor.getAlpha()));
                }
            }
        }
        
        setImage(originalImage);
    }

    public void applySilhouetteFilter(boolean active)
    {
        if (active) setImage(silhouetteImage);
        else setImage(originalImage);
    }
    public void act()
    {
        // Add your action code here.
    }
}
