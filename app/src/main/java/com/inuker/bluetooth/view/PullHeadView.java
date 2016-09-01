package com.inuker.bluetooth.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inuker.bluetooth.R;
import com.inuker.bluetooth.StringUtils;

public class PullHeadView extends ViewGroup{
	private final static int UPDATING_IDLE = 0;
	private final static int UPDATING_READY = 1;
	private final static int UPDATING_ON_GOING = 2;
	private final static int UPDATING_FINISH = 3;
	private final static int MAX_PULL_HEIGHT_DP = 200;
	private final static int MAX_DURATION = 350;
	private final static int INVALID_STATE = -1;
	private final static String SP_NAME = "pull_list_update_time";
	private int mHeight;
	private int mDistance;
	private int mInitHeight;
	private boolean mImediateUpdate = false;
	private int mUpdateHeight;
	private boolean mCanUpdate;
	private int mNextState = INVALID_STATE;
	int mUpdatingStatus = UPDATING_IDLE;
	private int mMaxPullHeight;
	private Context mContext;
	private String mTimeTag;
	private ProgressBar pbRefreshing;
	private ImageView ivArrow;
	private TextView tvRefreshText;
	private TextView tvRefreshTime;
	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;
	private String strRefreshing;
	private String strPullRefresh;
	private String strReleaseRefresh;
	private CloseTimer mUpdateTimer;
	private CloseTimer mResettimer;
	private OnStateListerer mListener;
	private static final Interpolator sInterpolator = new Interpolator() {
		
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
		}
		
	};

	public PullHeadView(Context context) {
		super(context);
		initView(context);
	}

	public PullHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PullHeadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	
	private void initView(Context context){
		this.mContext = context;		
		
		mMaxPullHeight = (int) (context.getResources().getDisplayMetrics().density* MAX_PULL_HEIGHT_DP + 0.5f);
		LayoutInflater.from(context).inflate(R.layout.view_layout_list_head, this, true);
		pbRefreshing = (ProgressBar) findViewById(R.id.pb_refresh);
		ivArrow = (ImageView) findViewById(R.id.imageview_refresh);
		tvRefreshText = (TextView) findViewById(R.id.tv_pull_to_refresh_text);
		tvRefreshTime = (TextView) findViewById(R.id.tv_pull_to_refresh_time);
		strPullRefresh = mContext.getString(R.string.pull_to_refresh_pull_label);
		strReleaseRefresh = mContext.getString(R.string.release_to_refresh_pull_label);
		strRefreshing = mContext.getString(R.string.string_refreshing);
		
		applyPullHeadViewTheme();
		/**
		 * 初始化动画
		 */
		mFlipAnimation = new RotateAnimation(0, -180,
		RotateAnimation.RELATIVE_TO_SELF, 0.5f,
		RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(200);
		mFlipAnimation.setFillAfter(true);

		/**
		 * 反转动画
		 */
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(200);
		mReverseFlipAnimation.setFillAfter(true);
	}

	public void showInitState() {
		ivArrow.setVisibility(View.VISIBLE);
		pbRefreshing.setVisibility(View.GONE);
		tvRefreshText.setText(strPullRefresh);
	}
	
	public void showInitTime(){
		String lastTime = getLastTimeLable();
		/* SLog.v(TAG, "lastTime:"+lastTime); */
		if (lastTime != null) {
			tvRefreshTime.setVisibility(View.VISIBLE);
			tvRefreshTime.setText(lastTime);
		} else {
			tvRefreshTime.setVisibility(View.GONE);
		}
	}

	public void showRefreshingState() {
		ivArrow.clearAnimation();
		ivArrow.setVisibility(View.GONE);
		pbRefreshing.setVisibility(View.VISIBLE);
		tvRefreshText.setText(strRefreshing);
	}

	/**
	 * 只有它在从release to refresh是才需要显示动画
	 * 
	 */
	public void showPullState() {
		ivArrow.setVisibility(View.VISIBLE);
		pbRefreshing.setVisibility(View.GONE);
		tvRefreshText.setText(strPullRefresh);
		ivArrow.clearAnimation();
		ivArrow.startAnimation(mReverseFlipAnimation);
	}

	public void showReleaseState() {
		ivArrow.setVisibility(View.VISIBLE);
		pbRefreshing.setVisibility(View.GONE);
		tvRefreshText.setText(strReleaseRefresh);
		ivArrow.clearAnimation();
		ivArrow.startAnimation(mFlipAnimation);
	}

	public void setStateListener(OnStateListerer listener){
		mListener = listener;
	}
	
	public void setTimeTag(String timeTag) {
		this.mTimeTag = timeTag;
	}
	
	/**
	 * 初始化上次更新时间 */
	private String getLastTimeLable() {
		if(mTimeTag != null){
			long time = mContext.getSharedPreferences(SP_NAME, 0).getLong(mTimeTag, 0);
			String format = mContext.getString(R.string.string_update_time,
					StringUtils.getTimeDisplayNameNormal(time));
			return format;
		}else {
		    return "最后更新于：刚刚";
        }
	}
	
	/**
	 * 刷新成功后调用 */
	public void updateLastTimeLable() {
		if (mTimeTag != null && mTimeTag.length() > 0) {
			long curTime = System.currentTimeMillis();
			mContext.getSharedPreferences(SP_NAME, 0).edit()
					.putLong(mTimeTag, curTime).commit();
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final View childView = getChildView();
		if (childView == null) {
			return;
		}
		final int childViewWidth = childView.getMeasuredWidth();
		final int childViewHeight = childView.getMeasuredHeight();
		final int measuredHeight = getMeasuredHeight();
		childView.layout(0, measuredHeight - childViewHeight, childViewWidth,
				measuredHeight);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		if (mHeight < 0) {
			mHeight = 0;
		}
		setMeasuredDimension(width, mHeight);
		final View childView = getChildView();
		if (childView != null) {
			childView.measure(widthMeasureSpec, heightMeasureSpec);
			mUpdateHeight = childView.getMeasuredHeight();
			/* SLog.v(TAG, "mUpdateHeight"+mUpdateHeight); */
		}
	}
	
	public void startUpdate() {
		mUpdatingStatus = UPDATING_READY;
		mInitHeight = mHeight;
		mDistance = mInitHeight - mUpdateHeight;
		if (mDistance < 0) {
			mDistance = mInitHeight;
		}
		int duration = (int) (mDistance * 3);
		duration = duration > MAX_DURATION ? MAX_DURATION : duration;
		/* Log.d(VIEW_LOG_TAG, "duration:" + duration); */
		mUpdateTimer = new CloseTimer(duration);
		mUpdateTimer.startTimer();
	}
	
	public void reset(int nextState) {
		if(mUpdatingStatus == UPDATING_ON_GOING){
			mUpdatingStatus = UPDATING_FINISH;
		}
		mDistance = mInitHeight = mHeight;
		int duration = (int) (mDistance * 4);
		duration = duration > MAX_DURATION ? MAX_DURATION : duration;
		mNextState = nextState;
		if(mUpdateTimer != null){
			mUpdateTimer.cancel();
		}
		mResettimer = new CloseTimer(duration);
		mResettimer.startTimer();
	}

	public boolean isUpdateNeeded() {
		if (mImediateUpdate) {
			mImediateUpdate = false;
			return true;
		}

		final int distance = mHeight - mUpdateHeight;
		boolean needUpdate = distance >= 0;
		return needUpdate;
	}

	public void moveToUpdateHeight() {
		showInitTime();
		setHeaderHeight(mUpdateHeight);
		mImediateUpdate = true;
	}

	private class CloseTimer extends CountDownTimer {
		private long mStart;
		private float mDurationReciprocal;
		private static final int COUNT_DOWN_INTERVAL = 15;

		public CloseTimer(long millisInFuture) {
			super(millisInFuture, COUNT_DOWN_INTERVAL);
			mDurationReciprocal = 1.0f / millisInFuture;
		}

		public void startTimer() {
			mStart = AnimationUtils.currentAnimationTimeMillis();
			start();
		}

		@Override
		public void onFinish() {
			if (mNextState != INVALID_STATE) {
				mNextState = INVALID_STATE;
				if(mListener != null){
					mListener.onReset();
				}
			}
			setHeaderHeight((int) (mInitHeight - mDistance));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			final int interval = (int) (AnimationUtils
					.currentAnimationTimeMillis() - mStart);
			float x = interval * mDurationReciprocal;
			x = sInterpolator.getInterpolation(x);
			setHeaderHeight((int) (mInitHeight - mDistance * x));
		}

	}

	private View getChildView() {
		final int childCount = getChildCount();
		if (childCount != 1) {
			return null;
		}
		return getChildAt(0);
	}

	public void setHeaderHeight(int height) {
		/* SLog.v(TAG, "mHeight:"+mHeight+"height:"+height); */
		if (mHeight == height && height == 0) {
			showInitTime();
			return;
		}
		if (height > mMaxPullHeight) {
			return;
		}
		mHeight = height;
		if (mUpdatingStatus != UPDATING_IDLE) {
			if (mUpdatingStatus == UPDATING_READY) {
				showRefreshingState();
				mUpdatingStatus = UPDATING_ON_GOING;
			}
		} else {
			if ((height < mUpdateHeight) && mCanUpdate) {
				showPullState();
				mCanUpdate = false;
			} else if ((height >= mUpdateHeight) && !mCanUpdate) {
				showReleaseState();
				mCanUpdate = true;
			}
		}
		requestLayout();
		if (height == 0) {
			mUpdatingStatus = UPDATING_IDLE;
			mCanUpdate = false;
			showInitState();
		}
	}

	public void applyPullHeadViewTheme() {
		ivArrow.setImageResource(R.drawable.refresh_arrow);
		tvRefreshText.setTextColor(mContext.getResources().getColor(R.color.pull_to_refresh_tips_color));
		tvRefreshTime.setTextColor(mContext.getResources().getColor(R.color.pull_to_refresh_time_color));
	}
	
	/**
	 * 状态重置
	 *
	 */
	public interface OnStateListerer {
		public void onReset();
	}
}