package com.dermotblair.touchball;

public class Ball {
	int x = 0;
	int y = 0;
	int color = 0;
	boolean touched = false; // The user touched the ball in time. i.e. before the next ball was displayed.
	
	Ball(int x, int y, int color){
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getColor() {
		return color;
	}

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	// Checks if x and y given are contained in the ball.
	// Uses Pythagoras method: (x - center_x)^2 + (y - center_y)^2 < radius^2
	public boolean contains(float x, float y)
	{
		return ( (((x - this.x)*(x - this.x)) + ((y - this.y)*(y - this.y))) < (Constants.BALL_RADIUS * Constants.BALL_RADIUS) ) ? true : false;
	}
}
