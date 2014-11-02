package ui;

import javax.swing.JTextField;

import util.StreamConstants;
import util.StreamUtil;

/**
 * A simple non-editable JTextField component to display log messages. Error
 * messages and normal log messages are displayed differently.
 * 
 * @version V0.4
 * @author Wilson Kurniawan
 */
public class StreamUILogger extends JTextField {

	private static final long serialVersionUID = 1L;

	// @author A0093874N

	StreamUILogger() {
		super();
		setEditable(false);
	}

	// @author A0093874N

	/**
	 * Displays normal log messages.
	 * 
	 * @author Wilson Kurniawan
	 * @param logMsg
	 *            - the message to be logged
	 */
	void showLogMessage(String logMsg) {
		setForeground(StreamConstants.UI.COLOR_LOG_MSG);
		setText(StreamUtil.showAsTerminalResponse(logMsg));
	}

	// @author A0093874N

	/**
	 * Displays error messages.
	 * 
	 * @author Wilson Kurniawan
	 * @param errMsg
	 *            - the error message to be logged
	 */
	void showErrorMessage(String errMsg) {
		setForeground(StreamConstants.UI.COLOR_ERR_MSG);
		setText(StreamUtil.showAsTerminalResponse(errMsg));
	}

}
