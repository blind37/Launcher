/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yao_guet;

//import org.adw.launcher.catalogue.CataGridView;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import com.blahti.example.drag.DragController;
import com.blahti.example.drag.DragSource;
import com.blahti.example.drag.Drawer;

import java.util.ArrayList;
import java.util.List;

public  class AllAppsGridView extends GridView implements
		AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
		DragSource, Drawer {
    private WindowManager.LayoutParams windowParams;
    private WindowManager windowManager;
    private int xtox;
    private int ytoy;
    private String LastAnimationID;
    private  ViewGroup itemView;
    private int m_startPosition;

    public interface AllView{
    public ScrollLayout getAllView();
}
	private DragController mDragger;
	final static String TAG = "AllAppsGridView";
	private Paint mPaint;
    private AllView mAllView;
    private ScrollLayout mScrollLayout;
    public static boolean flag = false;
	private final static int CLOSED = 1;
	private final static int OPEN = 2;
	private final static int CLOSING = 3;
	private final static int OPENING = 4;
	private int mStatus = CLOSED;
	private boolean isAnimating;
	private long startTime;
	private float mScaleFactor;
	private int mIconSize = 0;
	private int mBgAlpha = 255;
	private int mTargetAlpha = 255;
	private Paint mLabelPaint;
	private boolean shouldDrawLabels = false;
	private int mAnimationDuration = 800;
	private int mBgColor = 0x00000000;
	private boolean mDrawLabels = true;
	private boolean mFadeDrawLabels = false;
	private float mLabelFactor;
    private Context mContext;
    private ImageView deleteView;
    List<PackageInfo> apps = new ArrayList<PackageInfo>();
    private AppAdapter.AppItem appitem;
    static boolean LongClickMode = false;
    private int x_down;
    private int y_down;
    private int mLastX;
    private int mLastY;
    private int dragPosition;
    private int dropPosition;
    private int holdPosition;
    private int startPosition;
    private int specialPosition = -1;
    private int leftBottomPosition = -1;
    private boolean isCountXY = false;
    private boolean isMoving = false;
    static boolean flag_moving = false;
    private int itemTotalCount;
    private int halfItemWidth;
    private int nColumns = 4;
    private int nRows;
    private int Remainder;
    private int specialItemY;
    private int leftBtmItemY;
    private ImageView dragImageView = null;
    private ViewGroup dragItemView = null;
    private int distH;
    private int distV;
    private float x;
    private float y;
    private float width;
    private float height;
    private Rect rl1=new Rect();
    private Rect rl2=new Rect();
    private float scale;
    private Rect r3=new Rect();
    private int xx;

	public AllAppsGridView(Context context,ScrollLayout scrollLayout) {
//		super(context);

        this(context,null,android.R.attr.gridViewStyle);
        mScrollLayout = scrollLayout;
	}

	public AllAppsGridView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.gridViewStyle);
	}

	public AllAppsGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        mContext = context;
		mPaint = new Paint();
		mPaint.setDither(false);
		mLabelPaint = new Paint();
		mLabelPaint.setDither(false);
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
        setSelector(new ColorDrawable(Color.TRANSPARENT));




	}

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (dragImageView != null
                && dragPosition != AdapterView.INVALID_POSITION) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    Log.e(TAG,"OnMove");
                    if(!isCountXY) {
                        xtox = x-mLastX;
                        ytoy = y-mLastY;
                        isCountXY= true;
                    }
                    onDrag(x, y);
                    if(!isMoving )
                        OnMove(x,y);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.e(TAG,"stopdrag");
                    stopDrag();
                    onDrop(x, y);
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
	public boolean isOpaque() {
		if (mBgAlpha >= 255)
			return true;
		else
			return false;
	}

	@Override
	protected void onFinishInflate() {
		setOnItemClickListener(this);
		setOnItemLongClickListener(this);
	}

    @Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (!view.isInTouchMode()) {
			return false;
		}
        for(int i = 0;i<mScrollLayout.getChildCount();i++){
        AllAppsGridView gridView = (AllAppsGridView)mScrollLayout.getChildAt(i);
        AppAdapter m_adpater = (AppAdapter)gridView.getAdapter();
        Log.e(TAG,"getView---001");
        m_adpater.notifyDataSetChanged();
        }
