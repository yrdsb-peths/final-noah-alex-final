import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DialogueBox here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DialogueBox extends Actor
{
    /**
     * Act - do whatever the DialogueBox wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private int w, h;

    public DialogueBox(int width, int height)
    {
        this.w = width;
        this.h = height;
        drawText("", 16, Color.WHITE); // Default empty display configuration
    }

    public void drawText(String msg, int fontSize, Color textColor)
    {
        GreenfootImage canvas = new GreenfootImage(w, h);
        
        // Render stylized dark backing layer pane box
        canvas.setColor(new Color(10, 10, 20, 220)); // Clean dark glass aesthetic
        canvas.fillRect(0, 0, w - 1, h - 1);
        
        // Render bright gray frame box border outline
        canvas.setColor(new Color(180, 180, 190));
        canvas.drawRect(0, 0, w - 1, h - 1);
        
        // Append text alignment specifications
        if (!msg.isEmpty())
        {
            canvas.setColor(textColor);
            canvas.setFont(new Font("Verdana", true, false, fontSize));
            // Injects text safe bounds margins padding (X:12 offset, Y: centered)
            canvas.drawString(msg, 12, (h / 2) + (fontSize / 3));
        }
        
        setImage(canvas);
    }
    
    public void act()
    {
        // Add your action code here.
    }
}
