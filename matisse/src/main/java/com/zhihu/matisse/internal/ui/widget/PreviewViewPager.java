/*
 * Copyright 2017 Zhihu Inc.
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
package com.zhihu.matisse.internal.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.zhihu.matisse.internal.entity.Item;

public class PreviewViewPager extends RecyclerView {

    private final PagerSnapHelper mSnapHelper;
    private final LinearLayoutManager mLayoutManager;

    private OnPageChangeListener mOnPageChangeListener;

    public PreviewViewPager(@NonNull Context context) {
        this(context, null, 0);
    }

    public PreviewViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("deprecation")
    public PreviewViewPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLayoutManager = new LinearLayoutManager(context, HORIZONTAL, false);
        setLayoutManager(mLayoutManager);

        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(this);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(getCurrentPosition());
                }
            }
        });
    }

    /**
     * @deprecated 不允许外部调用
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Override
    @Deprecated
    public void setLayoutManager(@Nullable LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    public final PagerSnapHelper getSnapHelper() {
        return mSnapHelper;
    }

    public final void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public int getCurrentPosition() {
        View snapView = mSnapHelper.findSnapView(mLayoutManager);
        if (snapView == null) return 0;
        int position = mLayoutManager.getPosition(snapView);
        return Math.max(position, 0);
    }

    public interface OnPageChangeListener {

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        void onPageSelected(int position);

    }
}
