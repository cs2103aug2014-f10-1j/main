package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import model.StreamTask;
import util.StreamConstants;

public class TaskLogic extends BaseLogic {

	public static TaskLogic init() {
		return new TaskLogic();
	}

	// @author A0093874N
	public ArrayList<String> addTags(StreamTask task, String... tags) {
		logDebug(String.format(StreamConstants.LogMessage.TAGS_TO_ADD, 
				task.getTaskName(), Arrays.toString(tags)));
		ArrayList<String> tagsAdded = new ArrayList<String>();
		for (String tag:tags) {
			if (tag.contains(" ")) {
				addTags(task, tag.split(" "));
			} else {
				tag = tag.toUpperCase();
				if (!task.hasTag(tag)) {
					task.getTags().add(tag);
					tagsAdded.add(tag);
				}
			}
		}

		logDebug(String.format(StreamConstants.LogMessage.TAGS_ADDED,
				task.getTaskName(), Arrays.toString(tagsAdded.toArray())));
		Collections.sort(task.getTags());
		return tagsAdded;
	}

	// @author A0093874N
	public ArrayList<String> removeTags(StreamTask task, String... tags) {
		logDebug(String.format(StreamConstants.LogMessage.TAGS_TO_REMOVE, 
				task.getTaskName(), Arrays.toString(tags)));
		ArrayList<String> tagsRemoved = new ArrayList<String>();
		for (String tag:tags) {
			tag = tag.toUpperCase();
			if (task.hasTag(tag)) {
				task.getTags().remove(tag);
				tagsRemoved.add(tag);
			}
		}

		logDebug(String.format(StreamConstants.LogMessage.TAGS_REMOVED, 
				task.getTaskName(), Arrays.toString(tags)));
		Collections.sort(task.getTags());
		return tagsRemoved;
	}

	@Override 
	protected String getLoggerComponentName() {
		return StreamConstants.ComponentTag.STREAMTASK;
	}

	// @author A0093874N
	/*
	 * migrated from StreamTask.java
	 * 
	private void log(String message) {
		logger.log(LogLevel.DEBUG, message);
	}

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


}
