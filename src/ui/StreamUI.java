package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import model.StreamTask;
import stream.Stream;
import util.StreamConstants;
import util.StreamLogger;
import util.StreamLogger.LogLevel;
import util.StreamUtil;

//@author A0093874N

/**
 * <p>
 * The GUI for Stream, featuring graphical view of user's added tasks, console
 * for user input, and logger for terminal feedback. Also equipped with some
 * keyboard shortcuts for user's convenience.
 * </p>
 * 
 * <h3>API</h3>
 * <ul>
 * <li>StreamUI.resetAvailableTasks(ArrayList&lt;Integer&gt; indices,
 * ArrayList&lt;StreamTask&gt; tasks, Boolean isReset, Boolean isSearching)</li>
 * <li>StreamUI.log(String logMsg, Boolean isErrorMsg)</li>
 * <li>StreamUI.displayDetails(StreamTask task)</li>
 * <li>StreamUI.getNumberOfTasksStored()</li>
 * <li>StreamUI.goToFirstPage()</li>
 * <li>StreamUI.goToPrevPage()</li>
 * <li>StreamUI.goToNextPage()</li>
 * <li>StreamUI.goToLastPage()</li>
 * </ul>
 * <p>
 * Refer to method documentation for details.
 * </p>
 * 
 * @version V0.4
 * @author Wilson Kurniawan
 */
public class StreamUI {

	private Stream stream;

	private JFrame mainFrame;
	private JPanel contentPanel;
	private StreamUIConsole console;
	private JTextField newTaskTextField;
	private StreamUILogger logger;
	private static final StreamLogger loggerDoc = StreamLogger
			.init(StreamConstants.ComponentTag.STREAMUI);

	private JButton undoButton;
	private JButton clearSearchButton;
	private JButton firstPageButton;
	private JButton prevPageButton;
	private JButton nextPageButton;
	private JButton lastPageButton;
	private JButton sortAlphaButton;
	private JButton sortDeadlineButton;
	private JButton addTaskButton;
	private ArrayList<JButton> buttons = new ArrayList<JButton>();

	private boolean isSearch;
	private int pageShown;
	private int totalPage;
	private StreamTaskView[] shownTasks;
	private ArrayList<StreamTask> availTasks;
	private ArrayList<Integer> availIndices;

	public StreamUI(Stream str) {

		stream = str;
		initFonts();

		setupLookAndFeel();
		addMainFrame();
		addContentPanel();
		addHeader();
		// addMenu();
		setUpView();
		// addUndoButton();
		// addNavigationButtons();
		// addClearSearchButton();
		addConsole();
		addAutocomplete();
		empowerConsole(new StreamUIConsoleEnterAction(stream, console));
		addLogger();
		addKeyboardShortcuts();
		addNavigShortcuts();
		addFooter();
		log(StreamConstants.Message.WELCOME, false);
		pageShown = 1;
		totalPage = 1;
		availTasks = new ArrayList<StreamTask>();
		availIndices = new ArrayList<Integer>();
		setFocusTraversal();
		mainFrame.setVisible(true);
	}

	/**
	 * Loads the fonts used.
	 */
	private void initFonts() {
		InputStream titleFont = getClass().getResourceAsStream(
				"/fonts/Awesome Java.ttf");
		InputStream consoleFont = getClass().getResourceAsStream(
				"/fonts/Ubuntu.ttf");
		StreamConstants.UI.initFonts(titleFont, consoleFont);
	}

	/**
	 * Constructs the autocomplete helper texts.
	 */
	private void addAutocomplete() {
		HashMap<String, String> helpTexts = new HashMap<String, String>();
		helpTexts.put("add", "Add a new task here");
		helpTexts.put("due",
				"Set the deadline for a task based on index number");
		helpTexts.put("start",
				"Set the start time for a task based on index number");
		helpTexts.put("search",
				"Searches tasks by its name, description, or tags");
		helpTexts.put("delete", "Delete based on index number");
		helpTexts.put("filter", "Filters tasks by dates or ranks");
		helpTexts.put("mark", "Marks task as done or ongoing");
		helpTexts.put("modify",
				"Modifies multiple parameters of a task in one go");
		helpTexts.put("view", "Views the details of a task");
		helpTexts.put("sort",
				"Sorts tasks by alphabetical or chronological order");
		helpTexts.put("clear", "Clears all added tasks");
		helpTexts.put("clrsrc", "Clears search result");
		helpTexts.put("rank",
				"Change the rank of a certain task based on index number");
		helpTexts.put("first", "Go to the first page");
		helpTexts.put("last", "Go to the last page");
		helpTexts.put("next", "Go to the next page");
		helpTexts.put("prev", "Go to the previous page");
		helpTexts.put("undo", "Undo the last action");
		helpTexts.put("exit", "Exits the program");
		for (String h : helpTexts.keySet()) {
			console.addPossibility(h, helpTexts.get(h));
		}
	}

