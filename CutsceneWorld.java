import greenfoot.*;

public class CutsceneWorld extends World
{
    private int savedScore; // Variable to keep the score safe
    private String[][] dialogueData = {
        { "???", "oh... a stupid human has shown up in this realm...", "HIDDEN" },
        { "Hero", "...", "HIDDEN" },
        { "Dagon", "I'm surprised you defeated the kraken...", "REVEALED" },
        { "Dagon", "but now your time ends *here.*", "REVEALED" },
        { "Dagon", "Domain expansion....", "HANDSIGN" }, // ---> Triggers the close-up cut
        { "Dagon", "HORIZON OF THE CAPTIVATING SKANDHA!!", "FINALE" } // ---> Fixed spelling
    };

    private int currentLine = 0;
    private boolean spacePressedLastFrame = true;

    private DialogueBox nameBox;
    private DialogueBox textBox;
    private CutsceneActor heroSprite;
    private CutsceneActor dagonSprite;
    
    // --- AUDIO HANDLING ENGINE ---
    private GreenfootSound cutsceneBgm = new GreenfootSound("shrine.mp3");
    
    public CutsceneWorld(int scoreFromPreviousWorld)
    {    
        super(800, 600, 1); 
        this.savedScore = scoreFromPreviousWorld; // Save it!
        GreenfootImage bg = new GreenfootImage("background.png");
        bg.scale(800, 600);
        setBackground(bg);

        // --- SILENCE THE KRAKEN AND MYWORLD SOUNDS IMMEDIATELY ---
        if (MyWorld.regularBgm != null && MyWorld.regularBgm.isPlaying()) {
            MyWorld.regularBgm.stop();
        }
        if (MyWorld.krakenBgm != null && MyWorld.krakenBgm.isPlaying()) {
            MyWorld.krakenBgm.stop();
        }

        // --- START CUTSCENE BGM (Comfortable 40% Volume) ---
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
            // --- 1. CUT TO BLACK BACKGROUND ---
            GreenfootImage darkBg = new GreenfootImage(800, 600);
            darkBg.setColor(Color.BLACK);
            darkBg.fillRect(0, 0, 800, 600);
            setBackground(darkBg);

            // --- 2. VANISH THE HERO ---
            heroSprite.getImage().setTransparency(0);

            // --- 3. SHOW DAGON CLOSE-UP USING HANDSIGN.PNG ---
            // Swap to handsign sprite sheet image, make it large, and center him up
            GreenfootImage handImg = new GreenfootImage("handsign.png");
            handImg.scale(400, 400); // Massive close-up dimensions
            dagonSprite.setImage(handImg);
            dagonSprite.setLocation(400, 250); // Move him to absolute center stage
            dagonSprite.getImage().setTransparency(255);
        }
        else if (state.equals("FINALE"))
        {
            nameBox.drawText("", 1, Color.BLACK);
            textBox.drawText("HORIZON OF THE CAPTIVATING SKANDHA!!", 24, Color.RED);
        }
    }
}