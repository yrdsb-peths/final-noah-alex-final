import greenfoot.*;

public class CutsceneWorld extends World
{
    private int savedScore; // Variable to keep the score safe
    private String[][] dialogueData = {
        { "???", "oh... a stupid human has shown up in this realm...", "HIDDEN" },
        { "Hero", "...", "HIDDEN" },
        { "Dagon", "I'm surprised you defeated the kraken...", "REVEALED" },
        { "Dagon", "but now your time ends here.", "REVEALED" },
        { "Dagon", "Domain expansion....", "HANDSIGN" }, 
        { "Dagon", "HORIZON OF THE CAPTIVATING SKANDHA!!", "FINALE" } 
    };

    private int currentLine = 0;
    private boolean spacePressedLastFrame = true;

    private DialogueBox nameBox;
    private DialogueBox textBox;
    private CutsceneActor heroSprite;
    private CutsceneActor dagonSprite;
    
    //bgm
    private GreenfootSound cutsceneBgm = new GreenfootSound("shrine.mp3");
    
    public CutsceneWorld(int scoreFromPreviousWorld)
    {    
        super(800, 600, 1); 
        this.savedScore = scoreFromPreviousWorld;
        GreenfootImage bg = new GreenfootImage("background.png");
        bg.scale(800, 600);
        setBackground(bg);

        //stops bgm
        if (MyWorld.regularBgm != null && MyWorld.regularBgm.isPlaying()) {
            MyWorld.regularBgm.stop();
        }
        if (MyWorld.krakenBgm != null && MyWorld.krakenBgm.isPlaying()) {
            MyWorld.krakenBgm.stop();
        }
        cutsceneBgm.setVolume(40);
        cutsceneBgm.playLoop();

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

    public CutsceneWorld() {
        this(0);
    }

    @Override
    public void started()
    {
        cutsceneBgm.playLoop();
    }

    @Override
    public void stopped()
    {
        cutsceneBgm.pause();
    }
    
    public void act()
    {
        //if u press space it goes to the next dialogue thing
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
        //goes to next line
        currentLine++;
        if (currentLine < dialogueData.length)
        {
            displayLine(currentLine);
        }
        else
        {
            cutsceneBgm.stop(); 
            Greenfoot.setWorld(new TechniqueSelectWorld()); 
        }
    }

    private void displayLine(int index)
    {
        String speaker = dialogueData[index][0];
        String text    = dialogueData[index][1];
        String state   = dialogueData[index][2];

        nameBox.drawText(speaker, 18, Color.YELLOW);
        textBox.drawText(text, 16, Color.WHITE);

        //hides dagon, reveals him or shows his handsign 
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
            // black bgm
            GreenfootImage darkBg = new GreenfootImage(800, 600);
            darkBg.setColor(Color.BLACK);
            darkBg.fillRect(0, 0, 800, 600);
            setBackground(darkBg);

            // hero gone
            heroSprite.getImage().setTransparency(0);

            // shows his hand sign cuz its a domain expansion
            // handsign image, make it larger, and center him 
            GreenfootImage handImg = new GreenfootImage("handsign.png");
            handImg.scale(400, 400); 
            dagonSprite.setImage(handImg);
            dagonSprite.setLocation(400, 250); // centers him
            dagonSprite.getImage().setTransparency(255);
        }
        else if (state.equals("FINALE"))
        {
            nameBox.drawText("", 1, Color.BLACK);
            textBox.drawText("HORIZON OF THE CAPTIVATING SKANDHA!!", 24, Color.RED);
        }
    }
}