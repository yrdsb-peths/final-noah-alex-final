import greenfoot.*; 
public class DialogueBox extends Actor
{
    private int w, h;
    public DialogueBox(int width, int height)
    {
        this.w = width;
        this.h = height;
        drawText("", 16, Color.WHITE); // default empty display 
    }

    public void drawText(String msg, int fontSize, Color textColor)
    {
        GreenfootImage canvas = new GreenfootImage(w, h);
        
        // dark box
        canvas.setColor(new Color(10, 10, 20, 220)); 
        canvas.fillRect(0, 0, w - 1, h - 1);
        
        // grey outline
        canvas.setColor(new Color(180, 180, 190));
        canvas.drawRect(0, 0, w - 1, h - 1);
        
        if (!msg.isEmpty())
        {
            canvas.setColor(textColor);
            canvas.setFont(new Font("Verdana", true, false, fontSize));
            canvas.drawString(msg, 12, (h / 2) + (fontSize / 3));
        }
        
        setImage(canvas);
    }
}
