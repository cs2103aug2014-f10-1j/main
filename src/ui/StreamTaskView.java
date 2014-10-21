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
 * including task name and dates (not implemented yet). In addition, two buttons
 * for immediate task deletion or task marking (both done and not done) are
 * available for user's convenience.
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

		index = new JLabel(String.format(StreamUtil.TEXT_INDEX, ind));
		index.setHorizontalAlignment(SwingConstants.CENTER);
		index.setFont(StreamUtil.FONT_INDEX);
		addComponent(index, new Insets(0, 0, 0, 0), 0, 0, 2, 1, 0,
				(float) 1.0 / 9);

		taskName = new JLabel(task);
		taskName.setHorizontalAlignment(SwingConstants.CENTER);
		taskName.setFont(StreamUtil.FONT_TASK);
		addComponent(taskName, new Insets(0, 0, 0, 0), 1, 0, 1, 1, 0,
				(float) 7.0 / 9);

		timing = new JLabel(StreamUtil.getWrittenTime(startTime, endTime));
		timing.setFont(StreamUtil.FONT_TASK);
		addComponent(timing, new Insets(0, 0, 0, 0), 1, 1, 1, 1, 0,
				(float) 7.0 / 9);

		delButton = new JButton(StreamUtil.BTN_DELETE);
		delButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(StreamUtil.CMD_DELETE,
						index.getText()));
			}
		});
		addComponent(delButton, new Insets(0, 0, 0, 0), 2, 0, 1, 1, 0,
				(float) 1.0 / 9);

		markButton = new JButton();
		addComponent(markButton, new Insets(0, 0, 0, 0), 2, 1, 1, 1, 0,
				(float) 1.0 / 9);
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
		timing.setText(StreamUtil.getWrittenTime(task.getStartTime(), task.getDeadline()));
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
		StreamUtil.clearAllActionListeners(markButton, markButton.getActionListeners());
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
		StreamUtil.clearAllActionListeners(markButton, markButton.getActionListeners());
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
	 * @param gridwidth
	 *            - the grid-wise horizontal length
	 * @param ipady
	 *            - the height of the component
	 * @param weightx
	 *            - the weight of white-space distribution horizontally
	 */
	private void addComponent(Component comp, Insets inset, int gridx,
			int gridy, int gridheight, int gridwidth, int ipady, float weightx) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = inset;
		gbc.weightx = weightx;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridheight = gridheight;
		gbc.gridwidth = gridwidth;
		gbc.ipady = ipady;
		add(comp, gbc);
	}

}