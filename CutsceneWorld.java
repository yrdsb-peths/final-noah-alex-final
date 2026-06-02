import greenfoot.*;

public class CutsceneWorld extends World
{
    private String[][] dialogueData = {
        { "???", "oh... a stupid human has shown up in this realm...", "HIDDEN" },
        { "Hero", "...", "HIDDEN" },
        { "Dagon", "I'm surprised you defeated the kraken...", "REVEALED" },
        { "Dagon", "but now your time ends *here.*", "REVEALED" },
        { "Dagon", "Domain expansion....", "HANDSIGN" },
        { "Dagon", "HORIZON OF THE CAPTIVATING SANDAI!!", "FINALE" }
    };

    private int currentLine = 0;
    private boolean spacePressedLastFrame = true;

    private DialogueBox nameBox;
    private DialogueBox textBox;
    private CutsceneActor heroSprite;
    private CutsceneActor dagonSprite;

    public CutsceneWorld()
    {
        super(800, 600, 1);

        GreenfootImage bg = new GreenfootImage("background.png");
        bg.scale(800, 600);
        setBackground(bg);

        nameBox = new DialogueBox(180, 40);
        textBox = new DialogueBox(680, 90);
        addObject(nameBox, 110, 490);
        addObject(textBox, 420, 550);

        heroSprite  = new CutsceneActor("hero-talk.png",  160, 240);
        dagonSprite = new CutsceneActor("dagon-talk.png", 160, 240);

        addObject(heroSprite,  150, 300);
        addObject(dagonSprite, 650, 300);

        displayLine(currentLine);
    }

    public void act()
    {
        if (Greenfoot.isKeyDown("space"))
        {
            if (!spacePressedLastFrame)
            {
                spacePressedLastFrame = true;
                advanceDialogue();
            }
        }
        else
        {
            spacePressedLastFrame = false;
        }
    }

    private void advanceDialogue()
    {
        currentLine++;
        if (currentLine < dialogueData.length)
            displayLine(currentLine);
        else
            Greenfoot.setWorld(new TechniqueSelectWorld());
    }

    private void displayLine(int index)
    {
        String speaker = dialogueData[index][0];
        String text    = dialogueData[index][1];
        String state   = dialogueData[index][2];

        nameBox.drawText(speaker, 18, Color.YELLOW);
        textBox.drawText(text, 16, Color.WHITE);

        if (state.equals("HIDDEN"))
        {
            heroSprite.getImage().setTransparency(255);
            dagonSprite.applySilhouetteFilter(true);
        }
        else if (state.equals("REVEALED"))
        {
            heroSprite.getImage().setTransparency(255);
            dagonSprite.applySilhouetteFilter(false);
            dagonSprite.getImage().setTransparency(255);
        }
        else if (state.equals("HANDSIGN"))
        {
            heroSprite.getImage().setTransparency(0);
            dagonSprite.getImage().setTransparency(0);
            GreenfootImage handBg = new GreenfootImage("image_d4c13f.jpg");
            handBg.scale(800, 600);
            setBackground(handBg);
        }
        else if (state.equals("FINALE"))
        {
            nameBox.drawText("", 1, Color.BLACK);
            textBox.drawText("HORIZON OF THE CAPTIVATING SANDAI!!", 22, Color.RED);
        }
    }
}