	/**
	 * Adds the keyboard shortcuts.
	 */
	private void addKeyboardShortcuts() {
		HashMap<Character, String> shortcut = new HashMap<Character, String>();
		shortcut.put('a', "add ");
		shortcut.put('s', "search ");
		shortcut.put('d', "delete ");
		shortcut.put('f', "filter ");
		shortcut.put('m', "mark ");
		shortcut.put('y', "modify ");
		shortcut.put('v', "view ");
		shortcut.put('t', "sort ");
		shortcut.put('u', "undo");
		shortcut.put('e', "exit");
		shortcut.put('c', "");
		for (Character c : shortcut.keySet()) {
			empowerKeyboardShortcuts(c, shortcut.get(c));
		}
	}

	/**
	 * Adds the navigation shortcuts.
	 */
	private void addNavigShortcuts() {
		HashMap<String, String> navig = new HashMap<String, String>();
		navig.put("DOWN", "first");
		navig.put("LEFT", "prev");
		navig.put("RIGHT", "next");
		navig.put("UP", "last");
		for (String s : navig.keySet()) {
			empowerNavigationShortcuts(s, navig.get(s));
		}
	}

	/**
	 * Sets the customized tab-based focus traversal policy.
	 */
	private void setFocusTraversal() {
		Vector<Component> order = new Vector<Component>(2);
		// order.add(newTaskTextField);
		order.add(console);
		order.add(logger);
		mainFrame.setFocusTraversalPolicy(new StreamUIFocusTraversal(order));
	}

	/**
	 * Constructs the main frame for Stream's User Interface.
	 */
	private void addMainFrame() {
		mainFrame = new JFrame(StreamConstants.Message.TEXT_TITLE);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(StreamConstants.UI.WIDTH_MAINFRAME,
				StreamConstants.UI.HEIGHT_MAINFRAME);
		mainFrame.setResizable(false);
		mainFrame.setLocationRelativeTo(null);
	}

	/**
	 * Constructs the content panel for Stream's User Interface.
	 */
	private void addContentPanel() {
		contentPanel = new JPanel();
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setLayout(null);
		mainFrame.setContentPane(contentPanel);
	}

	/**
	 * Constructs the task view panel.
	 */
	private void setUpView() {
		shownTasks = new StreamTaskView[StreamConstants.UI.MAX_VIEWABLE_TASK];
		for (int i = 0; i < StreamConstants.UI.MAX_VIEWABLE_TASK; i++) {
			StreamTaskView taskPanel = new StreamTaskView(stream);
			taskPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			addComponent(taskPanel,
					70 + i * (StreamConstants.UI.HEIGHT_TASKPANEL + StreamConstants.UI.MARGIN_COMPONENT),
					StreamConstants.UI.HEIGHT_TASKPANEL);
			shownTasks[i] = taskPanel;
			taskPanel.setVisible(false);
		}
	}

	/**
	 * Constructs the header portion.
	 */
	private void addHeader() {
		JLabel title = new JLabel(StreamConstants.Message.TEXT_HEADER);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(StreamConstants.UI.FONT_TITLE);
		title.setForeground(StreamConstants.UI.COLOR_HEADER);
		addComponent(title, 10, 50);
	}

	/**
	 * Constructs the console for user input.
	 */
	private void addConsole() {
		console = new StreamUIConsole();
		console.setFont(StreamConstants.UI.FONT_CONSOLE);
		addComponent(console, 530, 32);
	}

	/**
	 * Constructs the logger panel to display terminal response.
	 */
	private void addLogger() {
		logger = new StreamUILogger();
		logger.setFont(StreamConstants.UI.FONT_LOGGER);
		addComponent(logger, 575, 45);
	}

	/**
	 * Constructs the footer portion.
	 */
	private void addFooter() {
		JLabel footer = new JLabel(StreamConstants.Message.TEXT_FOOTER);
		footer.setFont(StreamConstants.UI.FONT_FOOTER);
		footer.setHorizontalAlignment(SwingConstants.RIGHT);
		addComponent(footer, 625, 32);
	}

	/**
	 * Adds a component to the User Interface based on the determined settings
	 * and dimensions.
	 * 
	 * @param comp
	 *            - the component to be added
	 * @param y
	 *            - the absolute vertical position
	 * @param width
	 *            - the width of the component
	 */
	private void addComponent(Component comp, int y, int height) {
		comp.setBounds(StreamConstants.UI.MARGIN_SIDE, y,
				StreamConstants.UI.COMPONENT_WIDTH, height);
		contentPanel.add(comp);
	}

