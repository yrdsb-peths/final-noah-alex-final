import greenfoot.*;

public class TechniqueSelectWorld extends World
{
    private TechniqueButton makiBtn;
    private TechniqueButton naoBtn;
    private TechniqueButton nanamiBtn;

    public TechniqueSelectWorld()
    {
        super(800, 600, 1);

        // Dark background
        GreenfootImage bg = new GreenfootImage(800, 600);
        bg.setColor(new Color(5, 5, 15));
        bg.fillRect(0, 0, 800, 600);
        setBackground(bg);

        // Title
        Label title = new Label("CHOOSE YOUR TECHNIQUE", 36);
        title.setLineColor(new Color(200, 180, 255));
        addObject(title, 400, 80);

        Label sub = new Label("press space to confirm selection", 18);
        sub.setLineColor(new Color(120, 100, 160));
        addObject(sub, 400, 130);

        // Three technique buttons spaced across screen
        makiBtn  = new TechniqueButton("NO TECHNIQUE",     "Maki Zenin",  "Faster movement + cloud weapon",    new Color(180, 60, 60));
        naoBtn   = new TechniqueButton("PROJECTION SORCERY", "Naobito Zenin", "Freeze enemies, trace your path", new Color(60, 120, 200));
        nanamiBtn = new TechniqueButton("7:3 RATIO",        "Kento Nanami", "Time your strike at the weak point", new Color(200, 160, 40));

        addObject(makiBtn,   160, 350);
        addObject(naoBtn,    400, 350);
        addObject(nanamiBtn, 640, 350);
    }

    public void act()
    {
        // Update hover glow on all buttons each frame
        MouseInfo mouse = Greenfoot.getMouseInfo();
        makiBtn.update(mouse);
        naoBtn.update(mouse);
        nanamiBtn.update(mouse);

        if (Greenfoot.isKeyDown("space"))
        {
            if (makiBtn.isSelected())
                Greenfoot.setWorld(new BeachWorld("MAKI"));
            else if (naoBtn.isSelected())
                Greenfoot.setWorld(new BeachWorld("NAOBITO"));
            else if (nanamiBtn.isSelected())
                Greenfoot.setWorld(new BeachWorld("NANAMI"));
        }
    }
}