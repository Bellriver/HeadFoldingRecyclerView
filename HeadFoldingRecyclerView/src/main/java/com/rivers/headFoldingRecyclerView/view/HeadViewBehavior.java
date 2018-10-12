package com.rivers.headFoldingRecyclerView.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.OverScroller;
import android.widget.RelativeLayout;


import com.rivers.headFoldingRecyclerView.R;

import java.lang.ref.WeakReference;

/**
 * Created by Rivers on 2018/10/10.
 * Description:
 */

public class HeadViewBehavior extends CoordinatorLayout.Behavior<RelativeLayout> {

    private String TAG = this.getClass().getSimpleName();
    private int maxHeight;
    private int minHeight;
    private final OverScroller mOverScroller;
    private boolean isAbleFold = true;  //是否可以折叠
    private FlingRunnable mFlingRunnable;
    private int duration = 400;
    private OnUnfoldFinishLister mOnUnfoldFinishLister;
    private WeakReference<CoordinatorLayout> coordinatorLayoutWeakReference;
    private WeakReference<RelativeLayout> cardViewWeakReference;
    private UnfoldRunnable unfoldRunnable;
    private int maxWidth;
    private int minWidth;

    public HeadViewBehavior(Context context, AttributeSet attr) {
        this.minHeight = (int) context.getResources().getDimension(R.dimen.main_topview_min_height);
        this.maxHeight = (int) context.getResources().getDimension(R.dimen.main_topview_max_height);
        mOverScroller = new OverScroller(context);
    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, RelativeLayout child, int layoutDirection) {
        Log.d(TAG, "onLayoutChild");
        if (coordinatorLayoutWeakReference == null) {
            coordinatorLayoutWeakReference = new WeakReference<CoordinatorLayout>(parent);
        }
        if (cardViewWeakReference == null) {
            cardViewWeakReference = new WeakReference<RelativeLayout>(child);
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RelativeLayout child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if (target instanceof RecyclerView) {
            LinearLayoutManager manager = (LinearLayoutManager) ((RecyclerView) target).getLayoutManager();
            int position = manager.findFirstCompletelyVisibleItemPosition();
            if (!mOverScroller.isFinished()) {
                mOverScroller.forceFinished(true);
            }
            return position == 0 && isAbleFold;
        }
        return false;
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RelativeLayout child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
        if (!mOverScroller.isFinished()) {
            mOverScroller.forceFinished(true);
        }
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        snapToChildIfNeeded(coordinatorLayout, child, params.height, params);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RelativeLayout child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (!isAbleFold) {
            return;
        }
        Log.d(TAG, "onNestedPreScroll");
        if (!mOverScroller.isFinished()) {
            mOverScroller.forceFinished(true);
        }

        if (target instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) target;
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstCompletelyVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            int oldheight = layoutParams.height;
            int finalHeight = oldheight - dy;
            if (dy > 0 && oldheight >= minHeight /*&& firstCompletelyVisibleItemPosition == 0*/) {//up
                finalHeight = finalHeight > minHeight ? finalHeight : minHeight;
                refresh(coordinatorLayout, child, layoutParams, finalHeight);
                consumed[1] = /*dy*/oldheight - finalHeight;
            } else if (dy < 0 && oldheight <= maxHeight && firstCompletelyVisibleItemPosition == 0) {//down
                finalHeight = finalHeight > maxHeight ? maxHeight : finalHeight;
                refresh(coordinatorLayout, child, layoutParams, finalHeight);
                consumed[1] = dy/*oldheight - finalHeight*/;
            } else {
//                CoordinatorLayout.LayoutParams layoutParams1 = (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
//                CoordinatorLayout.Behavior behavior = layoutParams1.getBehavior();
//                behavior.onNestedPreScroll(coordinatorLayout, recyclerView, recyclerView, dx, dy, consumed, type);
//                refresh(child, layoutParams, finalHeight);
            }
        }
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RelativeLayout child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RelativeLayout child, @NonNull View target, float velocityX, float velocityY) {
        Log.d(TAG, "onNestedPreScroll");
        if (!isAbleFold) {
            return false;
        }
        RecyclerView target1 = (RecyclerView) target;
        LinearLayoutManager layoutManager = (LinearLayoutManager) target1.getLayoutManager();
        if (layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
            return false;
        }
        if (!mOverScroller.isFinished()) {
            mOverScroller.forceFinished(true);
        }
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (mFlingRunnable == null) {
            mFlingRunnable = new FlingRunnable(coordinatorLayout, child, layoutParams);
        }
        int height = layoutParams.height;
        if (velocityY > 0 && height != minHeight) {
            mOverScroller.startScroll(0, height, 0, height - minHeight, duration);
            ViewCompat.postOnAnimation(child, mFlingRunnable);
            return true;
        } else if (velocityY < 0 && height != maxHeight) {
            mOverScroller.startScroll(0, height, 0, maxHeight - height, duration);
            ViewCompat.postOnAnimation(child, mFlingRunnable);
            return true;
        }
        return false;
    }

