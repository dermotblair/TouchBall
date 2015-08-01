package com.dermotblair.touchball;

public class HighScore implements Comparable<HighScore>{
	
	private long gameFinishTimeMillis = 0;
	private int score = 0;

	HighScore(long gameFinishTimeMillis, int score)
	{
		this.gameFinishTimeMillis = gameFinishTimeMillis;
		this.score = score;
	}
	
	@Override
	public int compareTo(HighScore another) 
	{
		if(this.score != another.score)
			return this.score - another.score;
		else
			return (int)(this.gameFinishTimeMillis - another.gameFinishTimeMillis);
	}
	
	public long getGameFinishTimeMillis() {
		return gameFinishTimeMillis;
	}

	public void setGameFinishTimeMillis(long gameFinishTimeMillis) {
		this.gameFinishTimeMillis = gameFinishTimeMillis;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	@Override
	public boolean equals(Object other){

	    if (!(other instanceof HighScore))
	    	return false;
	    
	    HighScore otherHighScoreObject = (HighScore)other;
	    return (this.gameFinishTimeMillis == otherHighScoreObject.gameFinishTimeMillis &&
	       this.score == otherHighScoreObject.score);
	}
	

	 @Override
	 public int hashCode()
	 {
		 int result = 17;
		 result = 31 * result + (int) (gameFinishTimeMillis ^ (gameFinishTimeMillis >>> 32));
		 result = 31 * result + score;
		 return result;
	 }
}
