package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import exception.StreamParserException;


public class StreamParserTest {

	StreamParser stparser;
	
	@Before
	public void setUp() throws Exception {
		stparser = new StreamParser();
	}

	@Test
	public void parserAddTest() {
		
		try{
			
			stparser.interpretCommand("add ", 0);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = "Nothing to add!";
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserDescTest() {
		
		try{
			stparser.interpretCommand("desc -1 ok", 0);
			fail();
		}catch (Exception e) {
			final String expectedMessage = "Please enter a valid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("desc newcq ok", 0);
			fail();
		}catch (Exception e) {
			final String expectedMessage = "Please enter a valid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("desc 300 ok", 0);
			fail();
		}catch (Exception e) {
			final String expectedMessage = "The index you entered is out of range!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("desc 1", 0);
			fail();
		}catch (Exception e) {
			final String expectedMessage = "Please provide more information!";
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserDelTest() {
		
		try{
			
			stparser.interpretCommand("del ", 0);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = "Please enter a valid command!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			
			stparser.interpretCommand("del as", 0);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = "Please enter a valid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserMarkTest() {
		
		try{
			
			stparser.interpretCommand("done tutorial", 0);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = "Please enter a valid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	

}
