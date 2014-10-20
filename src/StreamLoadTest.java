import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import model.StreamTask;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StreamLoadTest {
	private static final String CREATEFILE_EXCEPTION_MESSAGE = "Test file could not be created "
			+ "- %1$s\nDelete the file if already present.";
	private StreamTask task1, task2;
	private HashMap<String, StreamTask> map;
	private ArrayList<String> taskList;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
			Locale.ENGLISH);
	private File testFile = new File("test.json");

	// @author A0096529N
	@Before public void setUp() throws Exception {
		task1 = new StreamTask("Code Jarvis");
		Calendar calendar = Calendar.getInstance();
		Date date = simpleDateFormat.parse("20410719000000");
		calendar.setTime(date);
		task1.setDeadline(calendar);
		task1.setDescription("Just\na\nRather\nVery\nIntelligent\nSystem");
		task1.addTag("epic");
		task1.addTag("impossible");

		task2 = new StreamTask("Build IoT");
		Calendar calendar2 = Calendar.getInstance();
		Date date2 = simpleDateFormat.parse("20180101123456");
		calendar2.setTime(date2);
		task2.setDeadline(calendar2);
		task2.setDescription("Internet of Things");
		task2.addTag("epic");
		task2.addTag("popular");
		task2.addTag("urgent");

		map = new HashMap<String, StreamTask>();
		map.put(task1.getTaskName().toLowerCase(), task1);
		map.put(task2.getTaskName().toLowerCase(), task2);

		taskList = new ArrayList<String>();
		taskList.add(task1.getTaskName());
		taskList.add(task2.getTaskName());

		String fileContent = "{\"taskList\":{\"1\":\"Build IoT\",\"0\":\"Code Jarvis\"},"
				+ "\"allTasks\":[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\"," + "\"taskDescription\":\"Internet of Things\"}]}";
		try {
			if (testFile.exists()) {
				testFile.delete();
			}
			stringToFile(testFile, fileContent);
		} catch (IOException e) {
			throw new IOException(String.format(CREATEFILE_EXCEPTION_MESSAGE, e.getMessage()), e);
		}
	}

	// @author A0096529N
	@After public void tearDown() throws Exception {
		testFile.delete();
	}

	// @author A0096529N
	@Test public void testLoadMap() {
		Stream stream = new Stream(testFile.getAbsolutePath());
		assertEquals("Loaded task map", serializeTaskMap(map),
				serializeTaskMap(stream.stobj.getTaskMap()));
	}

	// @author A0096529N
	@Test public void testLoadList() {
		Stream stream = new Stream(testFile.getAbsolutePath());
		assertEquals("Loaded task map", taskList, stream.stobj.getTaskList());
	}

	// @author A0096529N
	private void stringToFile(File destin, String content) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(destin)) {
			if (destin.exists()) {
				destin.delete();
			}
			fos.write(content.getBytes());
		}
	}

	// @author A0096529N
	private String serializeTaskMap(HashMap<String, StreamTask> taskMap) {
		JSONObject taskMapJson = new JSONObject(taskMap);
		return taskMapJson.toString();
	}
}