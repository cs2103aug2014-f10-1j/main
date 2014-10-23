package parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import stream.Stream;
import exception.StreamParserException;

//@author A0119401U


/* Note to myself: format of the unit testing for exception messages

public void test()
{
    try {
        Shipping.shippingCost('P', -5);
        fail(); // if we got here, no exception was thrown, which is bad
    } 
    catch (Exception e) {
        final String expected = "Legal Values: Package Type must be P or R";
        assertEquals( expected, e.getMessage());
    }        
}
*/


public class StreamParserTest {

	private static Stream st;
	
	@Before
	public void setUp() throws Exception {
		new File("parsertest.json").delete();
		st = new Stream("parsertest");
		
	}


	@Test
	public void parserAddTest() throws Exception {
		//st.filterAndProcessInput("add CS2103 Tutorial");
		//assertEquals("CS2103 Tutorial is included", true, st.hasTask("do CS2103 Tutorial"));
		
		
		try{
			st.filterAndProcessInput(("add "));
		}catch (Exception e) {
			final String expectedMessage = "Nothing to add!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
	}
	
	@Test
	public void parserDescTest() throws Exception {
		//st.filterAndProcessInput("desc 1 finished V0.2");
		
		st.filterAndProcessInput("add CS2103 Tutorial");
		try{
			st.filterAndProcessInput("desc huu okk");
		}catch (Exception e) {
			final String expectedMessage = "Invalid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			st.filterAndProcessInput("desc 1");
		}catch (Exception e) {
			final String expectedMessage = "Not enough information!";
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	
	@Test
	public void parserDelTest() throws Exception {
	
		try{
			st.filterAndProcessInput("del");
		}catch (Exception e) {
			final String expectedMessage = "Invalid input!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
		try{
			st.filterAndProcessInput("del as");
		}catch (Exception e) {
			final String expectedMessage = "Invalid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	
	@Test
	public void parserMarkTest() throws Exception {
		
		//Here is the interesting thing: can we implement this?
		//at first I just want to give an example of invalid input for 
		//mark-as-done. But here if I type done tutorial, it'll be good if
		//Stream can identify the meaning of tutorial and search 
		//for the task with the closest meaning as 'tutorial' and then 
		//mark as done. This will help to improve how well Stream can understand human language
		
		try{
			st.filterAndProcessInput("done tutorial");
			//st.filterAndProcessInput(null);
		}catch (Exception e) {
			final String expectedMessage = "Invalid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	

}
