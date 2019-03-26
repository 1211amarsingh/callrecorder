package com.kv.callrecorder;


import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kv.callrecorder.Utility.Utils.log;


public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    Activity context;
    ArrayList<AudioModel> audiofiles = new ArrayList<>();
    MediaPlayer mediaPlayer;
    boolean isplaying;
    static int playing_audio = -1;

    public RecordAdapter(Activity context, ArrayList<AudioModel> audiofiles) {
        this.context = context;
        this.audiofiles = audiofiles;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                isplaying = false;
                playing_audio = -1;
                log("setOnCompletionListener");
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup container, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, container, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.name.setText(audiofiles.get(i).getName());
        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    log(isplaying +" " + i + " "+playing_audio);

                    if (isplaying && playing_audio == i) {  //stop case
                        log("mediaPlayer.stop");
                        mediaPlayer.stop();
                        isplaying = false;
                    } else {
                        playing_audio = i;
                        log("mediaPlayer.start "+playing_audio);
                        isplaying = true;

                        startMediaPlayer(audiofiles.get(i).getPath());
                    }
                    notifyDataSetChanged();
                }

            }
        });

        if (isplaying && playing_audio == i) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_pause_circle));
            }
        }
    }

    private void startMediaPlayer(String path) {
        try {
            if (isplaying || mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return audiofiles.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.relative_layout)
        RelativeLayout relativeLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
