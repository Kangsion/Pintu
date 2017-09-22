package com.imooc_game_pintu;

import com.imooc_game_pintu.view.GamePintuLayout;
import com.imooc_game_pintu.view.GamePintuLayout.GamePintuListener;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
    private GamePintuLayout gamePintuLayout;
    private TextView time;
    private TextView level;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gamePintuLayout=(GamePintuLayout) findViewById(R.id.id_gamelayout);
		gamePintuLayout.setTimeEnabled(true);
		time=(TextView) findViewById(R.id.id_time);
		level=(TextView) findViewById(R.id.id_level);
		gamePintuLayout.setGamePintuListener(new GamePintuListener() {
			
			@Override
			public void timeChanged(int currenttime) {
				// TODO Auto-generated method stub
				time.setText(""+currenttime);
			}
			
			@Override
			public void nextLevel(final int nextLevel) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("通关成功")
				.setMessage("恭喜你通关，点击进入下一关")
				.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						gamePintuLayout.nextLevel();
						level.setText(""+nextLevel);
					}
				}).show();
			}
			
			@Override
			public void gameover() {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("通关失败")
				.setMessage("点击确定重新开始")
				.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						gamePintuLayout.restart();
						
					}
				}).setNegativeButton("退出", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}).show();
			}
		});
	}
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	gamePintuLayout.Pause();
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	gamePintuLayout.resume();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
