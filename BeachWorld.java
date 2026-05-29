import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class BeachWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BeachWorld extends World
{

    /**
     * Constructor for objects of class BeachWorld.
     * 
     */
    public BeachWorld()
    {    
        super(600, 400, 1); 
        
        // Set the world background to the beach image
        GreenfootImage beachBg = new GreenfootImage("beach.jpg");
        beachBg.scale(600, 400);
        setBackground(beachBg);
        
        // Re-inject your Hero and gameplay loop setups back here!
        Hero player = new Hero();
        addObject(player, 300, 200);
        
        // Add your gameplay HP meters, score labels, etc.
    }
}
