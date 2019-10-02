package com.example.bhati.routeapplication.Pojo;

import java.util.ArrayList;

public class UniqueLabelData {

    String labelName;
    ArrayList<Float> scoreList;
    float average;

    public UniqueLabelData(String labelName){
        this.labelName = labelName;
        scoreList = new ArrayList<>();
        average = 0;
    }

    /**
     * this fxn adds the score the Label
     * @param score score to be added
     */
    public void appendScoreToLabel(float score){
        scoreList.add(score);
    }

    /**
     * get the label name
     * @return Label Name
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * get the average of the labels
     * @return average
     */
    public float getAverage(){
        return this.average;
    }

    /**
     * get the score list for a label
     * @return score list
     */
    public ArrayList<Float> getScoreList() {
        return scoreList;
    }

    /**
     * this fxn calculates the average and update the average value in the object
     */
    public void calculateAverage(){
        float sum = 0;
        for(float score: this.scoreList){
            sum += score;
        }
        this.average = sum/this.scoreList.size();
    }

}
