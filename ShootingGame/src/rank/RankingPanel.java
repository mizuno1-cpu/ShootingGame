package rank;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import main.*;
import fonts.FontLoader; 

public class RankingPanel extends JPanel implements KeyListener {
    private Dimension size;
    private MainPanel mp;

    public RankingPanel(Dimension size1, MainPanel mp1) {
        size = size1;
        mp = mp1;

        setLayout(null);
        setBackground(Color.white);

        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<ScoreEntry> ranking = RankingManager.getInstance().getScores();
        g.setFont(FontLoader.crtFont.deriveFont(40f));

        Font f = FontLoader.crtFont.deriveFont(Font.BOLD, 40);
        FontMetrics fm = g.getFontMetrics(f);
        String str = "ランキング";
        int w = fm.stringWidth(str);
        g.setFont(f);
        g.setColor(Color.BLACK);
        g.drawString(str, size.width / 2 - w / 2, size.height / 5);

        if (ranking == null || ranking.isEmpty()) {
            g.drawString("ランキングデータがありません", size.width / 2 - 180, size.height / 2);
        } else {
            int i = 1;
            g.setFont(FontLoader.crtFont.deriveFont(Font.PLAIN, 28));
            for (ScoreEntry entry : ranking) {
                if (entry == null || entry.getName() == null) continue;
                String rank = i + "位  " + entry.getName() + "： " + entry.getScore();
                g.drawString(rank, size.width / 2 - 150, size.height / 3 + i * 40);
                i++;
            }
        }

        // メッセージ描画
        g.setFont(FontLoader.crtFont.deriveFont( Font.PLAIN, 24));
        g.setColor(Color.GRAY);
        g.drawString("Enterキーでタイトルに戻る", size.width / 2 - 160, size.height - 80);
    }

    // ====== KeyListener 実装 ======
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            mp.state = 0; // タイトル画面に戻る
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // フォーカス時にキー操作を受け付けるためのヘルパー
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
}