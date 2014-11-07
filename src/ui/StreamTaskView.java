package ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.StreamTask;
import stream.Stream;
import util.StreamConstants;
import util.StreamUtil;

// @author A0093874N

/**
 * <p>
 * The task graphical view as viewed by the user. Immediately see-able fields
 * including task name and dates. In addition, two buttons for immediate task
 * deletion or task marking (both done and not done) are available for user's
 * convenience.
 * </p>
 * 
 * <h3>API</h3>
 * <ul>
 * <li>StreamTaskView.hideView()</li>
 * <li>StreamTaskView.updateView(final Integer ind, StreamTask task)</li>
 * </ul>
 * <p>
 * Refer to method documentation for details.
 * </p>
 * 
 * @version V0.4
 * @author Wilson Kurniawan
 */
public class StreamTaskView extends JPanel {

	private Stream st;
	private JLabel index;
	private JLabel taskName;
	private JLabel timing;
	private JButton delButton;
	private JButton markButton;
	private static final long serialVersionUID = 1L;

	StreamTaskView(Stream stream) {
		super();
		setLayout(null);
		st = stream;
		addIndexNumber();
		addTaskNameLabel();
		addTimingLabel();
		// addDeleteButton();
		// addMarkButton();
	}

	/**
	 * Adds the index number label to the task view.
	 */
	private void addIndexNumber() {
		index = new JLabel();
		index.setHorizontalAlignment(SwingConstants.CENTER);
		index.setFont(StreamConstants.UI.FONT_INDEX);
		addComponent(index, 0, 0, 100, 70);
	}

	/**
	 * Adds the task name label to the task view.
	 */
	private void addTaskNameLabel() {
		taskName = new JLabel();
		taskName.setHorizontalAlignment(SwingConstants.CENTER);
		taskName.setFont(StreamConstants.UI.FONT_TASK);
		addComponent(taskName, 100, 0, 530, 35);
	}

	/**
	 * Adds the timing label to the task view.
	 */
	private void addTimingLabel() {
		timing = new JLabel();
		timing.setFont(StreamConstants.UI.FONT_TASK);
		addComponent(timing, 100, 35, 530, 35);
	}

	/**
	 * Adds a component to the User Interface based on the determined settings
	 * and dimensions.
	 * 
	 * @param comp
	 *            - the component to be added
	 * @param x
	 *            - the absolute horizontal position
	 * @param y
	 *            - the absolute vertical position
	 * @param height
	 *            - the height of the component
	 * @param width
	 *            - the width of the component
	 */
	private void addComponent(Component comp, int x, int y, int width,
			int height) {
		comp.setBounds(x, y, width, height);
		add(comp);
	}

	/**
	 * Hides the task from the user view. Invoked if the view object has no task
	 * assigned to it.
	 */
	public void hideView() {
		setVisible(false);
	}

	/**
	 * Updates the task view according to the fields supplied by the
	 * <b>StreamTask</b> <i>task</i>, assigning it with index number <i>ind</i>.
	 * 
	 * @param ind
	 *            - the index number assigned
	 * @param task
	 *            - the <b>StreamTask</b> from which the information is obtained
	 *            from
	 */
	public void updateView(final Integer ind, StreamTask task) {
		index.setText(String.format(StreamConstants.Message.TEXT_INDEX,
				ind.toString()));
		taskName.setText(task.getTaskName());
		StreamUtil.clearAllMouseListeners(taskName,
				taskName.getMouseListeners());
		taskName.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// display details on mouse click
				st.filterAndProcessInput(String.format(
						StreamConstants.Commands.VIEW, ind));
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

		});
		timing.setText(StreamUtil.getWrittenTime(task.getStartTime(),
				task.getDeadline()));
		/*
		 * if (task.isDone()) { markButtonNotDone(); } else { markButtonDone();
		 * }
		 */
		setVisible(true);
	}

	/**
	 * Constructs the delete button.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addDeleteButton() {
		delButton = new JButton(StreamConstants.UI.BTN_DELETE);
		delButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(
						StreamConstants.Commands.DELETE, index.getText()
								.substring(1)));
			}
		});/*
			 * addComponent(delButton, StreamConstants.UI.MARGIN_TASKVIEW,
			 * StreamConstants.UI.GRIDX_DELETE_BTN,
			 * StreamConstants.UI.GRIDY_DELETE_BTN,
			 * StreamConstants.UI.GRIDHEIGHT_DELETE_BTN,
			 * StreamConstants.UI.WEIGHTX_DELETE_BTN);
			 */
	}

	/**
	 * Constructs the mark button.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void addMarkButton() {
		markButton = new JButton();
		/*
		 * addComponent(markButton, StreamConstants.UI.MARGIN_TASKVIEW,
		 * StreamConstants.UI.GRIDX_MARK_BTN, StreamConstants.UI.GRIDY_MARK_BTN,
		 * StreamConstants.UI.GRIDHEIGHT_MARK_BTN,
		 * StreamConstants.UI.WEIGHTX_MARK_BTN);
		 */
	}

	/**
	 * Converts the task view side button to a "mark as done" button.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void markButtonDone() {
		markButton.setText(StreamConstants.UI.BTN_MARK_DONE);
		StreamUtil.clearAllActionListeners(markButton,
				markButton.getActionListeners());
		markButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(
						StreamConstants.Commands.MARK_DONE, index.getText()
								.substring(1)));
			}
		});
	}

	/**
	 * Converts the task view side button to a "mark as not done" button.
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private void markButtonNotDone() {
		markButton.setText(StreamConstants.UI.BTN_MARK_NOT_DONE);
		StreamUtil.clearAllActionListeners(markButton,
				markButton.getActionListeners());
		markButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(
						StreamConstants.Commands.MARK_NOT_DONE, index.getText()
								.substring(1)));
			}
		});
	}

}