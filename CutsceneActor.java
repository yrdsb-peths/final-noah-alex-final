import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class CutsceneActor extends Actor
{
    private GreenfootImage originalImage;
    private GreenfootImage silhouetteImage;

    public CutsceneActor(String filename, int width, int height)
    {
        originalImage = new GreenfootImage(filename);
        originalImage.scale(width, height);
        
        // creates the dark shadow thing over dagon
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
        setImage(originalImage);
    }

    public void applySilhouetteFilter(boolean active)
    {
        //adds filter over dagon
        if (active) setImage(silhouetteImage);
        else setImage(originalImage);
    }
}
