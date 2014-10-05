package fileio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stream.Task;

public class FileIOTest {

	private static final String FAIL_EXCEPTION_MESSAGE = "%1$s, caused by %2$s - %3$s";
	private static final String CHECK_FILE = "streamtestCheckFile.json";
	private static final String TEST_SAVE_LOCATION = "streamtest.json";
	private Task task1, task2;
	private HashMap<String, Task> map;

	@Before 
	public void setUp() throws Exception {
		FileIO.SAVE_LOCATION = TEST_SAVE_LOCATION;
		File saveFile = new File(FileIO.SAVE_LOCATION);
		if (!saveFile.createNewFile()) {
			throw new IOException("Test file could not be created - " + saveFile.getAbsolutePath());
		}

		task1 = new Task("Code Jarvis");
		Calendar calendar = Calendar.getInstance();
		Date date = FileIO.dateFormat.parse("20410719000000");
		calendar.setTime(date);		// instead of Calendar.set(), for loadTest, serialized calendar.
		task1.setDeadline(calendar);
		task1.setDescription("Just\na\nRather\nVery\nIntelligent\nSystem");
		task1.addTag("epic");
		task1.addTag("impossible");

		task2 = new Task("Build IoT");
		Calendar calendar2 = Calendar.getInstance();
		Date date2 = FileIO.dateFormat.parse("20180101123456");
		calendar2.setTime(date2);	// instead of Calendar.set(), for loadTest, serialized calendar.
		task2.setDeadline(calendar2);
		task2.setDescription("Internet of Things");
		task2.addTag("epic");
		task2.addTag("popular");
		task2.addTag("urgent");

		map = new HashMap<String, Task>();
		map.put(task1.getTaskName(), task1);
		map.put(task2.getTaskName(), task2);


		String fileContent = "[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\","
				+ "\"taskDescription\":\"Internet of Things\"}]";
		try {
			stringToFile(new File(CHECK_FILE), fileContent);
		} catch (IOException e) {
			throw new IOException("Check file could not be created.");
		}
	}
	@After 
	public void tearDown() throws Exception {
		new File(FileIO.SAVE_LOCATION).delete();
		new File(CHECK_FILE).delete();
	}

	@Test
	public void saveTest() {
		String testMessage = "Write map to file";
		String expectedFileContent = "[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\","
				+ "\"taskDescription\":\"Internet of Things\"}]";
		try {
			File saveFile = new File(FileIO.SAVE_LOCATION);
			FileIO.save(map);
			assertEquals(testMessage, expectedFileContent, fileToString(saveFile));
		} catch (StreamIOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "StreamIOException", e.getMessage()));
		} catch (IOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "StreamIOException", e.getMessage()));
		}
	}
	@Test
	public void loadTest() {
		String testMessage = "Load map from file";
		FileIO.SAVE_LOCATION = CHECK_FILE;
		try {
			String expectedMap = serializeTaskMap(map);
			String actualMap = serializeTaskMap(FileIO.load());
			System.out.println(expectedMap);
			System.out.println(actualMap);
			assertEquals(testMessage, expectedMap, actualMap);
		} catch (StreamIOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "StreamIOException", e.getMessage()));
		} finally {
			FileIO.SAVE_LOCATION = TEST_SAVE_LOCATION;
		}
	}

	@Test 
	public void mapToJsonTest() {
		String testMessage = "Map to JSON conversion";
		String expectedJsonString = "[{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
				+ "\"taskName\":\"Code Jarvis\","
				+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"},"
				+ "{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
				+ "\"taskName\":\"Build IoT\","
				+ "\"taskDescription\":\"Internet of Things\"}]";
		try {
			assertEquals(testMessage, expectedJsonString, FileIO.mapToJson(map).toString());
		} catch (StreamIOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "StreamIOException", e.getMessage()));
		}
	}

	@Test 
	public void taskToJsonTest() {
		testOneTaskToJson("Task to JSON conversion - Code Jarvis", 
				"{\"tags\":[\"EPIC\",\"IMPOSSIBLE\"],\"deadline\":\"20410719000000\","
						+ "\"taskName\":\"Code Jarvis\","
						+ "\"taskDescription\":\"Just\\na\\nRather\\nVery\\nIntelligent\\nSystem\"}", task1);
		testOneTaskToJson("Task to JSON conversion - Build IoT", 
				"{\"tags\":[\"EPIC\",\"POPULAR\",\"URGENT\"],\"deadline\":\"20180101123456\","
						+ "\"taskName\":\"Build IoT\","
						+ "\"taskDescription\":\"Internet of Things\"}", task2);
	}
	private void testOneTaskToJson(String testMessage, String expected, Task task) {
		try {
			assertEquals(testMessage, expected, FileIO.taskToJson(task).toString());
		} catch (StreamIOException e) {
			fail(String.format(FAIL_EXCEPTION_MESSAGE, testMessage, "StreamIOException", e.getMessage()));
		}
	}

	@Test
	public void formatDateTest() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
		testOneFormatDate("Format date 11:22:33 30/05/1980", "19800530112233", simpleDateFormat.parse("19800530112233"));
		testOneFormatDate("Format date 23:00:33 30/12/2055", "20551230230033", simpleDateFormat.parse("20551230230033"));
		testOneFormatDate("Format date 17:09:00 02/10/2014", "21041002170900", simpleDateFormat.parse("21041002170900"));

		Calendar calendar = Calendar.getInstance();
		calendar.set(1965, 7, 9, 0, 0, 0);
		testOneFormatCalendar("Format calendar 00:00:00 09/08/1965", "19650809000000", calendar);
		calendar.set(2000, 0, 1, 12, 34, 56);
		testOneFormatCalendar("Format calendar 12:34:56 01/01/2000", "20000101123456", calendar);
		calendar.set(2014, 11, 24, 23, 59, 59);
		testOneFormatCalendar("Format calendar 23:59:59 24/12/2014", "20141224235959", calendar);
	}
	private void testOneFormatDate(String testMessage, String expected, Date date) {
		assertEquals(testMessage, expected, FileIO.formatDate(date));
	}
	private void testOneFormatCalendar(String testMessage, String expected, Calendar calendar) {
		assertEquals(testMessage, expected, FileIO.formatDate(calendar));
	}

	private String fileToString(File file) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			return stringBuilder.toString().trim();
		}
	}
	private void stringToFile(File destin, String content) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(destin)) {
			if (destin.exists()) {
				destin.delete();
			}
			fos.write(content.getBytes());
		}
	}
	private String serializeTaskMap(HashMap<String, Task> taskMap) {
		JSONObject taskMapJson = new JSONObject(taskMap);
		return taskMapJson.toString();
	}
}
