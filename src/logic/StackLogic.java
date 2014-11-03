package logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

import util.StreamConstants;
import util.StreamUtil;
import model.StreamTask;

public class StackLogic {
	private Stack<String> inputStack;
	private Stack<StreamTask> dumpedTasks;
	private Stack<ArrayList<String>> orderingStack;

	private StackLogic() {
		inputStack = new Stack<String>();
		dumpedTasks = new Stack<StreamTask>();
		orderingStack = new Stack<ArrayList<String>>();
	}

	public static StackLogic init() {
		return new StackLogic();
	}

	//@author A0118007R
	public void pushInverseDueCommand(int taskIndex, Calendar currentDeadline) {
		if (currentDeadline == null) {
			inputStack.push(String.format(StreamConstants.Commands.DUE,
					taskIndex, "null"));
		} else {
			// TODO incorporate some JChronic here?
			inputStack.push(String.format(StreamConstants.Commands.DUE,
					taskIndex, getInputDate(currentDeadline)));
		}
	}

	//@author A0119401U
	public void pushInverseStartCommand(int taskIndex, Calendar currentStartTime) {
		if (currentStartTime == null) {
			inputStack.push(String.format(StreamConstants.Commands.START,
					taskIndex, "null"));
		} else {
			inputStack.push(String.format(StreamConstants.Commands.START,
					taskIndex, getInputDate(currentStartTime)));
		}
	}

	//@author A0096529N
	public void pushInverseAddCommand(int index) {
		inputStack.push(String.format(StreamConstants.Commands.DISMISS,
				index));
	}

	//@author A0093874N
	public void pushInverseDeleteCommand(StreamTask deletedTask, ArrayList<String> order) {
		orderingStack.push(order);
		dumpedTasks.push(deletedTask);
		inputStack.push(String.format(StreamConstants.Commands.RECOVER, 1));
	}

	//@author A0096529N
	public void pushInverseClearCommand(ArrayList<String> originalOrder, ArrayList<StreamTask> deletedTasks) {
		orderingStack.add(originalOrder);
		for (StreamTask task:deletedTasks) {
			dumpedTasks.push(task);
		}
		inputStack.push(String.format(StreamConstants.Commands.RECOVER,
				deletedTasks.size()));
	}

	//@author A0093874N
	public void pushInverseSetRankingCommand(int index, String oldRank) {
		inputStack.push(String.format(StreamConstants.Commands.RANK, index, oldRank));
	}

	//@author A0093874N
	public void pushInverseSetDescriptionCommand(int index, String oldDescription) {
		inputStack.push(String.format(StreamConstants.Commands.DESC, index, oldDescription));
	}

	//@author A0096529N
	private String getInputDate(Calendar currentDeadline) {
		return currentDeadline.get(Calendar.DATE) + "/"
				+ (currentDeadline.get(Calendar.MONTH) + 1) + "/"
				+ currentDeadline.get(Calendar.YEAR);
	}

	//@author A0118007R
	public void pushInverseSetDoneCommand(boolean wasDone, int index) {
		if (wasDone) {
			inputStack.push(String.format(StreamConstants.Commands.MARK_DONE, index));
		} else {
			inputStack.push(String.format(StreamConstants.Commands.MARK_NOT_DONE, index));
		}
	}

	//@author A0096529N
	public void pushInverseSetNameCommand(int taskIndex, String oldTaskName) {
		inputStack.push(String.format(StreamConstants.Commands.NAME, taskIndex, oldTaskName));
	}

	//@author A0096529N
	public void pushInverseModifyCommand(String inverseCommand) {
		inputStack.push(inverseCommand.trim());
	}

	//@author A0096529N
	public void pushInverseSortCommand(ArrayList<String> oldOrdering) {
		orderingStack.push(oldOrdering);
		inputStack.push("unsort");
	}

	//@author A0118007R
	public void pushInverseUntagCommand(int taskIndex, ArrayList<String> tagsRemoved) {
		if (tagsRemoved.size() > 0) {
			inputStack.push(String.format(StreamConstants.Commands.TAG, taskIndex,
					StreamUtil.listDownArrayContent(tagsRemoved, " ")));
		}
	}

	//@author A0118007R
	public void pushInverseAddTagCommand(int taskIndex, ArrayList<String> tagsAdded) {
		if (tagsAdded.size() > 0) {
			inputStack.push(String.format(StreamConstants.Commands.UNTAG, taskIndex,
					StreamUtil.listDownArrayContent(tagsAdded, " ")));
		}
	}

