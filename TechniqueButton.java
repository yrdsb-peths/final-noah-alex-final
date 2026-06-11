import greenfoot.*;

public class TechniqueButton extends Actor
{
    private String techniqueName;
    private String characterName;
    private String description;
    private Color accentColor;

    private boolean selected = false;
    private boolean hovered = false;
    private int pulseTimer = 0;

    private static TechniqueButton lastSelected = null;

    private final int W = 200;
    private final int H = 280;

    public TechniqueButton(String techniqueName, String characterName, String description, Color accentColor)
    {
        this.techniqueName = techniqueName;
        this.characterName = characterName;
        this.description = description;
        this.accentColor = accentColor;
        redraw();
    }

    public void update(MouseInfo mouse)
    {
        pulseTimer++;

        boolean nowHovered = false;
        if (mouse != null)
        {
            int dx = Math.abs(mouse.getX() - getX());
            int dy = Math.abs(mouse.getY() - getY());
            nowHovered = (dx < W / 2 && dy < H / 2);
        }

        // Click to select this button, deselect others
        if (nowHovered && Greenfoot.mouseClicked(null))
        {
            if (lastSelected != null && lastSelected != this)
            {
                lastSelected.setSelected(false);
            }
            selected = true;
            lastSelected = this;
        }

        if (nowHovered != hovered)
        {
            hovered = nowHovered;
        }

        redraw();
    }

    private void redraw()
    {
        GreenfootImage img = new GreenfootImage(W, H);

        // Pulse glow amount
        double pulse = Math.sin(pulseTimer * 0.08) * 0.5 + 0.5; // 0.0 to 1.0

        // Background
        if (selected)
        {
            int glow = (int)(40 + pulse * 40);
            img.setColor(new Color(
                Math.min(255, accentColor.getRed() / 4 + glow),
                Math.min(255, accentColor.getGreen() / 4 + glow),
                Math.min(255, accentColor.getBlue() / 4 + glow)
            ));
        }
        else if (hovered)
        {
            img.setColor(new Color(30, 30, 50));
        }
        else
        {
            img.setColor(new Color(15, 15, 25));
        }
        img.fillRect(0, 0, W, H);

        // Border - glows when selected or hovered
        int borderAlpha = selected ? (int)(180 + pulse * 75) : (hovered ? 160 : 80);
        borderAlpha = Math.min(255, borderAlpha);
        img.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), borderAlpha));
        img.drawRect(0, 0, W - 1, H - 1);
        img.drawRect(1, 1, W - 3, H - 3); // Double border for thickness

        // Technique name (top, bold)
        img.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue()));
        img.setFont(new Font("Arial", true, false, 14));
        drawCentered(img, techniqueName, 14, 40);

        // Divider line
        img.setColor(new Color(80, 80, 100));
        img.drawLine(20, 60, W - 20, 60);

        // Character name
        img.setColor(Color.WHITE);
        img.setFont(new Font("Arial", true, false, 13));
        drawCentered(img, characterName, 13, 85);

        // Description (word-wrapped manually across two lines)
        img.setColor(new Color(180, 180, 200));
        img.setFont(new Font("Arial", false, false, 11));
        String[] words = description.split(" ");
        String line1 = "";
        String line2 = "";
        boolean second = false;
        for (String w : words)
        {
            if (!second && (line1 + w).length() < 20)
                line1 += w + " ";
            else { second = true; line2 += w + " "; }
        }
        drawCentered(img, line1.trim(), 11, 120);
        drawCentered(img, line2.trim(), 11, 140);

        // Selected indicator at bottom
        if (selected)
        {
            img.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), (int)(180 + pulse * 75)));
            img.setFont(new Font("Arial", true, false, 12));
            drawCentered(img, "[ SELECTED ]", 12, H - 25);
        }
        else if (hovered)
        {
            img.setColor(new Color(150, 150, 170));
            img.setFont(new Font("Arial", false, false, 11));
            drawCentered(img, "click to select", 11, H - 25);
        }

        setImage(img);
    }

    private void drawCentered(GreenfootImage img, String text, int fontSize, int y)
    {
        int approxX = Math.max(5, (W - text.length() * fontSize / 2) / 2);
        img.drawString(text, approxX, y);
    }

    public void setSelected(boolean s) { selected = s; }
    public boolean isSelected() { return selected; }
}