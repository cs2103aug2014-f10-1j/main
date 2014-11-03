package parser;

import static org.junit.Assert.*;
import model.StreamObject;

import org.junit.Before;
import org.junit.Test;

import exception.StreamParserException;


public class StreamParserTest {

	StreamParser stparser;
	StreamObject stobj;
	
	@Before
	public void setUp() throws Exception {
		stparser = new StreamParser();
		stobj = new StreamObject();
	}

	@Test
	public void parserAddTest() {
		
		try{
			
			stparser.interpretCommand("add ",stobj);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = "Nothing to add!";
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserDescTest() {
		
		try{
			stparser.interpretCommand("desc -1 ok",stobj);
			fail();
		}catch (Exception e) {
			final String expectedMessage = "Please enter a valid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("desc newcq ok",stobj);
			fail();
		}catch (Exception e) {
			final String expectedMessage = "Please enter a valid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("desc 300 ok",stobj);
			fail();
		}catch (Exception e) {
			final String expectedMessage = "The index you entered is out of range!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			stparser.interpretCommand("desc 1",stobj);
			fail();
		}catch (Exception e) {
			final String expectedMessage = "Please provide more information!";
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserDelTest() {
		
		try{
			
			stparser.interpretCommand("del ",stobj);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = "Please enter a valid command!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			
			stparser.interpretCommand("del as",stobj);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = "Please enter a valid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	
	@Test
	public void parserMarkTest() {
		
		try{
			
			stparser.interpretCommand("done tutorial",stobj);
			fail();
			
		}catch (StreamParserException e) {
			final String expectedMessage = "Please enter a valid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
			
	}
	

}
