package game;

import java.awt.*;
import java.util.Random;

class Bullet_e implements Runnable
{
    int width = 14;   // 弾の幅
    int no = 5;   // 弾の全数
    int ct;   // 現在の弾の数
    int x[], y[];   // 弾の位置
    int v = 30;   // 弾の速さ
    int vx, vy;   // 横及び縦方向の弾の速さ
    boolean ex[];   // 弾の存在
    boolean in_game = true;
    Thread td;
    Dimension size;
    Random rn;
    Enemy em;

    int startX, startY;
            // コンストラクタ
    public Bullet_e(Dimension size1, Random rn1, Enemy em1)
    {
        size = size1;
        rn   = rn1;
        em   = em1;
                    // 初期設定と最初の弾の発射
        x  = new int [no];
        y  = new int [no];
        ex = new boolean [no];
        shoot();
                    // スレッドの生成
        td = new Thread(this);
        td.start();
    }
            // スレッドの実行
    public void run()
    {
        while (in_game) {
            try {
                td.sleep(50);
            }
            catch (InterruptedException e) {}
                    // 弾の数
            if (ct < no)
                ct++;
                    // 弾の移動
            boolean sw = false;
            for (int i1 = 0; i1 < no; i1++) {
                if (ex[i1]) {
                    x[i1] += vx;
                    y[i1] += vy;
                    if (x[i1] < -width || x[i1] > size.width || y[i1] > size.height)
                        ex[i1] = false;
                    else
                        sw = true;
                }
            }
                    // 最初の弾の発射
            if (!sw)
                shoot();
                    // 次の弾の発射
            else {
                if (ct < no) {
                    x[ct]  = em.x + em.width / 2 - width / 2;
                    y[ct]  = em.y + em.height;
                    ex[ct] = true;
                }
            }
        }
    }
            // 最初の弾の発射
    void shoot()
    {
        ct    = 0;
        ex[0] = true;
        for (int i1 = 1; i1 < no; i1++)
            ex[i1] = false;
        double ang = 0.25 * Math.PI + 0.5 * Math.PI * rn.nextDouble();
        vx   = (int)(v * Math.cos(ang) + 0.5);
        vy   = (int)(v * Math.sin(ang) + 0.5);
        x[0] = em.x + em.width / 2 - width / 2;
        y[0] = em.y + em.height;
    }
}