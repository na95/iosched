/*
 * Copyright (c) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.samples.apps.iosched.feed;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.samples.apps.iosched.feed.data.FeedMessage;
import com.google.samples.apps.iosched.lib.R;

/**
 * Adapter for the {@link RecyclerView} that holds a list of conference updates.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    private static final int MIN_CAPACITY = 10;

    private final Point mScreenSize;
    private Context mContext;
    private SortedList<FeedMessage> mDataset;
    private int mPaddingNormal;
    private int mMessageCardImageWidth;
    private int mMessageCardImageHeight;

    public FeedAdapter(Context context, Point screenSize) {
        mContext = context;
        mDataset = new SortedList<>(FeedMessage.class, new FeedMessageCallback(), MIN_CAPACITY);
        mScreenSize = screenSize;
        mPaddingNormal =
                (int) context.getResources().getDimension(R.dimen.padding_normal);
        mMessageCardImageWidth = ((int) context.getResources()
                .getDimension(R.dimen.feed_message_card_image_width));
        mMessageCardImageHeight = ((int) context.getResources()
                .getDimension(R.dimen.feed_message_card_image_height));
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater li = LayoutInflater.from(parent.getContext());
        return new FeedViewHolder
                (li.inflate(R.layout.feed_message_card, parent, false));
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, final int position) {
        final FeedMessage feedMessage = mDataset.get(position);

        if (feedMessage.isEmergency()) {
            feedMessage.setExpanded(true);
            holder.expanded = feedMessage.isExpanded();
            holder.updateIconVisibilityForEmergency();
            holder.hasImage = !feedMessage.getImageUrl().isEmpty();
            holder.updateExpandOrCollapse
                    (false, mPaddingNormal, mMessageCardImageWidth, mMessageCardImageHeight);
        } else {
            holder.updateIconVisibilityForNonEmergency();
            holder.expanded = feedMessage.isExpanded();
            holder.updateExpandIcon(false);
            holder.hasImage = !feedMessage.getImageUrl().isEmpty();
            holder.updateExpandOrCollapse
                    (false, mPaddingNormal, mMessageCardImageWidth, mMessageCardImageHeight);
            holder.setOnFeedItemExpandListener(new OnFeedItemExpandListener() {
                @Override
                public void onFeedItemExpand() {
                    holder.updateExpandIcon(true);
                    holder.updateExpandOrCollapse
                            (true, mPaddingNormal, mMessageCardImageWidth, mMessageCardImageHeight);
                    feedMessage.flipExpanded();
                    int pos = holder.getAdapterPosition();
                    notifyItemChanged(pos);
                }
            });
        }
        holder.updateTitle(feedMessage.getTitle());
        holder.updateDateTime(feedMessage.getTimestamp());
        holder.updateDescription(feedMessage.getMessage());
        holder.updateImage(mContext, mScreenSize, feedMessage.getImageUrl());
        holder.updateCategory(feedMessage.getCategory(), feedMessage.getCategoryColor());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addFeedMessage(final FeedMessage feedMessage) {
        mDataset.add(feedMessage);
        notifyDataSetChanged();
    }

    public void updateFeedMessage(final FeedMessage feedMessage) {
        for(int i = 0; i < mDataset.size(); i++) {
            if(mDataset.get(i) != null) {
                if(feedMessage.getId() == mDataset.get(i).getId()) {
                    mDataset.updateItemAt(i, feedMessage);
                }
            }
        }
    }

    public void removeFeedMessage(final FeedMessage feedMessage) {
        mDataset.remove(feedMessage);
    }

    private class FeedMessageCallback extends SortedList.Callback<FeedMessage> {
        @Override
        public int compare(FeedMessage o1, FeedMessage o2) {
            return o1.compareTo(o2);
        }

        @Override
        public void onChanged(int position, int count) {
            // TODO(36778365) but this should never get called unless the CMS input breaks rules.
            notifyDataSetChanged();
        }

        @Override
        public boolean areContentsTheSame(FeedMessage oldItem, FeedMessage newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(FeedMessage item1, FeedMessage item2) {
            return item1.getId() == (item2.getId());
        }

        @Override
        public void onInserted(int position, int count) {
            // TODO(36778365) for fancy animation.
            notifyDataSetChanged();
        }

        @Override
        public void onRemoved(int position, int count) {
            // TODO(36778365) for fancy animation.
            notifyDataSetChanged();
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            // TODO(36778365) but this should never get called unless the CMS input breaks rules.
            notifyDataSetChanged();
        }
    }
}