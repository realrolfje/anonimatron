package com.rolfje.anonimatron.progress;

import java.util.Date;

import junit.framework.TestCase;

public class ProgressTest extends TestCase {
	private Progress progress;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		progress = new Progress();
	}

	public void testPercentageCalculations() throws Exception {
		progress.setTotalitemstodo(1000);
		progress.setTotalitemscompleted(0);

		assertEquals(0, progress.getCompletePercentage());

		progress.setTotalitemscompleted(333);
		assertEquals(33, progress.getCompletePercentage());

		progress.setTotalitemscompleted(495);
		assertEquals(50, progress.getCompletePercentage());

		progress.setTotalitemscompleted(504);
		assertEquals(50, progress.getCompletePercentage());

		progress.setTotalitemscompleted(666);
		assertEquals(67, progress.getCompletePercentage());

		progress.setTotalitemscompleted(754);
		assertEquals(75, progress.getCompletePercentage());

		progress.setTotalitemscompleted(999);
		assertEquals(100, progress.getCompletePercentage());
	}

	public void testETAfifty() throws Exception {
		progress.setTotalitemstodo(100);
		progress.startTimer();

		Thread.sleep(100);
		progress.setTotalitemscompleted(50);

		long now = System.currentTimeMillis();
		long eta = progress.getETA().getTime();
		long start = progress.getStartTime().getTime();

		long expected = now + (now - start);

		assertETA(expected, eta);

	}

	private void assertETA(long expected, long eta) {
		int maxerror = 1;

		long error = Math.abs(Math.round(100F * (eta - expected) / expected));
		assertTrue("ETA is more than " + maxerror + "% off, should be "
				+ expected + "(" + new Date(expected) + ") but was " + eta
				+ "(" + new Date(eta) + ").", error < maxerror);
	}

	public void testETAWeek() throws Exception {
		long millisInWeek = 7L * 24 * 60 * 60 * 100;

		progress.setTotalitemstodo(millisInWeek);
		progress.startTimer();

		Thread.sleep(100);
		progress.setTotalitemscompleted(100);

		long start = progress.getStartTime().getTime();
		long eta = progress.getETA().getTime();
		long expected = start + millisInWeek;

		assertETA(expected, eta);
	}

}
