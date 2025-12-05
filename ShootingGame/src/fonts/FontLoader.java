package fonts;

import java.awt.Font;
import java.io.InputStream;

public class FontLoader {

    public static Font crtFont;

    static {
        try {
            InputStream is = FontLoader.class.getResourceAsStream(
                "/fonts/CRT8-bit8-dotFontTrialRegular.otf"
            );

            if (is == null) {
                System.err.println("フォントファイルが見つかりません！");
            }

            crtFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            e.printStackTrace();
            crtFont = new Font("Monospaced", Font.PLAIN, 12);
        }
    }
}