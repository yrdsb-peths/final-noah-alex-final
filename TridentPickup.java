import greenfoot.*;

public class TridentPickup extends Actor
{
    private boolean sliding = true;
    private int slideSpeed = 8;

    public TridentPickup()
    {
        GreenfootImage img = new GreenfootImage("trident.png");
        img.scale(30, 30);
        setImage(img);
    }

    public void act()
    {
        if (sliding)
        {
            move(slideSpeed);

            if (isAtEdge())
            {
                sliding = false;
            }
        }
        else
        {
            Hero hero = (Hero) getOneIntersectingObject(Hero.class);
            if (hero != null)
            {
                Trident t = new Trident();
                getWorld().addObject(t, hero.getX() + 15, hero.getY() + 15);
                t.setCarried(true);
                hero.pickUpTrident(t);
                getWorld().removeObject(this);
            }
        }
    }
}