package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

//@author A0118007R
/**
 * Documentation for StreamObject.
 * 
 * @version V0.5
 */
public class StreamObject {

	private HashMap<String, StreamTask> taskMap;
	private ArrayList<String> taskList;

	private StreamObject(HashMap<String, StreamTask> taskMap,
			ArrayList<String> taskList) {
		this.taskMap = taskMap;
		this.taskList = taskList;
	}

	private StreamObject() {
		this.taskMap = new HashMap<String, StreamTask>();
		this.taskList = new ArrayList<String>();
	}

	public static StreamObject getInstance() {
		return new StreamObject();
	}

	public static StreamObject getInstance(HashMap<String, StreamTask> taskMap,
			ArrayList<String> taskList) {
		return new StreamObject(taskMap, taskList);
	}

	//@author generated
	public HashMap<String, StreamTask> getTaskMap() {
		return taskMap;
	}

	public void setTaskMap(HashMap<String, StreamTask> taskMap) {
		this.taskMap = taskMap;
	}

	public ArrayList<String> getTaskList() {
		return taskList;
	}

	public void setTaskList(ArrayList<String> taskList) {
		this.taskList = taskList;
	}

	//@author A0096529N

	// Delegate methods

	public int size() {
		return taskList.size();
	}

	public StreamTask get(String taskName) {
		return taskMap.get(taskName.toLowerCase());
	}

	public String get(int index) {
		return taskList.get(index);
	}

	public StreamTask put(String taskName, StreamTask task) {
		taskList.add(taskName);
		return taskMap.put(taskName.toLowerCase(), task);
	}

	public StreamTask put(String taskName, StreamTask task, int index) {
		taskList.add(index, taskName);
		return taskMap.put(taskName.toLowerCase(), task);
	}

	public Set<String> keySet() {
		return taskMap.keySet();
	}

	public boolean containsKey(String taskName) {
		return taskMap.containsKey(taskName.toLowerCase());
	}

	public boolean containsValue(StreamTask task) {
		return taskMap.containsValue(task);
	}

	public int indexOf(String taskName) {
		return taskList.indexOf(taskName);
	}

	public boolean contains(String taskName) {
		return taskList.contains(taskName);
	}

	public void remove(String taskName) {
		taskMap.remove(taskName.toLowerCase());
		taskList.remove(taskName);
	}

	public void clear() {
		taskMap.clear();
		taskList.clear();
	}
}
