package util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StreamUtilTest {

	// The test cases below are written by A0118007R for demo during tutorial 09
	// which follows partitioning and boundary cases
	// @author A0118007R

	// Tests for isValidMonth. Input range 1-12. Test cases = 5, 1, 12, 50, -50
	@Test
	// this is an input in the middle of the valid input range
	public void testIsValidMonthOne() throws Exception {
		int input = 5;
		assertTrue(StreamUtil.isValidMonth(input));
	}

	@Test
	// this is an input that is the lower boundary
	public void testIsValidMonthTwo() throws Exception {
		int input = 1;
		assertTrue(StreamUtil.isValidMonth(input));
	}

	@Test
	// this is an input that is the upper boundary
	public void testIsValidMonthThree() throws Exception {
		int input = 12;
		assertTrue(StreamUtil.isValidMonth(input));
	}

	@Test
	// this is an input that is above / larger than the upper bound
	public void testIsValidMonthFour() throws Exception {
		int input = 50;
		assertFalse(StreamUtil.isValidMonth(input));
	}

	@Test
	// this is an input that is smaller than the lower bound
	public void testIsValidMonthFive() throws Exception {
		int input = -50;
		assertFalse(StreamUtil.isValidMonth(input));
	}

	// Tests for isValidDate. Input range 1-31. Test cases = 20, -4, 1, 31, 50
	@Test
	// this is an input in the middle of the valid input range
	public void testIsValidDateOne() throws Exception {
		int input = 20;
		assertTrue(StreamUtil.isValidDate(input, 1, 2100));
	}

	@Test
	// this is an input that is the lower boundary
	public void testIsValidDateTwo() throws Exception {
		int input = 1;
		assertTrue(StreamUtil.isValidDate(input, 1, 2100));
	}

	@Test
	// this is an input that is the upper boundary
	public void testIsValidDateThree() throws Exception {
		int input = 31;
		assertTrue(StreamUtil.isValidDate(input, 1, 2100));
	}

	@Test
	// this is an input that is above / larger than the upper bound
	public void testIsValidDateFour() throws Exception {
		int input = 50;
		assertFalse(StreamUtil.isValidDate(input, 1, 2100));
	}

	@Test
	// this is an input that is smaller than the lower bound
	public void testIsValidDateFive() throws Exception {
		int input = -4;
		assertFalse(StreamUtil.isValidDate(input, 1, 2100));
	}
	
	//Test for is valid year
	
	@Test
	public void testIsValidYearOne(){
		int input = 2013;
		assertFalse(StreamUtil.isValidYear(input));
	}
	
	@Test
	public void testIsValidYearTwo(){
		int input = 2014;
		assertTrue(StreamUtil.isValidYear(input));
	}
	
	@Test
	public void testIsValidYearThree(){
		int input = 2015;
		assertTrue(StreamUtil.isValidYear(input));
	}
	


}
