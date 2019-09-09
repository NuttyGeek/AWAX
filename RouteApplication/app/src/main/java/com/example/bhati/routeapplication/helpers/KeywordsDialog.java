package com.example.bhati.routeapplication.helpers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bhati.routeapplication.R;

import java.util.ArrayList;

public class KeywordsDialog {

    public Context context;

    public KeywordsDialog(Context context){
        this.context = context;
    }

    public void showDialog(String msg){
//      creating dialog
        Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.keywords_dialog_layout);
        dialog.setTitle("Keywords: ");
//      setting message in dialog
        TextView content = dialog.findViewById(R.id.content);
        content.setText(msg);
//      setting click listeners on buttons
        Button button = dialog.findViewById(R.id.button);
//        adding dismiss button functionality
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
//      showing the dialog
        dialog.show();
    }


    /**
     * this fxn converts the list into a string which is needed in keywords dialog
     * @param list list of keywords
     * @return string to be shown in dialog
     */
    public String convertListIntoString(ArrayList<String> list){
        String str = "";
        for (int i=0; i<list.size(); i++){
            if(i==list.size()-1){
                str += list.get(i);
            }else{
                str += list.get(i)+", ";
            }
        }
        return str;
    }

}
