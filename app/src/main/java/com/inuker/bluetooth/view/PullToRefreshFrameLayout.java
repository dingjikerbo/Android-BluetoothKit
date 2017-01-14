package com.inuker.bluetooth.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inuker.bluetooth.AppConstants;
import com.inuker.bluetooth.R;

public class PullToRefreshFrameLayout extends FrameLayout {
	private Context mContext;
	private boolean hasHeader;
	private boolean hasFooter;
	private boolean hasDivider;
	private boolean hasShadow;
	private PullRefreshListView pullToRefreshListView;
	private int nType;
	private RelativeLayout loadLayout = null;
	private RelativeLayout errorLayout = null;
	private RelativeLayout emptyLayout = null;
	private ImageView mEmptyImageView = null;
	private FrameLayout mBackGroudLayout = null;
	private ImageView mShadowTop = null;
	private ImageView mShadowBottom = null;
	private ImageView mLoadingImg = null;
	private TextView mTips = null;

	public PullToRefreshFrameLayout(Context context) {
		this(context, null);
	}

	public PullToRefreshFrameLayout(Context context, boolean hasHeader, boolean hasFooter, boolean hasDivider, boolean hasShadow) {
		super(context);
		mContext = context;
		this.hasHeader = hasHeader;
		this.hasFooter = hasFooter;
		this.hasDivider = hasDivider;
		this.hasShadow = hasShadow;
		Init();
	}

	public PullToRefreshFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PullToRefreshFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		TypedArray arrayType = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshFrameLayout);
		hasHeader = arrayType.getBoolean(R.styleable.PullToRefreshFrameLayout_has_header, false);
		hasFooter = arrayType.getBoolean(R.styleable.PullToRefreshFrameLayout_has_footer, false);
		hasDivider = arrayType.getBoolean(R.styleable.PullToRefreshFrameLayout_has_divider, false);
		hasShadow = arrayType.getBoolean(R.styleable.PullToRefreshFrameLayout_has_shadow, true);
		arrayType.recycle();
		Init();
	}

	private void Init() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.pull_to_refresh_layout, this, true);
		pullToRefreshListView = (PullRefreshListView) findViewById(R.id.timeline_list);
		pullToRefreshListView.setHasHeader(hasHeader);
		if (!hasDivider) {
			pullToRefreshListView.setDivider(null);
			pullToRefreshListView.setDividerHeight(0);
		}
		pullToRefreshListView.initView();
		mBackGroudLayout = (FrameLayout) findViewById(R.id.pull_to_refresh_layout);
		loadLayout = (RelativeLayout) findViewById(R.id.loading_layout);
//		loadLayout = (ImageView) findViewById(R.id.loading_img);
		errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
		emptyLayout = (RelativeLayout) findViewById(R.id.empty_layout);
		mEmptyImageView = (ImageView) findViewById(R.id.empty_img);
		mShadowTop = (ImageView) findViewById(R.id.list_top_shadow);
		mShadowBottom = (ImageView) findViewById(R.id.list_bottom_shadow);
		mLoadingImg = (ImageView) findViewById(R.id.loading_img);
		mTips = (TextView) findViewById(R.id.empty_text_notice);
		if (hasShadow) {
			mShadowBottom.setVisibility(View.VISIBLE);
			mShadowTop.setVisibility(View.VISIBLE);
		} else {
			mShadowBottom.setVisibility(View.GONE);
			mShadowTop.setVisibility(View.GONE);
		}
	}

	public void applyFrameLayoutTheme() {
		mBackGroudLayout.setBackgroundColor(getResources().getColor(R.color.pull_to_refresh_bg_color));
		mShadowTop.setBackgroundResource(R.drawable.top_shadow_bg);
		mShadowBottom.setBackgroundResource(R.drawable.bottom_shadow_bg);
		mLoadingImg.setImageResource(0);

		pullToRefreshListView.applyPullRefreshViewTheme();
	}

	public PullRefreshListView getPullToRefreshListView() {
		return pullToRefreshListView;
	}

	public void setPullToRefreshListView(PullRefreshListView pullToRefreshListView) {
		this.pullToRefreshListView = pullToRefreshListView;
	}

	public void showState(int nState) {
		switch (nState) {
		case AppConstants.LIST:
			pullToRefreshListView.setVisibility(View.VISIBLE);
			loadLayout.setVisibility(View.GONE);
			emptyLayout.setVisibility(View.GONE);
			errorLayout.setVisibility(View.GONE);
			break;
		case AppConstants.LOADING:
			loadLayout.setVisibility(View.VISIBLE);
			pullToRefreshListView.setVisibility(View.GONE);
			emptyLayout.setVisibility(View.GONE);
			errorLayout.setVisibility(View.GONE);
			break;
		case AppConstants.EMPTY:
			emptyLayout.setVisibility(View.VISIBLE);
			loadLayout.setVisibility(View.GONE);
			pullToRefreshListView.setVisibility(View.GONE);
			errorLayout.setVisibility(View.GONE);
			break;
		case AppConstants.ERROR:
			errorLayout.setVisibility(View.VISIBLE);
			emptyLayout.setVisibility(View.GONE);
			loadLayout.setVisibility(View.GONE);
			pullToRefreshListView.setVisibility(View.GONE);
			break;
		case AppConstants.ALLOW_PULL_IN_EMPTY_PAGE: // 没有内容，但是允许下拉刷新
			pullToRefreshListView.setVisibility(View.VISIBLE);
			loadLayout.setVisibility(View.GONE);
			emptyLayout.setVisibility(View.VISIBLE);
			errorLayout.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		nType = nState;
	}

	public void showState(int nState, int emptyImageId, int enptyTextId) {
		showState(nState);

		if (nState == AppConstants.ALLOW_PULL_IN_EMPTY_PAGE) {
			if (mEmptyImageView != null) {
				if (emptyImageId > 0) {
					mEmptyImageView.setImageResource(emptyImageId);
					mEmptyImageView.setVisibility(View.VISIBLE);
				} else {
					mEmptyImageView.setVisibility(View.GONE);
				}
			}
			if (enptyTextId > 0 && mTips != null) {
				mTips.setText(enptyTextId);
			}
		}
	}

	public void setRetryButtonClickedListener(OnClickListener listener) {
		errorLayout.setOnClickListener(listener);
	}

	public int getStateType() {
		return this.nType;
	}

	public void setPullListViewTimeTag(String tag) {
		pullToRefreshListView.setPullTimeTag(tag);
	}

	public void setTipsText(String text) {
		mTips.setText(text);
	}
	
	public void setTopShadowHeight(int height) {
		ViewGroup.LayoutParams param = mShadowTop.getLayoutParams();
		param.height = height;
		mShadowTop.setLayoutParams(param);
	}
}
