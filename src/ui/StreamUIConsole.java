package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JTextField;
import util.StreamConstants;

// @author A0093874N

/**
 * <p>
 * A simple <b>JTextField</b> functioning as console for user input. Equipped with
 * placeholder text so that user knows where to go.
 * </p>
 * 
 * @version V0.4
 * @author Wilson Kurniawan
 */
public class StreamUIConsole extends JTextField {

	private static final long serialVersionUID = 1L;

	StreamUIConsole() {
		super();
	}

	@Override
	protected void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
		if (getText().isEmpty()) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setFont(StreamConstants.UI.FONT_CONSOLE);
			g2.setColor(Color.GRAY);
			g2.drawString("Enter your command here", 5, 20);
			g2.dispose();
		}
	}

}