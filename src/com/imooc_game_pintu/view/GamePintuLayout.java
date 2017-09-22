package com.imooc_game_pintu.view;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;


import com.imooc_game_pintu.MainActivity;
import com.imooc_game_pintu.R;
import com.imooc_game_utils.*;
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GamePintuLayout extends RelativeLayout implements OnClickListener{
     private int mColumm=3;
     private int mPadding;
     private int mMagin=3;
     
     private ImageView[] mImageViews;
     private Bitmap mbitmap;
     private List<ImagePiece> imagePieces;
     private Boolean once=false;
	 private int mWidth;
	 private int mItemWidth;
     
	 //选中的图片
	 private ImageView firstView;
	 private ImageView SceondView;
	 
	public GamePintuLayout(Context context, AttributeSet attrs, int defStyle)  {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}
	private void init() {
		// TODO Auto-generated method stub
		mMagin=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mColumm,
				getResources().getDisplayMetrics());
		mPadding=min(getPaddingTop(),getPaddingBottom(),getPaddingLeft(),getPaddingRight());
	     
		
	}
	private int min(int...params) {
		// TODO Auto-generated method stub
		int min=params[0];
		for(int param:params)
		{
		    if(min>param)
		    {
		    	min=param;
		    }
		}
		return min;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth=Math.min(getMeasuredHeight(),getMeasuredWidth());
		 if(!once)
		 {
			//进行分图及排序
			initBitmap();
			//设置imageView()的宽高
			initItem();
			checkTimeEnable();
			once=true;
		 }
		
		
		setMeasuredDimension(mWidth, mWidth);
	}
	private void initItem() {
		// TODO Auto-generated method stub
		mItemWidth=(mWidth-mPadding*2-mMagin*(mColumm-1))/mColumm;
		mImageViews=new ImageView[mColumm*mColumm];
		for(int i=0;i<mImageViews.length;i++)
		{
			ImageView item=new ImageView(getContext());
            
			item.setImageBitmap(imagePieces.get(i).getBitmap());
            item.setOnClickListener(this);
			mImageViews[i]=item;
			
			item.setId(i+1);
			
			item.setTag(i+"_"+imagePieces.get(i).getIndex());
			
			RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(mItemWidth,mItemWidth);
			
			//设置Item间横向间隙,通过rightMargin
			//不是最后一列
			if((i+1)%mColumm!=0)
			{
				lp.rightMargin=mMagin;
			}
			//不是第一列
			if(i%mColumm!=0)
			{
				lp.addRule(RelativeLayout.RIGHT_OF,mImageViews[i-1].getId());
			}
			//不是第一行
			if((i+1)>mColumm)
			{
				lp.topMargin=mMagin;
				lp.addRule(RelativeLayout.BELOW,mImageViews[i-mColumm].getId());
			}
			addView(item,lp);
		
		}
	}
	private void initBitmap() {
		// TODO Auto-generated method stub
		if(mbitmap==null)
		{
			
			mbitmap=BitmapFactory.decodeResource(getResources(),R.drawable.image);
		
		}
		imagePieces=ImageSplitterUtil.splitImage(mbitmap, mColumm);
		Collections.sort(imagePieces, new Comparator<ImagePiece>() {

			@Override
			public int compare(ImagePiece lhs, ImagePiece rhs) {
				// TODO Auto-generated method stub	
				return Math.random()>0.5?1:-1;
			}
		});
	}
	public GamePintuLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}
	public GamePintuLayout(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//两次点击同一图片
		if(firstView==v)
		{
			firstView.setColorFilter(null);
			firstView=null;
			return;
		}
		if(firstView==null)
		{
			firstView=(ImageView) v;
			firstView.setColorFilter(Color.parseColor("#55ff0000"));
		}
		else
		{
			SceondView=(ImageView) v;
			ExchangeView();
			CheckSucess();
		}
	}
	//交换图片
	private void ExchangeView() {
		// TODO Auto-generated method stub
		
		firstView.setColorFilter(null);
		
		String firstTag=(String) firstView.getTag();
		String SecondTag=(String) SceondView.getTag();
		
		String[] firstSplit=firstTag.split("_");
		String[] SecondSplit=SecondTag.split("_");
		
		Bitmap fristBitmap=imagePieces.get(Integer.parseInt(firstSplit[0])).getBitmap();
		Bitmap SecondBitmap=imagePieces.get(Integer.parseInt(SecondSplit[0])).getBitmap();
		
		firstView.setImageBitmap(SecondBitmap);
		SceondView.setImageBitmap(fristBitmap);
		firstView.setTag(SecondTag);
		SceondView.setTag(firstTag);
		firstView=null;
		SceondView=null;
	}
	//判断是否过关
	private void CheckSucess()
	{
		isGameSuccess=true;
		for(int i=0;i<mImageViews.length;i++)
		{
			ImageView imageView=mImageViews[i];
			if(getImageIndexByTag((String)imageView.getTag())!=i)
			{
				isGameSuccess=false;
			}
			
		}
		if(isGameSuccess)
		{
			mHandler.removeMessages(TIMECHANGED);
			Toast.makeText(getContext(), "成功，下一关", Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessage(NEXTLEVEL);
		}
	}
	/*private void startAnimator() {
	        float firstX = firstView.getX();
	        float firstY = SceondView.getY();

	        float secondX = SceondView.getX();
	        float secondY = SceondView.getY();

	        ObjectAnimator oafx = ObjectAnimator.ofFloat(firstView, "x", secondX);
	        ObjectAnimator oafy = ObjectAnimator.ofFloat(firstView, "y", secondY);

	        ObjectAnimator oasx = ObjectAnimator.ofFloat(SceondView, "x", firstX);
	        ObjectAnimator oasy = ObjectAnimator.ofFloat(SceondView, "y", firstY);

	        AnimatorSet animatorSet = new AnimatorSet();
	        animatorSet.playTogether(oafx, oafy, oasx, oasy);
	        animatorSet.setDuration(500);
	        animatorSet.start();
	    }*/
	private int getImageIndexByTag(String tag) {
		// TODO Auto-generated method stub
		String[] split=tag.split("_");
		return Integer.parseInt(split[1]);
	}
	//实现接口进行回调
	public interface GamePintuListener
	{
		void nextLevel(int nextLevel);
		void timeChanged(int currenttime);
		void gameover();
		
	}
    private GamePintuListener mListener;
    
	public void setGamePintuListener(GamePintuListener mListener) {
		this.mListener = mListener;
	}
    private int Level=1;
	private static final int NEXTLEVEL=0x110;
	private static final int TIMECHANGED=0x111;
	private boolean isGameOver=false;
	private boolean isGameSuccess=false;
	private boolean isTimeEnabled;
	public void setTimeEnabled(boolean isTimeEnabled) {
		this.isTimeEnabled = isTimeEnabled;
	}
	private int mTime=60;
    private Handler mHandler=new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		super.handleMessage(msg);
    		switch(msg.what)
    		{
    		  case NEXTLEVEL:
    			  Level++;
    			  if(mListener!=null)
    			  mListener.nextLevel(Level);
    			  break;
    		  case TIMECHANGED:
    			  if(isGameOver||isGameSuccess||isPause)
    			  return;
    			  if(mListener!=null)
    			  {
    			    mListener.timeChanged(mTime);
    			    if(mTime==0)
    			    {
    			    	isGameOver=true;
    			    	mListener.gameover();
    			    	return;
    			    }
    			  }
    			  mTime--;
    			  mHandler.sendEmptyMessageDelayed(TIMECHANGED, 1000);
    		}
    	}
    };
    //
    public void restart()
	{
		isGameOver = false;
	    mColumm--;
		nextLevel();
	}
    private boolean isPause=false;
	//游戏暂停
    public void Pause()
    {
    	if(!isPause)
    	{
    		isPause=true;
    		mHandler.removeMessages(TIMECHANGED);
    	}
    }
    public void resume()
    {
    	if(isPause)
    	{
    		isPause=false;
    		mHandler.sendEmptyMessage(TIMECHANGED);
    	}
    }

	public void nextLevel()
	{
		this.removeAllViews();
		isGameSuccess=false;
		mColumm++;
		initBitmap();
		initItem();
		checkTimeEnable();
	}
    //设置游戏时间
	private void checkTimeEnable()
	{
		if(isTimeEnabled)
		{
		  countTimeBaseLevel();
		  mHandler.sendEmptyMessage(TIMECHANGED);
		}
	}
	private void countTimeBaseLevel() {
		// TODO Auto-generated method stub
		mTime=(int) (Math.pow(2, Level)*60);
	}
}
