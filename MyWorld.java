import greenfoot.*;
import java.util.List;

public class MyWorld extends World {
    private int score = 0;
    private boolean bossSpawned = false;
    private boolean bossDefeated = false;
    private int nemoKillsAfterBoss = 0;
    private boolean pufferWaveSpawned = false;
    private int spawnTimer = 0;
    private int nemoSpawnCount = 0;
    private Label scoreLabel;

    // Scrolling
    private int bgOffsetX = 0;
    private GreenfootImage bgImage;
    private static final int BG_WIDTH = 600; // same as world, tiles seamlessly
    private static final int BG_HEIGHT = 400;

    public static final int FLOOR_Y = 380; // hero stands here
    public static final int GRAVITY = 1;

    public MyWorld() {
        super(600, 400, 1);

        bgImage = new GreenfootImage("background.png");
        bgImage.scale(BG_WIDTH, BG_HEIGHT);
        drawBackground();

        Hero al = new Hero();
        addObject(al, 300, FLOOR_Y);

        HpBar bar = new HpBar();
        addObject(bar, 90, 370);
        al.setHpBar(bar);

        scoreLabel = new Label("Score: 0", 30);
        scoreLabel.setLineColor(Color.WHITE);
        addObject(scoreLabel, 80, 30);

        spawnFish();
    }

    public void scrollWorld(int dx)
    {
        bgOffsetX -= dx;
        drawBackground();

        List<Actor> allActors = getObjects(Actor.class);
        for (Actor a : allActors)
        {
            if (!(a instanceof Hero) && !(a instanceof HpBar) && !(a instanceof Label))
            {
                a.setLocation(a.getX() - dx, a.getY());
            }
        }
    }

    private void drawBackground()
    {
        GreenfootImage screen = new GreenfootImage(600, 400);

        // Wrap offset so background tiles seamlessly
        int ox = ((bgOffsetX % BG_WIDTH) + BG_WIDTH) % BG_WIDTH;

        screen.drawImage(bgImage, -ox, 0);
        screen.drawImage(bgImage, -ox + BG_WIDTH, 0);

        setBackground(screen);
    }

    public void act()
    {
        int fishCount = getObjects(Fish.class).size();

        if (!bossSpawned && !bossDefeated)
        {
            if (fishCount < 2) spawnFish();
        }

        if (bossDefeated && !pufferWaveSpawned)
        {
            spawnTimer++;
            if (spawnTimer % 60 == 0 && nemoSpawnCount < 4)
            {
                spawnFish();
                nemoSpawnCount++;
                if (nemoSpawnCount == 1) spawnPufferfish();
            }
            if (nemoSpawnCount >= 4) pufferWaveSpawned = true;
        }
    }

    public void increaseScore()
    {
        score++;
        scoreLabel.setValue("Score: " + score);
        if (score >= 5 && !bossSpawned)
        {
            bossSpawned = true;
            spawnBoss();
        }
    }

    public void notifyBossDefeated() { bossDefeated = true; }

    public void notifyNemoKilled()
    {
        if (bossDefeated && !pufferWaveSpawned) return;
        if (bossDefeated) nemoKillsAfterBoss++;
    }

    public void notifyPufferKilled() {}

    private void spawnFish()
{
    Fish enemy = new Fish();
    int x, y;
    int edge = Greenfoot.getRandomNumber(4);

    switch (edge)
    {
        case 0: // top
            x = Greenfoot.getRandomNumber(getWidth());
            y = 0;
            break;
        case 1: // bottom
            x = Greenfoot.getRandomNumber(getWidth());
            y = getHeight() - 1;
            break;
        case 2: // left
            x = 0;
            y = Greenfoot.getRandomNumber(getHeight());
            break;
        default: // right
            x = getWidth() - 1;
            y = Greenfoot.getRandomNumber(getHeight());
            break;
    }

    addObject(enemy, x, y);
}

    private void spawnBoss()
    {
        SwordfishBoss boss = new SwordfishBoss();
        addObject(boss, 300, FLOOR_Y);
    }

    private void spawnPufferfish()
    {
        Pufferfish puffer = new Pufferfish();
        int randomX = Greenfoot.getRandomNumber(getWidth());
        addObject(puffer, randomX, FLOOR_Y);
    }
}