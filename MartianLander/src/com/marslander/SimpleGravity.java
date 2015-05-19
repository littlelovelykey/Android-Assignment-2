package com.marslander;

import com.jcasey.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;

public class SimpleGravity extends Activity {
	private GameLoop gameLoop;

	/**
	 * @author Zhi Li 14/05/2015
	 */

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main); // set the content view or our widget
										// lookups will fail

		gameLoop = (GameLoop) findViewById(R.id.gameLoop);

		final Button btnRestart = (Button) findViewById(R.id.btnRestart);
		final Button btnUp = (Button) findViewById(R.id.btnUp);
		final Button btnLeft = (Button) findViewById(R.id.btnLeft);
		final Button btnRight = (Button) findViewById(R.id.btnRight);

		btnRestart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gameLoop.reset();
				gameLoop.invalidate();
			}
		});

		btnLeft.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					gameLoop.left();

					break;
				case MotionEvent.ACTION_UP:
					gameLoop.maintain();
					break;
				default:
					break;

				}
				return true;
			}

		});

		btnRight.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					gameLoop.right();
					break;
				case MotionEvent.ACTION_UP:
					gameLoop.maintain();
					break;
				default:
					break;

				}
				return true;
			}
		});

		btnUp.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					gameLoop.up();
					break;
				case MotionEvent.ACTION_UP:
					gameLoop.maintain();
					break;
				default:
					break;

				}
				return true;
			}
		});
	}
}
