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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import model.StreamTask;
import stream.Stream;
import util.StreamUtil;

public class StreamUI {

	private Stream stream;

	private JFrame mainFrame;
	private JPanel contentPanel;
	private JTextField console;
	private StreamUILogger logger;

	private JButton undoButton;
	private JButton clearSearchButton;
	private JButton firstPageButton;
	private JButton prevPageButton;
	private JButton nextPageButton;
	private JButton lastPageButton;

	private int pageShown;
	private int totalPage;
	private StreamTaskView[] shownTasks;
	private ArrayList<StreamTask> availTasks;
	private ArrayList<Integer> availIndices;

	public StreamUI(Stream str) {

		stream = str;

		mainFrame = new JFrame(StreamUtil.TEXT_TITLE);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(StreamUtil.WIDTH_MAINFRAME,
				StreamUtil.HEIGHT_MAINFRAME);
		mainFrame.setLocationRelativeTo(null);

		contentPanel = new JPanel();
		contentPanel.setBorder(StreamUtil.MARGIN_MAINFRAME);
		contentPanel.setLayout(new GridBagLayout());
		mainFrame.setContentPane(contentPanel);

		addMainComponents();
		empowerConsole(this.new EnterAction());
		log(StreamUtil.MSG_WELCOME, false);
		pageShown = 1;
		totalPage = 1;
		availTasks = new ArrayList<StreamTask>();
		availIndices = new ArrayList<Integer>();

		mainFrame.setVisible(true);
	}

	private void addMainComponents() {
		addTitle();
		setUpView();
		addButtons();
		addConsole();
		addLogger();
		addFooter();
	}

	private void setUpView() {
		shownTasks = new StreamTaskView[StreamUtil.MAX_VIEWABLE_TASK];
		for (int i = 0; i < StreamUtil.MAX_VIEWABLE_TASK; i++) {
			StreamTaskView taskPanel = new StreamTaskView(null, null, null,
					null, stream);
			taskPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			addComponent(taskPanel, StreamUtil.MARGIN_ELEM,
					StreamUtil.GRIDX_TASK, i + 1, StreamUtil.GRIDWIDTH_TASK,
					StreamUtil.IPADY_TASK);
			shownTasks[i] = taskPanel;
			taskPanel.setVisible(false);
		}
	}

	private void addTitle() {
		JLabel title = new JLabel(StreamUtil.TEXT_HEADER);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(StreamUtil.FONT_TITLE);
		title.setForeground(StreamUtil.COLOR_HEADER);
		addComponent(title, StreamUtil.MARGIN_HEADER, StreamUtil.GRIDX_HEADER,
				StreamUtil.GRIDY_HEADER, StreamUtil.GRIDWIDTH_HEADER,
				StreamUtil.IPADY_HEADER);
	}

	private void addButtons() {
		addUndoButton();
		addFirstPageButton();
		addPrevPageButton();
		addNextPageButton();
		addLastPageButton();
		addClearSearchButton();
	}