	//@author A0118007R
	public String pushInverseModifyCommand(String taskName, int taskIndex,
			StreamTask currTask) {
		String inverseCommand = "modify " + taskIndex + " name " + taskName
				+ " ";
		// TODO help to refactor these
		// added by A0093874N
		Boolean isDone = currTask.isDone();
		if (isDone) {
			inverseCommand += "mark done ";
		} else {
			inverseCommand += "mark ongoing ";
		}
		String oldRank = currTask.getRank();
		inverseCommand += "rank " + oldRank + " ";
		//
		inverseCommand = buildInverseModifyDescription(currTask, inverseCommand);
		inverseCommand = buildInverseModifyDeadline(currTask, inverseCommand);
		inverseCommand = buildInverseModifyTag(currTask, inverseCommand);
		return inverseCommand;
	}

	//@author A0096529N
	private String buildInverseModifyTag(StreamTask currTask, String inverseCommand) {
		inverseCommand += "settags ";
		for (String tag:currTask.getTags()) {
			inverseCommand += tag + " ";
		}
		return inverseCommand;
	}

	//@author A0118007R
	private String buildInverseModifyDeadline(StreamTask currTask,
			String inverseCommand) {
		Calendar oldDue = currTask.getDeadline();
		if (oldDue != null) {
			String dueString = StreamUtil.getCalendarWriteUp(oldDue);
			inverseCommand = inverseCommand + "due " + dueString + " ";
		} else {
			inverseCommand = inverseCommand + "due null ";
		}
		return inverseCommand;
	}

	//@author A0118007R
	private String buildInverseModifyDescription(StreamTask currTask,
			String inverseCommand) {
		String oldDesc = currTask.getDescription();
		if (oldDesc != null) {
			inverseCommand = inverseCommand + "desc " + oldDesc + " ";
		} else {
			inverseCommand = inverseCommand + "desc null ";
		}
		return inverseCommand;
	}

	//@author A0096529N
	public StreamTask recoverTask() {
		return dumpedTasks.pop();
	}

	//@author A0096529N
	public boolean hasInverseInput() {
		return !inputStack.isEmpty();
	}

	//@author A0096529N
	public String popInverseInput() {
		return inputStack.pop();
	}

	//@author A0096529N
	public void pushPlaceholderInput() {
		inputStack.add("CS2103");
	}

	//@author A0096529N
	public ArrayList<String> popOrder() {
		return orderingStack.pop();
	}

	
	// Depreciated methods

	//@author A0118007R
	/**
	 * 
	 * @param inverseCommand
	 * @param oldTags
	 * @param newTags
	 * @return
	 * @deprecated replaced with new methodology
	 * use settags to clear tags then add previous 
	 * tag state
	 */
	public String processTagModification(String inverseCommand,
			ArrayList<String> oldTags, ArrayList<String> newTags) {
		String inverseTag = compareTagged(oldTags, newTags);
		String inverseUntag = compareUntagged(oldTags, newTags);
		if (inverseTag != "tag ") {
			inverseCommand += inverseTag;
		}
		if (inverseUntag != "untag ") {
			inverseCommand += inverseUntag;
		}
		return inverseCommand;
	}

	//@author A0093874N
	/**
	 * 
	 * @param oldTags
	 * @param newTags
	 * @return
	 * @deprecated replaced with new methodology
	 * use settags to clear tags then add previous 
	 * tag state
	 */
	private String compareTagged(ArrayList<String> oldTags,
			ArrayList<String> newTags) {
		String inverseTag = "tag ";
		inverseTag = buildInverseTag(oldTags, newTags, inverseTag);
		return inverseTag;
	}

	//@author A0118007R
	/**
	 * 
	 * @param oldTags
	 * @param newTags
	 * @param inverseTag
	 * @return
	 * @deprecated replaced with new methodology
	 * use settags to clear tags then add previous 
	 * tag state
	 */
	private String buildInverseTag(ArrayList<String> oldTags,
			ArrayList<String> newTags, String inverseTag) {
		for (String old : oldTags) {
			if (!newTags.contains(old)) {
				inverseTag = inverseTag + old + " ";
			}
		}
		return inverseTag;
	}

	//@author A0093874N
	/**
	 * 
	 * @param oldTags
	 * @param newTags
	 * @return
	 * @deprecated replaced with new methodology
	 * use settags to clear tags then add previous 
	 * tag state
	 */
	private String compareUntagged(ArrayList<String> oldTags,
			ArrayList<String> newTags) {
		String inverseUntag = "untag ";
		inverseUntag = buildInverseUntag(oldTags, newTags, inverseUntag);
		return inverseUntag;
	}

	//@author A0118007R
	/**
	 * 
	 * @param oldTags
	 * @param newTags
	 * @param inverseUntag
	 * @return
	 * @deprecated replaced with new methodology
	 * use settags to clear tags then add previous 
	 * tag state
	 */
	private String buildInverseUntag(ArrayList<String> oldTags,
			ArrayList<String> newTags, String inverseUntag) {
		for (String newer : newTags) {
			if (!oldTags.contains(newer)) {
				inverseUntag += inverseUntag + newer + " ";
			}
		}
		return inverseUntag;
	}

}
