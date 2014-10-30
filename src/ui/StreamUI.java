package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import model.StreamTask;
import stream.Stream;
import util.StreamLogger;
import util.StreamUtil;

/**
 * The GUI for Stream, featuring graphical view of user's added tasks, console
 * for user input, logger for terminal feedback, and some buttons for users'
 * convenience in processing some tasks.
 * 
 * @version V0.2
 * @author Wilson Kurniawan
 */
public class StreamUI {

	private Stream stream;

	private JFrame mainFrame;
	private JPanel contentPanel;
	private JTextField console;
	private StreamUILogger logger;
	private static final StreamLogger loggerDoc = StreamLogger
			.init(StreamUtil.COMPONENT_UI);

	private JButton undoButton;
	private JButton clearSearchButton;
	private JButton firstPageButton;
	private JButton prevPageButton;
	private JButton nextPageButton;
	private JButton lastPageButton;

	private boolean isSearch;
	private int pageShown;
	private int totalPage;
	private StreamTaskView[] shownTasks;
	private ArrayList<StreamTask> availTasks;
	private ArrayList<Integer> availIndices;

	// @author A0093874N

	public StreamUI(Stream str) {

		stream = str;

		addMainFrame();
		addContentPanel();
		addHeader();
		setUpView();
		addButtons();
		addConsole();
		empowerConsole(this.new EnterAction());
		addLogger();
		addFooter();
		log(StreamUtil.MSG_WELCOME, false);
		pageShown = 1;
		totalPage = 1;
		availTasks = new ArrayList<StreamTask>();
		availIndices = new ArrayList<Integer>();

		mainFrame.setVisible(true);
	}

	// @author A0093874N

