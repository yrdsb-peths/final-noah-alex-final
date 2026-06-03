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

        GreenfootImage img = new GreenfootImage("poison-cloud.png");
        img.scale(35, 35);
        setImage(img);
    }

    public void act()
    {
        timer++;

        if (mode.equals("ORBIT"))
        {
            handleOrbit();
        }
        else if (mode.equals("BOOMERANG"))
        {
            handleBoomerang();
        }
        else if (mode.equals("CURVE"))
        {
            handleCurve();
        }

        if (getWorld() != null)
        {
            dealDamage();
        }
    }

    private void handleOrbit()
    {
        List<Maki> makis = getWorld().getObjects(Maki.class);
        if (!makis.isEmpty())
        {
            Maki m = makis.get(0);
            ownerX = m.getX();
            ownerY = m.getY();
        }

        orbitAngle += ORBIT_SPEED_DEG;
        double rad = Math.toRadians(orbitAngle);
        int x = ownerX + (int)(Math.cos(rad) * ORBIT_RADIUS);
        int y = ownerY + (int)(Math.sin(rad) * ORBIT_RADIUS);
        setLocation(x, y);

        if (orbitAngle >= 360)
        {
            getWorld().removeObject(this);
            return;
        }
    }

    private void handleBoomerang()
    {
        if (!returning)
        {
            // SHOOT STRAIGHT: Extends straight along its exact targeted axis
            setRotation(boomerangAngle);
            move(10);

            if (timer > 30) { returning = true; hitActors.clear(); }
            if (isAtEdge()) { returning = true; hitActors.clear(); }
        }
        else
        {
            List<Maki> makis = getWorld().getObjects(Maki.class);
            if (makis.isEmpty())
            {
                getWorld().removeObject(this);
                return;
            }

            // RETURN STRAIGHT: Tracks straight backward directly to Maki's position
            Maki m = makis.get(0);
            turnTowards(m.getX(), m.getY());
            move(12);

            int dx = Math.abs(getX() - m.getX());
            int dy = Math.abs(getY() - m.getY());
            if (dx < 20 && dy < 20)
            {
                getWorld().removeObject(this);
                return;
            }
        }
    }

    private void handleCurve()
    {
        curveAngle += curveTurn;
        setRotation(curveAngle);
        move(12);

        if (isAtEdge() || timer > 80)
        {
            getWorld().removeObject(this);
            return;
        }
    }

    private void dealDamage()
    {
        if (getWorld() == null) return;

        List<Crab> crabs = getObjectsInRange(18, Crab.class);
        for (Crab c : crabs)
        {
            if (c.getWorld() != null && !hitActors.contains(c)) 
            { 
                c.takeDamage(damage); 
                hitActors.add(c); 
            }
        }

        List<Fish> fish = getObjectsInRange(18, Fish.class);
        for (Fish f : fish)
        {
            if (!hitActors.contains(f)) { f.takeDamage(damage); hitActors.add(f); }
        }

        List<Pufferfish> puffers = getObjectsInRange(18, Pufferfish.class);
        for (Pufferfish p : puffers)
        {
            if (!hitActors.contains(p)) { p.takeDamage(damage); hitActors.add(p); }
        }

        List<SwordfishBoss> bosses = getObjectsInRange(18, SwordfishBoss.class);
        for (SwordfishBoss b : bosses)
        {
            if (!hitActors.contains(b)) { b.takeDamage(damage); hitActors.add(b); }
        }

        List<Kraken> krakens = getObjectsInRange(18, Kraken.class);
        for (Kraken k : krakens)
        {
            if (!hitActors.contains(k)) { k.takeDamage(damage); hitActors.add(k); }
        }
    }
}