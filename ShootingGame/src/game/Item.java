package game;

import java.awt.*;

public class Item {
    int x, y;
    int size = 20;
    int type;  // 0:スコア, 1:パワー, 2:回復
    boolean active = true;
    private Color color;

    
    public Item(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        switch (type) {
            case 0 -> color = Color.YELLOW; // スコア
            case 1 -> color = Color.RED;    //
            case 2 -> color = Color.GREEN;  // 回復
            default -> color = Color.WHITE;
        }
    }

    public void move() {
        y += 3;  // 下方向に落ちる
        if (y > 800) active = false;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, size, size);
    }

    public void applyEffect(My my, GamePanel gp) {
        switch (type) {
            case 0 -> gp.score += 500;     // スコア加算
            case 1 -> gp.score += 100;
            case 2 -> my.heal(1);        // 
        }
        active = false;
    }

    

    public Rectangle getRect() {
        return new Rectangle(x, y, size, size);
    }

    public Color getColor() {
        return color;
    }
}