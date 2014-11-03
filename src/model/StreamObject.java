package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class StreamObject {

	private HashMap<String, StreamTask> taskMap;
	private ArrayList<String> taskList;

	public StreamObject(HashMap<String, StreamTask> taskMap, ArrayList<String> taskList) {
		this.taskMap = taskMap;
		this.taskList = taskList;
	}
	
	public StreamObject() {
		this.taskMap = new HashMap<String, StreamTask>();
		this.taskList = new ArrayList<String>();
	}

	//@author generated
	public HashMap<String, StreamTask> getTaskMap() {
		return taskMap;
	}

	//@author generated
	public void setTaskMap(HashMap<String, StreamTask> taskMap) {
		this.taskMap = taskMap;
	}

	//@author generated
	public ArrayList<String> getTaskList() {
		return taskList;
	}

	//@author generated
	public void setTaskList(ArrayList<String> taskList) {
		this.taskList = taskList;
	}

	// Delegate methods
	
	//@author A0096529N
	public int size() {
		return taskList.size();
	}

	//@author A0096529N
	public StreamTask get(String taskName) {
		return taskMap.get(taskName.toLowerCase());
	}

	//@author A0096529N
	public String get(int index) {
		return taskList.get(index);
	}

	//@author A0096529N
	public StreamTask put(String taskName, StreamTask task) {
		taskList.add(taskName);
		return taskMap.put(taskName.toLowerCase(), task);
	}

	//@author A0096529N
	public StreamTask put(String taskName, StreamTask task, int index) {
		taskList.add(index, taskName);
		return taskMap.put(taskName.toLowerCase(), task);
	}
	
	//@author A0096529N
	public Set<String> keySet() {
		return taskMap.keySet();
	}
	
	//@author A0096529N
	public boolean containsKey(String taskName) {
		return taskMap.containsKey(taskName.toLowerCase());
	}
	
	//@author A0096529N
	public boolean containsValue(StreamTask task) {
		return taskMap.containsValue(task);
	}
	
	//@author A0096529N
	public int indexOf(String taskName) {
		return taskList.indexOf(taskName);
	}
	
	//@author A0096529N
	public boolean contains(String taskName) {
		return taskList.contains(taskName);
	}
	
	//@author A0096529N
	public void remove(String taskName) {
		taskMap.remove(taskName.toLowerCase());
		taskList.remove(taskName);
	}
}
