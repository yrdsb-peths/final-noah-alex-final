import greenfoot.*;

public class CutsceneWorld extends World
{
    private int savedScore; 
    private String[][] dialogueData = {
        { "???", "oh... a stupid human has shown up in this realm...", "HIDDEN" },
        { "Hero", "...", "HIDDEN" },
        { "Dagon", "I'm surprised you defeated the kraken...", "REVEALED" },
        { "Dagon", "but now your time ends *here.*", "REVEALED" },
        { "Dagon", "Domain expansion....", "HANDSIGN" }, 
        { "Dagon", "HORIZON OF THE CAPTIVATING SKANDHA!!", "FINALE" } 
    };

    private int currentLine = 0;
    private boolean spacePressedLastFrame = true;

    private DialogueBox nameBox;
    private DialogueBox textBox;
    private CutsceneActor heroSprite;
    private CutsceneActor dagonSprite;
    
    private GreenfootSound cutsceneBgm = new GreenfootSound("shrine.mp3");
    
    public CutsceneWorld(int scoreFromPreviousWorld)
    {    
        super(800, 600, 1); 
        this.savedScore = scoreFromPreviousWorld; 
        GreenfootImage bg = new GreenfootImage("background.png");
        bg.scale(800, 600);
        setBackground(bg);

        //layering 
        setPaintOrder(DialogueBox.class, CutsceneActor.class);

        if (MyWorld.regularBgm != null && MyWorld.regularBgm.isPlaying()) {
            MyWorld.regularBgm.stop();
        }
        if (MyWorld.krakenBgm != null && MyWorld.krakenBgm.isPlaying()) {
            MyWorld.krakenBgm.stop();
        }

        cutsceneBgm.setVolume(40);
        cutsceneBgm.playLoop();

        nameBox = new DialogueBox(180, 45);
        nameBox.setCentered(true); 
        
        textBox = new DialogueBox(760, 150); 
        textBox.setSpacePrompt(true);
        
        textBox.setTextOffset(215); 


        addObject(textBox, 400, 515); 
        addObject(nameBox, 125, 515); 

        heroSprite  = new CutsceneActor("hero-talk.png",  160, 240);
        dagonSprite = new CutsceneActor("dagon-talk.png", 160, 240);

    
        addObject(heroSprite,  150, 320);
        addObject(dagonSprite, 650, 320);

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
            dagonSprite.getImage().setTransparency(255);

            heroSprite.applySilhouetteFilter(false);
            dagonSprite.applySilhouetteFilter(true); 

            if (speaker.equals("Hero")) {
                heroSprite.setDimmed(false);
            } else {
                heroSprite.setDimmed(true); 
            }
        }
        else if (state.equals("REVEALED"))
        {
            heroSprite.getImage().setTransparency(255);
            dagonSprite.getImage().setTransparency(255);
            
            heroSprite.applySilhouetteFilter(false);
            dagonSprite.applySilhouetteFilter(false);

            if (speaker.equals("Hero"))
            {
                heroSprite.setDimmed(false);  
                dagonSprite.setDimmed(true);  
            }
            else 
            {
                heroSprite.setDimmed(true);   
                dagonSprite.setDimmed(false); 
            }
        }
        else if (state.equals("HANDSIGN"))
        {
            GreenfootImage darkBg = new GreenfootImage(800, 600);
            darkBg.setColor(Color.BLACK);
            darkBg.fillRect(0, 0, 800, 600);
            setBackground(darkBg);

            heroSprite.getImage().setTransparency(0);

            GreenfootImage handImg = new GreenfootImage("handsign.png");
            handImg.scale(400, 400); 
            dagonSprite.setImage(handImg);
            
            dagonSprite.setLocation(400, 240); 
            dagonSprite.getImage().setTransparency(255);
        }
        else if (state.equals("FINALE"))
        {
            textBox.setTextOffset(20); 
            nameBox.drawText("", 1, Color.BLACK); 
            textBox.drawText("HORIZON OF THE CAPTIVATING SKANDHA!!", 24, Color.RED);
        }
    }
}