package game;

import java.awt.*;
import main.*;
import java.util.ArrayList;
import java.util.List;

public class My
{
    Image image;   // 自機の画像
    int width = 50;   // 自機の幅
    int height = 50;   // 自機の高さ
    int x, y;   // 自機の位置
    int v = 10;   // 移動キーが押されたときの移動量
    Bullet bl;   // 弾
    public int life = 3;  // ライフ初期値
    private final int maxLife = 3; // ライフ上限
    private boolean invincible = false; // 無敵フラグ
    private long invincibleStartTime = 0; // 無敵開始時間
    private final int invincibleDuration = 1000; // 1秒
    boolean alive = true;
    Dimension size;
    public List<Item> itemStock = new ArrayList<>(); 
    public int selectedItem = 0; // 現在選択中のアイテム番号

       // コンストラクタ
    public My(Dimension size, MainPanel mp){
        
                    // 自機画像の読み込み
        image = mp.getToolkit().getImage("src/game/image/my.png");
                    // 自機の初期位置
        x  = size.width / 2 - width / 2;
        y  = size.height - height -10;
                    // Bullet クラスのオブジェクトを生成
        bl = new Bullet(size, this);

        
    }


    public boolean isInvincible() {
        return invincible;
    }

    public long getInvincibleStartTime() {
        return invincibleStartTime;
    }

    public void takeDamage(int dmg) {
        if (!invincible && alive) {
            life -= dmg;
            if (life <= 0) {
                life = 0;
                alive = false;
            }

            // 無敵開始
            invincible = true;
            invincibleStartTime = System.currentTimeMillis();
        }
    }

    // ライフを回復する
    public void heal(int hp) {
        life += hp;
        if (life > maxLife) life = maxLife; // 上限チェック
    }

    public void update() {
        if (invincible) {
            long elapsed = System.currentTimeMillis() - invincibleStartTime;
            if (elapsed >= invincibleDuration) {
                invincible = false;
            }
        }
    }

    public int getLife() {
        return life;
    }


    // アイテムを拾う
        public void pickItem(Item item) {
            if (itemStock.size() < 3) {  // ← 最大3個まで
                itemStock.add(item);
                item.active = false;
            }
        }

        // ←キーで選択を左へ
        public void selectPrevItem() {
        if (!itemStock.isEmpty()) {
            selectedItem = (selectedItem - 1 + itemStock.size()) % itemStock.size();
            }
        }

        // →キーで選択を右へ
        public void selectNextItem() {
        if (!itemStock.isEmpty()) {
            selectedItem = (selectedItem + 1) % itemStock.size();
            }
        }

        public int getSelectedItem() {
            return selectedItem;
        }

        // Zキーで使用
        public void useSelectedItem(GamePanel gp) {
            if (!itemStock.isEmpty()) {
                Item used = itemStock.get(selectedItem);
                used.applyEffect(this, gp);
                itemStock.remove(selectedItem);

                // アイテムを使った後、選択位置調整
                if (selectedItem >= itemStock.size() && selectedItem > 0) {
                    selectedItem--;
                }
            }
        }

        


        

        public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
        }

        public void movex(int dx) { //自機x軸計算
        final int SCREEN_WIDTH = 500;
        switch (x) {
            case 1280  -> {
                if(dx == -10){
                    x += dx;
                }else{
                    x += 0;
                }
            }
            case 0 -> {
                if(dx == 10){
                    x += dx;
                }else{
                    x += 0;
                }
            }
            default -> x += dx;
        }
        x += dx;
        // 画面端で止める
        if (x < 0) x = 0;
        if (x + width > SCREEN_WIDTH) x = SCREEN_WIDTH - width;
        
    }

    public void movey(int dy) { //自機y軸計算
        final int SCREEN_HEIGHT = 700; 
        switch (y) {
            case 900 -> {
                if(dy == -10){
                    y += dy;
                }else{
                    y += 0;
                }
            }
            case 0 -> {
                if(dy == 10){
                    y += dy;
                }else{
                    y += 0;
                }
            }
            default -> y += dy;
        }
        y += dy;
        // 画面端で止める
        if (y < 0) y = 0;
        if (y + height >SCREEN_HEIGHT) y = SCREEN_HEIGHT - height;
    }
    public void draw(Graphics g) {
        if (invincible) {
            long elapsed = System.currentTimeMillis() - invincibleStartTime;
            // 100msごとに点滅
            if ((elapsed / 100) % 2 == 0) {
                g.drawImage(image, x, y, width, height, null);
            }
        } else {
            g.drawImage(image, x, y, width, height, null);
        }
    }
}