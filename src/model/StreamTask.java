package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import util.StreamConstants;
import util.StreamLogger;
import util.StreamLogger.LogLevel;
import util.StreamUtil;

/**
 * Some documentation.
 * 
 * @version V0.4
 * @author John Kevin Tjahjadi
 */
public class StreamTask {

	// Attributes
	private String taskName;
	private String taskDescription;
	private Calendar startTime;
	private Calendar deadline;
	private ArrayList<String> tags;
	private boolean done;
	private String rank;
	private static StreamLogger logger = StreamLogger.init(StreamConstants.ComponentTag.STREAMOBJECT);

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

	// @author A0118007R

	public String getTaskName() {
		return this.taskName;
	}

	// @author A0118007R

	public void setTaskName(String newTaskName) {
		this.taskName = newTaskName;
	}
	
	// @author A0093874N
	
	private void log(String message) {
		logger.log(LogLevel.DEBUG, message);
	}

	// @author A0118007R

	public String getDescription() {
		return this.taskDescription;
	}

	// @author A0118007R

	/**
	 * @deprecated
	 */
	public void resetParams() {
		this.taskDescription = null;
		this.deadline = null;
		this.tags = new ArrayList<String>();
	}

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

	// @author A0093874N

	public Calendar getStartTime() {
		return this.startTime;
	}

	// @author A0093874N

	public void setNullStartTime() {
		this.startTime = null;
	}

	// @author A0093874N

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}
	
	// @author A0119401U
	
	public String getRank() {
		return this.rank;
	}

	// @author A0119401U

	public Calendar getDeadline() {
		return this.deadline;
	}

	// @author A0118007R

	public void setNullDeadline() {
		this.deadline = null;
	}

	// @author A0119401U

	public void setDeadline(Calendar deadline) {
		this.deadline = deadline;
	}

	// @author A0119401U
	// improved by A0093874N

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
	
	// @author A0119401U
	
	public void setRank(String newRank) {
		this.rank = newRank;
	}

	// @author A0096529N
	public boolean isTimedTask() {
		return startTime != null;
	}

	// @author A0096529N
	public boolean isDeadlineTask() {
		return deadline != null;
	}

	// @author A0096529N
	public boolean isFloatingTask() {
		return startTime == null && deadline == null;
	}

	// TODO @author?

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

	// @author A0119401U

	public ArrayList<String> getTags() {
		return tags;
	}

	// @author A0118007R

	/**
	 * @deprecated
	 */
	public void resetTags() {
		this.tags = new ArrayList<String>();
	}

	// @author A0096529N

	public Boolean addTag(String newTag) {
		if (!hasTag(newTag)) {
			tags.add(newTag.toUpperCase());
			Collections.sort(tags);
			return true;
		} else {
			return false;
		}
	}
	
	// @author A0096529N

	public Boolean deleteTag(String tag) {
		if (hasTag(tag.toUpperCase())) {
			tags.remove(tag.toUpperCase());
			return true;
		} else {
			System.out.println(String.format(StreamConstants.ExceptionMessage.ERR_TAG_DOES_NOT_EXIST,
					tag));
			return false;
		}
	}

	/*
	 * brought from Stream.java 
	 */
	
	public ArrayList<String> addTags(String[] tags) {
		ArrayList<String> tagsAdded = new ArrayList<String>();
		ArrayList<String> tagsNotAdded = new ArrayList<String>();
		int start = 0;
		if (StreamUtil.isInteger(tags[0])) {
			start = 1;
		}
		for (int i = start; i < tags.length; i++) {
			if (addTag(tags[i])) {
				tagsAdded.add(tags[i]);
			} else {
				tagsNotAdded.add(tags[i]);
			}
		}
		// log both tagsAdded and tagsNotAdded
		return tagsAdded;
	}

	public ArrayList<String> removeTags(String[] tags) {
		ArrayList<String> tagsRemoved = new ArrayList<String>();
		ArrayList<String> tagsNotRemoved = new ArrayList<String>();
		int start = 0;
		if (StreamUtil.isInteger(tags[0])) {
			start = 1;
		}
		for (int i = start; i < tags.length; i++) {
			if (deleteTag(tags[i])) {
				tagsRemoved.add(tags[i]);
			} else {
				tagsNotRemoved.add(tags[i]);
			}
		}
		// log both tagsRemoved and tagsNotRemoved
		return tagsRemoved;
	}

