package com.example.bhati.routeapplication.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.bhati.routeapplication.Interface.OnAudioCompletedListener;
import com.example.bhati.routeapplication.Pojo.SentenceAudioPOJO;
import com.example.bhati.routeapplication.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SentenceAudioAdapter extends ArrayAdapter<SentenceAudioPOJO>{

    List<SentenceAudioPOJO> pojoList;
    Context context;
    MediaPlayer mediaPlayer;
    OnAudioCompletedListener callback;
    ArrayList<Button> playingButtons;


    public SentenceAudioAdapter(@NonNull Context context, int resource, @NonNull List<SentenceAudioPOJO> objects, OnAudioCompletedListener callback) {
        super(context, resource, objects);
        this.context = context;
        this.pojoList = objects;
        this.callback = callback;
        playingButtons = new ArrayList<>();

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.sentence_pojo_item, parent, false);

        final int index = position;

        TextView sentenceTextView = v.findViewById(R.id.sentence);
//        TextView audioTextView = v.findViewById(R.id.audio);
        SentenceAudioPOJO object = pojoList.get(position);
        String sentence = object.getSentence();
        String audioString = object.getAudioFile().getAbsolutePath();
        sentenceTextView.setText(sentence);
//        audioTextView.setText(audioString);
        final Button playBtn = v.findViewById(R.id.playBtn);
        playingButtons.add(playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.fromFile(pojoList.get(index).getAudioFile());
                Log.v("nuttygeek_uri",uri.toString());
                if(mediaPlayer!=null){
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
                mediaPlayer = MediaPlayer.create(context, uri);
                mediaPlayer.start();
                resetAllPlayingButtons(playingButtons);
                playBtn.setText("Playing ...");
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        playBtn.setText("Play Audio");
                        callback.onCompleted();
                    }
                });
            }
        });
        return v;
    }


    /**
     * this fxn resets all the playing buttons
     * @param buttons
     */
    public void resetAllPlayingButtons(ArrayList<Button> buttons){
        for(Button button: buttons){
            button.setText("Play Audio");
        }
    }

}
