package com.rivers.headFoldingRecyclerView.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.rivers.headFoldingRecyclerView.R;

import java.util.List;

/**
 * Created by Rivers on 2018/10/10.
 * Description:
 */

public class RecycleBehavior extends CoordinatorLayout.Behavior<RecyclerView> {
    private int gapMaxHeight;
    private int maxHeight;
    private int minHeight;
    private String TAG=this.getClass().getSimpleName();

    public RecycleBehavior(Context context, AttributeSet attr) {
        this.minHeight= (int) context.getResources().getDimension(R.dimen.main_topview_min_height);
        this.maxHeight= (int) context.getResources().getDimension(R.dimen.main_topview_max_height);
        this.gapMaxHeight = context.getResources().getDimensionPixelOffset(R.dimen.divider_header_gap);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RecyclerView child, View dependency) {
        Log.d(TAG,"layoutDependsOn");
        return isDependsOn(dependency);
    }

    private boolean isDependsOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.card;
    }

    private View findFirstDependency(List<View> views) {
        for (int i = 0, z = views.size(); i < z; i++) {
            View view = views.get(i);
            if (isDependsOn(view))
                return view;
        }
        return null;
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, RecyclerView child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        Log.d(TAG,"onMeasureChild");
        List<View> dependencies = parent.getDependencies(child);
        View dependency = findFirstDependency(dependencies);
        if (dependency!=null){
            int mode = View.MeasureSpec.getMode(parentHeightMeasureSpec);
            int size = View.MeasureSpec.getSize(parentHeightMeasureSpec);
            CoordinatorLayout.LayoutParams layoutParams= (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
            int i=size-minHeight-layoutParams.topMargin+gapMaxHeight;
            int measureHeight = View.MeasureSpec.makeMeasureSpec(i, mode);
            parent.onMeasureChild(child,parentWidthMeasureSpec,widthUsed,measureHeight,heightUsed);
            return true;
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, RecyclerView child, int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RecyclerView child, View dependency) {
        Log.d(TAG,"onDependentViewChanged");
        CoordinatorLayout.LayoutParams params= (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
        float factor=(float)params.height/maxHeight;
        int gapHeight = (int) (factor* gapMaxHeight); //中間縫隙高度
        int y= (int) (factor*params.height+params.topMargin+gapHeight);  //child距离顶部的高度
        child.setY(y);
        return true;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.d(TAG,"onStartNestedScroll");
        List<View> dependencies = parent.getDependencies(child);
        View dependency = findFirstDependency(dependencies);
        if (isDependsOn(target)) {
            int dependencyHeight = target.getHeight();
            LinearLayoutManager manager = (LinearLayoutManager) child.getLayoutManager();
            int position = manager.findFirstCompletelyVisibleItemPosition();
            if (dependencyHeight == minHeight && position != 0) {
                return true;
            }
        }
        return false;
    }
}
