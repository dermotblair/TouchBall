package com.dermotblair.touchball;

import java.util.Random;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;

public class BallControllerTask extends AsyncTask<Void, Void, Void> {

	private Ball ballCurrentlyDisplayed = null;
	private MainActivity ownerActivity = null;
	private int consecutiveBallsMissedCount = 0;
	private final String CLASS_NAME = BallControllerTask.class.getSimpleName();
	private int maxX = 0;
	private int maxY = 0;
	private Random randomX = new Random();
	private Random randomY = new Random();
	private int currentColorIndex = 0;
	private boolean continueRunning = true;
	
	// Suppressed error here as Display::getSize() needs at least API 13 but min API required for this app is 8.
	// However this function is only called when the API is greater than or equal to 13.
	@SuppressLint("NewApi")
	void determineMaxXyApiOverTwelve(Display display){
		final Point mdispSize = new Point();
        display.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;
	}
	
	BallControllerTask(MainActivity ownerActivity){
		
		this.ownerActivity = ownerActivity;
		
		// Determine the max x and y values.
		final Display display = ownerActivity.getWindowManager().getDefaultDisplay();
		final int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
		        maxX = display.getWidth();
		        maxY = display.getHeight();
		} else {
			determineMaxXyApiOverTwelve(display);
		}
		
		// Prevent balls going off the screen if their x and y values are too high.
		maxX -= Constants.BALL_DIAMTER;
		maxY -= Constants.BALL_DIAMTER;	
	}
	  
	@Override
	protected Void doInBackground(Void... params) 
	{	
		int preDisplayDelayMillis = 200;
		int postDisplayDelayMillis = 2000;
		
		while(continueRunning){
			
			try {
				Thread.sleep(preDisplayDelayMillis);
			} catch (InterruptedException e) {

				Log.w(Constants.APP_NAME, this.CLASS_NAME + Constants.SCOPE_RESOLUTION_OPERATOR + 
				"doInBackground() Exception occurred when executing Thread.sleep(" + preDisplayDelayMillis + ")");
			}
			
			if(!continueRunning)
				break;
			
			// Check if the last number of consecutive balls that the user 
			// missed have surpassed MAX_CONSECUTIVE_BALLS_MISSED.
			if(ballCurrentlyDisplayed != null)
			{
				if(!ballCurrentlyDisplayed.isTouched())
				{
					consecutiveBallsMissedCount++;
					ownerActivity.updateMissedBallCount(consecutiveBallsMissedCount);
				}
				
				if(consecutiveBallsMissedCount >= Constants.MAX_CONSECUTIVE_BALLS_MISSED)
				{
					continueRunning = false;
					ownerActivity.doGameOver();
					break;
				}
			}
	
			int nextX = randomX.nextInt(maxX);
			int nextY = randomY.nextInt(maxY); 
			int nextColor = this.nextColor();
			
			// Prevent balls from being off the screen if their x and y values are too low.
			if(nextX <= Constants.BALL_DIAMTER)
				nextX += Constants.BALL_DIAMTER; 
			if(nextY <= Constants.BALL_DIAMTER)
			{
				nextY += Constants.BALL_DIAMTER;
			
				// Also increment nextY by the height of the top layout (that contains score and duration)
				// to avoid balls being displayed under this layout
				nextY += ownerActivity.getTopLayoutHeight();	
			}

			ballCurrentlyDisplayed = new Ball(nextX, nextY, nextColor);
			
			this.ownerActivity.updateTouchView();
			
			try {
				Thread.sleep(postDisplayDelayMillis);
			} catch (InterruptedException e) {

				Log.w(Constants.APP_NAME, this.CLASS_NAME + Constants.SCOPE_RESOLUTION_OPERATOR + 
				"doInBackground() Exception occurred when executing Thread.sleep(" + postDisplayDelayMillis + ")");
			}
			
			int delayReductionMillis;
			if(postDisplayDelayMillis >= 1500)
				delayReductionMillis = 20;
			else if(postDisplayDelayMillis >= 1000)
				delayReductionMillis = 15;
			else if(postDisplayDelayMillis >= 500)
				delayReductionMillis = 10;
			else
				delayReductionMillis = 5;
			
			if(postDisplayDelayMillis >= delayReductionMillis)
				postDisplayDelayMillis -= delayReductionMillis;	
		}
		
		return null;
	}
	
	private int nextColor() {
		final int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
			      Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
			      Color.LTGRAY, Color.YELLOW };
		
		if(colors[this.currentColorIndex] == Color.YELLOW){
			this.currentColorIndex = 0;
			return colors[this.currentColorIndex];
		}
		else
			return colors[++this.currentColorIndex];
			
	}
	
	public void setContinueRunning(boolean continueRunning) {
		this.continueRunning = continueRunning;
	}
	
	public Ball getBallCurrentlyDisplayed()
	{
		return ballCurrentlyDisplayed;
	}
	
	public void setConsecutiveBallsMissedCount(int consecutiveBallsMissedCount)
	{
		this.consecutiveBallsMissedCount = consecutiveBallsMissedCount;
	}  
  }
