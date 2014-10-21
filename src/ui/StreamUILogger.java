package ui;

import java.awt.Color;

import javax.swing.JTextField;

import util.StreamUtil;

public class StreamUILogger extends JTextField {

	private static final long serialVersionUID = 1L;

	public StreamUILogger() {
		super();
		setEditable(false);
	}

	void showLogMessage(String logMsg) {
		setForeground(Color.BLACK);
		setText(StreamUtil.showAsTerminalResponse(logMsg));
	}

	void showErrorMessage(String errMsg) {
		setForeground(Color.RED);
		setText(StreamUtil.showAsTerminalResponse(errMsg));
	}

}