//        getStartOrStopAnim(true,view);
        boolean DoubleLongClick = true;
        if (true) {
            mLastX = x_down;
            mLastY = y_down;
            startPosition = dragPosition = dropPosition = position;
            if (dragPosition == AdapterView.INVALID_POSITION) {
                  return false;
            }
             itemView = (ViewGroup) getChildAt(dragPosition
                    - getFirstVisiblePosition());
            Log.e(TAG,"itemView = "+itemView.getChildCount());
            if(!isCountXY){
                halfItemWidth = itemView.getWidth()/2;
                int rows;
                itemTotalCount = getCount();
                rows = itemTotalCount/nColumns;
                Remainder = itemTotalCount%nColumns;
                nRows =  (Remainder == 0) ? rows : rows + 1;
                Log.e(TAG,"nRows= "+nRows+"    Remainder= "+Remainder);
                specialPosition = itemTotalCount - 1 - Remainder;
                if(Remainder!=1)
                    leftBottomPosition = nColumns*(nRows-1);
                if(Remainder == 0 || nRows == 1)
                    specialPosition = -1;
                isCountXY = true;
            }
            Log.e(TAG,"dragPosition = "+dragPosition);
            if(specialPosition != -1 && dragPosition != -1){
                Log.e(TAG,"specialPosition= "+specialPosition);
                specialItemY = getChildAt(specialPosition).getTop();
            }else{
                specialItemY = -1;
            }
            if(leftBottomPosition != -1 && dragPosition != -1){
                leftBtmItemY = getChildAt(leftBottomPosition).getTop();
            }else{
                leftBtmItemY = -1;
            }
            dragItemView = itemView;
            itemView.destroyDrawingCache();
            itemView.setDrawingCacheEnabled(true);
            itemView.setDrawingCacheBackgroundColor(0x000000);
            Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache(true));
            Bitmap bitmap = Bitmap.createBitmap(bm, 0,0 , bm.getWidth(), bm.getHeight());
            startDrag(bitmap, x_down, y_down);
            hideDropItem(pointToPosition(x_down,y_down));
//            itemView.clearAnimation();
//            itemView.setVisibility(View.GONE);

            isMoving = false;
            DoubleLongClick = false;
//            LongClickMode = false;
//            return false;
        }

