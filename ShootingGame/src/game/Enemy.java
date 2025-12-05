package game;

import java.awt.*;
import java.util.Random;
import main.*;

class Enemy implements Runnable
{
    Image image;   // 敵機の画像
    int width = 50;   // 敵機の幅
    int height = 50;   // 敵機の高さ
    int x, y;   // 敵機の位置
    int n;   // 敵機番号
    public int hp;
    Bullet_e bl;   // 弾
    Thread td;
    int v=10;
    int s_x,s_y;
    boolean in_game = true;
    Dimension size;
            // コンストラクタ
    public Enemy(int n1, Dimension size, Random rn, MainPanel mp)
    {
        n  = n1;
        this.size = size;
        this.hp = 5; //敵のHP量
                    // 敵機画像の読み込み
        image = mp.getToolkit().getImage("src/game/image/ene.png");
                    // 敵機の初期位置
        s_x=size.width;
        s_y=size.height;
        x=rn.nextInt(size.width - width);
        y= -height;

        td = new Thread(this);
        td.start();
                    // Bullet_e クラスのオブジェクトを生成
        bl = new Bullet_e(size, rn, this);
    }

    public void run(){
        while (in_game) {
            y += v;
            if (y > size.height) {
                in_game = false; // 画面外に出たら停止またはrespawnさせる
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }
    }
}