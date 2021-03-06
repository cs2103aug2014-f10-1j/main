package ui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import model.StreamTask;
import stream.Stream;
import util.StreamConstants;
import util.StreamExternals;
import util.StreamLogger;
import util.StreamLogger.LogLevel;
import util.StreamUtil;

//@author A0093874N

/**
 * <p>
 * StreamUI is the GUI for STREAM, featuring graphical view of user's added
 * tasks, console for user input, logger for terminal feedback, and helper box
 * for user assistance. Also equipped with some keyboard shortcuts and simple
 * auto-completion for user's convenience.
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
 * @version V0.5
 */
public class StreamUI {

	private Stream stream;

	private JFrame mainFrame;
	private JPanel contentPanel;
	private StreamUIConsole console;
	private StreamUIFeedback feedback;
	private StreamUILogger logger;
	private JLabel pageNumber;
	private static final StreamLogger loggerDoc = StreamLogger
			.init(StreamConstants.ComponentTag.STREAMUI);

	private boolean isSearch;
	private boolean isTaskHighlighted;
	private int pageShown;
	private int totalPage;
	private StreamTaskView[] shownTasks;
	private ArrayList<StreamTask> availTasks;
	private ArrayList<Integer> availIndices;
	private StreamTask activeTask;

	public StreamUI(Stream str) {

		initParams(str);
		setupLookAndFeel();
		addMainFrame();
		addContentPanel();
		addHeader();
		setUpView();
		addFeedbackBox();
		addConsole();
		addAutocomplete();
		empowerConsole(new StreamUIConsoleEnterAction(stream, console));
		addLogger();
		addKeyboardShortcuts();
		addNavigShortcuts();
		addPageNumber();
		addFooter();
		setFocusTraversal();
		log(StreamConstants.Message.WELCOME, false);
		presentToUser();
	}

	private void initParams(Stream str) {
		stream = str;
		isSearch = false;
		pageShown = 1;
		totalPage = 1;
		availTasks = new ArrayList<StreamTask>();
		availIndices = new ArrayList<Integer>();
	}

	//@author A0096529N
	/**
	 * Sets the active task for highlighting
	 * 
	 * @param task
	 *            the task to be highlighted on next UI update
	 * 
	 */
	public void setActiveTask(StreamTask task) {
		activeTask = task;
		isTaskHighlighted = false;
	}

	/**
	 * Highlights the task view containing the active task
	 */
	public void highlightActiveTaskView() {
		int index = availTasks.indexOf(activeTask);
		assert (index >= 0) : StreamConstants.Assertion.TASK_TAG_NOTFOUND;
		int page = index / StreamConstants.UI.MAX_VIEWABLE_TASK + 1;
		repopulateTaskView(page);
		fadeBorder(shownTasks[index % StreamConstants.UI.MAX_VIEWABLE_TASK]);
		isTaskHighlighted = true;
	}

	/**
	 * Fades the border by setting the alpha value.
	 * <p>
	 * Starts a daemon background thread to alter border
	 * </p>
	 * 
	 * @param taskView
	 *            to execute the fade effect on
	 */
	private void fadeBorder(final StreamTaskView taskView) {
		new Thread() {
			@Override
			public void run() {
				try {
					for (int i = 255; i > 0; i -= 10) {
						taskView.setBorder(BorderFactory
								.createLineBorder(new Color(48, 111, 163, i))); // #2d6ea3
						Thread.sleep(10);
					}
					for (int i = 0; i < 255; i += 10) {
						taskView.setBorder(BorderFactory
								.createLineBorder(new Color(48, 111, 163, i)));
						Thread.sleep(10);
					}
					for (int i = 255; i > 0; i -= 2) {
						taskView.setBorder(BorderFactory
								.createLineBorder(new Color(48, 111, 163, i)));
						Thread.sleep(10);
					}
					taskView.setBorder(null);
				} catch (Exception e) {
					loggerDoc
							.log(LogLevel.ERROR,
									String.format(
											StreamConstants.ExceptionMessage.ERR_UI_FADE_THREAD,
											e.getClass().getSimpleName(),
											e.getMessage()));
				}
			}
		}.start();
	}

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

	//@author A0093874N

