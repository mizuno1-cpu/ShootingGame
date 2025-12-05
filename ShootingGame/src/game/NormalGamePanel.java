package game;

import java.awt.*;
import main.MainPanel;

public class NormalGamePanel extends GamePanel {

    public NormalGamePanel(Dimension size, MainPanel mp) {
        super(size, mp);
        System.out.println("NormalGamePanel");
    }

    @Override
    public void run() {
        while (in_game) {
            try { Thread.sleep(60); } catch (InterruptedException e) {}
            time++;

            if (time % 30 == 0 && enn < ENEMY_MAX) spawnEnemy();
            if (my != null) my.update();

            checkHit(); 

            // ===== アイテムの移動と取得処理 =====
            for (int i = 0; i < items.size(); i++) {
                for (Item item : items) {
                    item.move();

                    // 自機との当たり判定
                    if (item.active && my.getRect().intersects(item.getRect())) {
                         my.pickItem(item);   // ストックに追加するだけ
                        item.active = false; // フィールドから消す
                    }
                }
                items.removeIf(it -> !it.active);
            }

            // ★クリア判定追加
            if (time >= limit * rate) {
                score += 1000;
                in_game = false;
                mp.score = score;
                mp.state = 3; // ゲームクリア画面
            }

            repaint();
        }
    }

    @Override
    protected void spawnEnemy() {
        em[enn] = new Enemy(enn, size, rn, mp);
        em[enn].v = 8; // ゆっくり動く
        ex[enn] = true;
        enn++;
    }

        // ====================================================
    // 当たり判定処理
    // ====================================================
    private void checkHit() {
        boolean hit = false;

        // --- 自機弾 vs 敵 ---
        for (int i1 = 0; i1 < ENEMY_MAX && !hit; i1++) {
            if (ex[i1]) {
                if (em[i1].y > size.height + em[i1].height) {
                    ex[i1] = false;
                    em[i1].bl.in_game = false;
                }
                for (int i2 = 0; i2 < my.bl.no && !hit; i2++) {
                    if (my.bl.ex[i2]) {
                        int xb = my.bl.x[i2] + my.bl.width / 2;
                        int yb = my.bl.y[i2] + my.bl.width / 2;
                        int xt = em[i1].x + em[i1].width / 2;
                        int yt = em[i1].y + em[i1].height / 2;
                        int w = em[i1].width / 2 + my.bl.width / 2;
                        int h = em[i1].height / 2 + my.bl.width / 2;

                        if (xb > xt - w && xb < xt + w && yb > yt - h && yb < yt + h) {
                            // HPを減らす
                            em[i1].hp--;

                            if (em[i1].hp <= 0) {
                                // 敵撃破！
                                hit = true;
                                ex[i1] = false;
                                my.bl.ex[i2] = false;
                                em[i1].bl.in_game = false;
                                score += 100;

                                if (rn.nextDouble() < 0.3) {
                                int type = rn.nextInt(3);
                                items.add(new Item(em[i1].x, em[i1].y, type));
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- ゲームループ内 ---
        my.update(); // 無敵時間管理

        // --- 敵弾 vs 自機 ---
        for (int i1 = 0; i1 < ENEMY_MAX && my.alive; i1++) {
            if (ex[i1]) {
                for (int i2 = 0; i2 < em[i1].bl.no; i2++) {
                    if (em[i1].bl.ex[i2]) {
                        Rectangle playerRect = my.getRect();
                        Rectangle bulletRect = new Rectangle(em[i1].bl.x[i2], em[i1].bl.y[i2], em[i1].bl.width, em[i1].bl.width);

                        if (!my.isInvincible() && playerRect.intersects(bulletRect)) {
                            em[i1].bl.ex[i2] = false; // 弾を消す
                            my.takeDamage(1);         // ライフを1減らす
                            if (!my.alive) {
                                in_game = false;
                                mp.state = 4;         // ゲームオーバー
                                mp.score = this.score; 
                            }
                        }
                    }
                }
            }
        }
    }}