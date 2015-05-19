package com.marslander;

import com.jcasey.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameLoop extends SurfaceView implements Runnable,
		SurfaceHolder.Callback
// ,OnTouchListener
{

	/**
	 * @author Zhi Li 14/05/2015
	 * 
	 */

	public static final double INITIAL_TIME = 2;
	static final int REFRESH_RATE = 20;
	static final int GRAVITY = 1;
	static final int MAIN_POWER = 2;

	Thread main;
	SoundPool soundPool;

	Paint paint = new Paint();
	Paint fuel = new Paint();
	Paint bar = new Paint();
	Paint landingZone = new Paint();

	private int thrusts;
	private int vics;
	private int expls;
	private int goUp = 0;
	private int fuelvolumn = 100;
	private Bitmap background;
	private Bitmap craft;
	private Bitmap expl;
	private Bitmap leftThrust;
	private Bitmap rightThrust;
	private Bitmap mainThrust;
	private Bitmap mars;

	int xcor[] = { 0, 200, 205, 218, 260, 275, 298, 309, 327, 336, 368, 382,
			448, 462, 476, 498, 527, 600, 600, 0, 0 };
	int ycor[] = { 616, 540, 550, 605, 605, 594, 530, 520, 520, 527, 626, 636,
			636, 623, 535, 504, 481, 481, 750, 750, 616 };

	int landingX[] = { 385, 460, 460, 385, 385 };
	int landingY[] = { 626, 626, 636, 636, 626 };

	Canvas offscreen;
	Bitmap buffer;

	boolean downPressed = false;
	boolean leftPressed = false;
	boolean rightPressed = false;
	boolean gameover = false;

	float x, y;
	int width = 0;

	double t = INITIAL_TIME;

	Path path;
	Path landing;

	public GameLoop(Context context) {
		super(context);

		init();
	}

	public GameLoop(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	public GameLoop(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public void init() {

		soundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);
		expls = soundPool.load(getContext(), R.raw.expl1, 1);
		thrusts = soundPool.load(getContext(), R.raw.thrust1, 2);
		vics = soundPool.load(getContext(), R.raw.vic, 3);

		path = new Path();

		for (int i = 0; i < xcor.length; i++) {
			path.lineTo(xcor[i], ycor[i]);
		}

		getHolder().addCallback(this);
		craft = BitmapFactory.decodeResource(getResources(), R.drawable.rocket);
		expl = BitmapFactory.decodeResource(getResources(), R.drawable.expl);
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.landscape);
		leftThrust = BitmapFactory.decodeResource(getResources(),
				R.drawable.thruster);
		rightThrust = BitmapFactory.decodeResource(getResources(),
				R.drawable.thruster);
		mainThrust = BitmapFactory.decodeResource(getResources(),
				R.drawable.main_flame);
		mars = BitmapFactory.decodeResource(getResources(), R.drawable.mars);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		width = w;

		x = width / 2;
	}

	public void run() {
		while (true) {
			while (!gameover) {
				Canvas canvas = null;
				SurfaceHolder holder = getHolder();
				synchronized (holder) {
					canvas = holder.lockCanvas();
					canvas.drawColor(Color.BLACK);
					paint.setColor(Color.GREEN);

					Shader mShader = new BitmapShader(mars,
							Shader.TileMode.REPEAT, Shader.TileMode.MIRROR);
					paint.setShader(mShader);
					canvas.drawPath(path, paint);
					fuelbar(canvas);
					landing(canvas);

					// canvas.drawBitmap(background,0, 459, null);

					// s = ut + 0.5 gt^2

					// not that the initial velocity (u) is zero so I have not
					// put ut into the code below
					y = (int) y + (int) ((0.5 * ((GRAVITY - goUp) * t * t)));

					t = t + 0.01; // increment the parameter for synthetic time
									// by a small amount

					if (x < 0) {
						x = x + getWidth();
					} else if (x > getWidth()) {
						x = x - getWidth();

					}

					boolean landingLeft = contains(landingX, landingY, x - 28,
							y + 25);
					boolean landingRight = contains(landingX, landingY, x + 25,
							y + 25);
					boolean bottomLeft = contains(xcor, ycor, x - 25, y + 25);
					boolean bottomRight = contains(xcor, ycor, x + 25, y + 25);

					if (landingLeft && landingRight) {
						soundPool.play(vics, 1, 3, 3, 0, 1);
						canvas.drawBitmap(craft, x - 20, y - 25, null);
						gameover = true;
					} else if (bottomLeft || bottomRight) {
						soundPool.play(expls, 1, 3, 3, 0, 1);
						canvas.drawBitmap(expl, x - 25, y - 25, null);

						t = INITIAL_TIME; // reset the time variable

						gameover = true;
						downPressed = false;
						leftPressed = false;
						rightPressed = false;

					} else {
						// canvas.drawRect(x-25, y-25, x+25, y+25, paint);
						canvas.drawBitmap(craft, x - 20, y - 20, null);
						if (leftPressed == true) {
							canvas.drawBitmap(leftThrust, x - 20, y + 28, null);
						} else if (rightPressed == true) {
							canvas.drawBitmap(rightThrust, x + 22, y + 28, null);
						} else if (downPressed == true) {
							canvas.drawBitmap(mainThrust, x - 2, y + 28, null);
						}

					}
				}

				try {
					Thread.sleep(REFRESH_RATE);
				} catch (Exception e) {
				}

				finally {
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

	}

	public boolean contains(int[] xcor, int[] ycor, double x0, double y0) {
		int crossings = 0;

		for (int i = 0; i < xcor.length - 1; i++) {
			int x1 = xcor[i];
			int x2 = xcor[i + 1];

			int y1 = ycor[i];
			int y2 = ycor[i + 1];

			int dy = y2 - y1;
			int dx = x2 - x1;

			double slope = 0;
			if (dx != 0) {
				slope = (double) dy / dx;
			}

			boolean cond1 = (x1 <= x0) && (x0 < x2); // is it in the range?
			boolean cond2 = (x2 <= x0) && (x0 < x1); // is it in the reverse
														// range?
			boolean above = (y0 < slope * (x0 - x1) + y1); // point slope y - y1

			if ((cond1 || cond2) && above) {
				crossings++;
			}
		}
		return (crossings % 2 != 0); // even or odd
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		main = new Thread(this);
		if (main != null)
			main.start();

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		while (retry) {
			try {
				main.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
	}

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	//
	// x = event.getX();
	// y = event.getY();
	//
	// t = 3;
	//
	// gameover = false;
	//
	// return true;
	// }

	public void reset() {
		gameover = false;

		x = width / 2;
		y = 0;
		t = INITIAL_TIME;

		downPressed = false;
		leftPressed = false;
		rightPressed = false;
		fuelvolumn = 100;
		goUp = 0;

	}

	public void left() {
		if (fuelvolumn > 0) {
			soundPool.play(thrusts, 1, 1, 1, 0, 1);
			x = x + 30;
			leftPressed = true;
			rightPressed = false;
			downPressed = false;
			fuelvolumn = fuelvolumn - 5;

		}
	}

	public void right() {
		if (fuelvolumn > 0) {
			soundPool.play(thrusts, 1, 1, 1, 0, 1);
			x = x - 30;
			rightPressed = true;
			downPressed = false;
			leftPressed = false;
			fuelvolumn = fuelvolumn - 2;

		}
	}

	public void up() {
		if (fuelvolumn > 0) {
			soundPool.play(thrusts, 1, 1, 1, 0, 1);
			goUp = MAIN_POWER;
			downPressed = true;
			leftPressed = false;
			rightPressed = false;
			fuelvolumn = fuelvolumn - 5;

		}
	}

	public void maintain() {
		goUp = 0;
		downPressed = false;
		leftPressed = false;
		rightPressed = false;
		t = INITIAL_TIME;

	}

	public void fuelbar(Canvas canvas) {

		fuel.setColor(Color.GREEN);
		bar.setColor(Color.GRAY);
		canvas.drawRect(0, 0, 105, 25, bar);
		canvas.drawRect(2, 2, fuelvolumn, 20, fuel);

	}

	public void landing(Canvas canvas) {
		landing = new Path();
		for (int j = 0; j < landingX.length; j++) {
			landing.lineTo(landingX[j], landingY[j]);

		}
		landingZone.setColor(Color.GREEN);
		canvas.drawPath(landing, landingZone);
	}

}