	/**
	 * Constructs the autocomplete helper texts.
	 */
	private void addAutocomplete() {
		HashMap<String, String> helpTexts = new HashMap<String, String>();
		helpTexts.put("add",
				"add (task name) (properties): Add a new task here");
		helpTexts
				.put("due",
						"due (index) (time): Set the deadline for a task based on index number");
		helpTexts
				.put("start",
						"start (index) (time): Set the start time for a task based on index number");
		helpTexts
				.put("search",
						"search (keyphrase): Searches tasks by its name, description, or tags");
		helpTexts.put("delete", "delete (index): Delete based on index number");
		helpTexts.put("del", "del (index): Delete based on index number");

		helpTexts
				.put("desc",
						"desc (index) (description): Sets a description to a task based on index number");

		helpTexts
				.put("describe",
						"describe (index) (description): Sets a description to a task based on index number");
		helpTexts.put("filter",
				"filter (criteria): Filters tasks by dates or ranks");
		helpTexts.put("mark",
				"mark (index) (mark type): Marks task as done or ongoing");
		helpTexts.put("modify",
				"Modifies multiple parameters of a task in one go");
		helpTexts.put("view", "view (index): Views the details of a task");
		helpTexts
				.put("tag",
						"tag (index) (tag1) ... (tagN): Add tags to a task based on index number.");
		helpTexts
				.put("name",
						"name (index) (new name): Changes a task's name based on index number.");
		helpTexts
				.put("untag",
						"tag (index) (tag1) ... (tagN): Remove tags of a task based on index number.");
		helpTexts
				.put("sort",
						"sort (criteria): Sorts tasks by alphabetical or chronological order");
		helpTexts.put("clear", "Clears all added tasks");
		helpTexts
				.put("clrsrc", "CLeaR SeaRCh - Clears search or filter result");
		helpTexts
				.put("rank",
						"rank (index) (rank type): Change the rank of a certain task based on index number");
		helpTexts.put("first ", "Go to the first page");
		helpTexts.put("last", "Go to the last page");
		helpTexts.put("next", "Go to the next page");
		helpTexts.put("prev", "Go to the previous page");
		helpTexts.put("page", "page (page): Go to a specific page");
		helpTexts.put("undo", "Undo the last action");
		helpTexts.put("help", "Opens the help dialog box");
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
		shortcut.put('p', "page ");
		shortcut.put('h', "help");
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
			StreamTaskView taskPanel = new StreamTaskView();
			taskPanel
					.setBounds(
							StreamConstants.UI.MARGIN_SIDE,
							StreamConstants.UI.MARGIN_COMPONENT
									* 2
									+ StreamConstants.UI.HEIGHT_HEADER
									+ i
									* (StreamConstants.UI.HEIGHT_TASKPANEL + StreamConstants.UI.MARGIN_COMPONENT),
							StreamConstants.UI.COMPONENT_WIDTH,
							StreamConstants.UI.HEIGHT_TASKPANEL);
			contentPanel.add(taskPanel);
			shownTasks[i] = taskPanel;
			taskPanel.hideView();
		}
	}

	/**
	 * Constructs the header portion.
	 */
	private void addHeader() {
		JLabel title = new JLabel();
		title.setIcon(StreamExternals.HEADER);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBounds(StreamConstants.UI.BOUNDS_HEADER);
		contentPanel.add(title);
	}

	/**
	 * Constructs the console for user input.
	 */
	private void addConsole() {
		console = new StreamUIConsole(feedback);
		console.setFont(StreamConstants.UI.FONT_CONSOLE);
		console.setBounds(StreamConstants.UI.BOUNDS_CONSOLE);
		contentPanel.add(console);
	}

	private void addFeedbackBox() {
		feedback = new StreamUIFeedback();
		feedback.setFont(StreamConstants.UI.FONT_CONSOLE);
		feedback.setBounds(StreamConstants.UI.BOUNDS_FEEDBACK);
		contentPanel.add(feedback);
	}

	/**
	 * Constructs the logger panel to display terminal response.
	 */
	private void addLogger() {
		logger = new StreamUILogger();
		logger.setFont(StreamConstants.UI.FONT_LOGGER);
		logger.setBounds(StreamConstants.UI.BOUNDS_LOGGER);
		contentPanel.add(logger);
	}

	/**
	 * Constructs the page number portion.
	 */
	private void addPageNumber() {
		pageNumber = new JLabel();
		pageNumber.setFont(StreamConstants.UI.FONT_PAGE_NUM);
		pageNumber.setHorizontalAlignment(SwingConstants.LEFT);
		pageNumber.setBounds(StreamConstants.UI.BOUNDS_PAGE_NUM);
		contentPanel.add(pageNumber);
	}

	/**
	 * Constructs the footer portion.
	 */
	private void addFooter() {
		JLabel footer = new JLabel(StreamConstants.Message.TEXT_FOOTER);
		footer.setFont(StreamConstants.UI.FONT_FOOTER);
		footer.setHorizontalAlignment(SwingConstants.RIGHT);
		footer.setBounds(StreamConstants.UI.BOUNDS_FOOTER);
		contentPanel.add(footer);
	}

	private void presentToUser() {
		mainFrame.setVisible(true);
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
		feedback.getInputMap().put(KeyStroke.getKeyStroke(key), cmd);
		feedback.getActionMap().put(cmd,
				new StreamUIKeyboardShortcut(console, cmd));
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
		feedback.getInputMap().put(KeyStroke.getKeyStroke(dir), cmd);
		feedback.getActionMap().put(cmd,
				new StreamUINavigationShortcut(stream, logger, cmd));
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
	public void repopulateTaskView(int page) {
		if (page > totalPage) {
			page = totalPage;
		} else if (page < 1) {
			page = 1;
		}
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
		pageNumber.setText(String.format(StreamConstants.Message.TEXT_PAGE_NUM,
				pageShown, totalPage));
		loggerDoc.log(StreamLogger.LogLevel.DEBUG,
				"Task viewer moved to page " + pageShown + "/" + totalPage);
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
		if (activeTask != null && !isTaskHighlighted) {
			highlightActiveTaskView();
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
	 * Displays the detailed information of a task in a dialog window.
	 * 
	 * @param task
	 *            - the <b>StreamTask</b> from which the information is obtained
	 *            from
	 */
	public void displayDetails(StreamTask task) {
		JOptionPane.showMessageDialog(mainFrame, String.format(
				StreamConstants.Message.DETAILS_CONTENT,
				task.getTaskName(),
				StreamUtil.displayStatus(task),
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
	 * Opens the help dialog panel.
	 */
	public void openHelpBox() {
		JOptionPane.showMessageDialog(mainFrame,
				StreamConstants.Message.UI_HELP);
	}

	//@author A0093874N-unused

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
		/*
		 * buttons.add(firstPageButton); buttons.add(prevPageButton);
		 * buttons.add(nextPageButton); buttons.add(lastPageButton);
		 */
	}

	/**
	 * Constructs the navigate-to-first-page button.
	 * 
	 * @deprecated
	 */
	private void addFirstPageButton() {
		// firstPageButton = new JButton(StreamConstants.UI.BTN_FIRST);
		/*
		 * firstPageButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * stream.filterAndProcessInput("first"); } });
		 */
		// addComponent(firstPageButton, 25, 500, 160, 32);
	}

	/**
	 * Constructs the navigate-to-previous-page button.
	 * 
	 * @deprecated
	 */
	private void addPrevPageButton() {
		// prevPageButton = new JButton(StreamConstants.UI.BTN_PREV);
		/*
		 * prevPageButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * stream.filterAndProcessInput("prev"); } });
		 */
		// addComponent(prevPageButton, 205, 500, 160, 32);
	}

	/**
	 * Constructs the navigate-to-next-page button.
	 * 
	 * @deprecated
	 */
	private void addNextPageButton() {
		// nextPageButton = new JButton(StreamConstants.UI.BTN_NEXT);
		/*
		 * nextPageButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * stream.filterAndProcessInput("next"); } });
		 */
		// addComponent(nextPageButton, 385, 500, 160, 32);
	}

	/**
	 * Constructs the navigate-to-last-page button.
	 * 
	 * @deprecated
	 */
	private void addLastPageButton() {
		// lastPageButton = new JButton(StreamConstants.UI.BTN_LAST);
		/*
		 * lastPageButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * stream.filterAndProcessInput("last"); } });
		 */
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
		/*
		 * undoButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * stream.filterAndProcessInput(StreamConstants.Commands.UNDO); } });
		 */
	}

	/**
	 * Constructs the clear-search-result button.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addClearSearchButton() {
		// clearSearchButton = new JButton(StreamConstants.UI.BTN_CLEAR);
		/*
		 * clearSearchButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * stream.filterAndProcessInput(StreamConstants.Commands.CLRSRC); } });
		 */
	}

	/**
	 * Determines which navigator buttons are clickable based on the current
	 * page shown and total pages available.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void determineClickableNavigators() {
		/*
		 * firstPageButton.setEnabled(true); prevPageButton.setEnabled(true);
		 * nextPageButton.setEnabled(true); lastPageButton.setEnabled(true); if
		 * (pageShown == 1) { firstPageButton.setEnabled(false);
		 * prevPageButton.setEnabled(false); } if (pageShown == totalPage) {
		 * nextPageButton.setEnabled(false); lastPageButton.setEnabled(false); }
		 */
	}

	//@author A0096529N-unused

	/**
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addMenu() {
		// newTaskTextField = new JTextField();
		// newTaskTextField.setFont(StreamConstants.UI.FONT_CONSOLE);
		// addTaskButton = new JButton(StreamConstants.UI.BTN_ADD_TASK);
		/*
		 * addTaskButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { String params
		 * = newTaskTextField.getText(); newTaskTextField.setText("");
		 * stream.filterAndProcessInput(String.format(
		 * StreamConstants.Commands.ADD_TASK, params)); } });
		 */
		addSortDeadlineButton();
		addSortAlphaButton();
	}

	/**
	 * @deprecated
	 */
	private void addSortAlphaButton() {
		// sortAlphaButton = new JButton(StreamConstants.UI.BTN_SORT_ALPHA);
		/*
		 * sortAlphaButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * stream.filterAndProcessInput(StreamConstants.Commands.SORT_ALPHA); }
		 * });
		 */
	}

	/**
	 * @deprecated
	 */
	private void addSortDeadlineButton() {
		// sortDeadlineButton = new
		// JButton(StreamConstants.UI.BTN_SORT_DEADLINE);
		/*
		 * sortDeadlineButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * stream.filterAndProcessInput(StreamConstants.Commands.SORT_DEADLINE);
		 * } });
		 */
	}

}