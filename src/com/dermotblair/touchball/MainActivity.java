package com.dermotblair.touchball;

import java.util.ArrayList;

import com.dermotblair.touchball.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private BallControllerTask ballControllerTask = null;
	private DurationTimerTask durationTimerTask = null;
	private MusicTask musicTask = null;
	private PopulateHighScoresTask populateHighScoresTask = null;
	
	private TouchView touchView = null;
	private TextView timeTextView = null;
	private TextView missedBallCountTextView = null;
	private TextView scoreTextView = null;
	
	private ProgressDialog progressDialog = null;

	private long timeStartedMillis = 0;
	private long timeFinishedMillis = 0;
	private int score = 0;
	private int topLayoutHeight = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Keep screen awake
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		touchView = (TouchView)findViewById(R.id.touchView); 
		timeTextView = (TextView)findViewById(R.id.timeTextView);
		scoreTextView = (TextView)findViewById(R.id.scoreTextView);
		missedBallCountTextView = (TextView)findViewById(R.id.missedBallCountTextView);
		
		LinearLayout topLayout = (LinearLayout)findViewById(R.id.topLayout);
		topLayoutHeight = topLayout.getHeight();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Instructions")
				.setMessage("Touch each ball as it appears. To gain a point, you must touch "
						+ "the ball before the next ball is displayed. The balls will be displayed "
						+ "faster and faster as the game progresses. When you miss "
						+ Constants.MAX_CONSECUTIVE_BALLS_MISSED + " balls in a row, it is game over.")
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                startGame();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();

	}
	
	public int getScore() {
		return score;
	}
	
	public void updateTouchView()
	{

		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 touchView.invalidate();

		    }
		});
	}
	
	public void startGame()
	{
		score = 0;
		scoreTextView.setText(String.valueOf(score));
		missedBallCountTextView.setText("0");
		timeTextView.setText("0:00");
		touchView.setGameOver(false);
		
		timeStartedMillis = System.currentTimeMillis();
		
		ballControllerTask = new BallControllerTask(this);//, timeStartedMillis);
	    ballControllerTask.execute();
	    
	    durationTimerTask = new DurationTimerTask(this, timeStartedMillis);
	    durationTimerTask.execute();
	    
	    musicTask = new MusicTask(this);
	    musicTask.execute();
	}

	public void doGameOver(){
		
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 touchView.setGameOver(true);
		    	 
		    	 timeFinishedMillis = System.currentTimeMillis();
		    	 
		    	 stopThreads();
		    	 
		 		progressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Loading scoreboard...", true);
		 		
		 		populateHighScoresTask = new PopulateHighScoresTask(MainActivity.this, 
		 				timeFinishedMillis, score);
		 		populateHighScoresTask.execute();
		    }
		});
	}
	
	public void onPopulateHighScoresTaskFinished(PopulateHighScoresTask.Status populateHighScoresStatus)
	{
		runOnUiThread(new Runnable() 
		{
		     @Override
		     public void run() {
		    	 if(progressDialog != null)
		    		 progressDialog.dismiss();
		     }
		});
	 		
	 	displayGameOverDialog(populateHighScoresStatus);
	}
	
	private void displayGameOverDialog(final PopulateHighScoresTask.Status populateHighScoresStatus)
	{
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 
		    	 ArrayList<HighScore> highScoresList = new ArrayList<HighScore>();
		    	 boolean newHighScoreAchieved = false;
		    	 if(populateHighScoresTask != null)
		    	 {
		    		 highScoresList = populateHighScoresTask.getHighScoreList();
		    		 newHighScoreAchieved = populateHighScoresTask.newHighScoreAchieved();
		    	 }
		    	 
		    	 ScoreBoardDialog scoreBoardDialog = new ScoreBoardDialog(MainActivity.this, 
		    			 populateHighScoresStatus, highScoresList, score, timeFinishedMillis, newHighScoreAchieved);
		    	 scoreBoardDialog.show(); 
		    }
		});
	}
	
	public void updateMissedBallCount(final int missedBallCount) {
		
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 missedBallCountTextView.setText(String.valueOf(missedBallCount));
		    }
		});
		
		if(ballControllerTask != null)
			ballControllerTask.setConsecutiveBallsMissedCount(missedBallCount);
	}

	public void updateTime(final String duration) {
		
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 timeTextView.setText(duration);
		    }
		});	
	}
	
   public void addToScore(final int amount) {
		
	   score += amount;
	   
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 scoreTextView.setText(String.valueOf(score));
		    }
		});	
		
		
	}
	
	@Override
    public void onDestroy()
    {
        super.onDestroy();    
        stopThreads();
    }
	
	private void stopThreads()
	{
		ballControllerTask.setContinueRunning(false);
		durationTimerTask.setContinueRunning(false);
		musicTask.setContinueRunning(false);
		
		// No need to cancel populateHighScoresTask as it is not running all the time or 
		// running in a loop and does not execute for long.	
	}
	
	public int getTopLayoutHeight()
	{
		return topLayoutHeight;
	}

	public Ball getBallCurrentlyDisplayed() 
	{
		if(ballControllerTask != null)
			return ballControllerTask.getBallCurrentlyDisplayed();
		else
			return null;
	}
}
