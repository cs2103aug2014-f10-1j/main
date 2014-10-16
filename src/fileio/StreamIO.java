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
	 * HashMap<String, Task> object.
	 * </p>
	 * 
	 * @return <strong>HashMap</strong> of tasks if the storage file is present,
	 *         or <strong>null</strong> if there is no storage file found.
	 * @throws StreamIOException
	 *             when JSON conversion fail due file corruption or IO failures
	 *             when loading/accessing storage file.
	 */
	public static HashMap<String, StreamTask> load() throws StreamIOException {
		JSONArray tasksJson = loadFromFile(new File(SAVE_LOCATION));
		if (tasksJson == null) {
			return null;
		} else {
			return jsonToMap(tasksJson);
		}
	}

	/**
	 * <p>
	 * Serializes and write the contents of HashMap<String, Task> object into
	 * storage file.
	 * </p>
	 * 
	 * @param allTasks
	 *            - map of all the tasks
	 * @throws StreamIOException
	 *             when JSON conversion fail due file corruption or IO failures
	 *             when loading/accessing storage file.
	 */
	public static void save(HashMap<String, StreamTask> allTasks)
			throws StreamIOException {
		JSONArray tasksJson = mapToJson(allTasks);
		writeToFile(new File(SAVE_LOCATION), tasksJson);
	}

	static void writeToFile(File destin, JSONArray tasksJson)
			throws StreamIOException {
		try (FileOutputStream fos = new FileOutputStream(destin)) {
			if (destin.exists()) {
				destin.delete();
			}
			fos.write(tasksJson.toString().getBytes());
		} catch (IOException e) {
			throw new StreamIOException("Could not write to file - "
					+ e.getMessage());
		}
	}

	static JSONArray loadFromFile(File file) throws StreamIOException {
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
				return new JSONArray(mapJsonString);
			}
		} catch (IOException e) {
			throw new StreamIOException("Could not load file - "
					+ e.getMessage());
		} catch (JSONException e) {
			throw new StreamIOException(
					"File corrupted, could not parse file contents - "
							+ e.getMessage());
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
					+ e.getMessage());
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
					+ e.getMessage());
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
					+ e.getMessage());
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

	/**
	 * Saves the log file upon exiting.
	 * 
	 * @author Wilson Kurniawan
	 */

	public void saveLogFile(ArrayList<String> logMessages, String logFileName)
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
		static final String DEADLINE = "deadline";
		static final String NAME = "taskName";
		static final String DESCRIPTION = "taskDescription";
		static final String TAGS = "tags";
	}
}
