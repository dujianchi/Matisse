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
package com.zhihu.matisse.internal.ui.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;
import com.zhihu.matisse.listener.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class PreviewPagerAdapter extends RecyclerView.Adapter<PreviewPagerAdapter.PreviewViewHolder> {

    private final ArrayList<Item> mItems = new ArrayList<>();
    private OnFragmentInteractionListener mListener;

    public PreviewPagerAdapter(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    public Item getMediaItem(int position) {
        return mItems.get(position);
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PreviewViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_preview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        Item item = mItems.get(position);
        if (item.isVideo()) {
            holder.videoPlayButton.setVisibility(View.VISIBLE);
            holder.videoPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(item.uri, "video/*");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, R.string.error_no_video_activity, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            holder.videoPlayButton.setVisibility(View.GONE);
        }

        holder.image.resetMatrix();
        holder.image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        holder.image.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

        Point size = PhotoMetadataUtils.getBitmapSize(item.getContentUri(), context);
        if (item.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadGifImage(context, size.x, size.y, holder.image,
                    item.getContentUri());
        } else {
            SelectionSpec.getInstance().imageEngine.loadImage(context, size.x, size.y, holder.image,
                    item.getContentUri());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addAll(List<Item> items) {
        if (items != null && !items.isEmpty()) {
            mItems.addAll(items);
        }
    }

    public static class PreviewViewHolder extends RecyclerView.ViewHolder {
        View videoPlayButton;
        ImageViewTouch image;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            videoPlayButton = itemView.findViewById(R.id.video_play_button);
            image = (ImageViewTouch) itemView.findViewById(R.id.image_view);
        }
    }

}
