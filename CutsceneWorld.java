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
        { "Dagon", "HORIZON OF THE CAPTIVATING SKANDHA!!", "FINALE" }
    };

    private int currentLine = 0;
    private boolean spacePressedLastFrame = true;

    // UI elements references
    private DialogueBox nameBox;
    private DialogueBox textBox;
    private CutsceneActor heroSprite;
    private CutsceneActor dagonSprite;
    private Label skipPromptLabel;
    
    public CutsceneWorld()
    {    
        super(800, 600, 1); 
        
        GreenfootImage bg = new GreenfootImage("background.png");
        bg.scale(800, 600);
        setBackground(bg);

        // Initialize UI Boxes at the bottom of the 800x600 world
        nameBox = new DialogueBox(180, 40);
        textBox = new DialogueBox(680, 90);
        addObject(nameBox, 110, 490);
        addObject(textBox, 420, 550);
        
        // Creates a small font size 16 label saying "Space ->"
        skipPromptLabel = new Label("[space]", 20);
        skipPromptLabel.setLineColor(new Color(150, 150, 160)); // Clean secondary gray color
        // Placed in the bottom-right corner inside the dialogue container frame
        addObject(skipPromptLabel, 700, 580);
        
        // Initialize Character Avatars spaced across the wider world
        heroSprite = new CutsceneActor("hero-talk.png", 160, 240);
        dagonSprite = new CutsceneActor("dagon-talk.png", 160, 240);
        
        addObject(heroSprite, 150, 300);
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
        {
            displayLine(currentLine);
        }
        else
        {
            Greenfoot.setWorld(new BeachWorld());
        }
    }

    private void displayLine(int index)
    {
        String speaker = dialogueData[index][0];
        String text = dialogueData[index][1];
        String state = dialogueData[index][2];

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
    
    // 1. Create a blank image the size of your world (e.g., 800x600)
    GreenfootImage blackBg = new GreenfootImage(800, 600);
    
    // 2. Use Greenfoot's Color class to fill it with black
    blackBg.setColor(greenfoot.Color.BLACK);
    blackBg.fill();
    
    // 3. Load your handsign image
    GreenfootImage handImg = new GreenfootImage("handsign.png");
    
    // 4. Scale it down a bit so the black background is visible around it
    // (Adjust 500, 400 to whatever size looks best for your image)
    handImg.scale(500, 400); 
    
    // 5. Calculate coordinates to draw the hand perfectly in the center
    int x = (blackBg.getWidth() - handImg.getWidth()) / 2;
    int y = (blackBg.getHeight() - handImg.getHeight()) / 2;
    
    // 6. Draw the hand onto the black canvas and set it
    blackBg.drawImage(handImg, x, y);
    setBackground(blackBg);
}
        else if (state.equals("FINALE"))
        {
            nameBox.drawText("", 1, Color.BLACK);
            textBox.drawText("HORIZON OF THE CAPTIVATING SANDAI!!", 22, Color.RED);
            // --- NEW POLISH: Clear the skip label on the final chant frame ---
            // This lets the ultimate final splash line look completely cinematic!
            removeObject(skipPromptLabel);
        }
    }
}