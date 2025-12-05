package game;

import java.awt.*;

class Bullet implements Runnable
{
    int width = 24;   // 弾の幅
    int no = 300;   // 弾の全数
    int no_1 = 2;   // 一度に撃てる弾数
    int ct = 0;   // 現在の弾の数
    int x[], y[];   // 弾の位置
    int v = 30;   // 弾の速さ
    boolean fire = false;
    boolean ex[];   // 弾の存在
    boolean in_game = true;
    Thread td;
    Dimension size;
    My my;
            // コンストラクタ
    public Bullet(Dimension size1, My my1)
    {
        size = size1;
        my   = my1;
                    // 初期設定
        x  = new int [no];
        y  = new int [no];
        ex = new boolean [no];
        for (int i1 = 0; i1 < no; i1++)
            ex[i1] = false;
                    // スレッドの生成
        td = new Thread(this);
        td.start();
    }
            // スレッドの実行
    public void run()
    {
        while (in_game) {
            try {
                td.sleep(16);
            }
            catch (InterruptedException e) {}
                    // 弾の移動
            for (int i1 = 0; i1 < no; i1++) {
                if (ex[i1]) {
                    y[i1] -= v;
                    if (y[i1] < -width)
                        ex[i1] = false;
                }
            }
                    // 次の弾の発射
            if (fire) {
                if (ct < no_1) {
                    boolean sw = true;
                    for (int i1 = 0; i1 < no && sw; i1++) {
                        if (!ex[i1]) {
                            x[i1]  = my.x + my.width / 2 - width / 2;
                            y[i1]  = my.y - width;
                            ex[i1] = true;
                            sw     = false;
                        }
                    }
                    ct++;
                    if (ct >= no_1) {
                        fire = false;
                        ct   = 0;
                    }
                }
            }
        }
    }
            // 最初の弾の発射
    void shoot()
    {
        if (!fire) {
            fire = true;
            ct   = 1;
            boolean sw = true;
            for (int i1 = 0; i1 < no && sw; i1++) {
                if (!ex[i1]) {
                    x[i1]  = my.x + my.width / 2 - width / 2;
                    y[i1]  = my.y - width;
                    ex[i1] = true;
                    sw     = false;
                }
            }
        }
    }
}