	/**
	 * Equips the console with the action invoked upon pressing enter.
	 * 
	 * @param action
	 *            - the enter action
	 */
	private void empowerConsole(Action action) {
		console.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
				"processInput");
		console.getActionMap().put("processInput", action);
	}

	/**
	 * Equips keyboard shortcut to elements outside the command line.
	 * 
	 * @param key
	 *            - the key shortcut
	 * @param cmd
	 *            - the target command
	 */
	private void empowerKeyboardShortcuts(char key, String cmd) {
		/*
		 * for (JButton b : buttons) {
		 * b.getInputMap().put(KeyStroke.getKeyStroke(key), cmd);
		 * b.getActionMap().put(cmd, new StreamUIKeyboardShortcut(console,
		 * cmd)); }
		 */
		logger.getInputMap().put(KeyStroke.getKeyStroke(key), cmd);
		logger.getActionMap().put(cmd,
				new StreamUIKeyboardShortcut(console, cmd));
	}

	/**
	 * Equips navigation shortcut to elements outside the command line.
	 * 
	 * @param dir
	 *            - the key shortcut (L/R/U/D)
	 * @param cmd
	 *            - the target command
	 */
	private void empowerNavigationShortcuts(String dir, String cmd) {
		/*
		 * for (JButton b : buttons) {
		 * b.getInputMap().put(KeyStroke.getKeyStroke(dir), cmd);
		 * b.getActionMap().put(cmd, new StreamUINavigationShortcut(stream,
		 * logger, cmd)); }
		 */
		logger.getInputMap().put(KeyStroke.getKeyStroke(dir), cmd);
		logger.getActionMap().put(cmd,
				new StreamUINavigationShortcut(stream, logger, cmd));
	}

	/**
	 * Resets the viewable tasks according to the page shown.
	 * 
	 * @param page
	 *            - the page number to be shown
	 */
	private void repopulateTaskView(int page) {
		assert (page <= totalPage) : StreamConstants.Assertion.TOO_MANY_PAGES;
		pageShown = page;
		int startPoint = (pageShown - 1) * StreamConstants.UI.MAX_VIEWABLE_TASK;
		for (int i = 0; i < StreamConstants.UI.MAX_VIEWABLE_TASK; i++) {
			StreamTaskView taskPanel = shownTasks[i];
			try {
				int index = availIndices.get(startPoint + i);
				StreamTask task = availTasks.get(startPoint + i);
				taskPanel.updateView(index, task);
			} catch (IndexOutOfBoundsException e) {
				taskPanel.hideView();
			}
		}
		// determineClickableNavigators();
	}

	/**
	 * Resets the viewable tasks to the chosen indices and <b>StreamTask</b>s.
	 * 
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
		assert (indices.size() == tasks.size()) : StreamConstants.Assertion.SIZE_DIFFERENT;
		availIndices = indices;
		availTasks = tasks;
		if (tasks.size() == 0) {
			// no task added: go to page one
			totalPage = 1;
		} else {
			totalPage = (int) Math.ceil(1.0 * tasks.size()
					/ StreamConstants.UI.MAX_VIEWABLE_TASK);
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
					/ StreamConstants.UI.MAX_VIEWABLE_TASK) < pageShown) {
				// last task in the last page deleted: move back one page
				assert (pageShown != 1) : StreamConstants.Assertion.NO_PREV_PAGE;
				repopulateTaskView(pageShown - 1);
			} else {
				repopulateTaskView(pageShown);
			}
		}
		loggerDoc.log(StreamLogger.LogLevel.DEBUG,
				"Task viewer refreshed with " + indices.size() + " new tasks");
	}

	/**
	 * Logs a message/error message to the logger.
	 * 
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

	/**
	 * Displays the detailed information of a task in a dialog window upon
	 * clicking the name.
	 * 
	 * @param task
	 *            - the <b>StreamTask</b> from which the information is obtained
	 *            from
	 */
	public void displayDetails(StreamTask task) {
		JOptionPane.showMessageDialog(mainFrame, String.format(
				StreamConstants.Message.DETAILS_CONTENT,
				task.getTaskName(),
				StreamUtil.displayStatus(task.isDone()),
				StreamUtil.getWrittenTime(task.getStartTime(),
						task.getDeadline()),
				StreamUtil.displayDescription(task.getDescription()),
				StreamUtil.displayTags(task.getTags()), task.getRank()), String
				.format(StreamConstants.Message.DETAILS_HEADER,
						task.getTaskName()), JOptionPane.INFORMATION_MESSAGE);
		loggerDoc.log(StreamLogger.LogLevel.DEBUG, "Displaying details for"
				+ task.getTaskName());
	}

	/**
	 * Gets the number of tasks stored in the task viewer after
	 * search/filter/add/remove/...
	 * 
	 * @return <b>int</b> - the number of tasks stored in task viewer
	 */
	public int getNumberOfTasksStored() {
		return availTasks.size();
	}

	/**
	 * Navigates to the first page.
	 */
	public void goToFirstPage() {
		repopulateTaskView(1);
	}

	/**
	 * Navigates to the previous page.
	 */
	public void goToPrevPage() {
		if (pageShown != 1) {
			repopulateTaskView(pageShown - 1);
		}
	}

	/**
	 * Navigates to the next page.
	 */
	public void goToNextPage() {
		if (pageShown != totalPage) {
			repopulateTaskView(pageShown + 1);
		}
	}

	/**
	 * Navigates to the last page.
	 */
	public void goToLastPage() {
		repopulateTaskView(totalPage);
	}

	/**
	 * Constructs the usable buttons of Stream's User Interface.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addNavigationButtons() {
		addFirstPageButton();
		addPrevPageButton();
		addNextPageButton();
		addLastPageButton();
		buttons.add(firstPageButton);
		buttons.add(prevPageButton);
		buttons.add(nextPageButton);
		buttons.add(lastPageButton);
	}

	/**
	 * Constructs the navigate-to-first-page button.
	 * 
	 * @deprecated
	 */
	private void addFirstPageButton() {
		// firstPageButton = new JButton(StreamConstants.UI.BTN_FIRST);
		firstPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput("first");
			}
		});
		// addComponent(firstPageButton, 25, 500, 160, 32);
	}

	/**
	 * Constructs the navigate-to-previous-page button.
	 * 
	 * @deprecated
	 */
	private void addPrevPageButton() {
		// prevPageButton = new JButton(StreamConstants.UI.BTN_PREV);
		prevPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput("prev");
			}
		});
		// addComponent(prevPageButton, 205, 500, 160, 32);
	}

	/**
	 * Constructs the navigate-to-next-page button.
	 * 
	 * @deprecated
	 */
	private void addNextPageButton() {
		// nextPageButton = new JButton(StreamConstants.UI.BTN_NEXT);
		nextPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput("next");
			}
		});
		// addComponent(nextPageButton, 385, 500, 160, 32);
	}

	/**
	 * Constructs the navigate-to-last-page button.
	 * 
	 * @deprecated
	 */
	private void addLastPageButton() {
		// lastPageButton = new JButton(StreamConstants.UI.BTN_LAST);
		lastPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput("last");
			}
		});
		// addComponent(lastPageButton, 565, 500, 160, 32);
	}

	/**
	 * Constructs the undo button.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addUndoButton() {
		// undoButton = new JButton(StreamConstants.UI.BTN_UNDO);
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamConstants.Commands.UNDO);
			}
		});
	}

	/**
	 * Constructs the clear-search-result button.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addClearSearchButton() {
		// clearSearchButton = new JButton(StreamConstants.UI.BTN_CLEAR);
		clearSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamConstants.Commands.CLRSRC);
			}
		});
	}

	/**
	 * Determines which navigator buttons are clickable based on the current
	 * page shown and total pages available.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
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

	// @author A0096529N

	/**
	 * Sets up the UI according to system theme i.e. MacOS, Windows, Ubuntu,
	 * etc.
	 */
	private void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			loggerDoc.log(LogLevel.ERROR,
					StreamConstants.LogMessage.UI_LOOKANDFEEL_FAIL);
		}
	}

	/**
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addMenu() {
		newTaskTextField = new JTextField();
		newTaskTextField.setFont(StreamConstants.UI.FONT_CONSOLE);
		// addTaskButton = new JButton(StreamConstants.UI.BTN_ADD_TASK);
		addTaskButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String params = newTaskTextField.getText();
				newTaskTextField.setText("");
				stream.filterAndProcessInput(String.format(
						StreamConstants.Commands.ADD_TASK, params));
			}
		});
		addSortDeadlineButton();
		addSortAlphaButton();
	}

	/**
	 * @deprecated
	 */
	private void addSortAlphaButton() {
		// sortAlphaButton = new JButton(StreamConstants.UI.BTN_SORT_ALPHA);
		sortAlphaButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamConstants.Commands.SORT_ALPHA);
			}
		});
	}

	/**
	 * @deprecated
	 */
	private void addSortDeadlineButton() {
		// sortDeadlineButton = new JButton(StreamConstants.UI.BTN_SORT_DEADLINE);
		sortDeadlineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamConstants.Commands.SORT_DEADLINE);
			}
		});
	}

}