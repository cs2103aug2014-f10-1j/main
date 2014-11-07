package ui;

import java.awt.Color;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.StreamTask;
import util.StreamConstants;
import util.StreamExternals;

//@author A0093874N

/**
 * <p>
 * The task graphical view as viewed by the user. Immediately see-able fields
 * include start/end dates (if applicable), task name, task rank, and task
 * status (done or not done).
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
 * @version V0.5
 */
public class StreamTaskView extends JPanel {

	private JLabel index;
	private StreamUICalendarIcon startCal;
	private StreamUICalendarIcon endCal;
	private JLabel taskName;
	private JLabel rankImage;
	private JLabel statusImage;
	private static final long serialVersionUID = 1L;

	StreamTaskView() {
		super();
		initParams();
		addIndexNumber();
		addStartCalendar();
		addEndCalendar();
		addTaskNameLabel();
		addRankImage();
		addStatusImage();
	}

	private void initParams() {
		setLayout(null);
		Color bgcolor = new Color(228, 228, 228);
		setBackground(bgcolor);
	}

	/**
	 * Adds the index number label to the task view.
	 */
	private void addIndexNumber() {
		index = new JLabel();
		index.setHorizontalAlignment(SwingConstants.CENTER);
		index.setFont(StreamConstants.UI.FONT_INDEX);
		index.setBounds(StreamConstants.UI.BOUNDS_INDEX_NUM);
		add(index);
	}

	/**
	 * Adds the start calendar icon.
	 */
	private void addStartCalendar() {
		startCal = new StreamUICalendarIcon();
		startCal.setBounds(StreamConstants.UI.BOUNDS_START_CAL);
		add(startCal);
	}

	/**
	 * Adds the end calendar icon.
	 */
	private void addEndCalendar() {
		endCal = new StreamUICalendarIcon();
		endCal.setBounds(StreamConstants.UI.BOUNDS_END_CAL);
		add(endCal);
	}

	/**
	 * Adds the task name label to the task view.
	 */
	private void addTaskNameLabel() {
		taskName = new JLabel();
		taskName.setHorizontalAlignment(SwingConstants.CENTER);
		taskName.setFont(StreamConstants.UI.FONT_TASK);
		taskName.setBounds(StreamConstants.UI.BOUNDS_TASK_NAME);
		add(taskName);
	}

	/**
	 * Adds the rank image to the task view.
	 */
	private void addRankImage() {
		rankImage = new JLabel();
		rankImage.setBounds(StreamConstants.UI.BOUNDS_RANK_ICON);
		add(rankImage);
	}

	/**
	 * Adds the status image to the task view.
	 */
	private void addStatusImage() {
		statusImage = new JLabel();
		statusImage.setBounds(StreamConstants.UI.BOUNDS_STATS_ICON);
		add(statusImage);
	}

	private void updateStartTime(Calendar startTime) {
		if (startTime == null) {
			startCal.hideView();
		} else {
			startCal.updateView(startTime);
		}
	}
	
	private void updateEndTime(Calendar endTime) {
		if (endTime == null) {
			endCal.hideView();
		} else {
			endCal.updateView(endTime);
		}
	}
	
	private void updateRank(String rank) {
		switch (rank) {
			case "high":
				rankImage.setIcon(StreamExternals.ICON_HI_RANK);
				break;
			case "medium":
				rankImage.setIcon(StreamExternals.ICON_MED_RANK);
				break;
			case "low":
				rankImage.setIcon(StreamExternals.ICON_LOW_RANK);
				break;
			default:

		}
	}
	
	private void updateDoneStatus(StreamTask task) {
		if (task.isDone()) {
			statusImage.setIcon(StreamExternals.ICON_DONE);
		} else if (task.isOverdue()) {
			statusImage.setIcon(StreamExternals.ICON_OVERDUE);
		} else {
			statusImage.setIcon(StreamExternals.ICON_NOT_DONE);
		}
	}
	
	private void updateBasicParams(Integer ind, String name) {
		index.setText(String.format(StreamConstants.Message.TEXT_INDEX,
				ind.toString()));
		taskName.setText(name);
		setVisible(true);
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
	public void updateView(Integer ind, StreamTask task) {
		updateStartTime(task.getStartTime());
		updateEndTime(task.getDeadline());
		updateRank(task.getRank());
		updateDoneStatus(task);
		updateBasicParams(ind, task.getTaskName());
	}
}