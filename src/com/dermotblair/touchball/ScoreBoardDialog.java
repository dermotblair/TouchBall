package com.dermotblair.touchball;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.dermotblair.touchball.R;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreBoardDialog extends Dialog implements android.view.View.OnClickListener {

	private MainActivity ownerActivity = null;
	private Button yesButton = null;
	private Button noButton = null;
	private TextView postGameMessageTextView = null;
	private ArrayList<HighScore> highScoresList = null;
	private int latestScore = 0;
	private long timeFinishedMillis = 0;
	private TableLayout scoreBoardTable = null; 
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
    private Calendar calendar = Calendar.getInstance();
    private boolean newHighScoreAchieved = false;
    private PopulateHighScoresTask.Status populateHighScoresTaskStatus = null;
	
	public ScoreBoardDialog(MainActivity ownerActivity, PopulateHighScoresTask.Status populateHighScoresTaskStatus, ArrayList<HighScore> highScoresList, int latestScore, long timeFinishedMillis, boolean newHighScoreAchieved) {
		super(ownerActivity);
		this.ownerActivity = ownerActivity;
		this.highScoresList = highScoresList;
		this.latestScore = latestScore;
		this.timeFinishedMillis = timeFinishedMillis;
		this.newHighScoreAchieved = newHighScoreAchieved;
		this.populateHighScoresTaskStatus = populateHighScoresTaskStatus;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scoreboard_dialog);
		
		postGameMessageTextView = (TextView) findViewById(R.id.postGameMessageTextView);
		postGameMessageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		postGameMessageTextView.setTextColor(Color.parseColor("#000000"));
		postGameMessageTextView.setBackgroundColor(Color.parseColor("#dcdcdc"));
		postGameMessageTextView.setGravity(Gravity.CENTER);
		int paddingInDp = 5;
		final float scale = ownerActivity.getResources().getDisplayMetrics().density;
		int paddingInPx = (int) (paddingInDp * scale + 0.5f);  
		postGameMessageTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

		scoreBoardTable = (TableLayout) findViewById(R.id.scoreBoardTable);
		
		yesButton = (Button) findViewById(R.id.yesButton);
		yesButton.setOnClickListener(this);
		noButton = (Button) findViewById(R.id.noButton);
		noButton.setOnClickListener(this);
		
		postGameMessageTextView.setText(generatePostGameMessage());
		
		if(populateHighScoresTaskStatus != null && populateHighScoresTaskStatus != PopulateHighScoresTask.Status.SUCCESS)
		{
			String errorMsg = populateHighScoresTaskStatus.toString();
			
			if(errorMsg != null)
				Toast.makeText(ownerActivity, errorMsg, Toast.LENGTH_LONG).show();
			
		}
		
		populateScoreBoard();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.yesButton:
			ownerActivity.startGame();
			break;
		case R.id.noButton:
			ownerActivity.finish();
			break;
		default:
		  break;
		}
		dismiss();
	}
	
	private String generatePostGameMessage()
	{
		String postGameMessage = "Your Score: " + this.latestScore;
		if(newHighScoreAchieved)
			postGameMessage += "\n\nCongratulations! You have\n achieved a new high score which\n is now on the score board!";
		else
			postGameMessage += "\n\nUnfortunately you have not\n achieved a new high score this\n time.";
		
		return postGameMessage;
	}
	
	private void populateScoreBoard()
	{	
		for(int i=0;i<10;i++)
		{
			TableRow tableRow = new TableRow(ownerActivity);
			
			TextView rankTextView=new TextView(ownerActivity);
			TextView dateTimeTextView=new TextView(ownerActivity);
			TextView scoreTextView=new TextView(ownerActivity);
			
		    String rankStr = (i+1) + ".";
		    String dateTimeStr = "";
		    String scoreStr = "";
		    
		    if(highScoresList == null || i >= highScoresList.size())
		    {
		    	// If we don't have enough high scores yet, leave them blank with hyphens.
		    	dateTimeStr = "---------";
		    	scoreStr = "---";
		    }
		    else
		    {
		    	HighScore currentHighScore = highScoresList.get(i);
		    	long dateTime = currentHighScore.getGameFinishTimeMillis();
		    	int score = currentHighScore.getScore();
		    	
		        calendar.setTimeInMillis(dateTime);
		        dateTimeStr = sdf.format(calendar.getTime());
		    	
		    	scoreStr = String.valueOf(score);
		    }
		    
		    rankTextView.setText(rankStr);
		    dateTimeTextView.setText(dateTimeStr);
		    scoreTextView.setText(scoreStr);
		    
		    setTableRowTextViewStyle(rankTextView);
		    setTableRowTextViewStyle(dateTimeTextView);
		    setTableRowTextViewStyle(scoreTextView);
		    
		    tableRow.addView(rankTextView);
		    tableRow.addView(dateTimeTextView);
		    tableRow.addView(scoreTextView);

		    scoreBoardTable.addView(tableRow);

		}
	}
	
	private void setTableRowTextViewStyle(TextView textView)
	{
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		textView.setTextColor(Color.parseColor("#000000"));
		textView.setBackgroundColor(Color.parseColor("#dcdcdc"));
		textView.setGravity(Gravity.CENTER);
		
		 int paddingInDp = 10;
		 final float scale = ownerActivity.getResources().getDisplayMetrics().density;
		 int paddingInPx = (int) (paddingInDp * scale + 0.5f);
		    
		textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);	
	}
}