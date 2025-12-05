package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import game.GamePanel;
import game.Item;
import fonts.FontLoader; 

public class InfoPanel extends JPanel {

    private GamePanel gp;  // GamePanelへの参照
    private Timer timer;   // 定期的に再描画するためのSwingタイマー

    public InfoPanel(GamePanel gp) {
        this.gp = gp;
        setBackground(Color.LIGHT_GRAY);

        // ★ 0.1秒ごとにInfoPanelを更新
        timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.setFont(FontLoader.crtFont.deriveFont(Font.BOLD, 18));

        // ====== GamePanelから情報を取得 ======
        int score = gp.score;     
        int time = gp.time;      
        int rate = gp.rate;         


        // タイムを秒単位に変換（1桁小数表示）
        double seconds = time / (double) rate;

        // ====== 情報表示 ======
        int y = 50;
        g.drawString("SCORE:", 20, y);
        g.drawString(String.valueOf(score), 120, y);

        y += 40;
        g.drawString("TIME:", 20, y);
        g.drawString(String.format("%.1f s", seconds), 120, y);

        y += 40;
        g.drawString("RATE:", 20, y);
        g.drawString(String.valueOf(rate), 120, y);

        // --- ライフ表示 ---
        if (gp.my != null) {
            g.drawString("LIFE:", 20, 170);
            g.setColor(Color.RED);
            int lifeCount = gp.my.life;
            for (int i = 0; i < lifeCount; i++) {
                g.fillOval(90 + i * 30, 150, 20, 20); // 横に並べて表示
            }
        }

         // --- アイテムストック表示 ---
        if (gp.my != null) {
            g.setColor(Color.WHITE);
            g.drawString("ITEM STOCK:", 20, 210);

            int x = 20;
            //int y = 200;

            // for文の中でiを定義する
            for (int i = 0; i < gp.my.itemStock.size(); i++) {
                Item item = gp.my.itemStock.get(i);
                g.setColor(item.getColor());
                g.fillOval(x, 230, 20, 20);

                // 選択中のアイテムを白枠で囲む
                if (i == gp.my.getSelectedItem()) {
                    g.setColor(Color.WHITE);
                    g.drawOval(x - 2, 228, 24, 24);
                }

                x += 30;
            }
        }

    }

    // ★ ゲーム終了時などにタイマーを止める（任意）
    public void stopTimer() {
        if (timer != null) timer.stop();
    }
}
