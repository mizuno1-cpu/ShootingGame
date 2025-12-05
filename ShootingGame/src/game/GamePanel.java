package game;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import main.*;
import fonts.FontLoader; 

public class GamePanel extends JPanel implements Runnable {

    // === 基本設定 ===
    public Dimension size;  // 表示可能領域（MainPanelから渡される）
    public MainPanel mp;    // MainPanel参照
    private Image backgroundImage; // 背景画像を保持
    public My my;           // 自機
    protected int enemySpawnRate = 120;
    protected int enemySpeed = 4;
    protected int bulletSpeed = 6;

    // === 敵関連 ===
    public static final int ENEMY_MAX = 100;
    public Enemy[] em = new Enemy[ENEMY_MAX];
    public boolean[] ex = new boolean[ENEMY_MAX];
    public int enn = 0;

    // === ゲーム管理 ===
    public boolean in_game = true;
    public int wait = 50;           // 1ループの待機ms
    public int rate = 1000 / wait;  // 秒換算レート
    public int limit = 20;         // ゲーム時間制限（秒）
    public int time = 0;            // 経過フレーム数
    public int score = 0;           // スコア
    private Thread td;              // ゲームループスレッド
    protected Random rn = new Random();

    // === 入力処理 ===
    private Set<Integer> pressedKeys = new HashSet<>();
    javax.swing.Timer moveTimer;        // 移動用タイマー
    javax.swing.Timer fireTimer;        // 自動射撃タイマー

    ArrayList<Item> items = new ArrayList<>();

    // ====================================================
    // コンストラクタ
    // ====================================================
    public GamePanel(Dimension playSize, MainPanel mp1) {
        this.size = playSize;
        this.mp = mp1;
        //resetGame();

        // 背景画像の読み込み
        try {
            backgroundImage = new ImageIcon(
                getClass().getResource("/game/image/back1.png")
            ).getImage();
            System.out.println("背景ロード成功");
        } catch (Exception e) {
            System.out.println("背景ロード失敗: " + e.getMessage());
        }
        
        setLayout(null);
        setBackground(Color.WHITE);

        // 自機の生成（InfoPanel領域を除いた範囲で生成）
        my = new My(size, mp);

        // スレッド起動
        td = new Thread(this);
        td.start();

        // --- キーリスナ ---
        GamePanel self = this; // 自分自身を変数に入れておく
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                int key = e.getKeyCode();
                if (my != null) {
                    switch (key) {
                        case KeyEvent.VK_LEFT -> my.selectPrevItem();
                        case KeyEvent.VK_RIGHT -> my.selectNextItem();
                        case KeyEvent.VK_ENTER -> my.useSelectedItem(self);
                    }
                }
            }
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
            
        });
        setFocusable(true);
        requestFocusInWindow();

        // --- 自機移動（16msごと） ---
        moveTimer = new javax.swing.Timer(16, e -> {
            int speed = pressedKeys.contains(KeyEvent.VK_SHIFT) ? 1 : 5;
            if (pressedKeys.contains(KeyEvent.VK_W)) my.movey(-speed);
            if (pressedKeys.contains(KeyEvent.VK_S)) my.movey(speed);
            if (pressedKeys.contains(KeyEvent.VK_A)) my.movex(-speed);
            if (pressedKeys.contains(KeyEvent.VK_D)) my.movex(speed);
            repaint();
        });
        moveTimer.start();

        // --- 自動連射（100msごと） ---
        fireTimer = new javax.swing.Timer(100, e -> {
            if (pressedKeys.contains(KeyEvent.VK_SPACE)) {
                my.bl.shoot();
            }
        });
        fireTimer.start();

        
    }

    // ====================================================
    // ゲームループ
    // ====================================================
    public void run() {
        while (in_game) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {}

            time++;

            // ① 自機の状態更新（無敵時間など）
            if (my != null) {
                my.update();
            }

            // ===== 敵のスポーン =====
            if (time >= 40 + (int)(20 * (1.5 * enn)) && enn < ENEMY_MAX) {
                em[enn] = new Enemy(enn, size, rn, mp);
                ex[enn] = true;
                
                enn++;
            }

            // ===== 当たり判定 =====
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

            // ===== ゲーム終了判定 =====
            if (time >= limit * rate) {
                score += 200;
                in_game = false;
                mp.score = score;
                mp.state = 3; // ゲームクリア画面
            }

            // ===== 再描画 =====
            repaint();
        }
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

        
    }

    // ====================================================
    // 描画処理
    // ====================================================
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 背景を描画
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        // --- 自機の描画 ---
        boolean drawPlayer = true;
        if (my.isInvincible()) {
            // 無敵時間中は100msごとに点滅
            long elapsed = System.currentTimeMillis() - my.getInvincibleStartTime();
            drawPlayer = (elapsed / 100) % 2 == 0;
        }

        if (drawPlayer) {
            g.drawImage(my.image, my.x, my.y, this);
        }

        // --- 自機弾描画 ---
        g.setColor(Color.GREEN);
        for (int i1 = 0; i1 < my.bl.no; i1++) {
            if (my.bl.ex[i1])
                g.fillOval(my.bl.x[i1], my.bl.y[i1], my.bl.width, my.bl.width);
        }

        // --- 敵と敵弾描画 ---
        g.setColor(Color.RED);
        for (int i1 = 0; i1 < ENEMY_MAX; i1++) {
            if (ex[i1]) {
                g.drawImage(em[i1].image, em[i1].x, em[i1].y, this);
                for (int i2 = 0; i2 < em[i1].bl.no; i2++) {
                    if (em[i1].bl.ex[i2])
                        g.fillOval(em[i1].bl.x[i2], em[i1].bl.y[i2], em[i1].bl.width, em[i1].bl.width);
                }
            }
        }

        // アイテム描画
        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
        Item item = it.next();
        item.draw(g);
        if (!item.active) {
            it.remove(); // ここで安全に削除
        }
    }

    

    
    // 弾・アイテム・スコア・時間など、前回の状態を初期化
    in_game = true;

        // --- フォーカスを確保 ---
        requestFocusInWindow();
    }

    protected void spawnEnemy() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'spawnEnemy'");
    }

    protected void updateGame() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }
}