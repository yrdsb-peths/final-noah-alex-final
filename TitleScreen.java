import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
public class TitleScreen extends World
{
    Label titleLabel = new Label("fish shooter", 60);
    private GreenfootSound bgm = new GreenfootSound("smash.mp3");

    /**
     * Constructor for objects of class TitleScreen.
     * */
    public TitleScreen()
    {    
        super(800, 600, 1); 
        GreenfootImage beachBg = new GreenfootImage("background.jpg");
        beachBg.scale(800, 600);
        setBackground(beachBg);
        
        addObject(titleLabel, getWidth()/2, 200);
        prepare();
        
        bgm.setVolume(40);
        bgm.playLoop();
    }

    public void act()
    {
        if(Greenfoot.isKeyDown("space"))
        {
            bgm.stop();
            MyWorld gameWorld = new MyWorld();
            Greenfoot.setWorld(gameWorld);
        }
    }
    
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
        label.setLocation(422,487);
        label2.setLocation(411,336);
        label.setLocation(440,422);
        Label label3 = new Label("press r to DASH", 50);
        addObject(label3,106,292);
        label3.setLocation(410,300);
        Label label4 = new Label("press 'e' to throw trident", 50);
        addObject(label4,106,292);
        label4.setLocation(100,300);
        label3.setLocation(651,241);
        label4.setLocation(495,255);
        label4.setLocation(440,250);
        label.setLocation(315,410);
        label3.setLocation(531,291);
        label3.setLocation(421,289);
        label.setLocation(528,402);
        label.setLocation(443,370);
        label.setLocation(425,382);
        label4.setLocation(458,488);
        label3.setLocation(451,441);
        label.setLocation(412,237);
        label2.setLocation(438,333);
        label2.setLocation(400,336);
        label3.setLocation(408,354);
        label3.setLocation(420,373);
        label3.setLocation(410,382);
        label3.setLocation(410,374);
        label4.setLocation(428,425);
        label4.setLocation(432,414);
    }
}