package fileio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import logic.TaskLogic;
import model.StreamTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.StreamConstants;
import util.StreamLogger;
import util.StreamLogger.LogLevel;
import exception.StreamIOException;

//@author A0096529N
/**
 * <p>
 * File management component responsible for saving to and loading from storage
 * file containing application state.
 * </p>
 * 
 * <h3>Storage Format</h3>
 * <p>
 * Application state is serialized into JSON format.
 * </p>
 * 
 * <h3>Storage Location</h3>
 * <p>
 * Save location defaults to "stream.json", and may not be modified.
 * </p>
 * 
 * <h3>API</h3>
 * <ul>
 * <li>StreamIO.save(Map&lt;String, StreamTask&gt; taskMap, List&lt;String&gt; taskList) </li>
 * <li>StreamIO.load(Map&lt;String, StreamTask&gt; taskMap, List&lt;String&gt; taskList) </li>
 * <li>StreamIO.getSaveLocation() </li>
 * <li>StreamIO.setSaveLocation(String saveLocation) </li>
 * <li>StreamIO.saveLogFile(List&lt;String&gt; logMessages, String logFileName) </li>
 * </ul>
 * <p>
 * Refer to method documentation for details.
 * </p>
 * 
 * @version V0.5
 */
public class StreamIO {
	
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.ENGLISH);
	static String STREAM_FILENAME = "default.json";
	private static final TaskLogic taskLogic = TaskLogic.init();
	private static final StreamLogger logger = StreamLogger.init(StreamConstants.ComponentTag.STREAMIO);

	/**
	 * Reads and inflate the contents of serialized storage file into
	 * StreamObject.
	 * 
	 * @throws StreamIOException
	 *             when JSON conversion fail due file corruption or IO failures
	 *             when loading/accessing storage file.
	 */
	public static void load(Map<String, StreamTask> taskMap, List<String> taskList) 
			throws StreamIOException {
		File streamFile = new File(getStorageFile(STREAM_FILENAME));
		if (streamFile.exists()) {
			loadAndInflate(streamFile, taskMap, taskList);
		} else {
			loadLegacyStorage(taskMap, taskList);
		}
	}

	/**
	 * Serializes and write the contents of StreamObject into
	 * storage file.
	 * 
	 * @throws StreamIOException
	 *             when JSON conversion fail due file corruption or IO failures
	 *             when loading/accessing storage file.
	 */
	public static void save(Map<String, StreamTask> taskMap, List<String> taskList)
			throws StreamIOException {
		try {
			File streamFile = new File(getStorageFile(STREAM_FILENAME));

			JSONArray taskMapJson = mapToJson(taskMap);
			JSONObject orderListJson = taskListToJson(taskList);
			JSONObject tasksJson = new JSONObject();
			tasksJson.put(TaskKey.TASKMAP, taskMapJson);
			tasksJson.put(TaskKey.TASKLIST, orderListJson);
			writeToFile(streamFile, tasksJson);
			logger.log(LogLevel.DEBUG, "Saved to file: " + getSaveLocation());
		} catch (JSONException e) {
			logger.log(LogLevel.DEBUG, "JSON conversion failed during save - " + e.getMessage());
			throw new StreamIOException("JSON conversion failed - "
					+ e.getMessage(), e);
		} catch (IOException e) {
			logger.log(LogLevel.DEBUG, "IO failure during save - " + e.getMessage());
			throw new StreamIOException("Could not save to file - "
					+ e.getMessage(), e);
		}
	}

	/**
	 * @param saveLocation file path of storage file to save.
	 */
	public static void setSaveLocation(String saveLocation) {
		STREAM_FILENAME = saveLocation;
	}

	/**
	 * @return file path of the save location.
	 * @throws StreamIOException 
	 */
	public static String getSaveLocation() 
			throws StreamIOException {
		return new File(getStorageFile(STREAM_FILENAME)).getAbsolutePath();
	}

	private static void loadAndInflate(File file, Map<String, StreamTask> taskMap, List<String> taskList) 
			throws StreamIOException {
		JSONObject tasksJson = loadFromFile(file);
		if (tasksJson != null) {
			loadMap(taskMap, tasksJson);
			loadTaskList(taskList, tasksJson);
			logger.log(LogLevel.DEBUG, "Loaded file: " + STREAM_FILENAME);
		} else {
			logger.log(LogLevel.DEBUG, "File not found: " + STREAM_FILENAME);
		}
	}

	private static void loadLegacyStorage(Map<String, StreamTask> taskMap, List<String> taskList) 
			throws StreamIOException {
		File streamFile = new File(STREAM_FILENAME);
		if (streamFile.exists()) {
			loadAndInflate(streamFile, taskMap, taskList);
			streamFile.delete();
		}
	}

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

	static void writeToFile(File destin, JSONObject tasksJson)
			throws IOException {
		FileWriter fwriter = new FileWriter(destin, false);
		BufferedWriter bw = new BufferedWriter(fwriter);
		try {
			bw.write(tasksJson.toString());
			bw.newLine();
		} finally {
			try {
				bw.close();
			} catch (Exception e) {
				// ignore exception
			}
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
				task.setStartTime(startTime);
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
					taskLogic.addTags(task, tagsJson.getString(i));
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

	private static String getUserHomeDirectory() {
		String dir = null;
		try {
			dir = System.getProperty("user.home");
		} catch (Exception e) {
			logger.log(LogLevel.ERROR, String.format(
					StreamConstants.LogMessage.LOAD_FAIL_USER_HOME, 
					e.getClass().getSimpleName(), e.getMessage()));
		}
		return dir == null ? "" : dir + File.separator;
	}

	private static String getStreamDirectory() throws StreamIOException {
		String dir = getUserHomeDirectory() + "Documents" + File.separator + "Stream" + File.separator;
		File streamDirectory = new File(dir);
		if (!streamDirectory.exists()) {
			if (!streamDirectory.mkdirs())
				throw new StreamIOException(
						StreamConstants.ExceptionMessage.ERR_CREATE_STREAM_DIR);
		}
		return dir;
	}

	private static String getStorageFile(String filename) 
			throws StreamIOException {
		return getStreamDirectory() + filename;
	}

	private static String getLogsDirectory() 
			throws StreamIOException {
		String dir = getStreamDirectory() + "Logs" + File.separator;
		File streamDirectory = new File(dir);
		if (!streamDirectory.exists()) {
			if (!streamDirectory.mkdirs())
				throw new StreamIOException(
						StreamConstants.ExceptionMessage.ERR_CREATE_LOG_DIR);
		}
		return dir;
	}

	private static String getLogsStorageFile(String logFileName) 
			throws StreamIOException {
		return getLogsDirectory() + logFileName;
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
	
	//@author A0093874N
	/**
	 * Saves the log file upon exiting.
	 * 
	 * @param logMessages
	 *            - the list of log messages to be stored
	 * @param logFileName
	 *            - the name of the log file
	 * @throws StreamIOException
	 *             if IO failures encountered during accessing of log file.
	 */
	public static void saveLogFile(List<String> logMessages, String logFileName) 
			throws StreamIOException {
		try {
			FileWriter fwriter = new FileWriter(getLogsStorageFile(logFileName), true);
			BufferedWriter bw = new BufferedWriter(fwriter);
			try {
				for (int i = 0; i < logMessages.size(); i++) {
					bw.write(logMessages.get(i));
					bw.newLine();
				}
			} finally {
				bw.close();
			}
		} catch (IOException e) {
			throw new StreamIOException(
					StreamConstants.ExceptionMessage.ERR_SAVE_LOG, e);
		}
	}
}