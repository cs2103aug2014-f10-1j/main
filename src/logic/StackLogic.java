package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Stack;

import util.StreamConstants;
import util.StreamUtil;
import model.StreamTask;

public class StackLogic extends BaseLogic {
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
		String inverseCommand = null;
		if (currentDeadline == null) {
			inverseCommand = String.format(StreamConstants.Commands.DUE,
					taskIndex, "null");
		} else {
			inverseCommand = String.format(StreamConstants.Commands.DUE,
					taskIndex, StreamUtil.getCalendarWriteUp(currentDeadline));
		}
		pushInput(inverseCommand);
	}

	//@author A0119401U
	public void pushInverseStartCommand(int taskIndex, Calendar currentStartTime) {
		String inverseCommand = null;
		if (currentStartTime == null) {
			inverseCommand = String.format(StreamConstants.Commands.START,
					taskIndex, "null");
		} else {
			inverseCommand = String.format(StreamConstants.Commands.START,
					taskIndex, StreamUtil.getCalendarWriteUp(currentStartTime));
		}
		pushInput(inverseCommand);
	}

	//@author A0096529N
	public void pushInverseAddCommand(int index) {
		pushInput(String.format(StreamConstants.Commands.DISMISS,
				index));
	}

	//@author A0093874N
	public void pushInverseDeleteCommand(StreamTask deletedTask, ArrayList<String> order) {
		pushOrder(order);
		pushDumpedTask(deletedTask);
		pushInput(String.format(StreamConstants.Commands.RECOVER, 1));
	}

	//@author A0096529N
	public void pushInverseClearCommand(ArrayList<String> originalOrder, ArrayList<StreamTask> deletedTasks) {
		pushOrder(originalOrder);
		for (StreamTask task:deletedTasks) {
			pushDumpedTask(task);
		}
		pushInput(String.format(StreamConstants.Commands.RECOVER,
				deletedTasks.size()));
	}

	//@author A0093874N
	public void pushInverseSetRankingCommand(int index, String oldRank) {
		pushInput(String.format(StreamConstants.Commands.RANK, index, oldRank));
	}

	//@author A0093874N
	public void pushInverseSetDescriptionCommand(int index, String oldDescription) {
		pushInput(String.format(StreamConstants.Commands.DESC, index, oldDescription));
	}

	//@author A0118007R
	public void pushInverseSetDoneCommand(boolean wasDone, int index) {
		String inverseCommand = null;
		if (wasDone) {
			inverseCommand = String.format(StreamConstants.Commands.MARK_DONE, index);
		} else {
			inverseCommand = String.format(StreamConstants.Commands.MARK_NOT_DONE, index);
		}
		pushInput(inverseCommand);
	}

	//@author A0096529N
	public void pushInverseSetNameCommand(int taskIndex, String oldTaskName) {
		pushInput(String.format(StreamConstants.Commands.NAME, taskIndex, oldTaskName));
	}

	//@author A0096529N
	public void pushInverseModifyCommand(String inverseCommand) {
		pushInput(inverseCommand.trim());
	}

	//@author A0096529N
	public void pushInverseSortCommand(ArrayList<String> oldOrdering) {
		pushOrder(oldOrdering);
		pushInput("unsort");
	}

	//@author A0118007R
	public void pushInverseUntagCommand(int taskIndex, ArrayList<String> tagsRemoved) {
		if (tagsRemoved.size() > 0) {
			pushInput(String.format(StreamConstants.Commands.TAG, taskIndex,
					StreamUtil.listDownArrayContent(tagsRemoved, " ")));
		}
	}

	//@author A0118007R
	public void pushInverseAddTagCommand(int taskIndex, ArrayList<String> tagsAdded) {
		if (tagsAdded.size() > 0) {
			pushInput(String.format(StreamConstants.Commands.UNTAG, taskIndex,
					StreamUtil.listDownArrayContent(tagsAdded, " ")));
		}
	}

	//@author A0118007R
	public String prepareInverseModifyCommand(String taskName, int taskIndex,
			StreamTask currTask) {
		String inverseCommand = "modify " + taskIndex + " -name " + taskName
				+ " ";
		// TODO help to refactor these
		// added by A0093874N
		Boolean isDone = currTask.isDone();
		if (isDone) {
			inverseCommand += "-mark done ";
		} else {
			inverseCommand += "-mark ongoing ";
		}
		String oldRank = currTask.getRank();
		inverseCommand += "-rank " + oldRank + " ";
		//
		inverseCommand = buildInverseModifyDescription(currTask, inverseCommand);
		inverseCommand = buildInverseModifyDeadline(currTask, inverseCommand);
		inverseCommand = buildInverseModifyTag(currTask, inverseCommand);
		return inverseCommand;
	}

	//@author A0096529N
	private String buildInverseModifyTag(StreamTask currTask, String inverseCommand) {
		inverseCommand += "-settags ";
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
			inverseCommand = inverseCommand + "-due " + dueString + " ";
		} else {
			inverseCommand = inverseCommand + "-due null ";
		}
		return inverseCommand;
	}

	//@author A0118007R
	private String buildInverseModifyDescription(StreamTask currTask,
			String inverseCommand) {
		String oldDesc = currTask.getDescription();
		if (oldDesc != null) {
			inverseCommand = inverseCommand + "-desc " + oldDesc + " ";
		} else {
			inverseCommand = inverseCommand + "-desc null ";
		}
		return inverseCommand;
	}

	//@author A0096529N
	private void pushDumpedTask(StreamTask deletedTask) {
		assert(deletedTask != null) : StreamConstants.Assertion.NULL_INVERSE_TASK;
		dumpedTasks.push(deletedTask);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_INVERSE_TASK, 
				deletedTask.getTaskName()));
	}
	
	//@author A0096529N
	public StreamTask recoverTask() {
		StreamTask dumpedTask = dumpedTasks.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_INVERSE_TASK, 
				dumpedTask.getTaskName()));
		return dumpedTask;
	}

	//@author A0096529N
	private void pushInput(String inverseCommand) {
		assert(inverseCommand != null && !inverseCommand.isEmpty()) : 
			StreamConstants.Assertion.EMPTY_INVERSE_COMMAND;
		inputStack.push(inverseCommand);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_INVERSE_COMMAND, 
				inverseCommand));
	}

	//@author A0096529N
	public String popInverseCommand() {
		String inverseCommand = inputStack.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_INVERSE_COMMAND, 
				inverseCommand));
		return inverseCommand;
	}

	//@author A0096529N
	public boolean hasInverseInput() {
		return !inputStack.isEmpty();
	}

	//@author A0096529N
	public void pushPlaceholderInput() {
		pushInput("placeholderforundo");
	}

	//@author A0096529N
	private void pushOrder(ArrayList<String> order) {
		assert(order != null && !order.isEmpty()) : 
			StreamConstants.Assertion.EMPTY_INVERSE_ORDER;
		orderingStack.push(order);
		logDebug(String.format(StreamConstants.LogMessage.PUSH_ORDER, 
				Arrays.toString(order.toArray())));
	}

	//@author A0096529N
	public ArrayList<String> popOrder() {
		ArrayList<String> order = orderingStack.pop();
		logDebug(String.format(StreamConstants.LogMessage.POP_ORDER, 
				Arrays.toString(order.toArray())));
		return order;
	}

	//@author A0096529N
	/**
	 * @deprecated use StreamUtil's getCalendarWriteUp
	 */
	@SuppressWarnings("unused")
	private String getInputDate(Calendar currentDeadline) {
		return currentDeadline.get(Calendar.MONTH + 1) + "/"
				+ (currentDeadline.get(Calendar.DATE) + "/"
				+ currentDeadline.get(Calendar.YEAR));
	}

	//@author generated
	@Override 
	protected String getLoggerComponentName() {
		return StreamConstants.ComponentTag.STREAMSTACK;
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
