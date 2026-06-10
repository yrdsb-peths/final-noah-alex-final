import greenfoot.*;

public class TechniqueSelectWorld extends World
{
    private TechniqueButton makiBtn;
    private TechniqueButton naoBtn;
    private TechniqueButton nanamiBtn;
    private int preservedScore = 0;
    GreenfootSound ratio = new GreenfootSound("ratio.mp3");
    //tried to carry the score over from different worlds 
    public TechniqueSelectWorld(int scoreCarryOver)
    {
        super(800, 600, 1);
        this.preservedScore = scoreCarryOver;

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

    /**
     * Legacy default constructor compatibility
     */
    public TechniqueSelectWorld()
    {
        this(0);
    }

    public void act()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        makiBtn.update(mouse);
        naoBtn.update(mouse);
        nanamiBtn.update(mouse);

        //confirmation
        if (Greenfoot.isKeyDown("space"))
        {
            String selected = "";
            if (makiBtn.isSelected()) selected = "MAKI";
            else if (naoBtn.isSelected()) selected = "NAOBITO";
            else if (nanamiBtn.isSelected()) selected = "NANAMI";
            
            if (!selected.equals(""))
            {
                ratio.play();
                Greenfoot.setWorld(new ControlsScreen(selected));
            }
        }
    }
}