package ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.StreamTask;
import stream.Stream;
import util.StreamUtil;

public class StreamTaskView extends JPanel {

	private Stream st;
	private JLabel index;
	private JLabel taskName;
	private JLabel timing;
	private JButton delButton;
	private JButton markButton;
	private static final long serialVersionUID = 1L;

	public StreamTaskView(Integer ind, String task, String startTime,
			String endTime, Stream stream) {
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

		timing = new JLabel(getWrittenTime(startTime, endTime));
		timing.setFont(StreamUtil.FONT_TASK);
		addComponent(timing, new Insets(0, 0, 0, 0), 1, 1, 1, 1, 0,
				(float) 7.0 / 9);

		delButton = new JButton(StreamUtil.BTN_DELETE);
		delButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(StreamUtil.CMD_DELETE, index.getText()));
			}
		});
		addComponent(delButton, new Insets(0, 0, 0, 0), 2, 0, 1, 1, 0,
				(float) 1.0 / 9);

		markButton = new JButton();
		addComponent(markButton, new Insets(0, 0, 0, 0), 2, 1, 1, 1, 0,
				(float) 1.0 / 9);
		// markButtonDone();
	}

	private String getWrittenTime(String startTime, String endTime) {
		if (startTime == null && endTime == null) {
			return "No timing specified";
		} else if (startTime == null) {
			return "By " + endTime;
		} else if (endTime == null) {
			// is there a task like this?
			return "From " + startTime;
		} else {
			return "From " + startTime + " to " + endTime;
		}
	}

	void hideView() {
		setVisible(false);
	}

	void updateView(Integer ind, StreamTask task) {
		index.setText(ind.toString());
		taskName.setText(task.getTaskName());
		// TODO timing-related stuff
		if (task.isDone()) {
			markButtonNotDone();
		} else {
			markButtonDone();
		}
		setVisible(true);
	}
	
	private void clearAllActionListeners(ActionListener[] actions) {
		for (ActionListener action: actions) {
			markButton.removeActionListener(action);
		}
	}

	private void markButtonDone() {
		markButton.setText(StreamUtil.BTN_MARK_DONE);
		clearAllActionListeners(markButton.getActionListeners());
		markButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(StreamUtil.CMD_MARK_DONE,
						index.getText()));
			}
		});
	}

	private void markButtonNotDone() {
		markButton.setText(StreamUtil.BTN_MARK_NOT_DONE);
		clearAllActionListeners(markButton.getActionListeners());
		markButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				st.filterAndProcessInput(String.format(StreamUtil.CMD_MARK_NOT_DONE,
						index.getText()));
			}
		});
	}

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