package com.dermotblair.touchball;

import com.dermotblair.touchball.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View {

  private Paint mPaint;
  private Paint textPaint;
  private Ball ballCurrentlyDisplayed = null;
  
  private SoundPool soundPool;
  private int soundID;
  private boolean loadedBallTouchSound = false;
  private AudioManager audioManager = null; 
  private MainActivity context = null;
  private float actualVolume = 0;
  private float maxVolume = 0;
  private float volume = 0;
  private boolean gameOver = false;

  public TouchView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = (MainActivity)context;
    initView();
  }

  private void initView() {
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setColor(Color.BLUE);
    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textPaint.setTextSize(30);
    
    Typeface typeFace = Typeface.create((String)null,Typeface.BOLD);
    textPaint.setTypeface(typeFace);
    
    // Set the hardware buttons to control the music
    ((MainActivity)getContext()).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    
    // Load the sound
    soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
      @Override
      public void onLoadComplete(SoundPool soundPool, int sampleId,
          int status) {
    	  loadedBallTouchSound = true;
      }
    });
    soundID = soundPool.load(context, R.raw.small_explosion_8_bit, 1);
  
    audioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
 
    // Getting the user sound settings
    actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    volume = actualVolume / maxVolume;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {

    int pointerIndex = event.getActionIndex();
    int maskedAction = event.getActionMasked();

    switch (maskedAction) {

    case MotionEvent.ACTION_DOWN:
    case MotionEvent.ACTION_POINTER_DOWN: {
    	
      float x = event.getX(pointerIndex);
      float y = event.getY(pointerIndex);
        
      // Check if the user touched the last ball that was displayed. 
      if(ballCurrentlyDisplayed != null)
      {
    	 if(!ballCurrentlyDisplayed.isTouched() && ballCurrentlyDisplayed.contains(x, y))
	      {
    		  if(gameOver)
    			  return true;
    		  
	    	  ballCurrentlyDisplayed.setTouched(true);
	    	
		      if (loadedBallTouchSound) {
		        soundPool.play(soundID, volume, volume, 1, 0, 1f);
		      }
		      
		      invalidate();
		      
		      context.addToScore(1);
			  context.updateMissedBallCount(0);
	      }
      }
     
      break;
    }
    case MotionEvent.ACTION_MOVE:
    case MotionEvent.ACTION_UP:
    case MotionEvent.ACTION_POINTER_UP:
    case MotionEvent.ACTION_CANCEL: 
      break;
    
    }
    
    return true;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if(gameOver)
    	return;
    
    ballCurrentlyDisplayed = ((MainActivity)getContext()).getBallCurrentlyDisplayed();
    
    if(ballCurrentlyDisplayed != null && !ballCurrentlyDisplayed.isTouched())
    {
    	mPaint.setColor(ballCurrentlyDisplayed.getColor());
		canvas.drawCircle(ballCurrentlyDisplayed.getX(), ballCurrentlyDisplayed.getY(), Constants.BALL_RADIUS, mPaint);
    }
  }
  
  public void setGameOver(boolean gameOver)
  {
	  this.gameOver = gameOver;
  }
} 