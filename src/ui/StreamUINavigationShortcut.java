package ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import stream.Stream;

//@author A0093874N

/**
 * The action invoked upon pressing the navigation shortcut, i.e matching the
 * key pressed to the corresponding navigation command.
 * 
 * @version V0.5
 */
public class StreamUINavigationShortcut extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private String command;
	private Stream stream;
	private StreamUILogger logger;

	@Override
	public void actionPerformed(ActionEvent e) {
		stream.filterAndProcessInput(command);
		logger.requestFocus();
	}

	StreamUINavigationShortcut(Stream str, StreamUILogger log, String cmd) {
		this.stream = str;
		this.logger = log;
		this.command = cmd;
	}

}
