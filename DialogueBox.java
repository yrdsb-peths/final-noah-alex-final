import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Enhanced DialogueBox with support for internal text offsets and centering profiles.
 */
public class DialogueBox extends Actor
{
    private int w, h;
    private boolean isCentered = false;
    private boolean showSpacePrompt = false;
    private int textOffset = 20; // Default left margin padding

    public DialogueBox(int width, int height)
    {
        this.w = width;
        this.h = height;
        drawText("", 16, Color.WHITE); 
    }

    /**
     * Toggles whether text should be automatically centered within this box.
     */
    public void setCentered(boolean centered)
    {
        this.isCentered = centered;
    }

    /**
     * Sets a custom left padding offset to prevent text overlapping with embedded sub-boxes.
     */
    public void setTextOffset(int offset)
    {
        this.textOffset = offset;
    }

    /**
     * Toggles the presence of a space bar indicator helper.
     */
    public void setSpacePrompt(boolean show)
    {
        this.showSpacePrompt = show;
    }

    public void drawText(String msg, int fontSize, Color textColor)
    {
        // If the name or message is empty, clear the image completely (makes it invisible)
        if (msg.isEmpty())
        {
            setImage(new GreenfootImage(w, h));
            return;
        }

        GreenfootImage canvas = new GreenfootImage(w, h);
        
        // Render stylized dark backing layer pane box
        canvas.setColor(new Color(10, 10, 20, 220)); // Clean dark glass aesthetic
        canvas.fillRect(0, 0, w - 1, h - 1);
        
        // Render bright gray frame box border outline
        canvas.setColor(new Color(180, 180, 190));
        canvas.drawRect(0, 0, w - 1, h - 1);
        
        // Append text alignment specifications
        canvas.setColor(textColor);
        canvas.setFont(new Font("Verdana", true, false, fontSize));
        
        int x = textOffset; 
        if (isCentered)
        {
            int approxTextWidth = 0;
            
            // Hardcoded exact pixel-perfect widths for name tracking
            if (msg.equals("???")) {
                approxTextWidth = (int)(fontSize * 1.9);
            } else if (msg.equals("Hero")) {
                approxTextWidth = (int)(fontSize * 2.5);
            } else if (msg.equals("Dagon")) {
                approxTextWidth = (int)(fontSize * 3.1);
            } else {
                approxTextWidth = (int)(msg.length() * fontSize * 0.6);
            }
            
            x = (w - approxTextWidth) / 2;
            if (x < 0) x = 2;
        }
        
        // Vertically center string perfectly inside box frame
        int y = (h / 2) + (fontSize / 2) - 2;
        canvas.drawString(msg, x, y);

        // Add small space bar indicator prompt to text panel if active
        if (showSpacePrompt)
        {
            canvas.setColor(Color.GRAY);
            canvas.setFont(new Font("Verdana", false, true, 10));
            canvas.drawString("[Space] >", w - 75, h - 12);
        }
        
        setImage(canvas);
    }
}