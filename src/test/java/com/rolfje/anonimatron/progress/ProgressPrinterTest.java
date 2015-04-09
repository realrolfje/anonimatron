package com.rolfje.anonimatron.progress;

import junit.framework.TestCase;

public class ProgressPrinterTest extends TestCase {
	
	public void testPrintProgress() throws Exception {
		
		Progress p = new Progress();
		ProgressPrinter printer = new ProgressPrinter(p);
		printer.setPrintIntervalMillis(10);
		
		p.setTotalitemstodo(10000);
		p.setTotalitemscompleted(0);
		
		printer.start();
		
		Thread.sleep(30);
		p.incItemsCompleted(5000);

		Thread.sleep(30);
		p.incItemsCompleted(3000);
		
		Thread.sleep(30);
		p.incItemsCompleted(2000);
		
		Thread.sleep(30);
		
		printer.stop();
	}
}
