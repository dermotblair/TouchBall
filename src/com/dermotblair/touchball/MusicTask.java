package com.dermotblair.touchball;

import com.dermotblair.touchball.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;

public class MusicTask extends AsyncTask<Void, Void, Void> {

	  private AudioManager audioManager = null;
	  private MainActivity ownerActivity;
	  private MediaPlayer player = null;
	
	 MusicTask(MainActivity ownerActivity)
	 {	
		 this.ownerActivity = ownerActivity;
	 }
	
	 @Override
	 protected Void doInBackground(Void... params) 
	 {
		 // Play music in a continuous loop.
		 // Getting the user sound settings
 		 audioManager = (AudioManager)ownerActivity.getSystemService(Context.AUDIO_SERVICE);
		 
		 float actualVolume = (float) audioManager
	          .getStreamVolume(AudioManager.STREAM_MUSIC);
		 float maxVolume = (float) audioManager
	          .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		 float volume = actualVolume / maxVolume;
	      
	     // Play background music
	     player = MediaPlayer.create(ownerActivity, R.raw.cyarons_gate); 
	     if(player != null)
	     {
		      player.setLooping(true); // Set looping 
		      player.setVolume(volume,volume); 
		      player.start(); 
	     }

	    return null;
    }
	
	public void setContinueRunning(boolean continueRunning) {
		if(player != null && player.isPlaying())
			player.stop();
	}
}
