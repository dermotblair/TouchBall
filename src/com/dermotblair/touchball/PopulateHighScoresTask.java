package com.dermotblair.touchball;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PopulateHighScoresTask extends AsyncTask<Void, Void, Void> {

	  private MainActivity ownerActivity;
	  private ArrayList<HighScore> highScoreList = new ArrayList<HighScore>();
	  private long gameFinishTimeMillis = 0;
	  private int latestScore = 0;
	  private boolean newHighScoreAchieved = false;
	  private Status status = null;
	  
	  
	  public static enum Status 
	  { 
		SUCCESS,
	  	WRITING_TO_INTERNAL_STORAGE_FAILED,
	  	DELETING_FILE_FAILED;
	  	
	  	 @Override
	  	  public String toString() {
	  	    switch(this) {
	  	      case SUCCESS: 
	  	    	  return "Success";
	  	      case WRITING_TO_INTERNAL_STORAGE_FAILED: 
	  	    	  return "Writing to the scoreboard file in internal storage failed.";
	  	      case DELETING_FILE_FAILED: 
	  	    	  return "Deleting the old scoreboard file from internal storage failed";
	  	      default: 
	  	    	  return null;
	  	    }
	  	  }
	  };
	  
	  PopulateHighScoresTask(MainActivity ownerActivity, long gameFinishTimeMillis, int latestScore)
	  {	
		 this.ownerActivity = ownerActivity;
		 this.gameFinishTimeMillis = gameFinishTimeMillis;
		 this.latestScore = latestScore;
	  }
	
	  @Override
	  protected Void doInBackground(Void... params) 
	  {
		// Check if file exists in internal storage
		File file = ownerActivity.getFileStreamPath(Constants.HIGH_SCORES_FILE_NAME);
         
		if(file.exists())
		{
			highScoreList = readHighScoresFromInternalStorage(Constants.HIGH_SCORES_FILE_NAME);
			
			HighScore latestHighScore = new HighScore(gameFinishTimeMillis, latestScore);
			
			if(latestScore != 0)
			{
				// Add recent score to list. List may be empty.
				highScoreList.add(latestHighScore);
			}
			
			// Sort the list in descending order.
			Collections.sort(highScoreList);
			Collections.reverse(highScoreList);
			
			// Make max size of list = 10.
			if(highScoreList.size() > 10)
			{
				for(int i=10; i<highScoreList.size(); i++)
				{
					highScoreList.remove(i);
				}
			}
			
			if(latestScore != 0)
			{
				// Check if latest score is in top 10.
				if(highScoreList.contains(latestHighScore))
				{
					newHighScoreAchieved = true;
				}
			}
			
			if(newHighScoreAchieved)
			{
				// Delete the file and write a new file
				boolean deleted = file.delete();
				if(!deleted){
					Log.e(Constants.APP_NAME, "Could not delete " + Constants.HIGH_SCORES_FILE_NAME);
					status = Status.DELETING_FILE_FAILED;
				}	
				else
				{
					StringBuilder newFileContents = new StringBuilder();
					for(HighScore currentHighScore : highScoreList)
					{
						String gameFinishTimeMillisStr = Long.toString(currentHighScore.getGameFinishTimeMillis());
						String score = Integer.toString(currentHighScore.getScore());
						
						newFileContents.append(gameFinishTimeMillisStr);
						newFileContents.append(",");
						newFileContents.append(score);
						newFileContents.append(Constants.END_OF_LINE);
					}
					
					if(!writeHighScoresToInternalStorage(newFileContents.toString()))
					{
						Log.e(Constants.APP_NAME, "Could not create or write to " + Constants.HIGH_SCORES_FILE_NAME);
						status = Status.WRITING_TO_INTERNAL_STORAGE_FAILED;
					}
					else
						status = Status.SUCCESS;
					
				}
			}
			else
				status = Status.SUCCESS;
		}
		else
		{
			if(latestScore != 0)
			{
				String highScoreStr = gameFinishTimeMillis + "," + latestScore + Constants.END_OF_LINE;
				HighScore highScore = new HighScore(gameFinishTimeMillis, latestScore);
				highScoreList.add(highScore);
				newHighScoreAchieved = true;
				
				if(!writeHighScoresToInternalStorage(highScoreStr))
				{
					Log.e(Constants.APP_NAME, "Could not create or write to " + Constants.HIGH_SCORES_FILE_NAME);
					status = Status.WRITING_TO_INTERNAL_STORAGE_FAILED;
				}
				else
					status = Status.SUCCESS;
			}
			else
				status = Status.SUCCESS;
		}
		
	    return null;
	  }
	  
	  @Override
	  protected void onPostExecute(Void param)
	  {
		    ownerActivity.onPopulateHighScoresTaskFinished(status);
	  }

	  private ArrayList<HighScore> readHighScoresFromInternalStorage(String fileName) 
	  {
		  BufferedReader input = null;
		  ArrayList<HighScore> highScoreList = new ArrayList<HighScore>();
		  try 
		  {
		    input = new BufferedReader(new InputStreamReader(ownerActivity.openFileInput(fileName)));
		    String line;
		    while ((line = input.readLine()) != null) 
		    {
		    	if(!line.contains(","))
		    		continue;
		    	
		    	String[] rowData = line.split(",");
		    	
		    	long gameStartTime = Long.valueOf(rowData[0]);
		    	int score = Integer.valueOf(rowData[1]);
	            
		    	HighScore highScore = new HighScore(gameStartTime, score);
		    	
		    	highScoreList.add(highScore);
		    }   
		    
		  } 
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  } 
		  finally 
		  {
		  if (input != null) 
		  {
		    try 
		    {
		    	input.close();
		    } 
		    catch (IOException e) 
		    {
		    	e.printStackTrace();
		    }
		    }
		  }
		  return highScoreList;
		} 
	  
	  private boolean writeHighScoresToInternalStorage(String highScores) 
	  {
		  boolean exceptionOccurred = false;
		  FileOutputStream openFileOutput= null;
		    try 
		    {
		      openFileOutput = ownerActivity.openFileOutput(Constants.HIGH_SCORES_FILE_NAME, Context.MODE_PRIVATE);
		      openFileOutput.write(highScores.getBytes());
		    } 
		    catch (Exception e) 
		    {
		      exceptionOccurred = true;
		      e.printStackTrace();
		      return false;
		    } 
		    finally 
		    {
		      if (openFileOutput != null) 
		      {
		        try 
		        {
		        	openFileOutput.close();
		        } catch (IOException e) 
		        {
		          e.printStackTrace();
		        }
		      }
		    }
		    return exceptionOccurred ? false : true;
	  }
	  
	  public boolean newHighScoreAchieved()
	  {
		  return newHighScoreAchieved;
	  }
	  
	  public ArrayList<HighScore> getHighScoreList() {
		return highScoreList;
	  }	
}
