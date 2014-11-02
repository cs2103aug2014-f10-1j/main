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
import java.util.ArrayList;
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

import util.StreamConstants;
import util.StreamLogger;
import util.StreamLogger.LogLevel;
import exception.StreamIOException;

// @author A0096529N
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
 * @version V0.4
 * @author Steven Khong
 */
public class StreamIO {

	static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.ENGLISH);
	static String SAVE_LOCATION = "default.json";
	private static final StreamLogger logger = StreamLogger.init(StreamConstants.ComponentTag.STREAMIO);

	// @author A0096529N
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
	public static void load(Map<String, StreamTask> taskMap, List<String> taskList) throws StreamIOException {
		JSONObject tasksJson = loadFromFile(new File(SAVE_LOCATION));
		if (tasksJson != null) {
			loadMap(taskMap, tasksJson);
			loadTaskList(taskList, tasksJson);
			logger.log(LogLevel.DEBUG, "Loaded file: " + SAVE_LOCATION);
		} else {
			logger.log(LogLevel.DEBUG, "File not found: " + SAVE_LOCATION);
		}
	}

	// @author A0096529N
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
	public static void save(Map<String, StreamTask> taskMap, List<String> taskList)
			throws StreamIOException {
		try {
			JSONArray taskMapJson = mapToJson(taskMap);
			JSONObject orderListJson = taskListToJson(taskList);
			JSONObject tasksJson = new JSONObject();
			tasksJson.put(TaskKey.TASKMAP, taskMapJson);
			tasksJson.put(TaskKey.TASKLIST, orderListJson);
			writeToFile(new File(SAVE_LOCATION), tasksJson);
			logger.log(LogLevel.DEBUG, "Saved to file: " + SAVE_LOCATION);
		} catch (JSONException e) {
			logger.log(LogLevel.DEBUG, "JSON conversion failed during save - " + e.getMessage());
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	// @author A0096529N
	/**
	 * @param saveLocation file path of storage file to save.
	 */
	public static void setSaveLocation(String saveLocation) {
		SAVE_LOCATION = new File(saveLocation).getAbsolutePath();
	}

	// @author A0093874N
	/**
	 * @return file path of the save location.
	 */
	public static String getSaveLocation() {
		return SAVE_LOCATION;
	}

	// @author A0096529N
	static void loadTaskList(List<String> taskList, JSONObject tasksJson) throws StreamIOException {
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

	// @author A0096529N
	static void loadMap(Map<String, StreamTask> taskMap, JSONObject tasksJson) throws StreamIOException {
		try {
			JSONArray taskMapJson = tasksJson.getJSONArray(TaskKey.TASKMAP);
			HashMap<String, StreamTask> storedTasks = jsonToMap(taskMapJson);
			taskMap.putAll(storedTasks);
		} catch (JSONException e) {
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage(), e);
		}
	}

	// @author A0096529N
	static JSONObject taskListToJson(List<String> taskList) throws StreamIOException {
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

	// @author A0096529N
	static List<String> jsonToTaskList(JSONObject orderListJson) throws StreamIOException {
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

	// @author A0096529N
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

	// @author A0096529N
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

	// @author A0096529N
	static JSONArray mapToJson(Map<String, StreamTask> map)
			throws StreamIOException {
		JSONArray mapJson = new JSONArray();
		for (String key : map.keySet()) {
			JSONObject taskJson = taskToJson(map.get(key));
			mapJson.put(taskJson);
		}
		return mapJson;
	}

	// @author A0096529N
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

	// @author A0096529N
	static JSONObject taskToJson(StreamTask task) throws StreamIOException {
		try {
			JSONObject taskJson = new JSONObject();
			taskJson.put(TaskKey.NAME, task.getTaskName());
			taskJson.put(TaskKey.DESCRIPTION, task.getDescription());
			taskJson.put(TaskKey.TAGS, task.getTags());
			taskJson.put(TaskKey.RANK, task.getRank());
			taskJson.put(TaskKey.STARTTIME, formatDate(task.getStartTime()));
			taskJson.put(TaskKey.DEADLINE, formatDate(task.getDeadline()));
			taskJson.put(TaskKey.DONE, task.isDone());
			return taskJson;
		} catch (JSONException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	// @author A0096529N
	static StreamTask jsonToTask(JSONObject taskJson) throws StreamIOException {
		try {
			String taskName = taskJson.getString(TaskKey.NAME);
			StreamTask task = new StreamTask(taskName);
			if (taskJson.has(TaskKey.DESCRIPTION)){
				task.setDescription(taskJson.getString(TaskKey.DESCRIPTION));				
			}

			if (taskJson.has(TaskKey.STARTTIME)) {
				Calendar startTime = Calendar.getInstance();
				Date startTimeDate = dateFormat.parse(taskJson
						.getString(TaskKey.STARTTIME));
				startTime.setTime(startTimeDate);
				task.setDeadline(startTime);
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
			
			if (taskJson.has(TaskKey.DONE)) {
				Boolean isDone = taskJson.getBoolean(TaskKey.DONE);
				if (isDone) {
					task.markAsDone();
				}
			}
			
			if (taskJson.has(TaskKey.RANK)){
				task.setRank(taskJson.getString(TaskKey.RANK));
			}
			return task;
		} catch (JSONException | ParseException e) {
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		}
	}

	// @author A0096529N
	static String formatDate(Calendar calendar) {
		if (calendar == null) {
			return null;
		} else {
			return formatDate(calendar.getTime());
		}
	}

	// @author A0096529N
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

	public static void saveLogFile(List<String> logMessages, String logFileName)
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
		static final String STARTTIME = "startTime";
		static final String DEADLINE = "deadline";
		static final String NAME = "taskName";
		static final String DESCRIPTION = "taskDescription";
		static final String TAGS = "tags";
		static final String DONE = "done";
		static final String RANK = "rank";
	}
}
