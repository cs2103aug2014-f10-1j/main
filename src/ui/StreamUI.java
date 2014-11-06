package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
		InputStream titleFont = getClass().getResourceAsStream(
				"/fonts/Awesome Java.ttf");
		InputStream indexFont = getClass().getResourceAsStream(
				"/fonts/Awesome Java.ttf");
		InputStream consoleFont = getClass().getResourceAsStream(
				"/fonts/Ubuntu.ttf");
		InputStream loggerFont = getClass().getResourceAsStream(
				"/fonts/Ubuntu.ttf");
		InputStream footerFont = getClass().getResourceAsStream(
				"/fonts/Ubuntu.ttf");
		InputStream taskFont = getClass().getResourceAsStream(
				"/fonts/Ubuntu.ttf");
		StreamConstants.UI.initFonts(titleFont, indexFont, consoleFont,
				loggerFont, footerFont, taskFont);

		setupLookAndFeel();
		addMainFrame();
		addContentPanel();
		addHeader();
		// addMenu();
		setUpView();
		// addUndoButton();
		addNavigationButtons();
		// addClearSearchButton();
		addConsole();
		HashMap<String, String> helpTexts = new HashMap<String, String>();
		// TODO SOMEBODY HALP ME FILL THIS IN
		helpTexts.put("add", "Add a new task here");
		helpTexts.put("due", "Set the deadline for a task based on index number");
		helpTexts.put("start", "Set the start time for a task based on index number");
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
		helpTexts.put("rank", "Change the rank of a certain task based on index number");
		helpTexts.put("first", "Go to the first page");
		helpTexts.put("last", "Go to the last page");
		helpTexts.put("next", "Go to the next page");
		helpTexts.put("prev", "Go to the previous page");
		helpTexts.put("undo", "Undo the last action");
		helpTexts.put("exit", "Exits the program");
		for (String h : helpTexts.keySet()) {
			console.addPossibility(h, helpTexts.get(h));
		}
		empowerConsole(new StreamUIConsoleEnterAction(stream, console));
		addLogger();

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

		HashMap<String, String> navig = new HashMap<String, String>();
		navig.put("DOWN", "first");
		navig.put("LEFT", "prev");
		navig.put("RIGHT", "next");
		navig.put("UP", "last");
		for (String s : navig.keySet()) {
			empowerNavigationShortcuts(s, navig.get(s));
		}
		addFooter();
		log(StreamConstants.Message.WELCOME, false);
		pageShown = 1;
		totalPage = 1;
		availTasks = new ArrayList<StreamTask>();
		availIndices = new ArrayList<Integer>();

		Vector<Component> order = new Vector<Component>(2);
		// order.add(newTaskTextField);
		order.add(console);
		order.add(logger);
		mainFrame.setFocusTraversalPolicy(new StreamUIFocusTraversal(order));

		mainFrame.setVisible(true);
	}

	/**
	 * Constructs the main frame for Stream's User Interface.
	 */
	private void addMainFrame() {
		mainFrame = new JFrame(StreamConstants.Message.TEXT_TITLE);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(StreamConstants.UI.WIDTH_MAINFRAME,
				StreamConstants.UI.HEIGHT_MAINFRAME);
		mainFrame.setLocationRelativeTo(null);
	}

	/**
	 * Constructs the content panel for Stream's User Interface.
	 */
	private void addContentPanel() {
		contentPanel = new JPanel();
		contentPanel.setBorder(StreamConstants.UI.MARGIN_MAINFRAME);
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setLayout(new GridBagLayout());
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
			addComponent(taskPanel, StreamConstants.UI.MARGIN_ELEM,
					StreamConstants.UI.GRIDX_TASK, i
							+ StreamConstants.UI.GRIDY_TASK_START,
					StreamConstants.UI.GRIDWIDTH_TASK,
					StreamConstants.UI.IPADY_TASK);
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
		addComponent(title, StreamConstants.UI.MARGIN_HEADER,
				StreamConstants.UI.GRIDX_HEADER,
				StreamConstants.UI.GRIDY_HEADER,
				StreamConstants.UI.GRIDWIDTH_HEADER,
				StreamConstants.UI.IPADY_HEADER);
	}

	/**
	 * Constructs the usable buttons of Stream's User Interface.
	 */
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
	 */
	private void addFirstPageButton() {
		firstPageButton = new JButton(StreamConstants.UI.BTN_FIRST);
		firstPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput("first");
			}
		});
		addComponent(firstPageButton, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_FIRST,
				StreamConstants.UI.GRIDY_BUTTON,
				StreamConstants.UI.GRIDWIDTH_NAVIG,
				StreamConstants.UI.IPADY_BUTTON);
	}

	/**
	 * Constructs the navigate-to-previous-page button.
	 */
	private void addPrevPageButton() {
		prevPageButton = new JButton(StreamConstants.UI.BTN_PREV);
		prevPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput("prev");
			}
		});
		addComponent(prevPageButton, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_PREV, StreamConstants.UI.GRIDY_BUTTON,
				StreamConstants.UI.GRIDWIDTH_NAVIG,
				StreamConstants.UI.IPADY_BUTTON);
	}

	/**
	 * Constructs the navigate-to-next-page button.
	 */
	private void addNextPageButton() {
		nextPageButton = new JButton(StreamConstants.UI.BTN_NEXT);
		nextPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput("next");
			}
		});
		addComponent(nextPageButton, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_NEXT, StreamConstants.UI.GRIDY_BUTTON,
				StreamConstants.UI.GRIDWIDTH_NAVIG,
				StreamConstants.UI.IPADY_BUTTON);
	}

	/**
	 * Constructs the navigate-to-last-page button.
	 */
	private void addLastPageButton() {
		lastPageButton = new JButton(StreamConstants.UI.BTN_LAST);
		lastPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput("last");
			}
		});
		addComponent(lastPageButton, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_LAST, StreamConstants.UI.GRIDY_BUTTON,
				StreamConstants.UI.GRIDWIDTH_NAVIG,
				StreamConstants.UI.IPADY_BUTTON);
	}

	/**
	 * Constructs the console for user input.
	 */
	private void addConsole() {
		console = new StreamUIConsole();
		console.setFont(StreamConstants.UI.FONT_CONSOLE);
		addComponent(console, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_CONSOLE,
				StreamConstants.UI.GRIDY_CONSOLE,
				StreamConstants.UI.GRIDWIDTH_CONSOLE,
				StreamConstants.UI.IPADY_CONSOLE);
	}

	/**
	 * Constructs the logger panel to display terminal response.
	 */
	private void addLogger() {
		logger = new StreamUILogger();
		logger.setFont(StreamConstants.UI.FONT_LOGGER);
		addComponent(logger, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_LOGGER,
				StreamConstants.UI.GRIDY_LOGGER,
				StreamConstants.UI.GRIDWIDTH_LOGGER,
				StreamConstants.UI.IPADY_LOGGER);
	}

	/**
	 * Constructs the footer portion.
	 */
	private void addFooter() {
		JLabel footer = new JLabel(StreamConstants.Message.TEXT_FOOTER);
		footer.setFont(StreamConstants.UI.FONT_FOOTER);
		footer.setHorizontalAlignment(SwingConstants.RIGHT);
		addComponent(footer, StreamConstants.UI.MARGIN_FOOTER,
				StreamConstants.UI.GRIDX_FOOTER,
				StreamConstants.UI.GRIDY_FOOTER,
				StreamConstants.UI.GRIDWIDTH_FOOTER,
				StreamConstants.UI.IPADY_FOOTER);
	}

	/**
	 * Adds a component to the User Interface based on the determined settings
	 * and dimensions.
	 * 
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
		for (JButton b : buttons) {
			b.getInputMap().put(KeyStroke.getKeyStroke(key), cmd);
			b.getActionMap().put(cmd,
					new StreamUIKeyboardShortcut(console, cmd));
		}
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
		for (JButton b : buttons) {
			b.getInputMap().put(KeyStroke.getKeyStroke(dir), cmd);
			b.getActionMap().put(cmd,
					new StreamUINavigationShortcut(stream, logger, cmd));
		}
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
		determineClickableNavigators();
	}

	/**
	 * Determines which navigator buttons are clickable based on the current
	 * page shown and total pages available.
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
	 * Constructs the undo button.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addUndoButton() {
		undoButton = new JButton(StreamConstants.UI.BTN_UNDO);
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamConstants.Commands.UNDO);
			}
		});
		addComponent(undoButton, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_UNDO, StreamConstants.UI.GRIDY_BUTTON,
				StreamConstants.UI.GRIDWIDTH_BUTTON,
				StreamConstants.UI.IPADY_BUTTON);
	}

	/**
	 * Constructs the clear-search-result button.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addClearSearchButton() {
		clearSearchButton = new JButton(StreamConstants.UI.BTN_CLEAR);
		clearSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamConstants.Commands.CLRSRC);
			}
		});
		addComponent(clearSearchButton, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_CLEAR,
				StreamConstants.UI.GRIDY_BUTTON,
				StreamConstants.UI.GRIDWIDTH_BUTTON,
				StreamConstants.UI.IPADY_BUTTON);
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
		addComponent(newTaskTextField, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_ADD_TASK_TEXTFIELD,
				StreamConstants.UI.GRIDY_MENU,
				StreamConstants.UI.GRIDWIDTH_INPUT,
				StreamConstants.UI.IPADY_BUTTON);

		addTaskButton = new JButton(StreamConstants.UI.BTN_ADD_TASK);
		addTaskButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String params = newTaskTextField.getText();
				newTaskTextField.setText("");
				stream.filterAndProcessInput(String.format(
						StreamConstants.Commands.ADD_TASK, params));
			}
		});
		addComponent(addTaskButton, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_ADD_TASK_BTN,
				StreamConstants.UI.GRIDY_MENU,
				StreamConstants.UI.GRIDWIDTH_NAVIG,
				StreamConstants.UI.IPADY_BUTTON);

		addSortDeadlineButton();
		addSortAlphaButton();
	}

	/**
	 * @deprecated
	 */
	private void addSortAlphaButton() {
		sortAlphaButton = new JButton(StreamConstants.UI.BTN_SORT_ALPHA);
		sortAlphaButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamConstants.Commands.SORT_ALPHA);
			}
		});
		addComponent(sortAlphaButton, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_SORT_ALPHA,
				StreamConstants.UI.GRIDY_MENU,
				StreamConstants.UI.GRIDWIDTH_NAVIG,
				StreamConstants.UI.IPADY_BUTTON);
	}

	/**
	 * @deprecated
	 */
	private void addSortDeadlineButton() {
		sortDeadlineButton = new JButton(StreamConstants.UI.BTN_SORT_DEADLINE);
		sortDeadlineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput(StreamConstants.Commands.SORT_DEADLINE);
			}
		});
		addComponent(sortDeadlineButton, StreamConstants.UI.MARGIN_ELEM,
				StreamConstants.UI.GRIDX_SORT_DEADLINE,
				StreamConstants.UI.GRIDY_MENU,
				StreamConstants.UI.GRIDWIDTH_NAVIG,
				StreamConstants.UI.IPADY_BUTTON);
	}

}