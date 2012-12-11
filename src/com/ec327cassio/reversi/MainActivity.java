package com.ec327cassio.reversi;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


//defines the (only) activity
public class MainActivity extends Activity {
	
	//member variables
	//The grid object
	public Grid grid;
	//keeping track of gamestate in two synchronized arrays
	public int[][] gamestate_int = new int[8][8];
	public Circle[][] gamestate_circles = new Circle[8][8];
	 
	//for the shake sensor
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity
	private int movecount = 0;
//=-------------------------------------------------------------------------	
	//link to the compiled C
	static {
        System.loadLibrary("reversi");
    }
	
	//declare C methods (defined in cpp files)
	public native String getString();
	
	//apparently Java doesn't know what to do with pointers
	public native boolean isValid(int x, int y, int board[][], int player);
	
	public native int[][] FixBoard(int x, int y,  int  board[][], int player);
//=------------------------------------------------------------------------
	

	
	public void setupBoard() {
		gamestate_int[4][3] = 0;
		redrawBoardFromIntArray();
		movecount++;
		
		gamestate_int[4][4] = 1;
		redrawBoardFromIntArray();
		movecount++;
		
		MainActivity.this.grid.select(3, 4);
		gamestate_int[3][4] = 0;
		redrawBoardFromIntArray();
		movecount++;
		
		MainActivity.this.grid.select(3, 3);
		gamestate_int[3][3] = 1;
		redrawBoardFromIntArray();
		movecount++;
/*		
		MainActivity.this.grid.select(0, 7);
		gamestate_int[0][7] = 1;
		redrawBoardFromIntArray();
		movecount++;
*/	
		}
	
	@Override
	//called when activity started
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//set main to be the grid
		 RelativeLayout main = (RelativeLayout) findViewById(R.id.board_view);
		 //set member variable
		 this.grid = new Grid(main.getContext(),  findViewById(R.id.board_view).getLayoutParams().width);
		 //add the grid to screen 
		 main.addView(this.grid);
		 //initialize gamestate_int to be an "empty" board
		 for (int i=0; i <8; i++ ) {
			 for (int j=0; j<8; j++) {
				 this.gamestate_int[i][j] = 2;
			 }
			 
		 }

		 
		 
		 
		setupBoard();


		 //set the member variables for the shaking sensor.		 
		 mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		    mAccel = 0.00f;
		    mAccelCurrent = SensorManager.GRAVITY_EARTH;
		    mAccelLast = SensorManager.GRAVITY_EARTH;		
		