	/**
	 * Constructs the main frame for Stream's User Interface.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addMainFrame() {
		mainFrame = new JFrame(StreamUtil.TEXT_TITLE);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(StreamUtil.WIDTH_MAINFRAME,
				StreamUtil.HEIGHT_MAINFRAME);
		mainFrame.setLocationRelativeTo(null);
	}

	// @author A0093874N

	/**
	 * Constructs the content panel for Stream's User Interface.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addContentPanel() {
		contentPanel = new JPanel();
		contentPanel.setBorder(StreamUtil.MARGIN_MAINFRAME);
		contentPanel.setLayout(new GridBagLayout());
		mainFrame.setContentPane(contentPanel);
	}

	// @author A0093874N

	/**
	 * Constructs the task view panel.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void setUpView() {
		shownTasks = new StreamTaskView[StreamUtil.MAX_VIEWABLE_TASK];
		for (int i = 0; i < StreamUtil.MAX_VIEWABLE_TASK; i++) {
			StreamTaskView taskPanel = new StreamTaskView(stream);
			taskPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			addComponent(taskPanel, StreamUtil.MARGIN_ELEM,
					StreamUtil.GRIDX_TASK, i + 1, StreamUtil.GRIDWIDTH_TASK,
					StreamUtil.IPADY_TASK);
			shownTasks[i] = taskPanel;
			taskPanel.setVisible(false);
		}
	}

	// @author A0093874N

	/**
	 * Constructs the header portion.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addHeader() {
		JLabel title = new JLabel(StreamUtil.TEXT_HEADER);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(StreamUtil.FONT_TITLE);
		title.setForeground(StreamUtil.COLOR_HEADER);
		addComponent(title, StreamUtil.MARGIN_HEADER, StreamUtil.GRIDX_HEADER,
				StreamUtil.GRIDY_HEADER, StreamUtil.GRIDWIDTH_HEADER,
				StreamUtil.IPADY_HEADER);
	}

	// @author A0093874N

	/**
	 * Constructs the usable buttons of Stream's User Interface.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addButtons() {
		addUndoButton();
		addFirstPageButton();
		addPrevPageButton();
		addNextPageButton();
		addLastPageButton();
		addClearSearchButton();
	}

	// @author A0093874N

	/**
	 * Constructs the undo button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addUndoButton() {
		undoButton = new JButton(StreamUtil.BTN_UNDO);
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamUtil.CMD_UNDO);
			}
		});
		addComponent(undoButton, StreamUtil.MARGIN_ELEM, StreamUtil.GRIDX_UNDO,
				StreamUtil.GRIDY_BUTTON, StreamUtil.GRIDWIDTH_BUTTON,
				StreamUtil.IPADY_BUTTON);
	}

	// @author A0093874N

	/**
	 * Constructs the navigate-to-first-page button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addFirstPageButton() {
		firstPageButton = new JButton(StreamUtil.BTN_FIRST);
		firstPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repopulateTaskView(1);
			}
		});
		addComponent(firstPageButton, StreamUtil.MARGIN_ELEM,
				StreamUtil.GRIDX_FIRST, StreamUtil.GRIDY_BUTTON,
				StreamUtil.GRIDWIDTH_NAVIG, StreamUtil.IPADY_BUTTON);
	}

	// @author A0093874N

	/**
	 * Constructs the navigate-to-previous-page button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addPrevPageButton() {
		prevPageButton = new JButton(StreamUtil.BTN_PREV);
		prevPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				assert (pageShown != 1) : StreamUtil.FAIL_NO_PREV_PAGE;
				repopulateTaskView(pageShown - 1);
			}
		});
		addComponent(prevPageButton, StreamUtil.MARGIN_ELEM,
				StreamUtil.GRIDX_PREV, StreamUtil.GRIDY_BUTTON,
				StreamUtil.GRIDWIDTH_NAVIG, StreamUtil.IPADY_BUTTON);
	}

	// @author A0093874N

	/**
	 * Constructs the navigate-to-next-page button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addNextPageButton() {
		nextPageButton = new JButton(StreamUtil.BTN_NEXT);
		nextPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				assert (pageShown != totalPage) : StreamUtil.FAIL_NO_NEXT_PAGE;
				repopulateTaskView(pageShown + 1);
			}
		});
		addComponent(nextPageButton, StreamUtil.MARGIN_ELEM,
				StreamUtil.GRIDX_NEXT, StreamUtil.GRIDY_BUTTON,
				StreamUtil.GRIDWIDTH_NAVIG, StreamUtil.IPADY_BUTTON);
	}

	// @author A0093874N

	/**
	 * Constructs the navigate-to-last-page button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addLastPageButton() {
		lastPageButton = new JButton(StreamUtil.BTN_LAST);
		lastPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repopulateTaskView(totalPage);
			}
		});
		addComponent(lastPageButton, StreamUtil.MARGIN_ELEM,
				StreamUtil.GRIDX_LAST, StreamUtil.GRIDY_BUTTON,
				StreamUtil.GRIDWIDTH_NAVIG, StreamUtil.IPADY_BUTTON);
	}

	// @author A0093874N

	/**
	 * Constructs the clear-search-result button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addClearSearchButton() {
		clearSearchButton = new JButton(StreamUtil.BTN_CLEAR);
		clearSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamUtil.CMD_CLRSRC);
			}
		});
		addComponent(clearSearchButton, StreamUtil.MARGIN_ELEM,
				StreamUtil.GRIDX_CLEAR, StreamUtil.GRIDY_BUTTON,
				StreamUtil.GRIDWIDTH_BUTTON, StreamUtil.IPADY_BUTTON);
	}

	// @author A0093874N

	/**
	 * Constructs the console for user input.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addConsole() {
		console = new JTextField();
		console.setFont(StreamUtil.FONT_CONSOLE);
		addComponent(console, StreamUtil.MARGIN_ELEM, StreamUtil.GRIDX_CONSOLE,
				StreamUtil.GRIDY_CONSOLE, StreamUtil.GRIDWIDTH_CONSOLE,
				StreamUtil.IPADY_CONSOLE);
	}

	// @author A0093874N

	/**
	 * Constructs the logger panel to display terminal response.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addLogger() {
		logger = new StreamUILogger();
		logger.setFont(StreamUtil.FONT_LOGGER);
		addComponent(logger, StreamUtil.MARGIN_ELEM, StreamUtil.GRIDX_LOGGER,
				StreamUtil.GRIDY_LOGGER, StreamUtil.GRIDWIDTH_LOGGER,
				StreamUtil.IPADY_LOGGER);
	}

	// @author A0093874N

	/**
	 * Constructs the footer portion.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addFooter() {
		JLabel footer = new JLabel(StreamUtil.TEXT_FOOTER);
		footer.setFont(StreamUtil.FONT_FOOTER);
		footer.setHorizontalAlignment(SwingConstants.RIGHT);
		addComponent(footer, StreamUtil.MARGIN_FOOTER, StreamUtil.GRIDX_FOOTER,
				StreamUtil.GRIDY_FOOTER, StreamUtil.GRIDWIDTH_FOOTER,
				StreamUtil.IPADY_FOOTER);
	}

	// @author A0093874N

	/**
	 * Adds a component to the User Interface based on the determined settings
	 * and dimensions.
	 * 
	 * @author Wilson Kurniawan
	 * @param comp
	 *            - the component to be added
	 * @param inset
	 *            - the margins
	 * @param gridx
	 *            - the grid-wise horizontal position
	 * @param gridy
	 *            - the grid-wise vertical position
	 * @param gridwidth
	 *            - the grid-wise horizontal length
	 * @param ipady
	 *            - the height of the component
	 */
	private void addComponent(Component comp, Insets inset, int gridx,
			int gridy, int gridwidth, int ipady) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		// allocate equal white spaces to all elements if there are extras
		gbc.weightx = 1.0 / 3;
		gbc.insets = inset;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridheight = 1;
		gbc.gridwidth = gridwidth;
		gbc.ipady = ipady;
		contentPanel.add(comp, gbc);
	}

	// @author A0093874N

	/**
	 * Logs a message/error message to the logger.
	 * 
	 * @author Wilson Kurniawan
	 * @param logMsg
	 *            - the message to be logged
	 * @param isErrorMsg
	 *            - determines the formatting (different for error message)
	 */
	public void log(String logMsg, Boolean isErrorMsg) {
		if (isErrorMsg) {
			logger.showErrorMessage(logMsg);
		} else {
			logger.showLogMessage(logMsg);
		}
	}

	// @author A0093874N

	/**
	 * Equips the console with the action invoked upon pressing enter.
	 * 
	 * @author Wilson Kurniawan
	 * @param action
	 *            - the enter action
	 */
	private void empowerConsole(Action action) {
		console.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
				"processInput");
		console.getActionMap().put("processInput", action);
	}

	// @author A0093874N

	/**
	 * Resets the viewable tasks to the chosen indices and <b>StreamTask</b>s.
	 * 
	 * @author Wilson Kurniawan
	 * @param indices
	 *            - the list of indices to be displayed
	 * @param tasks
	 *            - the list of <b>StreamTask</b>s to be displayed
	 * @param isReset
	 *            - indicating if the view needs to be reset to the first page
	 * @param isSearching
	 *            - indicating if this is a search result
	 */
	public void resetAvailableTasks(ArrayList<Integer> indices,
			ArrayList<StreamTask> tasks, Boolean isReset, Boolean isSearching) {
		// error: length not the same
		assert (indices.size() == tasks.size()) : StreamUtil.FAIL_SIZE_DIFFERENT;
		availIndices = indices;
		availTasks = tasks;
		if (tasks.size() == 0) {
			// no task added: go to page one
			totalPage = 1;
		} else {
			totalPage = (int) Math.ceil(1.0 * tasks.size()
					/ StreamUtil.MAX_VIEWABLE_TASK);
		}
		if (isReset || tasks.size() == 0 || isSearch) {
			/*
			 * resetting or clearing search result automatically resets the view
			 * back to first page
			 */
			repopulateTaskView(1);
			isSearch = isSearching;
		} else {
			if ((int) Math.ceil(1.0 * tasks.size()
					/ StreamUtil.MAX_VIEWABLE_TASK) < pageShown) {
				// last task in the last page deleted: move back one page
				assert (pageShown != 1) : StreamUtil.FAIL_NO_PREV_PAGE;
				repopulateTaskView(pageShown - 1);
			} else {
				repopulateTaskView(pageShown);
			}
		}
		loggerDoc.log(StreamLogger.LogLevel.DEBUG,
				"Task viewer refreshed with " + indices.size() + " new tasks");
	}

	// @author A0093874N

	/**
	 * Resets the viewable tasks according to the page shown.
	 * 
	 * @author Wilson Kurniawan
	 * @param page
	 *            - the page number to be shown
	 */
	private void repopulateTaskView(int page) {
		assert (page <= totalPage) : StreamUtil.FAIL_TOO_MANY_PAGES;
		pageShown = page;
		int startPoint = (pageShown - 1) * StreamUtil.MAX_VIEWABLE_TASK;
		for (int i = 0; i < StreamUtil.MAX_VIEWABLE_TASK; i++) {
			StreamTaskView taskPanel = shownTasks[i];
			try {
				int index = availIndices.get(startPoint + i);
				StreamTask task = availTasks.get(startPoint + i);
				taskPanel.updateView(index, task);
			} catch (IndexOutOfBoundsException e) {
				taskPanel.hideView();
			}
		}
		determineClickableNavigators();
	}

	// @author A0093874N

	/**
	 * Determines which navigator buttons are clickable based on the current
	 * page shown and total pages available.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void determineClickableNavigators() {
		firstPageButton.setEnabled(true);
		prevPageButton.setEnabled(true);
		nextPageButton.setEnabled(true);
		lastPageButton.setEnabled(true);
		if (pageShown == 1) {
			firstPageButton.setEnabled(false);
			prevPageButton.setEnabled(false);
		}
		if (pageShown == totalPage) {
			nextPageButton.setEnabled(false);
			lastPageButton.setEnabled(false);
		}
	}

	// @author A0093874N

	/**
	 * Displays the detailed information of a task in a dialog window upon
	 * clicking the name.
	 * 
	 * @author Wilson Kurniawan
	 * @param task
	 *            - the <b>StreamTask</b> from which the information is obtained
	 *            from
	 */
	public void displayDetails(StreamTask task) {
		JOptionPane.showMessageDialog(mainFrame, String.format(
				StreamUtil.DETAILS_CONTENT,
				task.getTaskName(),
				StreamUtil.displayStatus(task.isDone()),
				StreamUtil.getWrittenTime(task.getStartTime(),
						task.getDeadline()),
				StreamUtil.displayDescription(task.getDescription()),
				StreamUtil.displayTags(task.getTags())), String.format(
				StreamUtil.DETAILS_HEADER, task.getTaskName()),
				JOptionPane.INFORMATION_MESSAGE);
		loggerDoc.log(StreamLogger.LogLevel.DEBUG, "Displaying details for"
				+ task.getTaskName());
	}

	// @author A0093874N

	/**
	 * Gets the number of tasks stored in the task viewer after
	 * search/filter/add/remove/...
	 * 
	 * @return <b>int</b> - the number of tasks stored in task viewer
	 */
	public int getNumberOfTasksStored() {
		return availTasks.size();
	}

	// @author A0093874N

	/**
	 * <p>
	 * The action invoked upon pressing "enter" in console. It fires the text in
	 * console to the input parser and subsequently processor.
	 * </p>
	 * <p>
	 * Credits to developers from F10-04J for this idea.
	 * </p>
	 * 
	 * @author Wilson Kurniawan
	 */
	private class EnterAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			String input = console.getText();
			stream.filterAndProcessInput(input);
			console.setText("");
		}
	}

}