//        ResolveInfo app = (ResolveInfo) parent
//				.getItemAtPosition(position);
//		//app = new ApplicationInfo(app);
//        Log.e(TAG,"app= "+app);
//
//		mDragger.startDrag(view, this, app, DragController.DRAG_ACTION_COPY);

        LongClickMode = true;
		return LongClickMode&&DoubleLongClick;
	}

    private void hideDropItem(int x){
        final AppAdapter adapter = (AppAdapter)this.getAdapter();
        adapter.showDropItem(false,x);
        adapter.notifyDataSetChanged();
    }

    private void startDrag(Bitmap bm, int x, int y) {
        Log.e(TAG,"startDrag");
        stopDrag();
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowParams.x = dragItemView.getLeft() ;
        windowParams.y = dragItemView.getTop();
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.alpha = 0.0f;
        windowParams.format = PixelFormat.RGBA_8888;//此参数控制图片格式，效果为背景透明


        ImageView iv = new ImageView(getContext());
        iv.setBackgroundColor(Color.TRANSPARENT);
        iv.setImageBitmap(bm);
        windowManager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);

        windowManager.addView(iv, windowParams);

        dragImageView = iv;
    }

    private void stopDrag() {
        Log.e(TAG,"stopDrag");
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }

    private void onDrag(int x, int y) {
        Log.e(TAG,"onDrag");
        if (dragImageView != null) {
            windowParams.alpha = 0.8f;
            windowParams.x = (x-mLastX-xtox)+dragItemView.getLeft()+8;
            windowParams.y = (y-mLastY-ytoy)+dragItemView.getTop();
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
    }

    public  void OnMove(int x, int y){
        Log.e(TAG,"onMove");
        flag_moving = true;
        int TempPosition = pointToPosition(x,y);
        m_startPosition = TempPosition;
        int sOffsetY = specialItemY == -1 ? y - mLastY : y - specialItemY - halfItemWidth;
        int lOffsetY = leftBtmItemY == -1 ? y - mLastY : y - leftBtmItemY - halfItemWidth;
        if(TempPosition != AdapterView.INVALID_POSITION && TempPosition != dragPosition){
            dropPosition = TempPosition;
        }else if(specialPosition != -1 && dragPosition == specialPosition && sOffsetY >= halfItemWidth){
            dropPosition = (itemTotalCount - 1);
        }else if(leftBottomPosition != -1 && dragPosition == leftBottomPosition && lOffsetY >= halfItemWidth){
            dropPosition = (itemTotalCount - 1);
        }
        if(dragPosition != startPosition)
            dragPosition = startPosition;
        int MoveNum = dropPosition - dragPosition;
        if(dragPosition != startPosition && dragPosition == dropPosition)
            MoveNum = 0;
        if(MoveNum != 0){
            int itemMoveNum = Math.abs(MoveNum);
            float Xoffset,Yoffset;
            for (int i = 0;i < itemMoveNum;i++){
                if(MoveNum > 0){
                    holdPosition = dragPosition + 1;
                    Xoffset = (dragPosition/nColumns == holdPosition/nColumns) ? (-1) : (nColumns -1);
                    Yoffset = (dragPosition/nColumns == holdPosition/nColumns) ? 0 : (-1);
                }else{
                    holdPosition = dragPosition - 1;
                    Xoffset = (dragPosition/nColumns == holdPosition/nColumns) ? 1 : (-(nColumns-1));
                    Yoffset = (dragPosition/nColumns == holdPosition/nColumns) ? 0 : 1;
                }
                ViewGroup moveView = (ViewGroup)getChildAt(holdPosition);
                Animation animation = getMoveAnimation(Xoffset,Yoffset);
                moveView.startAnimation(animation);
                dragPosition = holdPosition;
                if(dragPosition == dropPosition)
                    LastAnimationID = animation.toString();
                final AppAdapter adapter = (AppAdapter)this.getAdapter();
                animation.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO Auto-generated method stub
                        isMoving = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // TODO Auto-generated method stub
                        String animaionID = animation.toString();
                        if(animaionID.equalsIgnoreCase(LastAnimationID)){
//                            adapter.exchange(startPosition, dropPosition);
                            adapter.exchange(startPosition, dropPosition, m_startPosition);
                            startPosition = dropPosition;
                            isMoving = false;
                        }
                    }
                });
            }
        }
    }

    private void onDrop(int x,int y){
        Log.e(TAG,"onDrop");
        flag_moving =false;
        final AppAdapter adapter = (AppAdapter) this.getAdapter();
        adapter.showDropItem(true,0);
        itemView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    public Animation getMoveAnimation(float x,float y){
        TranslateAnimation go = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, x,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, y);
        go.setFillAfter(true);
        go.setDuration(300);
        return go;
    }

	public void setDragger(DragController dragger) {
		mDragger = dragger;
	}

	public void onDropCompleted(View target, boolean success) {
	}

	
	
	/**
	 * ADW: easing functions for animation
	 */
	static float easeOut(float time, float begin, float end, float duration) {
		float change = end - begin;
		return change * ((time = time / duration - 1) * time * time + 1)
				+ begin;
	}

	static float easeIn(float time, float begin, float end, float duration) {
		float change = end - begin;
		return change * (time /= duration) * time * time + begin;
	}

	static float easeInOut(float time, float begin, float end, float duration) {
		float change = end - begin;
		if ((time /= duration / 2.0f) < 1)
			return change / 2.0f * time * time * time + begin;
		return change / 2.0f * ((time -= 2.0f) * time * time + 2.0f) + begin;
	}

	/**
	 * ADW: Override drawing methods to do animation
	 */
	@Override
	public void draw(Canvas canvas) {
		if (isAnimating) {
			long currentTime;
			if (startTime == 0) {
				startTime = SystemClock.uptimeMillis();
				currentTime = 0;
			} else {
				currentTime = SystemClock.uptimeMillis() - startTime;
			}
			if (mStatus == OPENING) {
				mScaleFactor = easeOut(currentTime, 3.0f, 1.0f, mAnimationDuration);
				mLabelFactor = easeOut(currentTime, -1.0f, 1.0f, mAnimationDuration);
			} else if (mStatus == CLOSING) {
				mScaleFactor = easeIn(currentTime, 1.0f, 3.0f, mAnimationDuration);
				mLabelFactor = easeIn(currentTime, 1.0f, -1.0f, mAnimationDuration);
			}
			if (mLabelFactor < 0)
				mLabelFactor = 0;
			if (currentTime >= mAnimationDuration) {
				isAnimating = false;
				if (mStatus == OPENING) {
					mStatus = OPEN;
				} else if (mStatus == CLOSING) {
					mStatus = CLOSED;
					
					setVisibility(View.GONE);
				}
			}
		}
		shouldDrawLabels = mFadeDrawLabels && mDrawLabels
				&& (mStatus == OPENING || mStatus == CLOSING);
		float porcentajeScale = 1.0f;
		if (isAnimating) {
			porcentajeScale = 1.0f - ((mScaleFactor - 1) / 3.0f);
			if (porcentajeScale > 0.9f)
				porcentajeScale = 1f;
			if (porcentajeScale < 0)
				porcentajeScale = 0;
			mBgAlpha = (int) (porcentajeScale * 255);
		}
        Log.e("gomtel","mPaint = "+mPaint);
		mPaint.setAlpha(mBgAlpha);
		if (getVisibility() == View.VISIBLE) {
//			canvas
//					.drawARGB((int) (porcentajeScale * mTargetAlpha), Color
//							.red(mBgColor), Color.green(mBgColor), Color
//							.blue(mBgColor));
			super.draw(canvas);
		}

	}


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e(TAG,"Action_down");
                x_down = (int)ev.getX();
                y_down = (int)ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

