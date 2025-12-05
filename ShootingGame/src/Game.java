
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import main.*;

public class Game {
	public static void main (String[] args)
	{
		Win win = new Win("シューティングゲーム");
	}
}

class Win extends JFrame
{
	/******************/
	/* コンストラクタ */
	/******************/
	Win(String name)
	{
					// JFrameクラスのコンストラクタ（Windowのタイトルを引き渡す）
		super(name);
					// Windowの大きさ
		setSize(600+160, 800);   // 40+20, 70+20
					// MainPanel の大きさを決定
		Dimension size = getSize();
		size.width  -=60;
		size.height -=90;
		
					// ContentPain を取得し，設定
		Container CP = getContentPane();   // ContentPane を取得
		CP.setLayout(null);   // レイアウトマネージャを停止
		CP.setBackground(new Color(220, 255, 220));   // 背景色
					// MainPanel を追加し，設定
		MainPanel pn = new MainPanel(size);   // MainPanel オブジェクトの生成
		CP.add(pn);   // MainPanel オブジェクトを ContentPane に追加
		pn.setSize(size.width, size.height);
		pn.setLocation(10, 10);
		setResizable(false);
					// ウィンドウを表示
		setVisible(true);
					// イベントリスナ
		addWindowListener(new WinEnd());
	}

	/******************************/
	/* 上，左，下，右の余白の設定 */
	/******************************/
	public Insets getInsets()
	{
		return new Insets(50, 20, 20, 20);
	}

	/************/
	/* 終了処理 */
	/************/
	class WinEnd extends WindowAdapter
	{
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
}