package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import parser.StreamParser;
import parser.StreamParser.MarkType;
import parser.StreamParser.RankType;
import model.StreamTask;
import util.StreamConstants;
import util.StreamUtil;

public class TaskLogic extends BaseLogic {

	public static TaskLogic init() {
		return new TaskLogic();
	}

	// @author A0093874N
	public ArrayList<String> addTags(StreamTask task, String... tags) {
		logDebug(String.format(StreamConstants.LogMessage.TAGS_TO_ADD,
				task.getTaskName(), Arrays.toString(tags)));
		ArrayList<String> tagsAdded = new ArrayList<String>();
		for (String tag : tags) {
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
		for (String tag : tags) {
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

	// @author A0093874N
	public String setDeadline(StreamTask task, Calendar calendar) {
		String result = null;
		String parsedCalendar = null;
		if (calendar == null) {
			task.setDeadline(null);
			result = String.format(StreamConstants.LogMessage.DUE_NEVER,
					task.getTaskName());
		} else {
			task.setDeadline(calendar);
			parsedCalendar = StreamUtil.getCalendarWriteUp(calendar);
			result = String.format(StreamConstants.LogMessage.DUE,
					task.getTaskName(), parsedCalendar);
		}
		logDebug(String.format(StreamConstants.LogMessage.SET_DEADLINE,
				task.getTaskName(), parsedCalendar));
		return result;
	}

	// @author A0119401U
	public String setStartTime(StreamTask task, Calendar calendar) {
		String result = null;
		String parsedCalendar = null;
		if (calendar == null) {
			task.setStartTime(null);
			result = String.format(
					StreamConstants.LogMessage.START_NOT_SPECIFIED,
					task.getTaskName());
		} else {
			task.setStartTime(calendar);
			parsedCalendar = StreamUtil.getCalendarWriteUp(calendar);
			result = String.format(StreamConstants.LogMessage.START,
					task.getTaskName(), parsedCalendar);
		}
		logDebug(String.format(StreamConstants.LogMessage.SET_STARTTIME,
				task.getTaskName(), parsedCalendar));
		return result;
	}

	// @author A0118007R
	// updated by A0119401U
	public void modifyTask(StreamTask task, String attribute, String contents) {
		contents = contents.trim();

		switch (attribute) {
			case "-desc":
				description(task, contents);
				break;
			case "-due":
			case "-by":
			case "-to":
				deadline(task, contents);
				break;
			case "-start":
			case "-from":
				startTime(task, contents);
				break;
			case "-tag":
				addTags(task, contents.split(" "));
				break;
			case "-untag":
				removeTags(task, contents.split(" "));
				break;
			case "-settags":
				settags(task, contents);
				break;
			case "-rank":
				rank(task, contents);
				break;
			case "-mark":
				mark(task, contents);
				break;
		}
		logDebug(String.format(StreamConstants.LogMessage.NEW_MODIFICATION,
				task.getTaskName(), attribute, contents));
	}

	// @author A0118007R
	private void description(StreamTask task, String contents) {
		if (contents.equals("null")) {
			task.setDescription(null);
		} else {
			task.setDescription(contents);
		}
	}

	// @author A0118007R
	private void deadline(StreamTask task, String contents) {
		if (contents.equals("null")) {
			task.setDeadline(null);
		} else {
			contents = StreamUtil.parseWithChronic(contents);
			try {
				Calendar due = StreamUtil.parseCalendar(contents);
				Calendar startTime = task.getStartTime();
				if (StreamUtil.isValidDeadline(due, startTime)) {
					task.setDeadline(due);				
				}
			} catch (Exception e) {
				
			}
		}
	}

	// @author A0118007R
	private void startTime(StreamTask task, String contents) {
		if (contents.equals("null")) {
			task.setStartTime(null);
		} else {
			contents = StreamUtil.parseWithChronic(contents);
			try {
				Calendar start = StreamUtil.parseCalendar(contents);
				Calendar deadline = task.getDeadline();
				if (StreamUtil.isValidStartTime(deadline, start)) {
					task.setStartTime(start);				
				}
			} catch (Exception e) {
				
			}
		}
	}

	// @author A0096529N
	private void settags(StreamTask task, String contents) {
		task.getTags().clear();
		if (!contents.trim().isEmpty()) {
			addTags(task, contents.split(" "));
		}
	}

	// @author A0118007R
	private void mark(StreamTask task, String contents) {
		String status = contents.trim();
		MarkType parsedMarkType = StreamParser.parseMarking(status);
		switch (parsedMarkType) {
			case DONE:
				task.markAsDone();
				break;
			case NOT:
				task.markAsOngoing();
				break;
			default:
		}
	}

	// @author A0119401U
	private void rank(StreamTask task, String contents) {
		String inputRank = contents.trim();
		RankType parsedRankType = StreamParser.parseRanking(inputRank);
		switch (parsedRankType) {
			case HI:
			case MED:
			case LO:
				String translatedRank = StreamParser
						.translateRanking(parsedRankType);
				task.setRank(translatedRank);
			default:
		}
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
			throws Stream



ationException {
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
