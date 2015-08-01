package com.dermotblair.touchball;

import android.os.AsyncTask;

public class DurationTimerTask extends AsyncTask<Void, Void, Void>{

	private MainActivity ownerActivity = null;
	private long timeStartedMillis = 0;
	private boolean continueRunning = true;
	private String currentDurationStr = "";
	
	DurationTimerTask(MainActivity ownerActivity, long timeStartedMillis)
	{
		this.ownerActivity = ownerActivity;
		this.timeStartedMillis = timeStartedMillis;
	}

	@Override
	protected Void doInBackground(Void... params) 
	{
		
		while(continueRunning)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			long currentTimeMillis = System.currentTimeMillis();
			long durationMillis = currentTimeMillis - timeStartedMillis;
			
			long durationTotalSecondsLong = durationMillis / 1000;
			int durationTotalSecondsInt = (int)durationTotalSecondsLong;
			int minutes = durationTotalSecondsInt / 60;
			int seconds = durationTotalSecondsInt % 60;
			
			currentDurationStr = minutes + ":" + String.format("%02d", seconds);
			
			this.ownerActivity.updateTime(currentDurationStr);
		}
		
		return null;
	}
	
	public void setContinueRunning(boolean continueRunning) {
		this.continueRunning = continueRunning;
	}
	
	public String getDuration()
	{
		return currentDurationStr;
	}

}
