import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Write a description of class SwordfishBoss here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SwordfishBoss extends Actor
{
    /**
     * Act - do whatever the SwordfishBoss wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    // Boss Core Stats
    private int bossHp = 10;
    private GreenfootImage baseBossImage;
    
    // State Machine Enums (Representing the states as numbers)
    private final int TRACKING = 0;
    private final int CHARGING = 1;
    private final int DASHING  = 2;
    private final int STUCK    = 3;
    private int currentState = TRACKING;
    
    // Timers (Assuming ~60 frames per second)
    private int stateTimer = 120; // 2 seconds to track initially
    private int dashAngle = 0;    // Locks the angle before charging/dashing
    
    public SwordfishBoss()
    {
        // Replace "swordfish.png" with your actual file name!
        baseBossImage = new GreenfootImage("swordfish.png");
        baseBossImage.scale(60, 60); // Made a bit bigger since it's a boss
        
    }
    
    public void act()
    {
        // Add your action code here.
    }
    
}
