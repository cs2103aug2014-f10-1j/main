package ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.StreamTask;
import stream.Stream;
import util.StreamUtil;

/**
 * The task graphical view as viewed by the user. Immediately see-able fields
 * including task name and dates. In addition, two buttons for immediate task
 * deletion or task marking (both done and not done) are available for user's
 * convenience.
 * 
 * @version V0.2
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

	// @author A0093874N

	public StreamTaskView(Integer ind, String task, Calendar startTime,
			Calendar endTime, Stream stream) {
		super();
		setLayout(new GridBagLayout());
		st = stream;
		addIndexNumber(ind);
		addTaskNameLabel(task);
		addTimingLabel(startTime, endTime);
		addDeleteButton();
		addMarkButton();
	}

	// @author A0093874N

	/**
	 * Adds the index number label to the task view.
	 * 
	 * @author Wilson Kurniawan
	 * @param ind
	 *            - the index number
	 */
	private void addIndexNumber(Integer ind) {
		index = new JLabel(String.format(StreamUtil.TEXT_INDEX, ind));
		index.setHorizontalAlignment(SwingConstants.CENTER);
		index.setFont(StreamUtil.FONT_INDEX);
		addComponent(index, StreamUtil.MARGIN_TASKVIEW, StreamUtil.GRIDX_INDEX,
				StreamUtil.GRIDY_INDEX, StreamUtil.GRIDHEIGHT_INDEX,
				StreamUtil.WEIGHTX_INDEX);
	}

	// @author A0093874N

	/**
	 * Adds the task name label to the task view.
	 * 
	 * @author Wilson Kurniawan
	 * @param task
	 *            - the task name
	 */
	private void addTaskNameLabel(String task) {
		taskName = new JLabel(task);
		taskName.setHorizontalAlignment(SwingConstants.CENTER);
		taskName.setFont(StreamUtil.FONT_TASK);
		addComponent(taskName, StreamUtil.MARGIN_TASKVIEW,
				StreamUtil.GRIDX_TASKNAME, StreamUtil.GRIDY_TASKNAME,
				StreamUtil.GRIDHEIGHT_TASKNAME, StreamUtil.WEIGHTX_TASKNAME);
	}

	// @author A0093874N

	/**
	 * Adds the timing label to the task view.
	 * 
	 * @author Wilson Kurniawan
	 * @param startTime
	 *            - the task's starting time
	 * @param endTime
	 *            - the task's ending time/deadline
	 */
	private void addTimingLabel(Calendar startTime, Calendar endTime) {
		timing = new JLabel(StreamUtil.getWrittenTime(startTime, endTime));
		timing.setFont(StreamUtil.FONT_TASK);
		addComponent(timing, StreamUtil.MARGIN_TASKVIEW,
				StreamUtil.GRIDX_TIMING, StreamUtil.GRIDY_TIMING,
				StreamUtil.GRIDHEIGHT_TIMING, StreamUtil.WEIGHTX_TIMING);
	}

	// @author A0093874N

	/**
	 * Constructs the delete button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addDeleteButton() {
		delButton = new JButton(StreamUtil.BTN_DELETE);
		delButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(StreamUtil.CMD_DELETE,
						index.getText()));
			}
		});
		addComponent(delButton, StreamUtil.MARGIN_TASKVIEW,
				StreamUtil.GRIDX_DELETE_BTN, StreamUtil.GRIDY_DELETE_BTN,
				StreamUtil.GRIDHEIGHT_DELETE_BTN, StreamUtil.WEIGHTX_DELETE_BTN);
	}

	// @author A0093874N

	/**
	 * Constructs the mark button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void addMarkButton() {
		markButton = new JButton();
		addComponent(markButton, StreamUtil.MARGIN_TASKVIEW,
				StreamUtil.GRIDX_MARK_BTN, StreamUtil.GRIDY_MARK_BTN,
				StreamUtil.GRIDHEIGHT_MARK_BTN, StreamUtil.WEIGHTX_MARK_BTN);
	}

	// @author A0093874N

	/**
	 * Hides the task from the user view. Invoked if the view object has no task
	 * assigned to it.
	 * 
	 * @author Wilson Kurniawan
	 */
	void hideView() {
		setVisible(false);
	}

	// @author A0093874N

	/**
	 * Updates the task view according to the fields supplied by the
	 * <b>StreamTask</b> <i>task</i>, assigning it with index number <i>ind</i>
	 * 
	 * @author Wilson Kurniawan
	 * @param ind
	 *            - the index number
	 * @param task
	 *            - the <b>StreamTask</b> where the information is obtained from
	 */
	void updateView(Integer ind, StreamTask task) {
		index.setText(ind.toString());
		taskName.setText(task.getTaskName());
		timing.setText(StreamUtil.getWrittenTime(task.getStartTime(),
				task.getDeadline()));
		if (task.isDone()) {
			markButtonNotDone();
		} else {
			markButtonDone();
		}
		setVisible(true);
	}

	// @author A0093874N

	/**
	 * Converts the task view side button to a "mark as done" button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void markButtonDone() {
		markButton.setText(StreamUtil.BTN_MARK_DONE);
		StreamUtil.clearAllActionListeners(markButton,
				markButton.getActionListeners());
		markButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(
						StreamUtil.CMD_MARK_DONE, index.getText()));
			}
		});
	}

	// @author A0093874N

	/**
	 * Converts the task view side button to a "mark as not done" button.
	 * 
	 * @author Wilson Kurniawan
	 */
	private void markButtonNotDone() {
		markButton.setText(StreamUtil.BTN_MARK_NOT_DONE);
		StreamUtil.clearAllActionListeners(markButton,
				markButton.getActionListeners());
		markButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(
						StreamUtil.CMD_MARK_NOT_DONE, index.getText()));
			}
		});
	}

	// @author A0093874N

	/**
	 * Adds a component to the task view based on the determined settings and
	 * dimensions.
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
	 * @param gridheight
	 *            - the grid-wise vertical length
	 * @param weightx
	 *            - the weight of white-space distribution horizontally
	 */
	private void addComponent(Component comp, Insets inset, int gridx,
			int gridy, int gridheight, float weightx) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = inset;
		gbc.weightx = weightx;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridheight = gridheight;
		add(comp, gbc);
	}

}