	private void addUndoButton() {
		undoButton = new JButton(StreamUtil.BTN_UNDO);
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stream.filterAndProcessInput("undo");
			}
		});
		addComponent(undoButton, StreamUtil.MARGIN_ELEM, StreamUtil.GRIDX_UNDO,
				StreamUtil.GRIDY_BUTTON, StreamUtil.GRIDWIDTH_BUTTON,
				StreamUtil.IPADY_BUTTON);
	}

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

	private void addPrevPageButton() {
		prevPageButton = new JButton(StreamUtil.BTN_PREV);
		prevPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repopulateTaskView(pageShown - 1);
			}
		});
		addComponent(prevPageButton, StreamUtil.MARGIN_ELEM,
				StreamUtil.GRIDX_PREV, StreamUtil.GRIDY_BUTTON,
				StreamUtil.GRIDWIDTH_NAVIG, StreamUtil.IPADY_BUTTON);
	}

	private void addNextPageButton() {
		nextPageButton = new JButton(StreamUtil.BTN_NEXT);
		nextPageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repopulateTaskView(pageShown + 1);
			}
		});
		addComponent(nextPageButton, StreamUtil.MARGIN_ELEM,
				StreamUtil.GRIDX_NEXT, StreamUtil.GRIDY_BUTTON,
				StreamUtil.GRIDWIDTH_NAVIG, StreamUtil.IPADY_BUTTON);
	}

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

	private void addClearSearchButton() {
		clearSearchButton = new JButton(StreamUtil.BTN_CLEAR);
		clearSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO add action
			}
		});
		addComponent(clearSearchButton, StreamUtil.MARGIN_ELEM,
				StreamUtil.GRIDX_CLEAR, StreamUtil.GRIDY_BUTTON,
				StreamUtil.GRIDWIDTH_BUTTON, StreamUtil.IPADY_BUTTON);
	}

	private void addConsole() {
		console = new JTextField();
		console.setFont(StreamUtil.FONT_CONSOLE);
		addComponent(console, StreamUtil.MARGIN_ELEM, StreamUtil.GRIDX_CONSOLE,
				StreamUtil.GRIDY_CONSOLE, StreamUtil.GRIDWIDTH_CONSOLE,
				StreamUtil.IPADY_CONSOLE);
	}

	private void addLogger() {
		logger = new StreamUILogger();
		logger.setFont(StreamUtil.FONT_LOGGER);
		addComponent(logger, StreamUtil.MARGIN_ELEM, StreamUtil.GRIDX_LOGGER,
				StreamUtil.GRIDY_LOGGER, StreamUtil.GRIDWIDTH_LOGGER,
				StreamUtil.IPADY_LOGGER);
	}

	private void addFooter() {
		JLabel footer = new JLabel(StreamUtil.TEXT_FOOTER);
		footer.setFont(StreamUtil.FONT_FOOTER);
		footer.setHorizontalAlignment(SwingConstants.RIGHT);
		addComponent(footer, StreamUtil.MARGIN_FOOTER, StreamUtil.GRIDX_FOOTER,
				StreamUtil.GRIDY_FOOTER, StreamUtil.GRIDWIDTH_FOOTER,
				StreamUtil.IPADY_FOOTER);
	}

	private void addComponent(Component comp, Insets inset, int gridx,
			int gridy, int gridwidth, int ipady) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0 / 3;
		gbc.insets = inset;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridheight = 1;
		gbc.gridwidth = gridwidth;
		gbc.ipady = ipady;
		contentPanel.add(comp, gbc);
	}

	public void log(String logMsg, Boolean isErrorMsg) {
		if (isErrorMsg) {
			logger.showErrorMessage(logMsg);
		} else {
			logger.showLogMessage(logMsg);
		}
	}

	public void empowerConsole(Action action) {
		console.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
				"processInput");
		console.getActionMap().put("processInput", action);
	}

	public void resetAvailableTasks(ArrayList<Integer> indices,
			ArrayList<StreamTask> tasks, Boolean isReset) {
		// error: length not the same
		assert (indices.size() == tasks.size()) : "";
		availIndices = indices;
		availTasks = tasks;
		if (tasks.size() == 0) {
			totalPage = 1;
		} else {
			totalPage = (int) Math.ceil(1.0 * tasks.size()
					/ StreamUtil.MAX_VIEWABLE_TASK);
		}
		if (isReset || tasks.size() == 0) {
			repopulateTaskView(1);
		} else {
			if ((int) Math.ceil(1.0 * tasks.size()
					/ StreamUtil.MAX_VIEWABLE_TASK) < pageShown) {
				repopulateTaskView(pageShown - 1);
			} else {
				repopulateTaskView(pageShown);
			}
		}
	}

	public void repopulateTaskView(int page) {
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

	public void determineClickableNavigators() {
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