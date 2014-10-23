package parser;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import stream.Stream;
import model.StreamObject;
import exception.StreamParserException;
import stream.StreamUndoTest;

//@author A0119401U


/*public void test()
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
	StreamObject stobj;
	
	@Before
	public void setUp() throws Exception {
		new File("parsertest.json").delete();
		st = new Stream("parsertest");
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void parserAddTest() throws Exception {
		//st.filterAndProcessInput("add CS2103 Tutorial");
		//assertEquals("CS2103 Tutorial is included", true, st.hasTask("do CS2103 Tutorial"));
		
		
		try{
			st.filterAndProcessInput(("add "));
			//fail();
		}catch (Exception e) {
			final String expectedMessage = "Nothing to add!";
			assertEquals(expectedMessage, e.getMessage());
		}
		
	}
	
	@Test
	public void parserDescTest() throws Exception {
		//st.filterAndProcessInput("desc 1 finished V0.2");
		//st.filterAndProcessInput("desc 2 location: E3-06");
		//st.filterAndProcessInput("desc 3 location: mr2");
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
			final String expectedMessage = "Invalid index!";
			assertEquals(expectedMessage, e.getMessage());
		}
	}
	

}
