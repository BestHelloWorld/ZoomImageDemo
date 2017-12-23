package com.example.mitnick.zoomimageviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by mitnick on 2017/12/9.
 */

public class ZoomImageView extends View
        implements Observer
{

    private Rect mDstRect;
    private Rect mSrcRect;

    private Bitmap mBitmap;

    private ZoomStatus mStatus = null;

    private Paint mPaint;

    private int mScreenWidth;
    private int mScreenHeight;

    private ZoomListener mZoomListener;

    public ZoomImageView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        mPaint = new Paint();

        mZoomListener = new ZoomListener();
        setOnTouchListener(mZoomListener);
        mZoomListener.addObserver(this);
    }

    public void setBitmap(Bitmap bitmap)
    {
        if (mBitmap == null)
            mBitmap = bitmap;

        mDstRect = new Rect();
        mSrcRect = new Rect();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (mBitmap != null)
        {
            int offset = 0;
            if (mStatus != null)
                offset = (int) mStatus.getFingerMoveOffset();

            Log.d("move offset", "offset" + offset);

            mDstRect.right = mScreenWidth;
            mDstRect.bottom = mScreenHeight;
            mSrcRect.right = mBitmap.getWidth();
            mSrcRect.bottom = mBitmap.getHeight();

            Log.d("dst rectangle", mDstRect.toString());
            Log.d("src rectangle", mSrcRect.toString());

            if (offset == 0)
            {
                canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, mPaint);
            } else
            {
                Bitmap tmp = Bitmap.createScaledBitmap(mBitmap, mDstRect.width() + offset,
                        mDstRect.height() + offset, true);

                canvas.drawBitmap(tmp, -offset >> 1, -offset >> 1, mPaint);
            }
        }
    }

    @Override
    public void update(Observable observable, Object o)
    {
        if (o instanceof ZoomStatus)
        {
            mStatus = (ZoomStatus) o;
            Log.d("observer update", mStatus.toString());
        }
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        mScreenWidth = getWidth();
        mScreenHeight = getHeight();
    }

    private class ZoomListener extends Observable
            implements View.OnTouchListener
    {
        private ZoomStatus mStatus = new ZoomStatus();

        private float mBeginDistance;
        private float mLastDistance;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            Point center = null;

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                if (motionEvent.getPointerCount() == 1)
                {
                    Log.d("motion event",
                            "down " + motionEvent.getX() + " - " + motionEvent.getY());
                } else
                {
                    mBeginDistance = distanceBetweenFingers(motionEvent.getX(0), motionEvent.getY(0),
                            motionEvent.getX(1), motionEvent.getY(1));

                    Log.d("motion event", "double down " + motionEvent.getX(0) + motionEvent.getY(0) +
                            motionEvent.getX(1) + motionEvent.getY(1));
                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE)
            {
                if (motionEvent.getPointerCount() == 1)
                {
                    Log.d("motion event",
                            "single move " + motionEvent.getX() + " - " + motionEvent.getY());
                } else
                {
                    mLastDistance = distanceBetweenFingers(motionEvent.getX(0), motionEvent.getY(0),
                            motionEvent.getX(1), motionEvent.getY(1));

                    if (mBeginDistance == 0)
                    {
                        mBeginDistance = mLastDistance;
                    } else
                    {
                        mStatus.setMoveOffset(mLastDistance - mBeginDistance);
                        mBeginDistance = mLastDistance;
                    }

                    center = centerBetweenFingers(motionEvent.getX(0), motionEvent.getY(0),
                            motionEvent.getX(1), motionEvent.getY(1));
                    mStatus.setPoint(center);
                    Log.d("motion event",
                            "double move length : " + (mBeginDistance) + " center : " + center.toString());

                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                mBeginDistance = 0;
                mLastDistance = 0;
            }


            setChanged();
            notifyObservers(getStatus());
            return true;
        }

        public ZoomStatus getStatus()
        {
            return mStatus;
        }

        public float distanceBetweenFingers(float x1, float y1, float x2, float y2)
        {
            return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        }

        public Point centerBetweenFingers(float x1, float y1, float x2, float y2)
        {
            Point p = new Point();
            p.x = (int) (x1 + x2) / 2;
            p.y = (int) (y1 + y2) / 2;
            return p;
        }

    }

    private class ZoomStatus
    {
        public Point mCenterPoint;
        public float mFingerMoveOffset;

        public void setMoveOffset(float fingerMoveOffset)
        {
            mFingerMoveOffset += fingerMoveOffset / 2;
        }

        public void setPoint(Point centerPoint)
        {
            mCenterPoint = centerPoint;
        }

        public Point getCenterPoint()
        {
            return mCenterPoint;
        }

        public float getFingerMoveOffset()
        {
            return mFingerMoveOffset;
        }
    }
}
