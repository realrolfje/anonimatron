package com.rolfje.anonimatron.progress;

import java.util.Date;

public class Progress {
	private long starttime = 0;
	private long totalitemstodo = 0;
	private long totalitemscompleted = 0;

	public Progress() {
	}

	public void startTimer() {
		starttime = System.currentTimeMillis();
	}
	
	public void reset(){
		setTotalitemscompleted(0);
		startTimer();
	}

	public void incItemsCompleted(long increase) {
		totalitemscompleted += increase;
	}

	public void setTotalitemscompleted(long totalitemscompleted) {
		this.totalitemscompleted = totalitemscompleted;
	}

	public void setTotalitemstodo(long totalitemstodo) {
		this.totalitemstodo = totalitemstodo;
	}

	public int getCompletePercentage() {
		double total = totalitemstodo;
		double done = totalitemscompleted;
		return (int) Math.round((done / total) * 100);
	}

	public Date getETA() {
		if (starttime <=  0){
			throw new UnsupportedOperationException("Can not calculate ETA if starttime is not set.");
		}
		
		long now = System.currentTimeMillis();
		long elapsed = now - starttime;
		long itemstogo = totalitemstodo - totalitemscompleted;
		long timetogo = Math.round((elapsed * itemstogo) / Math.max(1,totalitemscompleted)) ;
		return new Date(now + timetogo);
	}
	
	public Date getStartTime(){
		return new Date(starttime);
	}
	
	
	public long getTotalitemstodo() {
		return totalitemstodo;
	}
	
	public long getTotalitemscompleted() {
		return totalitemscompleted;
	}
}
