package com.example.bhati.routeapplication.Pojo;

import java.io.File;

public class SentenceAudioPOJO {

    private String sentence;
    private File audioFile;

    public SentenceAudioPOJO(String sentence, File audioFile){
        this.sentence = sentence;
        this.audioFile = audioFile;
    }

    public String getSentence() {
        return sentence;
    }

    public File getAudioFile() {
        return audioFile;
    }
}

