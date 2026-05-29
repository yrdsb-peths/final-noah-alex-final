import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class CutsceneWorld extends World
{
    // Dialogue sheet structure: { "Speaker Name", "Dialogue Text", "Dagon Sprite Type" }
    private String[][] dialogueData = {
        { "???", "oh... a stupid human has shown up in this realm...", "HIDDEN" },
        { "Hero", "...", "HIDDEN" },
        { "Dagon", "I'm surprised you defeated the kraken...", "REVEALED" },
        { "Dagon", "but now your time ends *here.*", "REVEALED" },
        { "Dagon", "Domain expansion....", "HANDSIGN" },
        { "Dagon", "HORIZON OF THE CAPTIVATING SANDAI!!", "FINALE" }
    };

    private int currentLine = 0;
    private boolean spacePressedLastFrame = true; // Prevents instantly skipping through all dialogue

    // UI elements references
    private DialogueBox nameBox;
    private DialogueBox textBox;
    private CutsceneActor heroSprite;
    private CutsceneActor dagonSprite;

    // --- 1. CONSTRUCTOR (Runs ONLY once when the world is first loaded) ---
    public CutsceneWorld()
    {    
        super(600, 400, 1); 
        
        // Setup initial water depth background
        GreenfootImage bg = new GreenfootImage("background.png"); // Match your main game background file name
        bg.scale(600, 400);
        setBackground(bg);

        // Initialize UI Boxes at the bottom
        nameBox = new DialogueBox(140, 35);
        textBox = new DialogueBox(560, 80);
        addObject(nameBox, 90, 270);
        addObject(textBox, 300, 335);

        // Initialize Character Avatars
        heroSprite = new CutsceneActor("baseguy.png", 70, 70);
        dagonSprite = new CutsceneActor("baseguy.png", 90, 90); // Replace with your dagon image filename if different
        
        addObject(heroSprite, 120, 200);
        addObject(dagonSprite, 480, 200);

        // Fire up the first dialogue line immediately
        displayLine(currentLine);
    }

    // --- 2. THE ACT METHOD (Where your input handling code is supposed to live) ---
    public void act()
    {
        // Continuous keyboard listener runs perfectly here every frame!
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
            // Transition immediately to the beach level after the domain chant concludes
            Greenfoot.setWorld(new BeachWorld());
        }
    }

    private void displayLine(int index)
    {
        String speaker = dialogueData[index][0];
        String text = dialogueData[index][1];
        String state = dialogueData[index][2];

        // Update Text inside the UI overlays
        nameBox.drawText(speaker, 18, Color.YELLOW);
        textBox.drawText(text, 16, Color.WHITE);

        // Manage Character Visibilities & Filter transformations
        if (state.equals("HIDDEN"))
        {
            heroSprite.getImage().setTransparency(255);
            dagonSprite.applySilhouetteFilter(true); // Turn Dagon flat black (???)
        }
        else if (state.equals("REVEALED"))
        {
            heroSprite.getImage().setTransparency(255);
            dagonSprite.applySilhouetteFilter(false); // Reveal true identity colours
            dagonSprite.getImage().setTransparency(255);
        }
        else if (state.equals("HANDSIGN"))
        {
            // Clear out individual actor sprites completely
            heroSprite.getImage().setTransparency(0);
            dagonSprite.getImage().setTransparency(0);
            
            // Swap global world background texture to the hand sign file image_d4c13f.jpg
            GreenfootImage handBg = new GreenfootImage("image_d4c13f.jpg");
            handBg.scale(600, 400);
            setBackground(handBg);
        }
        else if (state.equals("FINALE"))
        {
            // Remove text framing configurations for Dagon's domain chant background climax
            nameBox.drawText("", 1, Color.BLACK);
            textBox.drawText("HORIZON OF THE CAPTIVATING SANDAI!!", 22, Color.RED);
        }
    }
}