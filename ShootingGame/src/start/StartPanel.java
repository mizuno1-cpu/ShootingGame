package start;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import fonts.FontLoader;   
import main.MainPanel;

public class StartPanel extends JPanel implements KeyListener {

    private Dimension size;
    private MainPanel mp;

    // メニュー項目
    private final String[] menuItems = {"RULE", "GAME START", "RANKING", "EXIT"};
    private int selectedIndex = 0;

    public StartPanel(Dimension size1, MainPanel mp1) {
        size = size1;
        mp = mp1;
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ===== タイトルフォント =====
        Font titleFont = FontLoader.crtFont.deriveFont(Font.BOLD, 40f);
        g.setFont(titleFont);

        String title = "ShootingGame";
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(title);

        g.setColor(Color.BLACK);
        g.drawString(title, size.width / 2 - w / 2, size.height / 3);

        // ===== メニューフォント =====
        g.setFont(FontLoader.crtFont.deriveFont(40f));
        fm = g.getFontMetrics();
        int baseY = size.height / 2;

        for (int i = 0; i < menuItems.length; i++) {

            g.setColor(i == selectedIndex ? Color.ORANGE : Color.GRAY);

            String item = menuItems[i];
            int textWidth = fm.stringWidth(item);

            g.drawString(item, size.width / 2 - textWidth / 2, baseY + i * 60);
        }
    }

    // ======== キー入力処理 ========
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_UP -> {
                selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
                repaint();
            }
            case KeyEvent.VK_DOWN -> {
                selectedIndex = (selectedIndex + 1) % menuItems.length;
                repaint();
            }
            case KeyEvent.VK_ENTER -> {
                handleSelect();
            }
        }
    }

    private void handleSelect() {
        switch (selectedIndex) {
            case 0 -> { // 遊び方
                Method db = new Method();
                db.setVisible(true);
                requestFocusInWindow();
            }
            case 1 -> mp.state = 1; // 難易度選択
            case 2 -> mp.state = 5; // ランキング
            case 3 -> mp.state = 6; // 終了
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
}

/******************/
/* ゲームの遊び方 */
/******************/
class Method extends JDialog {

    Method() {
        setTitle("ゲームの遊び方");

        Container cp = getContentPane();
        cp.setLayout(new FlowLayout(FlowLayout.CENTER));
        cp.setBackground(new Color(220, 255, 220));

        Font f = new Font("ＭＳ 明朝", Font.PLAIN, 20);
        setSize(550, 240);

        JTextArea ta = new JTextArea(8, 50);
        ta.setFont(f);
        ta.setEditable(false);
        ta.setLineWrap(true);

        ta.setText("・ゲーム開始： 画面上でダブルクリック\n");
        ta.append("・自機の移動： 「↑」，「↓」，「←」，「→」キーで上下左右に移動\n");
        ta.append("・弾の発射： 「スペース」キー\n");
        ta.append("・20秒間生き残ればゲームクリア\n");
        ta.append("・20秒間生き残れば200ポイント\n");
        ta.append("・敵に弾を当てると100ポイント\n");
        ta.append("・敵の弾に当たるとゲームオーバー\n");

        JScrollPane scroll = new JScrollPane(ta);
        cp.add(scroll);

        addWindowListener(new WinEnd());
    }

    class WinEnd extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            setVisible(false);
        }
    }
}