		    //bind a touch listener to grid
		   this.grid.setOnTouchListener(new View.OnTouchListener() {
			    public boolean onTouch(View v, MotionEvent e) {
			    	if (e.getAction() == MotionEvent.ACTION_DOWN)  {
			    		int x = (int) e.getX();
			    		int y = (int) e.getY();
			    		//update the grid property for the space touched.
			    		MainActivity.this.grid.selectAtTouch(x, y);
			    		if (gameisnotover()) {MainActivity.this.tryMoveAtIndex(grid.selX,grid.selY);}
			    		return true;
			    	}
			    	else return false;
			    }
			    
			    
			    
			});	
	}
	
	public boolean gameisnotover() {
		if (movecount < 64) {
			return true;
		}
		else 
		{
		//TOAST!
		Context context = getApplicationContext();
		CharSequence text = "Game Over!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		return false;
		}
		
	}
	public void redrawBoardFromIntArray () {
		RelativeLayout gl = (RelativeLayout) findViewById(R.id.board_view);
		for (int i=0; i <8; i++) {
			for (int j=0; j<8; j++) {
				if ((MainActivity.this.gamestate_int[i][j] == 0) //if it should be black but its null
						&& (MainActivity.this.gamestate_circles[i][j] == null) ) {
					MainActivity.this.grid.select(i,j);
					gamestate_circles[i][j] = new Circle(gl.getContext(),
							(grid.tile_width/2)+grid.selX*(grid.tile_width),(grid.tile_height/2)+grid.selY*(grid.tile_height),25,movecount%2);
					gl.addView(gamestate_circles[i][j]);
						
				}
				else if ((MainActivity.this.gamestate_int[i][j] == 1) //if it should be white but its null
						&& (MainActivity.this.gamestate_circles[i][j] == null) ) {
					MainActivity.this.grid.select(i,j);
					gamestate_circles[i][j] = new Circle(gl.getContext(),
							(grid.tile_width/2)+grid.selX*(grid.tile_width),(grid.tile_height/2)+grid.selY*(grid.tile_height),25,movecount%2);
					gl.addView(gamestate_circles[i][j]);
						
				}
				else if ((MainActivity.this.gamestate_int[i][j] == 0) //if it should be black, but its white
						&& (MainActivity.this.gamestate_circles[i][j].mPaint.getColor() != Color.BLACK) ) {
					MainActivity.this.gamestate_circles[i][j].mPaint.setColor(Color.BLACK);
					MainActivity.this.gamestate_circles[i][j].invalidate();
				}
				else if ((MainActivity.this.gamestate_int[i][j] == 1) //if it should be white but its black
						&& (MainActivity.this.gamestate_circles[i][j].mPaint.getColor() != Color.WHITE) ) {
					MainActivity.this.gamestate_circles[i][j].mPaint.setColor(Color.WHITE);
					MainActivity.this.gamestate_circles[i][j].invalidate(); 
			
				}
			}
		}
	}
	
	public void tryMoveAtIndex(int x,int y) {
		//get board view
		// RelativeLayout gl = (RelativeLayout) findViewById(R.id.board_view);
		Log.d("The value of isValid is", Boolean.toString(isValid(x, y, gamestate_int, movecount %2)) );
		if(isValid(x, y, gamestate_int, movecount %2))
		{	//make move.		
			gamestate_int[x][y] = movecount %2;
			Log.d("The value of r1", Arrays.toString(gamestate_int[0]) );
			Log.d("The value of r2", Arrays.toString(gamestate_int[1]) );
			Log.d("The value of r3", Arrays.toString(gamestate_int[2]) );
			Log.d("The value of r4", Arrays.toString(gamestate_int[3]) );
			Log.d("The value of r5", Arrays.toString(gamestate_int[4]) );
			Log.d("The value of r6", Arrays.toString(gamestate_int[5]) );
			Log.d("The value of r7", Arrays.toString(gamestate_int[6]) );
			Log.d("The value of r8", Arrays.toString(gamestate_int[7]) );
			
			gamestate_int = FixBoard(x, y, gamestate_int, movecount %2);
			redrawBoardFromIntArray();
			movecount++;
			Log.d("The value2 of r1", Arrays.toString(gamestate_int[0]) );
			Log.d("The value2 of r2", Arrays.toString(gamestate_int[1]) );
			Log.d("The value2 of r3", Arrays.toString(gamestate_int[2]) );
			Log.d("The value2 of r4", Arrays.toString(gamestate_int[3]) );
			Log.d("The value2 of r5", Arrays.toString(gamestate_int[4]) );
			Log.d("The value2 of r6", Arrays.toString(gamestate_int[5]) );
			Log.d("The value2 of r7", Arrays.toString(gamestate_int[6]) );
			Log.d("The value2 of r8", Arrays.toString(gamestate_int[7]) );

			
			MainActivity.this.gameisnotover();

		}
		
		else
		{
			Context context = getApplicationContext();
			CharSequence text = "Invalid Move!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		}

		
	}
	Log.d("The value2 of r8", Arrays.toString(gamestate_circles[0]) );		
	Log.d("The value2 of r8", Arrays.toString(gamestate_circles[1]) );	
	Log.d("The value2 of r8", Arrays.toString(gamestate_circles[2]) );	
	Log.d("The value2 of r8", Arrays.toString(gamestate_circles[3]) );	
	Log.d("The value2 of r8", Arrays.toString(gamestate_circles[4]) );	
	Log.d("The value2 of r8", Arrays.toString(gamestate_circles[5]) );	
	Log.d("The value2 of r8", Arrays.toString(gamestate_circles[6]) );	
	Log.d("The value2 of r8", Arrays.toString(gamestate_circles[7]) );	
	public void reset(View view) {
		finish();
		startActivity(new Intent(MainActivity.this, MainActivity.class));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


		//set a sensor listener
	  private final SensorEventListener mSensorListener = new SensorEventListener() {
	    public void onSensorChanged(SensorEvent se) {
	      float x = se.values[0];
	      float y = se.values[1];
	      float z = se.values[2];
	      //sets member line
	      mAccelLast = mAccelCurrent;
	      mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
	      float delta = mAccelCurrent - mAccelLast;
	      mAccel = mAccel * 0.9f + delta; // perform low-cut filter
	      
	      //detect shake and reset
	      if (mAccel > 4)
	      {
	    	  MainActivity.this.reset(findViewById(R.id.main_view));
	      }
	    }

	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }
	  };

	  @Override
	  protected void onResume() {
	    super.onResume();
	    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	  }

	  @Override
	  protected void onStop() {
	    mSensorManager.unregisterListener(mSensorListener);
	    super.onStop();
	  }
	

}
