package com.peterlaurence.trekadvisor.menu.record.components;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.peterlaurence.trekadvisor.R;
import com.peterlaurence.trekadvisor.core.track.TrackImporter;
import com.peterlaurence.trekadvisor.menu.tools.RecyclerItemClickListener;
import com.peterlaurence.trekadvisor.service.event.GpxFileWriteEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * List of recordings.
 *
 * @author peterLaurence on 23/12/17.
 */
public class RecordListView extends CardView {
    private boolean mIsMultiSelectMode = false;
    private ArrayList<File> mSelectedRecordings = new ArrayList<>();
    private ArrayList<File> mRecordings = new ArrayList<>();
    private RecordingAdapter mRecordingAdapter;

    public RecordListView(Context context) {
        this(context, null);
    }

    public RecordListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        updateRecordings();
        init(context, attrs);
    }

    private void updateRecordings() {
        mRecordings.clear();
        File[] recordings = TrackImporter.getRecordings();
        if (recordings != null) {
            mRecordings.addAll(Arrays.asList(recordings));
        }
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.record_list_layout, this);

        Context ctx = getContext();
        RecyclerView recyclerView = findViewById(R.id.recordings_recycler_id);
        ImageButton deleteRecordingButton = findViewById(R.id.delete_recording_button);

        deleteRecordingButton.setOnClickListener(v -> {
            boolean success = true;
            for (File file : mSelectedRecordings) {
                if (file.exists()) {
                    if (file.delete()) {
                        mRecordings.remove(file);
                    } else {
                        success = false;
                    }
                }
            }
            mRecordingAdapter.notifyDataSetChanged();

            /* Alert the user that some files could not be deleted */
            if (!success) {
                Snackbar snackbar = Snackbar.make(getRootView(), R.string.files_could_not_be_deleted,
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(llm);

        mRecordingAdapter = new RecordingAdapter(mRecordings, mSelectedRecordings);
        recyclerView.setAdapter(mRecordingAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this.getContext(),
                recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mIsMultiSelectMode) {
                    multiSelect(position);

                    mRecordingAdapter.setSelectedRecordings(mSelectedRecordings);
                    mRecordingAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                mSelectedRecordings = new ArrayList<>();
                if (!mIsMultiSelectMode) {
                    mIsMultiSelectMode = true;
                    deleteRecordingButton.setVisibility(View.VISIBLE);
                    multiSelect(position);
                    mRecordingAdapter.setSelectedRecordings(mSelectedRecordings);
                    mRecordingAdapter.notifyItemChanged(position);
                } else {
                    mIsMultiSelectMode = false;
                    deleteRecordingButton.setVisibility(View.GONE);
                    mRecordingAdapter.setSelectedRecordings(mSelectedRecordings);
                    mRecordingAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    private void multiSelect(int position) {
        File recording = mRecordings.get(position);
        if (mSelectedRecordings.contains(recording)) {
            mSelectedRecordings.remove(recording);
        } else {
            mSelectedRecordings.add(recording);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGpxFileWriteEvent(GpxFileWriteEvent event) {
        updateRecordings();
        mRecordingAdapter.notifyDataSetChanged();
    }
}
