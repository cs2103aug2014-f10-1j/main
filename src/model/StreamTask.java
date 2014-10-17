package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;

public class StreamTask {

	// private final String ERROR_NO_DESCRIPTION =
	// "Error: The task \"%1$s\" does not have a description.";
	private final String ERROR_TAG_DOES_NOT_EXIST = "Error: The tag \"%1$s\" does not exist.";
	private final String[] MONTHS = { "January", "February", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };

	// Attributes
	private String taskName;
	private String taskDescription;
	private Calendar deadline;
	private ArrayList<String> tags;
	private boolean done;

	// Constructor
	public StreamTask(String taskName) {
		this.taskName = taskName;
		this.taskDescription = null;
		this.deadline = null;
		this.tags = new ArrayList<String>();
		this.done = false;
	}

	// Name Part
	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String newTaskName) {
		this.taskName = newTaskName;
	}

	// @author A0118007R
	// updated by A0119401U

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
			strDeadline = deadline.get(Calendar.DATE) + " "
					+ MONTHS[(deadline.get(Calendar.MONTH))] + " "
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

	// Description Part
	// @author A0118007R
	/**
	 * The methods below are related to set descriptions to a specified task
	 * 
	 * @author A0118007R
	 */

	public void setDescription(String description) {
		if (description.equals("null")) {
			this.taskDescription = null;
		} else {
			this.taskDescription = description;
		}
	}

	public String getDescription() {
		return this.taskDescription;
	}

	// @author A0119401U
	// Deadline Part
	public void setDeadline(Calendar deadline) {
		this.deadline = deadline;
	}

	public Calendar getDeadline() {
		return this.deadline;
	}

	// @author A0118007R

	public void setNullDeadline() {
		this.deadline = null;
	}

	// @author A0119401U

	// This one needs to be checked later
	public boolean isDue() {
		return deadline.before(Calendar.getInstance());
	}

	// Tags Part
	public void addTag(String newTag) {
		tags.add(newTag.toUpperCase());
		Collections.sort(tags);
	}

	public String getTag(int index) {
		if (tags.isEmpty()) {
			return null;
		} else {
			return tags.get(index);
		}
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public boolean hasTag(String tag) {
		if (tags.contains(tag.toUpperCase())) {
			return true;
		} else {
			return false;
		}
	}

	public void deleteTag(String tag) {
		if (hasTag(tag)) {
			tags.remove(tag.toUpperCase());
		} else {
			System.out.println(String.format(ERROR_TAG_DOES_NOT_EXIST, tag));
		}
	}

	// Mark_As_Done Part

	public boolean isDone() {
		return this.done;
	}

	public void markAsDone() {
		this.done = true;
	}

	public void markAsOngoing() {
		this.done = false;
	}

	// @author A0096529N
	/**
	 * Checks if there is a match between a list of given keywords and the tags
	 * on this task.
	 * 
	 * <p>
	 * Precondition: tags != null
	 * </p>
	 * 
	 * @param tags
	 *            list of keywords
	 * @return true if there is a match, false otherwise.
	 */
	public boolean hasTag(String[] tags) {
		for (String tag : tags) {
			if (this.hasTag(tag)) {
				return true;
			}
		}
		return false;
	}
}