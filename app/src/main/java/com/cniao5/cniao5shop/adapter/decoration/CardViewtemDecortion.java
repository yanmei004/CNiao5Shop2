package com.cniao5.cniao5shop.adapter.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * CardView分割线
 * 三个方法：
 * onDraw(Canvas c, RecyclerView parent, State state)
 * onDrawOver(Canvas c, RecyclerView parent, State state)
 * getItemOffsets(Rect outRect, View view, RecyclerView parent, State state)
 */

public class CardViewtemDecortion extends RecyclerView.ItemDecoration {

    /**
     * 该方法会在绘制 item 之前调用，绘制范围是 RecyclerView 范围内的任意位置，不局限在 item 中。
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    /**
     * 该方法会在 item 绘制完之后调用，绘制在最上层。使用方法和 onDraw() 相同。
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    /**
     * 通过对构造方法中传入的 orientation 判断后设置 padding
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int layoutOrientation = getOrientation(parent);
        if (layoutOrientation == LinearLayoutManager.VERTICAL) {
            //padding值
            outRect.top = 10;//上
            outRect.left=5;//左
            outRect.right=5;//右
        } else if(layoutOrientation == LinearLayoutManager.HORIZONTAL) {
            outRect.left = 5;
        }
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getOrientation();
        } else throw new IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.");
    }
}
