import greenfoot.*;

public class MyWorld extends World {
    public MyWorld() {
        super(600, 400, 1);
        
        Hero al = new Hero();
        addObject(al, 300, 300);
    }
}
