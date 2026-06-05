import greenfoot.*;

public class ControlsScreen extends World
{
    private String technique;
    private int inputSafetyDelay = 20; 

    public ControlsScreen(String technique)
    {    
        super(800, 600, 1); 
        this.technique = technique;

        // Dark clean background
        GreenfootImage bg = new GreenfootImage(800, 600);
        bg.setColor(new Color(10, 10, 20));
        bg.fillRect(0, 0, 800, 600);
        setBackground(bg);

        // Universal Title
        Label title = new Label("HOW TO PLAY", 40);
        title.setLineColor(new Color(200, 180, 255));
        addObject(title, 400, 60);

        // Core controls
        Label moveLabel = new Label("Movement: W, A, S, D", 22);
        moveLabel.setLineColor(Color.WHITE);
        addObject(moveLabel, 400, 120);
        
        Label dashLabel = new Label("Dash: Press R while moving (3s Cooldown)", 22);
        dashLabel.setLineColor(Color.WHITE);
        addObject(dashLabel, 400, 155);

        // Character Layouts
        Label charTitle = new Label("", 32);
        Label weaponDesc = new Label("", 18);

        if (technique.equals("MAKI"))
        {
            charTitle.setValue("== MAKI ZENIN ==");
            charTitle.setLineColor(new Color(180, 60, 60));
            
            weaponDesc.setValue("Left Click: Attack with floating weapon\n" +
                                "Middle Click: Heavy sweep attack\n" +
                                "Right Click: Throw weapon like a boomerang\n\n" +
                                "Passive: Moves faster and breaks blocks easily");
            
            addObject(charTitle, 400, 220);
            weaponDesc.setLineColor(Color.WHITE);
            addObject(weaponDesc, 400, 340);
        }
        else if (technique.equals("NAOBITO"))
        {
            charTitle.setValue("== NAOBITO ZENIN ==");
            charTitle.setLineColor(new Color(60, 120, 200));
            
            weaponDesc.setValue("Left Click: Normal directional strike\n" +
                                "Press Q: Activates Projection Sorcery Frame Trap\n\n" +
                                "• IF CURSOR IS YELLOW: Target is too far away!\n" +
                                "  You cannot teleport or freeze the enemy from this distance.\n\n" +
                                "• IF CURSOR IS RED: Target is in range!\n" +
                                "  Press Q to teleport and freeze them solid. Walk up and press SPACE to throw them into a wall.");
            
            addObject(charTitle, 400, 220);
            weaponDesc.setLineColor(Color.WHITE);
            addObject(weaponDesc, 400, 350);
            
            createNaobitoCursorDemo();
        }
        else if (technique.equals("NANAMI"))
        {
            charTitle.setValue("== KENTO NANAMI ==");
            charTitle.setLineColor(new Color(200, 160, 40));
            
            weaponDesc.setValue("Left Click: Normal blunt sword strike\n" +
                                "Press E: Activates 7:3 Ratio bar\n\n" +
                                "How it works: Time freezes and a bar shows up.\n" +
                                "Left Click exactly when the slider hits the RED Zone.\n" +
                                "Time it right to deal massive critical damage!");
            
            addObject(charTitle, 400, 220);
            weaponDesc.setLineColor(Color.WHITE);
            addObject(weaponDesc, 400, 340);
        }

        // Bottom confirmation prompt
        Label prompt = new Label("Press SPACE to start the game", 24);
        prompt.setLineColor(new Color(100, 220, 100));
        addObject(prompt, 400, 540);
    }

    private void createNaobitoCursorDemo()
    {
        int baseY = 475;
        
        Label neutralLabel = new Label("[ Yellow Cursor ]\nOut of Range\n(Cannot Teleport or Freeze)", 15);
        neutralLabel.setLineColor(new Color(240, 220, 80));
        addObject(neutralLabel, 240, baseY);
        
        Label lockLabel = new Label("[ Red Cursor ]\nIn Range\n(Ready to Teleport and Freeze!)", 15);
        lockLabel.setLineColor(new Color(255, 100, 100));
        addObject(lockLabel, 560, baseY);
    }

    public void act()
    {
        if (inputSafetyDelay > 0)
        {
            inputSafetyDelay--;
            return; 
        }

        if (Greenfoot.isKeyDown("space"))
        {
            Greenfoot.setWorld(new BeachWorld(technique));
        }
    }
}