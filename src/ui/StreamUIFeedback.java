package ui;

import javax.swing.JTextField;

import util.StreamConstants;

//@author A0093874N

/**
 * <p>
 * A simple, non-editable <b>JTextField</b> component to display feedback
 * messages and to assist users in entering their commands.
 * </p>
 * 
 * <h3>API</h3>
 * <ul>
 * <li>StreamUIFeedback.showFeedbackMessage(String msg)</li>
 * </ul>
 * 
 * @version V0.5
 */
public class StreamUIFeedback extends JTextField {

	private static final long serialVersionUID = 1L;
	
	StreamUIFeedback() {
		super();
		setBackground(StreamConstants.UI.COLOR_FEEDBACK);
		setForeground(StreamConstants.UI.COLOR_HELP_MSG);
		setMargin(StreamConstants.UI.MARGIN_CONSOLE);
		setEditable(false);
	}

	/**
	 * Displays feedback messages.
	 * 
	 * @param msg
	 *            - the message to be displayed
	 */
	public void showFeedbackMessage(String msg) {
		setText(msg);
	}

}