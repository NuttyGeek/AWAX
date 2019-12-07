package com.example.bhati.routeapplication.Model;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.example.bhati.routeapplication.Adapter.SentenceAudioAdapter;
import com.example.bhati.routeapplication.Interface.OnAudioCompletedListener;
import com.example.bhati.routeapplication.Pojo.SentenceAudioPOJO;
import com.example.bhati.routeapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.io.File;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private String word;
    ListView listView;
    Context context;
    ArrayList<SentenceAudioPOJO> pojoList;
    ArrayList<String> sentences;
    ArrayList<String> audioPaths;
    ArrayList<File> audioFiles;

    public BottomSheetDialog(Context context, String word, ArrayList<String> sentences, ArrayList<String> audioPaths){

        Log.v("ng_bottom", "word: "+word+ "\nsentences: "+sentences.toString()+ "\naudioPaths: "+audioPaths.toString());
        this.word = word;
        this.context = context;
        // convert sentences string into arraylist
        this.sentences = sentences;
        this.audioPaths = audioPaths;
        audioFiles = getFilesListFromStringList(audioPaths);
        pojoList = populatePojoList(sentences,audioFiles);
        // create a list of pojo items
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        TextView wordText = v.findViewById(R.id.title);
        wordText.setText(word+" is used in these sentences :");
        listView = v.findViewById(R.id.listView);
        // create adapter object
        SentenceAudioAdapter adapter = new SentenceAudioAdapter(context, R.layout.sentence_pojo_item, pojoList, new OnAudioCompletedListener() {
            @Override
            public void onCompleted() {
                Log.v("nuttygeek_audio", "Audio Completed!");
            }
        });
        listView.setAdapter(adapter);
        return v;
    }

    /**
     * this fxn takes list of paths and returns the list of files
     * @param paths paths as a string
     * @return list of files
     */
    public ArrayList<File> createFileArrayFromPaths(ArrayList<String> paths){
        ArrayList<File> files = new ArrayList<>();
        for(int i=0; i<paths.size(); i++){
            File f = new File(Environment.getExternalStorageDirectory(),"RouteApp/"+paths.get(i));
            files.add(f);
        }
        return files;
    }


    /**
     * this fxn prepares the pojo list and returns them
     * @param sentences list of sentences in string
     * @param audioFiles list of audio file s
     * @return list of pojo objects
     */
    public ArrayList<SentenceAudioPOJO> populatePojoList(ArrayList<String> sentences, ArrayList<File> audioFiles){
        ArrayList<SentenceAudioPOJO> objectList = new ArrayList<>();
        for(int i=0; i<sentences.size(); i++){
            objectList.add(new SentenceAudioPOJO(sentences.get(i), audioFiles.get(i)));
        }
        return objectList;
    }


    /**
     * this fxn converts a string with commas into an arraylist
     * @param str string to be converted into arraylist
     * @return array list of items
     */
    public ArrayList<String> getArrayListFromString(String str){
        ArrayList<String> arr = new ArrayList<>();
        String[] s;
        s = str.split(",");
        for(int i=0; i<s.length; i++){
            arr.add(s[i]);
        }
        return arr;
    }

    /**
     * this fxn create a list of files from list of paths
     * @param paths list of paths
     * @return list of file objects
     */
    public ArrayList<File> getFilesListFromStringList(ArrayList<String> paths){
        ArrayList<File> files = new ArrayList<>();
        for(String path: paths){
            File f = new File(Environment.getExternalStorageDirectory(),"RouteApp/"+path);
            files.add(f);
        }
        return files;
    }
}
