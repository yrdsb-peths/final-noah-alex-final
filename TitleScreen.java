import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class TitleScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TitleScreen extends World
{
    Label titleLabel = new Label("fish shooter", 60);
    /**
     * Constructor for objects of class TitleScreen.
     * 
     */
    public TitleScreen()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1); 

        addObject(titleLabel, getWidth()/2, 200);
        prepare();
    }

    public void act()
    {
        if(Greenfoot.isKeyDown("space"))
        {
            MyWorld gameWorld = new MyWorld();
            Greenfoot.setWorld(gameWorld);
        }
    }
    
    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
        Hero elephant = new Hero();
        addObject(elephant,494,83);
        elephant.setLocation(508,162);
        elephant.setLocation(488,93);
        elephant.setLocation(513,141);
        Label label = new Label("press space to start", 50);
        addObject(label,128,198);
        label.setLocation(334,198);
        label.setLocation(292,222);
        Label label2 = new Label("use wasd to move and m1 to shoot", 30);
        addObject(label2,106,292);
        label2.setLocation(389,300);
        label.setLocation(324,249);
        label.setLocation(328,210);
        label.setLocation(346,98);
        label.setLocation(247,359);
        label2.setLocation(357,247);
        label2.setLocation(320,247);
        label.setLocation(382,289);
        label.setLocation(343,290);
        label.setLocation(337,275);
        label.setLocation(312,281);
    }
}