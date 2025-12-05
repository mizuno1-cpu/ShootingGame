package clear;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import main.*;
import rank.RankingManager;
import rank.ScoreEntry;
import fonts.FontLoader; 

public class GameClearPanel extends JPanel implements KeyListener {
    Dimension size;
    MainPanel mp;
    private JTextField nameField;

    private final String[] menuItems = {"リトライ", "タイトルへ戻る", "終了"};
    private int selectedIndex = 0;

    public  GameClearPanel(Dimension size1, MainPanel mp1) {
        size = size1;
        mp = mp1;

        setLayout(null);
        setBackground(Color.white);
        setFocusable(true);
        addKeyListener(this);

        // 名前入力ラベル
        JLabel nameLabel = new JLabel("名前を入力してください:");
        nameLabel.setBounds(size.width / 2 - 140, size.height / 2 + 20, 280, 30);
        nameLabel.setFont(FontLoader.crtFont.deriveFont(Font.PLAIN, 18));
        add(nameLabel);

        // 名前入力欄
        nameField = new JTextField();
        nameField.setBounds(size.width / 2 - 140, size.height / 2 + 60, 280, 30);
        add(nameField);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // --- タイトル ---
        Font f = FontLoader.crtFont.deriveFont(Font.BOLD, 40);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        String title = "Game Clear!";
        g.setColor(Color.BLACK);
        g.drawString(title, size.width / 2 - fm.stringWidth(title) / 2, size.height / 2 - 60);

        // --- スコア表示 ---
        String str = "SCORE：" + mp.score;
        g.setFont(FontLoader.crtFont.deriveFont(Font.BOLD, 24));
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(str, size.width / 2 - fm2.stringWidth(str) / 2, size.height / 2);

        // --- メニュー描画 ---
        g.setFont(FontLoader.crtFont.deriveFont(Font.BOLD, 22));
        for (int i = 0; i < menuItems.length; i++) {
            if (i == selectedIndex) {
                g.setColor(Color.RED);
                g.drawString("▶ " + menuItems[i], size.width / 2 - 70, size.height - 230 + i * 40);
            } else {
                g.setColor(Color.BLACK);
                g.drawString(menuItems[i], size.width / 2 - 80, size.height - 230 + i * 40);
            }
        }

        // --- 操作説明 ---
        g.setColor(Color.GRAY);
        g.setFont(FontLoader.crtFont.deriveFont( Font.PLAIN, 18));
        g.drawString("↑↓キーで選択 / tabで名前入力切替/  Enterで決定", size.width / 2 - 200, size.height - 50);
    }

    // ========================================================
    // キー入力処理
    // ========================================================
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // ↑キー
        if (code == KeyEvent.VK_UP) {
            selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
            repaint();
        }

        // ↓キー
        else if (code == KeyEvent.VK_DOWN) {
            selectedIndex = (selectedIndex + 1) % menuItems.length;
            repaint();
        }

        // Enterキー
        else if (code == KeyEvent.VK_ENTER) {
            String name = nameField.getText().trim();
            int finalScore = mp.score;

            if (!name.isEmpty() && name.length() <= 6) {
                RankingManager.getInstance().addScore(new ScoreEntry(name, finalScore));

                switch (selectedIndex) {
                    case 0 -> mp.state = 2;  // リトライ
                    case 1 -> mp.state = 0;  // タイトルに戻る
                    case 2 -> mp.state = 6;  // 終了
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    name.isEmpty() ? "名前を入力してください" : "6文字以内で入力してください");
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // 画面遷移後にフォーカスを確保
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
}
