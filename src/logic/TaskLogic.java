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
	
	private ArrayList<String> failedModifications;

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
				setTags(task, contents);
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
	private void setTags(StreamTask task, String contents) {
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

}