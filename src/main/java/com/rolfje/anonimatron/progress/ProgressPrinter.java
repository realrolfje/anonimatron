package com.rolfje.anonimatron.progress;

import java.text.DateFormat;

import org.apache.log4j.Logger;

public class ProgressPrinter implements Runnable {
	private static Logger LOG = Logger.getLogger(ProgressPrinter.class);

	private int printIntervalMillis = 4000;
	private Progress progress;
	private boolean printing = false;
	private Thread thread;
	private String message = "";
	private String lastMessage = "";

	private DateFormat timeformat = DateFormat
			.getTimeInstance(DateFormat.MEDIUM);

	public ProgressPrinter(Progress p) {
		progress = p;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPrintIntervalMillis(int printIntervalMillis) {
		this.printIntervalMillis = printIntervalMillis;
	}

	@Override
	public void run() {
		if (progress.getStartTime().getTime() <= 0) {
			LOG.debug("Progress timer was not started by caller, starting it now to get sensible ETA figures.");
			progress.startTimer();
		}

		while (printing) {
			print();
			sleep();
		}
	}

	private void print() {
		String eta = timeformat.format(progress.getETA());
		String toprint = message + " [" + progress.getCompletePercentage()
				+ "%, ETA " + eta + "]";
		toprint = toprint.trim();

		if (!toprint.equals(lastMessage)) {
			// Only print if information changed.
			for (int i = 0; i < lastMessage.length(); i++) {
				// Clear old message with backspaces (does not work in some consoles)
				System.out.print('\b');
			}
			System.out.print(toprint);
			lastMessage = toprint;
		}
	}

	private void sleep() {
		try {
			Thread.sleep(printIntervalMillis);
		} catch (InterruptedException e) {
			// ignore and continue
		}
	}

	public void start() {
		printing = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		if (!printing) {
			return;
		}

		printing = false;

		while (thread != null && thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// ignore, retry
			}
		}
		print(); // Make sure 100% is printed
		thread = null;
	}
}