/*
	private void logTagsAdded(String taskName, ArrayList<String> tagsAdded,
			ArrayList<String> tagsNotAdded) {
		logAddedTags(taskName, tagsAdded);
		logUnaddedTags(taskName, tagsNotAdded);
	}

	private void logUnaddedTags(String taskName, ArrayList<String> tagsNotAdded) {
		if (!tagsNotAdded.isEmpty()) {
			log(String.format(StreamConstants.LogMessage.TAGS_NOT_ADDED,
					taskName,
					StreamUtil.listDownArrayContent(tagsNotAdded, ", ")));
		}
	}
*/
	
/*
	private void logTagsRemoved(String taskName, ArrayList<String> tagsRemoved,
			ArrayList<String> tagsNotRemoved) {
		logRemovedTags(taskName, tagsRemoved);
		logUnremovedTags(taskName, tagsNotRemoved);
	}

	private void logUnremovedTags(String taskName,
			ArrayList<String> tagsNotRemoved) {
		if (!tagsNotRemoved.isEmpty()) {
			log(String.format(StreamConstants.LogMessage.TAGS_NOT_REMOVED,
					taskName,
					StreamUtil.listDownArrayContent(tagsNotRemoved, ", ")));
		}
	}
*/
	
/*
	private void addTags(String[] tags, StreamTask task,
			ArrayList<String> tagsAdded, ArrayList<String> tagsNotAdded)
			throws StreamModificationException {
		int start = 0;
		processAddingTags(tags, task, tagsAdded, tagsNotAdded, start);
	}

	private void processAddingTags(String[] tags, StreamTask task,
			ArrayList<String> tagsAdded, ArrayList<String> tagsNotAdded,
			int start) throws StreamModificationException {
		if (StreamUtil.isInteger(tags[0])) {
			start = 1;
		}
		for (int i = start; i < tags.length; i++) {
			if (task.addTag(tags[i])) {
				tagsAdded.add(tags[i]);
			} else {
				tagsNotAdded.add(tags[i]);
			}
		}
	}
*/
	
/*
	private void removeTags(String[] tags, StreamTask task,
			ArrayList<String> tagsRemoved, ArrayList<String> tagsNotRemoved)
			throws StreamModificationException {
		int start = 0;
		if (StreamUtil.isInteger(tags[0])) {
			start = 1;
		}
		processRemoveTags(tags, task, tagsRemoved, tagsNotRemoved, start);
	}

	private void processRemoveTags(String[] tags, StreamTask task,
			ArrayList<String> tagsRemoved, ArrayList<String> tagsNotRemoved,
			int start) throws StreamModificationException {
		for (int i = start; i < tags.length; i++) {
			if (task.deleteTag(tags[i])) {
				tagsRemoved.add(tags[i]);
			} else {
				tagsNotRemoved.add(tags[i]);
			}
		}
	}
*/
	// @author A0096529N

	public boolean hasTag(String tag) {
		if (tags.contains(tag.toUpperCase())) {
			return true;
		} else {
			return false;
		}
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

	// @author A0119401U

	public boolean isDone() {
		return this.done;
	}

	// @author A0119401U

	public void markAsDone() {
		this.done = true;
	}

	// @author A0119401U

	public void markAsOngoing() {
		this.done = false;
	}

	// @author A0118007R
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
			strDeadline = deadline.get(Calendar.DATE) + " "
					+ StreamConstants.Calendar.MONTHS[(deadline.get(Calendar.MONTH))] + " "
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
}