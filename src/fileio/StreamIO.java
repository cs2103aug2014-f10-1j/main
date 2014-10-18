package fileio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.StreamTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import exception.StreamIOException;

//@author A0096529N
/**
 * <h1>StreamIO - Stream file IO component</h1>
 * 
 * <p>
 * File management component responsible for saving to and loading from storage
 * file containing application state.
 * </p>
 * 
 * <h2>Storage Format</h2>
 * <p>
 * Application state is serialized into JSON format.
 * </p>
 * 
 * <h2>Storage Location</h2>
 * <p>
 * Save location defaults to "stream.json", and may not be modified.
 * </p>
 * 
 * <h2>API</h2> StreamIO.save(HashMap&lt;String, Task&gt;) <br/>
 * StreamIO.load()
 * <p>
 * Refer to method documentation for details.
 * </p>
 * 
 * @author Steven Khong
 * 
 */
public class StreamIO {

	static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.ENGLISH);
	static String SAVE_LOCATION;

	public StreamIO(String saveLocation) {
		SAVE_LOCATION = saveLocation;
	}

	/**
	 * <p>
	 * Reads and inflate the contents of serialized storage file into
	 * StreamObject.
	 * </p>
	 * 
	 * @return <strong>StreamObject</strong> with previous state 
	 * if the storage file is present, nothing modified if storage file not found.
	 * 
	 * @throws StreamIOException
	 *             when JSON conversion fail due file corruption or IO failures
	 *             when loading/accessing storage file.
	 */
	public static void load(Map<String, StreamTask> allTasks, List<String> taskList) throws StreamIOException {
		JSONObject tasksJson = loadFromFile(new File(SAVE_LOCATION));
		if (tasksJson != null) {
			loadMap(allTasks, tasksJson);
			loadTaskList(taskList, tasksJson);
		}
	}

	private static void loadTaskList(List<String> taskList, JSONObject tasksJson) throws StreamIOException {
		try {
			JSONObject orderListJson = tasksJson.getJSONObject(TaskKey.TASKLIST);
			List<String> storedList = jsonToTaskList(orderListJson);
			taskList.addAll(storedList);
		} catch (JSONException e) {
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage(), e);
		}
	}
	private static void loadMap(Map<String, StreamTask> allTasks, JSONObject tasksJson) throws StreamIOException {
		try {
			JSONArray tasksMapJson = tasksJson.getJSONArray(TaskKey.TASKMAP);
			HashMap<String, StreamTask> storedTasks = jsonToMap(tasksMapJson);
			allTasks.putAll(storedTasks);
		} catch (JSONException e) {
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Serializes and write the contents of StreamObject into
	 * storage file.
	 * </p>
	 * 
	 * @param streamObject
	 *            - state of StreamObject to be saved
	 * @throws StreamIOException
	 *             when JSON conversion fail due file corruption or IO failures
	 *             when loading/accessing storage file.
	 */
	public static void save(Map<String, StreamTask> allTasks, List<String> taskList)
			throws StreamIOException {
		try {
			JSONArray tasksMapJson = mapToJson(allTasks);
			JSONObject orderListJson = taskListToJson(taskList);
			JSONObject tasksJson = new JSONObject();
			tasksJson.put(TaskKey.TASKMAP, tasksMapJson);
			tasksJson.put(TaskKey.TASKLIST, orderListJson);
			writeToFile(new File(SAVE_LOCATION), tasksJson);
		} catch (JSONException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	private static JSONObject taskListToJson(List<String> taskList) throws StreamIOException {
		try {
			JSONObject orderListJson = new JSONObject();
			for (int i=0; i<taskList.size(); i++) {
				orderListJson.put(String.valueOf(i), taskList.get(i));
			}
			return orderListJson;
		} catch (JSONException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	private static List<String> jsonToTaskList(JSONObject orderListJson) throws StreamIOException {
		try {
			List<String> taskList = new ArrayList<String>();
			for (int i=0; orderListJson.has(String.valueOf(i)); i++) {
				taskList.add(orderListJson.getString(String.valueOf(i)));
			}
			return taskList;
		} catch (JSONException e) {
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage(), e);
		}
	}

	static void writeToFile(File destin, JSONObject tasksJson)
			throws StreamIOException {
		try (FileOutputStream fos = new FileOutputStream(destin)) {
			if (destin.exists()) {
				destin.delete();
			}
			fos.write(tasksJson.toString().getBytes());
		} catch (IOException e) {
			throw new StreamIOException("Could not write to file - "
					+ e.getMessage(), e);
		}
	}

	static JSONObject loadFromFile(File file) throws StreamIOException {
		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			String mapJsonString = stringBuilder.toString().trim();
			if (mapJsonString.isEmpty()) {
				return null;
			} else {
				return new JSONObject(mapJsonString);
			}
		} catch (IOException e) {
			throw new StreamIOException("Could not load file - "
					+ e.getMessage(), e);
		} catch (JSONException e) {
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage(), e);
		}
	}

	static JSONArray mapToJson(Map<String, StreamTask> map)
			throws StreamIOException {
		JSONArray mapJson = new JSONArray();
		for (String key : map.keySet()) {
			JSONObject taskJson = taskToJson(map.get(key));
			mapJson.put(taskJson);
		}
		return mapJson;
	}

	static HashMap<String, StreamTask> jsonToMap(JSONArray tasksJson)
			throws StreamIOException {
		try {
			HashMap<String, StreamTask> map = new HashMap<String, StreamTask>();
			for (int i = 0; i < tasksJson.length(); i++) {
				StreamTask task = jsonToTask(tasksJson.getJSONObject(i));
				map.put(task.getTaskName().toLowerCase(), task);
			}
			return map;
		} catch (JSONException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	static JSONObject taskToJson(StreamTask task) throws StreamIOException {
		try {
			JSONObject taskJson = new JSONObject();
			taskJson.put(TaskKey.NAME, task.getTaskName());
			taskJson.put(TaskKey.DESCRIPTION, task.getDescription());
			taskJson.put(TaskKey.TAGS, task.getTags());
			taskJson.put(TaskKey.DEADLINE, formatDate(task.getDeadline()));
			return taskJson;
		} catch (JSONException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	static StreamTask jsonToTask(JSONObject taskJson) throws StreamIOException {
		try {
			String taskName = taskJson.getString(TaskKey.NAME);
			StreamTask task = new StreamTask(taskName);
			if (taskJson.has(TaskKey.DESCRIPTION)){
				task.setDescription(taskJson.getString(TaskKey.DESCRIPTION));				
			}

			if (taskJson.has(TaskKey.DEADLINE)) {
				Calendar deadline = Calendar.getInstance();
				Date deadlineDate = dateFormat.parse(taskJson
						.getString(TaskKey.DEADLINE));
				deadline.setTime(deadlineDate);
				task.setDeadline(deadline);
			}

			if (taskJson.has(TaskKey.TAGS)) {
				JSONArray tagsJson = taskJson.getJSONArray(TaskKey.TAGS);
				for (int i = 0; i < tagsJson.length(); i++) {
					task.addTag(tagsJson.getString(i));
				}
			}
			return task;
		} catch (JSONException | ParseException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	static String formatDate(Calendar calendar) {
		if (calendar == null) {
			return null;
		} else {
			return formatDate(calendar.getTime());
		}
	}

	static String formatDate(Date date) {
		if (date == null) {
			return null;
		} else {
			return dateFormat.format(date);
		}
	}

	// @author A0093874N
	
	/**
	 * Saves the log file upon exiting.
	 * 
	 * @author Wilson Kurniawan
	 */

	public void saveLogFile(List<String> logMessages, String logFileName)
			throws IOException {
		File fout = new File(logFileName);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for (int i = 0; i < logMessages.size(); i++) {
			bw.write(logMessages.get(i));
			bw.newLine();
		}
		bw.close();
	}

	private class TaskKey {
		static final String TASKMAP = "allTasks";
		static final String TASKLIST = "taskList";
		
		static final String DEADLINE = "deadline";
		static final String NAME = "taskName";
		static final String DESCRIPTION = "taskDescription";
		static final String TAGS = "tags";
	}
}
