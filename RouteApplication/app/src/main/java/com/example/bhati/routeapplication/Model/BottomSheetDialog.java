package com.example.bhati.routeapplication.Model;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bhati.routeapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private String sentences, word;

    public BottomSheetDialog(String sentences, String word){
        this.sentences = sentences;
        this.word = word;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        TextView sentencesText = v.findViewById(R.id.sentences);
        sentencesText.setText(sentences);
        TextView wordText = v.findViewById(R.id.title);
        wordText.setText(word+" is used in these sentences :");
        return v;
    }
}
