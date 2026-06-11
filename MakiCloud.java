import greenfoot.*;
import java.util.List;

public class MakiCloud extends Actor
{
    private String mode;
    private int timer = 0;
    private int damage = 5;

    // ORBIT
    private double orbitAngle = 0;
    private final int ORBIT_RADIUS = 55;
    private final int ORBIT_SPEED_DEG = 8;
    private int ownerX, ownerY;

    // BOOMERANG
    private boolean returning = false;
    private int boomerangAngle;

    // CURVE
    private int curveAngle;
    private int curveTurn = 3;

    private java.util.ArrayList<Actor> hitActors = new java.util.ArrayList<>();

    public MakiCloud(String mode, int startX, int startY, int angle)
    {
        this.mode = mode;
        this.ownerX = startX;
        this.ownerY = startY;
        this.boomerangAngle = angle;
        this.curveAngle = angle;

        if (mode.equals("ORBIT"))
        {
            GreenfootImage img = new GreenfootImage("cloudswing.png");
            img.scale(35, 35);
            setImage(img);
        }
        else if (mode.equals("BOOMERANG"))
        {
            GreenfootImage img = new GreenfootImage("cloudstrike.png");
            img.scale(45, 15);
            setImage(img);
        }
    }

    public void act()
    {
        timer++;
        
        if (mode.equals("ORBIT"))
        {
            Actor owner = getOrbitOwner();
            if (owner == null || owner.getWorld() == null)
            {
                if (getWorld() != null) getWorld().removeObject(this);
                return;
            }
            
            orbitAngle += ORBIT_SPEED_DEG;
            double radians = Math.toRadians(orbitAngle);
            int newX = owner.getX() + (int)(ORBIT_RADIUS * Math.cos(radians));
            int newY = owner.getY() + (int)(ORBIT_RADIUS * Math.sin(radians));
            setLocation(newX, newY);
            
            if (timer >= 180)
            {
                getWorld().removeObject(this);
                return;
            }
        }
        else if (mode.equals("BOOMERANG"))
        {
            setRotation(getRotation() + 15);
            
            if (!returning)
            {
                int curRot = getRotation();
                setRotation(boomerangAngle);
                move(12);
                setRotation(curRot);
                if (timer >= 25) returning = true;
            }
            else
            {
                Actor owner = getOrbitOwner();
                if (owner == null || owner.getWorld() == null)
                {
                    getWorld().removeObject(this);
                    return;
                }
                turnTowards(owner.getX(), owner.getY());
                move(14);
                if (getObjectsInRange(20, owner.getClass()).contains(owner))
                {
                    getWorld().removeObject(this);
                    return;
                }
            }
        }
        
        applyProximityDamage();
    }

    private Actor getOrbitOwner()
    {
        if (getWorld() == null) return null;
        if (!getWorld().getObjects(Maki.class).isEmpty()) return getWorld().getObjects(Maki.class).get(0);
        return null;
    }

    private void applyProximityDamage()
    {
        if (getWorld() == null) return;

        List<Fish> fishList = getObjectsInRange(18, Fish.class);
        for (Fish f : fishList)
        {
            if (f.getWorld() != null && !hitActors.contains(f)) { f.takeDamage(damage); hitActors.add(f); }
        }

        List<Pufferfish> puffers = getObjectsInRange(18, Pufferfish.class);
        for (Pufferfish p : puffers)
        {
            if (p.getWorld() != null && !hitActors.contains(p)) { p.takeDamage(damage); hitActors.add(p); }
        }

        List<Crab> crabs = getObjectsInRange(18, Crab.class);
        for (Crab c : crabs)
        {
            if (c.getWorld() != null && !hitActors.contains(c)) { c.takeDamage(damage); hitActors.add(c); }
        }

        List<Turtle> turtles = getObjectsInRange(22, Turtle.class);
        for (Turtle t : turtles)
        {
            if (t.getWorld() != null && !hitActors.contains(t)) { t.takeDamage(damage); hitActors.add(t); }
        }

        List<SwordfishBoss> bosses = getObjectsInRange(18, SwordfishBoss.class);
        for (SwordfishBoss b : bosses)
        {
            if (b.getWorld() != null && !hitActors.contains(b)) { b.takeDamage(damage); hitActors.add(b); }
        }

        List<Kraken> krakens = getObjectsInRange(18, Kraken.class);
        for (Kraken k : krakens)
        {
            if (k.getWorld() != null && !hitActors.contains(k)) { k.takeDamage(damage); hitActors.add(k); }
        }

        List<Dagon> dagons = getObjectsInRange(45, Dagon.class);
        for (Dagon d : dagons)
        {
            if (d.getWorld() != null && !hitActors.contains(d)) { d.takeDamage(damage); hitActors.add(d); }
        }
    }
}