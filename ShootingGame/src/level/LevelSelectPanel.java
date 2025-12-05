package level;

import main.MainPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import fonts.FontLoader; 

public class LevelSelectPanel extends JPanel implements KeyListener {

    private MainPanel mp;
    private String[] levels = {"EASY", "NORMAL", "HARD"};
    private String[] descriptions = {
        "敵が少なく、初心者向け。",
        "標準的な難易度。",
        "敵が多く、弾幕が激しい上級者向け。"
    };

    private int selectedIndex = 0;

    public LevelSelectPanel(Dimension size, MainPanel mp) {
        this.mp = mp;
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.white);

        // ★ フォーカスを確実に取る
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int centerY = getHeight() / 2 - 50;

        // タイトル
        g2.setColor(Color.gray);
        g2.setFont(FontLoader.crtFont.deriveFont(Font.BOLD, 40));
        String title = "Select Difficulty";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (panelWidth - titleWidth) / 2, centerY - 80);

        // ボタン間隔とサイズ
        int buttonWidth = 180;
        int buttonHeight = 60;
        int spacing = 50;
        int totalWidth = (levels.length * buttonWidth) + ((levels.length - 1) * spacing);
        int startX = (panelWidth - totalWidth) / 2;

        // ボタン描画
        for (int i = 0; i < levels.length; i++) {
            int x = startX + i * (buttonWidth + spacing);
            int y = centerY;

            // 選択中のボタンをハイライト
            if (i == selectedIndex) {
                g2.setColor(Color.YELLOW);
                g2.fillRoundRect(x - 5, y - 5, buttonWidth + 10, buttonHeight + 10, 20, 20);
            }

            // ボタン本体
            g2.setColor(Color.GRAY);
            g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);

            // ボタン文字
            g2.setColor(Color.WHITE);
            g2.setFont(FontLoader.crtFont.deriveFont(Font.BOLD, 24));
            String levelText = levels[i];
            int textWidth = g2.getFontMetrics().stringWidth(levelText);
            g2.drawString(levelText, x + (buttonWidth - textWidth) / 2, y + 38);

            // 説明文
            g2.setFont(FontLoader.crtFont.deriveFont(Font.PLAIN, 14));
            String desc = descriptions[i];
            int descWidth = g2.getFontMetrics().stringWidth(desc);
            g2.setColor(Color.gray);
            g2.drawString(desc, x + (buttonWidth - descWidth) / 2, y + buttonHeight + 25);
        }

        // 操作ヒント
        g2.setFont(FontLoader.crtFont.deriveFont(Font.PLAIN, 16));
        g2.setColor(Color.GRAY);
        String hint = "ESCでタイトル / ← → で選択 / Enter で決定";
        int hintWidth = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (panelWidth - hintWidth) / 2, getHeight() - 50);
    }

    // --- キー操作 ---
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_RIGHT:
                selectedIndex = (selectedIndex + 1) % levels.length;
                repaint();
                break;
            case KeyEvent.VK_LEFT:
                selectedIndex = (selectedIndex - 1 + levels.length) % levels.length;
                repaint();
                break;
            case KeyEvent.VK_ENTER:
                switch (selectedIndex) {
                    case 0 -> mp.level = 0; //Easy
                    case 1 -> mp.level = 1; //Normal
                    case 2 -> mp.level = 2; //Hard

                }
                mp.state = 2;   // ゲーム画面へ
                break;
            case KeyEvent.VK_ESCAPE:
                mp.state = 0;
                break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}