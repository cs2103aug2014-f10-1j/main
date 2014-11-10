package model;

import java.util.ArrayList;
import java.util.Calendar;

import util.StreamConstants;

//@author A0118007R
/**
 * Some documentation.
 * 
 * @version V0.5
 */
public class StreamTask {

	// Attributes
	private String taskName, taskDescription;
	private Calendar startTime, deadline;
	private ArrayList<String> tags;
	private boolean done;
	private String rank;

	// Constructor
	public StreamTask(String taskName) {
		this.taskName = taskName;
		this.taskDescription = null;
		this.startTime = null;
		this.deadline = null;
		this.tags = new ArrayList<String>();
		this.done = false;
		this.rank = "low";
	}

	// Getters and Setters

	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String newTaskName) {
		this.taskName = newTaskName;
	}

	public String getDescription() {
		return this.taskDescription;
	}

	public void setDescription(String description) {
		this.taskDescription = description;
	}

	//@author A0093874N
	public Calendar getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public boolean isOverdue() {
		if (this.getDeadline() == null) {
			return false;
		} else {
			return deadline.before(Calendar.getInstance());
		}
	}

	//@author A0119401U
	public Calendar getDeadline() {
		return this.deadline;
	}

	public void setDeadline(Calendar deadline) {
		this.deadline = deadline;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public String getRank() {
		return this.rank;
	}

	public void setRank(String newRank) {
		this.rank = newRank;
	}

	public boolean isDone() {
		return this.done;
	}

	public void markAsDone() {
		setDone(true);
	}

	public void markAsOngoing() {
		setDone(false);
	}

	//@author A0096529N
	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isTimedTask() {
		return startTime != null;
	}

	public boolean isDeadlineTask() {
		return deadline != null;
	}

	public boolean isFloatingTask() {
		return startTime == null && deadline == null;
	}

	public boolean hasTag(String tag) {
		return tags.contains(tag.toUpperCase());
	}

	public boolean hasTag(String[] tags) {
		for (String tag : tags) {
			if (this.hasTag(tag)) {
				return true;
			}
		}
		return false;
	}

	// Depreciated methods

	//@author A0118007R-unused
	/**
	 * @deprecated - same as setDeadline(null)
	 */
	public void setNullDeadline() {
		this.deadline = null;
	}

	// updated by A0119401U
	/**
	 * @deprecated by A0093874N, since we're implementing UI-based task details
	 *             viewer. thanks for all the hard work though!
	 */
	public void printTaskDetails() {
		System.out.println("Task name = " + taskName);
		String strDesc;
		if (taskDescription == null) {
			strDesc = "Not specified";
		} else {
			strDesc = taskDescription;
		}
		System.out.println("Description = " + strDesc);
		String strDeadline;
		if (deadline == null) {
			strDeadline = "Not specified";
		} else {
			strDeadline = deadline.get(Calendar.DATE)
					+ " "
					+ StreamConstants.Calendar.MONTHS[(deadline
							.get(Calendar.MONTH))] + " "
					+ deadline.get(Calendar.YEAR);
		}
		System.out.println("Deadline = " + strDeadline);
		System.out.print("Tags = ");
		String strTags = "";
		for (String tag : tags) {
			strTags += ", " + tag;
		}
		if (strTags.equals("")) {
			System.out.println("No tags found");
		} else {
			System.out.println(strTags.substring(2));
		}
		if (done) {
			System.out.println("Status: Done");
		} else {
			System.out.println("Status: Not finished");
		}
	}

	/**
	 * @deprecated
	 */
	public void resetTags() {
		this.tags = new ArrayList<String>();
	}

	/**
	 * @deprecated by A0093874N, we don't use methods to get tags by index
	 *             number
	 */
	public String getTag(int index) {
		if (tags.isEmpty()) {
			return null;
		} else {
			return tags.get(index);
		}
	}

	/**
	 * @deprecated
	 */
	public void resetParams() {
		this.taskDescription = null;
		this.deadline = null;
		this.tags = new ArrayList<String>();
	}

	//@author A0119401U-unused
	// This one needs to be checked later
	/**
	 * @deprecated
	 */
	public boolean isDue() {
		if (deadline != null) {
			return deadline.before(Calendar.getInstance());
		} else {
			return false;
		}
	}

}