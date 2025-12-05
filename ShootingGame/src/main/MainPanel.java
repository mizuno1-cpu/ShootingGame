
package main;

import clear.*;
import game.*;
import java.awt.*;
import java.util.logging.Level;

import javax.swing.*;
import over.*;
import rank.*;
import start.*;
import level.*;

public class MainPanel extends JPanel implements Runnable
{
    Dimension size;   // パネル全体の大きさ
    boolean in_game = true;
    public int state = 0;   // 0:タイトル 1:ゲーム 2:クリア 3:オーバー 4:ランキング 5:終了
    public int level = 2;
    int old_state = 0;
    public int score = 0;

    StartPanel sp;
    GamePanel gp;
    GameClearPanel gcp;
    GameOverPanel gop;
    RankingPanel rp;
	LevelSelectPanel sl;
    InfoPanel info;

    Thread td;
    java.util.List<ScoreEntry> rankingScores = RankingManager.getInstance().getScores();

    // コンストラクタ
    public MainPanel(Dimension size1)
    {
        this.size = size1;

        // ★ ここがポイント：BorderLayoutで左右分割できるようにする
        setLayout(new BorderLayout());

        // 初期状態（タイトル画面）
        sp = new StartPanel(size, this);
        add(sp, BorderLayout.CENTER);

        td = new Thread(this);
        td.start();
    }

    public void run()
    {
        while (in_game) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}

            if (state != old_state) {
                removeAll();

                switch (state) {
                    case 0: // タイトル
                        sp = new StartPanel(size, this);
                        add(sp, BorderLayout.CENTER);
                        break;
					
					case 1: // 難易度選択
                        sl = new LevelSelectPanel(size, this);
                        add(sl, BorderLayout.CENTER);
                        break;

                    case 2: // ゲーム画面easy
                        setupGameLayout();
                        break;

                    case 3: // クリア
                        gcp = new GameClearPanel(size, this);
                        add(gcp, BorderLayout.CENTER);
                        break;

                    case 4: // ゲームオーバー
                        gop = new GameOverPanel(size, this);
                        add(gop, BorderLayout.CENTER);
                        break;

                    case 5: // ランキング
                        rp = new RankingPanel(size, this);
                        add(rp, BorderLayout.CENTER);
                        break;

                    case 6: // 終了
                        in_game = false;
                        System.exit(0);
                        break;
                    /*case 7: // ゲーム画面normal
                        setupGameLayout();
                        break;
                    case 8: // ゲーム画面hard
                        setupGameLayout();
                        break;*/
                }

                validate();
                repaint();
                old_state = state;
            }
        }
    }

	public void setPanel() {
    removeAll();

    switch (state) {
        case 0 -> {
            sp = new StartPanel(size, this);
            add(sp, BorderLayout.CENTER);
        }
        case 1 -> {
            sl = new LevelSelectPanel(size, this);
            add(sl, BorderLayout.CENTER);
        }
        case 2 -> setupGameLayout();
        case 3 -> {
            gcp = new GameClearPanel(size, this);
            add(gcp, BorderLayout.CENTER);
        }
        case 4 -> {
            gop = new GameOverPanel(size, this);
            add(gop, BorderLayout.CENTER);
        }
        case 5 -> {
            rp = new RankingPanel(size, this);
            add(rp, BorderLayout.CENTER);
        }
        /*case 7 -> setupGameLayout();
        case 8 -> setupGameLayout();*/
    }

    revalidate();
    repaint();
}

    /**
     * ゲームプレイ中の左右レイアウトを構築
     */
    private void setupGameLayout() {
        removeAll();
        int infoWidth = 200; // InfoPanelの幅（右側固定）

        // 左：GamePanel（ゲーム本体）
        Dimension gameSize = new Dimension(size.width - infoWidth, size.height);
        //gp = new GamePanel(gameSize, this);
        //ゲーム難易度分岐
        switch (level) {
            case 0 -> gp = new EasyGamePanel(gameSize,this);
            case 1 -> gp = new NormalGamePanel(gameSize, this);
            case 2 -> gp = new HardGamePanel(gameSize, this);
            default -> gp = new NormalGamePanel(gameSize, this);
        }
        // 右：InfoPanel（スコア・タイムなど）
        Dimension infoSize = new Dimension(infoWidth, size.height);
        info = new InfoPanel(gp);
        info.setPreferredSize(infoSize);

        // BorderLayoutで左右分割
        add(gp, BorderLayout.CENTER);
        add(info, BorderLayout.EAST);
        
            
        }
    }