    private void snapToChildIfNeeded(CoordinatorLayout coordinatorLayout, RelativeLayout child, int finalHeight,
                                     CoordinatorLayout.LayoutParams layoutParams) {
        if (!isAbleFold) {
            return;
        }
        if (!mOverScroller.isFinished()) {
            mOverScroller.forceFinished(true);
        }
        if (finalHeight > (maxHeight - minHeight) / 2 + minHeight) {//fold
            mOverScroller.startScroll(0, finalHeight, 0, maxHeight - finalHeight, duration);
        } else {//unfold
            mOverScroller.startScroll(0, finalHeight, 0, minHeight - finalHeight, duration);
        }
        if (mFlingRunnable == null) {
            mFlingRunnable = new FlingRunnable(coordinatorLayout, child, layoutParams);
        }
        ViewCompat.postOnAnimation(child, mFlingRunnable);

    }

    private boolean isDependsOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.recyclerview;
    }

    private class FlingRunnable implements Runnable {
        private final CoordinatorLayout mParent;
        private final RelativeLayout mCardView;
        CoordinatorLayout.LayoutParams params;

        FlingRunnable(CoordinatorLayout parent, RelativeLayout layout, CoordinatorLayout.LayoutParams params) {
            mParent = parent;
            mCardView = layout;
            this.params = params;
        }

        @Override
        public void run() {
            if (mCardView != null && mOverScroller != null) {
                if (mOverScroller.computeScrollOffset()) {
                    int abs = Math.abs(mOverScroller.getCurrY());
                    refresh(mParent,mCardView, params, abs);
                    ViewCompat.postOnAnimation(mCardView, this);
                } else {
//                    onFlingFinished(mParent, mCardView, params);
                }
            }
        }
    }

    private void refresh(CoordinatorLayout parent, @NonNull RelativeLayout child, CoordinatorLayout.LayoutParams layoutParams, int finalHeight) {
        layoutParams.height = finalHeight;
        child.setLayoutParams(layoutParams);
        float factor = (float) finalHeight / maxHeight;
        float alpha = 0.5f * (1 + factor);
        float scale = 0.8f + 0.2f * factor;
        child.setAlpha(alpha);
        child.setScaleX(scale);
        child.setScaleY(scale);

        /*顶部右图固定方案*/
        RelativeLayout rightImg = parent.findViewById(R.id.ic_top_card_right);
        alpha = 0.9f + 0.1f * factor;
        rightImg.setAlpha(alpha);
        rightImg.setScaleY(scale);
        rightImg.setScaleX(scale);
    }

    public void setAbleFold(boolean ableFold) {
        isAbleFold = ableFold;
    }

    public void unfold() {
        if (coordinatorLayoutWeakReference != null && coordinatorLayoutWeakReference.get() != null
                && cardViewWeakReference != null && cardViewWeakReference.get() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
                    cardViewWeakReference.get().getLayoutParams();
            int height = layoutParams.height;
            //add--start--by--kezq--for--ncd#14558--2018/1/5
            if (height >= maxHeight) {
                if (mOnUnfoldFinishLister != null) {
                    mOnUnfoldFinishLister.onUnfoldFinished();
                }
                return;
            }//add--end--by--kezq--for--ncd#14558--2018/1/5
            mOverScroller.startScroll(0, height, 0, maxHeight - height, 200);
            unfoldRunnable = new UnfoldRunnable(coordinatorLayoutWeakReference.get(), cardViewWeakReference.get(), layoutParams);
            ViewCompat.postOnAnimation(cardViewWeakReference.get(), unfoldRunnable);
        }
    }

    private class UnfoldRunnable implements Runnable {
        private final CoordinatorLayout mParent;
        private final RelativeLayout mCardView;
        CoordinatorLayout.LayoutParams params;

        UnfoldRunnable(CoordinatorLayout parent, RelativeLayout layout, CoordinatorLayout.LayoutParams params) {
            mParent = parent;
            mCardView = layout;
            this.params = params;
        }

        @Override
        public void run() {
            if (mCardView != null && mOverScroller != null) {
                if (mOverScroller.computeScrollOffset()) {
                    refresh(mParent,mCardView, params, Math.abs(mOverScroller.getCurrY()));
                    ViewCompat.postOnAnimation(mCardView, this);
                } else {
                    if (mOnUnfoldFinishLister != null) {
                        mOnUnfoldFinishLister.onUnfoldFinished();
                    }
                }
            }
        }
    }

    public void onDestroy() {
        if (!mOverScroller.isFinished()) {
            mOverScroller.forceFinished(true);
        }
        if (cardViewWeakReference == null) {
            return;
        }
        final RelativeLayout mCardView = cardViewWeakReference.get();
        if (mCardView != null) {
            if (unfoldRunnable != null) {
                mCardView.removeCallbacks(unfoldRunnable);
            }
            if (mFlingRunnable != null) {
                mCardView.removeCallbacks(mFlingRunnable);
            }
        }
    }

    public interface OnUnfoldFinishLister {
        void onUnfoldFinished();
    }
}