//	@Override
//	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//		int saveCount = canvas.save();
//		Drawable[] tmp = ((TextView) child).getCompoundDrawables();
//		if (mIconSize == 0) {
//			mIconSize = tmp[1].getIntrinsicHeight() + child.getPaddingTop();
//		}
//		if (isAnimating) {
//			postInvalidate();
//			//float x;
//			//float y;
//			distH = (child.getLeft() + (child.getWidth() / 2))
//					- (getWidth() / 2);
//			distV = (child.getTop() + (child.getHeight() / 2))
//					- (getHeight() / 2);
//			x = child.getLeft() + (distH * (mScaleFactor - 1)) * (mScaleFactor);
//			y = child.getTop() + (distV * (mScaleFactor - 1)) * (mScaleFactor);
//			width = child.getWidth() * mScaleFactor;
//			height = (child.getHeight() - (child.getHeight() - mIconSize))
//					* mScaleFactor;
//			if (shouldDrawLabels)
//				child.setDrawingCacheEnabled(true);
//			if (shouldDrawLabels && child.getDrawingCache() != null) {
//				// ADW: try to manually draw labels
//				rl1.set(0, mIconSize, child.getDrawingCache()
//						.getWidth(), child.getDrawingCache().getHeight());
//				rl2.set(child.getLeft(),
//						child.getTop() + mIconSize, child.getLeft()
//								+ child.getDrawingCache().getWidth(), child
//								.getTop()
//								+ child.getDrawingCache().getHeight());
//				mLabelPaint.setAlpha((int) (mLabelFactor * 255));
//				canvas.drawBitmap(child.getDrawingCache(), rl1, rl2,
//						mLabelPaint);
//			}
//			scale = ((width) / child.getWidth());
//			r3 = tmp[1].getBounds();
//			xx = (child.getWidth() / 2) - (r3.width() / 2);
//			canvas.save();
//			canvas.translate(x + xx, y + child.getPaddingTop());
//			canvas.scale(scale, scale);
//			tmp[1].draw(canvas);
//			canvas.restore();
//		} else {
//			if (mDrawLabels) {
//				child.setDrawingCacheEnabled(true);
//				if (child.getDrawingCache() != null) {
//					mPaint.setAlpha(255);
//					canvas.drawBitmap(child.getDrawingCache(), child.getLeft(),
//							child.getTop(), mPaint);
//				} else {
//					canvas.save();
//					canvas.translate(child.getLeft(), child.getTop());
//					child.draw(canvas);
//					canvas.restore();
//				}
//			} else {
//				r3 = tmp[1].getBounds();
//				xx = (child.getWidth() / 2) - (r3.width() / 2);
//				canvas.save();
//				canvas.translate(child.getLeft() + xx, child.getTop()
//						+ child.getPaddingTop());
//				tmp[1].draw(canvas);
//				canvas.restore();
//			}
//		}
//		canvas.restoreToCount(saveCount);
//		return true;
//	}

	/**
	 * Open/close public methods
	 */
	public void open(boolean animate) {
		
	}

	public void close(boolean animate) {
        if(getAdapter()==null)
        	animate=false;
        else if(getAdapter().getCount()<=0)
        	animate=false;
		if (animate) {
			mStatus = CLOSING;
			isAnimating = true;
		} else {
			mStatus = CLOSED;
			isAnimating = false;
			
			setVisibility(View.GONE);
		}
		startTime = 0;
		invalidate();
	}
	public void setAnimationSpeed(int speed) {
		mAnimationDuration = speed;
	}

	public void updateAppGrp() {
		if(getAdapter()!=null){
			
		}
	}

	

	public void setNumRows(int numRows) {}

	public void setPageHorizontalMargin(int margin) {}



	@Override
	public void setDragController(DragController dragger) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void setAdapter(ListAdapter adapter) {
//
//
//	}
public void loadActivity(String name){
//    ResolveInfo appInfo = (ResolveInfo)parent.getItemAtPosition(position);

    Intent mainIntent = mContext.getPackageManager()
            .getLaunchIntentForPackage(name);
    Log.e(TAG,"packagename= "+name);
    if(mainIntent != null)
    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    try {
        // launcher the package
        mContext.startActivity(mainIntent);
    } catch (ActivityNotFoundException noFound) {
        Toast.makeText(mContext, "Package not found!", Toast.LENGTH_SHORT).show();
    }
}
    public void removeActivity(){
        Uri packageURI = Uri.parse("package:com.android.myapp");
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        mContext.startActivity(uninstallIntent);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		Toast.makeText(getContext(), "onItemClick", Toast.LENGTH_LONG).show();
        AppAdapter.AppItem item = (AppAdapter.AppItem)view.getTag();

//        ResolveInfo appInfo = (ResolveInfo)parent.getItemAtPosition(position);
        ContentValues appInfo = (ContentValues)parent.getItemAtPosition(position);
        Log.e(TAG,"appInfo ="+appInfo);
        String packagename = appInfo.getAsString("package");
        ImageView mDeleteView = item.mDeleteIcon;
        mDeleteView.setTag(packagename);
        Log.e(TAG," Action_down onItemclick   "+item.mAppName.getText());
        if(!LongClickMode){
            loadActivity(packagename);
        }
        if(LongClickMode){
            mDeleteView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG,"mDeleteIcon = "+view.getTag());
                    int m;
                    boolean flag_system = false;
                    PackageManager pManager = mContext.getPackageManager();
                    apps = pManager.getInstalledPackages(0);
                    for(m = 0;m<apps.size();m++){
                    if(apps.get(m).packageName.equals(view.getTag()))
                        if((apps.get(m).applicationInfo.flags&apps.get(m).applicationInfo.FLAG_SYSTEM)>0){
                            Toast.makeText(getContext(), "系统应用，请勿卸载", Toast.LENGTH_LONG).show();
                            flag_system = true;
                        }

                    }
                    if(!flag_system){
                    Uri packageURI = Uri.parse("package:"+view.getTag());
                    Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                    mContext.startActivity(uninstallIntent);
                    }
                }
            });
        }
		
	}


	


}
