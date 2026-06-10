import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Handles scaling, rendering, flat silhouettes, and dimmed states for cutscene portraits.
 */
public class CutsceneActor extends Actor
{
    private GreenfootImage originalImage;
    private GreenfootImage silhouetteImage;
    private GreenfootImage dimmedImage;

    public CutsceneActor(String filename, int width, int height)
    {
        originalImage = new GreenfootImage(filename);
        originalImage.scale(width, height);
        
        // 1. Create the flat shadow silhouette version (completely black)
        silhouetteImage = new GreenfootImage(originalImage);
        for (int x = 0; x < silhouetteImage.getWidth(); x++)
        {
            for (int y = 0; y < silhouetteImage.getHeight(); y++)
            {
                Color pixelColor = silhouetteImage.getColorAt(x, y);
                if (pixelColor.getAlpha() > 10)
                {
                    silhouetteImage.setColorAt(x, y, new Color(0, 0, 0, pixelColor.getAlpha()));
                }
            }
        }
        
        // 2. Create the naturally dimmed, greyed-out conversational version
        dimmedImage = new GreenfootImage(originalImage);
        for (int x = 0; x < dimmedImage.getWidth(); x++)
        {
            for (int y = 0; y < dimmedImage.getHeight(); y++)
            {
                Color pixelColor = dimmedImage.getColorAt(x, y);
                if (pixelColor.getAlpha() > 10)
                {
                    // Scale down RGB channels to 40% brightness to look greyed out/unfocused
                    int r = (int)(pixelColor.getRed() * 0.4);
                    int g = (int)(pixelColor.getGreen() * 0.4);
                    int b = (int)(pixelColor.getBlue() * 0.4);
                    dimmedImage.setColorAt(x, y, new Color(r, g, b, pixelColor.getAlpha()));
                }
            }
        }
        
        setImage(originalImage);
    }

    /**
     * Toggles whether this actor is an unrevealed solid black silhouette.
     */
    public void applySilhouetteFilter(boolean active)
    {
        if (active) {
            setImage(silhouetteImage);
        } else {
            setImage(originalImage);
        }
    }

    /**
     * Toggles whether this actor is conversational greyed out (not talking).
     */
    public void setDimmed(boolean active)
    {
        if (active) {
            setImage(dimmedImage);
        } else {
            setImage(originalImage);
        }
    }
}