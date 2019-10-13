package com.example.bhati.routeapplication.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.bhati.routeapplication.Activities.properties;
import com.example.bhati.routeapplication.Interface.GotLabels;
import com.example.bhati.routeapplication.Interface.OnFrameExtracted;
import com.example.bhati.routeapplication.Pojo.FramesResult;
import com.example.bhati.routeapplication.Pojo.ImageDetectionResult;
import com.example.bhati.routeapplication.Pojo.ImageLabel;
import com.example.bhati.routeapplication.Pojo.UniqueLabelData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;

/**
 * this class is a helper for extracting frames from video
 */
public class FramesHelper {

    private Context context;
    private MediaMetadataRetriever metadataRetriever;
    // frame interval
    private String videoPath;
    private int REGULAR_FRAME_INTERVAL;
    private ArrayList<Integer> timestamps;
    private int lengthOfVideo;
    SharedPrefHelper prefHelp;
    HashMap<Integer,LatLng> timeLocationMap;

    public FramesHelper(Context context){
        timestamps = new ArrayList<>();
        this.context = context;
        metadataRetriever = new MediaMetadataRetriever();
        REGULAR_FRAME_INTERVAL = properties.REGULAR_FRAME_INTERVAL_MILLIS;
        prefHelp = new SharedPrefHelper(context);
        timeLocationMap = new HashMap<>();
    }


    /**
     * sets the path of the video which will be used ot do the processing
     * @param path Uri of the video
     */
    public void setVideoPath(String path){
        String[] paths =  path.split("file://");
        Log.v("string", "[Splited String]: "+paths[1]);
        // setting path of video file
        this.videoPath = paths[1];
        metadataRetriever.setDataSource(videoPath);
        String length = this.getLengthOfVideo();
        this.lengthOfVideo = Integer.parseInt(length);
        Log.v("length", "[Length]: "+length);
        // GENERATE TIME STAMPS and add it to a public property of this class
        generateTimestamps();
    }

    /**
     * this fxn returns the name of the video
     * @return name of the video
     */
    public String getVideoName(){
        return getVideoFileNameFromUriWithoutExtensions(videoPath);
    }

    /**
     * takes a time and extract the frame from the video and save it in the external storage
     * @param time time at which the frames should be extracted
     */
    public void getFrameFromVideo(int time){
        // getting image from video at particular image
        Bitmap image = metadataRetriever.getFrameAtTime(time*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//         First Image is corrupted and is not is JPEG format , so we will have neglect that first image and take in consideration
//         other images
        String dirPath = "/RouteApp/"+getVideoFileNameFromUriWithoutExtensions(this.videoPath);
        String fileName = String.valueOf(time)+".jpg";
        saveBitmapToStorage(image, dirPath, fileName);
    }

    /**
     * this fxn converts the bitmap into JPEG and save it to the given path
     * id the file is already present this fxn will do nothing
     * @param image  bitmap image
     * @param dirPath absolute path to save it on
     * @param fileName name of the file to create
     */
    public void saveBitmapToStorage(Bitmap image, String dirPath, String fileName){
        // root path of external storage
        String root = Environment.getExternalStorageDirectory().toString();
        // creating file
        File dirFile = new File(root+dirPath);
        // create the directory
        dirFile.mkdirs();
        // creating file
        File file = new File(dirFile,fileName);
        // converting bitmap to jpeg
        try {
            if(file.exists()){
                Log.v("file", "File already present ! Doing Nothing");
                //file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
            Log.v("file", "File Created: "+fileName);
            Toast.makeText(context, "File Created !", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * this function returns the file name from the video Uri
     * @param fileUri file URI
     * @return filename
     */
    public String getVideoFileNameFromUriWithExtension(String fileUri){
        return fileUri.split("/RouteApp/")[1];
    }

    public String getVideoFileNameFromUriWithoutExtensions(String fileUri){
        return fileUri.split("/RouteApp/")[1].replace(".mp4", "");
    }

    /**
     * this fxn returns the length of video in milliseconds
     * @return
     */
    public String getLengthOfVideo(){
        String length = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return length;
    }

    /**
     * generates time stamps avoiding the 0th timestamp
     */
    public void generateTimestamps(){
        Log.v("time", "inside the time stamp fxn ");
        int i=0;
        while(i<this.lengthOfVideo){
            Log.v("time", "[val]: "+i);
            timestamps.add(i);
            i=i+REGULAR_FRAME_INTERVAL;
        }
        Log.v("timestamps", timestamps.toString());
    }

    /**
     * this fxn returns the path of dir named after the video
     */
    public String getVideoDirName(){
        return this.videoPath.replace(".mp4", "");
    }

    /**
     * this fxn extracts all the frames intended from teh video
     */
    public void extractAllFrames(OnFrameExtracted callback){
        Toast.makeText(context, "Extracting Frames from Video !", Toast.LENGTH_SHORT).show();
        // sending total no of frames to be processed
        callback.getTotalFramesCount(timestamps.size());
        for (int i=0; i<timestamps.size();i++){
            Log.v("filename", "filename: "+timestamps.get(i).toString());
            getFrameFromVideo(timestamps.get(i));
            // sending index of frame processed, used for updating progress
            callback.getExtractedFrameCount(i+1);
        }
        // sending the completion callback
        callback.onFrameExtractionCompleted();
    }


    /**
     * get random FrameResult Object
     * @return frame result object
     */
    public FramesResult getFramesData(){
        FramesResult res = new FramesResult();
        res.setCar(String.valueOf(getValue()));
        res.setVegetation(String.valueOf(getValue()));
        res.setSnpashot(String.valueOf(getValue()));
        res.setStreet(String.valueOf(getValue()));
        res.setPerson(String.valueOf(getValue()));
        return res;
    }


    /**
     * get a random value
     * @return
     */
    public double getValue(){
        return Math.random();
    }


    /**
     * this fxn takes all the images in the given path
     * runs them through Image Labeling process
     * and save the result in the shared pref
     */
    public void processAllImagesForLabeling(GotLabels callback){
        // get the video dir
        Log.v("path", "path: "+getVideoDirName());
        // get a list of file uri in that video dir
        ArrayList<Uri> uris = getAllImageUrisFromVideoFolder();
        Log.v("uris", uris.toString());
        // process each image
        for (Uri uri: uris){
            if(uris.indexOf(uri) == uris.size()-1){
                Log.v("last", "\n\nThis is the last uri in list: "+uri.toString());
                processImageForLabeling(uri, callback, uris.indexOf(uri), true);
            }else{
                processImageForLabeling(uri, callback, uris.indexOf(uri), false);
            }
        }
    }


    /**
     * this fxn processes the image for getting labels
     * NOTE: this fxn runs asynchronously
     * @param uri uri of the image to be processed
     * @param callback callback
     * @param index index of the uri passed
     * @param last boolean value to indicate the the uri passed is last
     */
    public void processImageForLabeling(@NotNull Uri uri, GotLabels callback, int index, boolean last){
        Log.v("label", "Uri: "+uri.toString());
        try{
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(context, uri);
            // get on device image labeler
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();
            labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {

                    ArrayList<ImageLabel> labels = new ArrayList<>();
                    // get video name from uri
                    String videoName = getVideoFileNameFromUriWithoutExtensions(videoPath);
                    // get frame name from uri
                    String frameName = getFrameNameFromUriWithoutExtension(uri, getVideoFileNameFromUriWithoutExtensions(videoPath));
                    Log.v("frame_name", "Frame Name: "+frameName+ " Video Name: "+videoName);
                    for(FirebaseVisionImageLabel label: firebaseVisionImageLabels){
                        Log.v("success", label.getText()+" : "+label.getConfidence());
                        // convert label object to our own label object
                        ImageLabel imgLabel = new ImageLabel(label.getEntityId(),label.getText(), label.getConfidence());
                        // adding all simplified label object to my arraylist
                        labels.add(imgLabel);
                    }
                    callback.getProcessedFramesCount(index);
                    callback.gotLabelsSuccess(videoName, frameName, labels);
                    if(last){
                        callback.gotLabelsCompleted(videoName);
                    }
                    // now save it in shared pref
                    //prefHelp.saveImageLabels(videoName, frameName, labels);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.gotLabelsFailure(e.toString());
                    e.printStackTrace();
                    Log.v("fail", "Error: "+e.toString());
                }
            });
        }catch(IOException e){
            Log.e("error", "Error: IO Error while reading image uri from video folder");
            Toast.makeText(context, "IO Error, while reading image uri for video folder ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    /**
     * get a list of URI to iterate over, from the video folder
     * @return list of image uri in an folder
     */
    public ArrayList<Uri> getAllImageUrisFromVideoFolder(){
        ArrayList<Uri> listOfImageUris = new ArrayList<>();
        String videoDir = getVideoDirName();
        File directory = new File(videoDir);
        File[] files = directory.listFiles();
        for (File f: files){
            String frameName = getFrameNameFromAbsPathWithExtension(f.getAbsolutePath(), getVideoFileNameFromUriWithoutExtensions(videoPath));
            Log.v("file", "Frame Name: "+frameName);
            // if the frame is 0.jpg don't add it for analysis
            if(frameName.equals("0.jpg")){
                Log.v("zero", "Frame name is 0.jpg not inclding this frame in analysis ");
            }else{
                Uri uri = Uri.fromFile(f);
                listOfImageUris.add(uri);
            }
        }
        return listOfImageUris;
    }

    /**
     * this fxn returns the frame name with file extension from the absolute path of file
     * @param path path of the file
     * @param videoName video Name in the path
     * @return frame name with extension
     */
    public String getFrameNameFromAbsPathWithExtension(String path, String videoName){
        return path.split("/"+videoName+"/")[1];
    }

    /**
     * this fxn return the frame name without exntension
     * @param path absolute path of the frame image
     * @param videoName video anme or directiry in which the image present in
     * @return Name of the frame without extension
     */
    public String getFrameNameFromAbsPathWithoutExtension(String path, String videoName){
        return getFrameNameFromAbsPathWithExtension(path, videoName).replace(".jpg", "");
    }

    /**
     * this fxn converts absolute path from uri
     * @param uri uri of the file whose absolute path is required
     */
    public void getAbsolutePathFromUri(Uri uri){
        String path = uri.toString();
        File f = new File(path);
        String absPath = f.getAbsolutePath();
        Log.v("abs", "Ansolute Path From Uri: "+absPath);
    }

    /**
     * this fxn exracts frame name without extension from the uri given
     * @param uri uri of the frame image
     * @param videoName video name
     * @return the image name as string
     */
    public String getFrameNameFromUriWithoutExtension(Uri uri, String videoName){
        String uriString = uri.toString();
        return uriString.split("/"+videoName+"/")[1].replace(".jpg","");
    }


    /**
     * this fxn gets the timestamp from the image detecttion object
     * @param result Image Detection Result
     * @return ArrayList of timestamps
     */
    public void createTimestampsFromImageDetectionResult(ArrayList<LatLng> points, ImageDetectionResult result){
        HashMap<String, ArrayList<ImageLabel>> map = result.getFrameDataMap();
        Set<String> keySet = map.keySet();
        ArrayList<String> frameTimeStamps = new ArrayList<>(keySet);
        ArrayList<Integer> frameIntTimeStamps = getIntArrayListFromStringArrayListSorted(frameTimeStamps);
        int frameTimeStampSize = frameTimeStamps.size();
        int coordinatesSize = points.size();
        Log.v("time", "Size of time Stamps: "+frameTimeStampSize);
        Log.v("points", "Size of polyline Points: "+coordinatesSize);
        int gap = points.size()/frameTimeStampSize;
        ArrayList<LatLng> newCoordinates = getEveryNthValue(gap, points);
        // creating the hashmap
        for(int i=0; i<frameTimeStampSize; i++){
            timeLocationMap.put(frameIntTimeStamps.get(i), newCoordinates.get(i));
        }
        Log.v("map", "Time Location Map: "+timeLocationMap);
    }


    /**
     * this fxn filter the given list and return a list with every nth value
     * @param n nth number
     * @param points list to be filtered
     * @return
     */
    public ArrayList<LatLng> getEveryNthValue(int n,ArrayList<LatLng> points){
        ArrayList<LatLng> newList = new ArrayList<>();
        for (int i=0; i<points.size(); i++){
            if((i+1)%n==0){
                newList.add(points.get(i));
            }
        }
        Log.v("new", "new List: "+newList.toString());
        Log.v("new", "Size of new List: "+newList.size());
        return newList;
    }

    /**
     * this fxn cinverts the stirng arraylist to int arraylist and sort it in ascending order
     * @param stringList string list to be converted
     * @return int arraylist
     */
    public ArrayList<Integer> getIntArrayListFromStringArrayListSorted(ArrayList<String> stringList){
        ArrayList<Integer> intList  = new ArrayList<>();
        for (int i=0; i<stringList.size(); i++){
            intList.add(Integer.parseInt(stringList.get(i)));
        }
        Collections.sort(intList);
        return intList;
    }

    /**
     * NOTE: make sure you call this fxn after createTimestampsFromImageDetectionResult fxn
     * this fxn returns the list of coordinates from tim location map created before
     * @return
     */
    public ArrayList<LatLng> getCoordinatesFromTimeLocationMap(){
        ArrayList<LatLng> points = new ArrayList<>();
        for(Map.Entry<Integer, LatLng> entry: timeLocationMap.entrySet()){
            points.add(entry.getValue());
        }
        return points;
    }


    /**
     * This fxn create sa list og image lable object from the overall result
     * @param result Image detecttion resul object
     */
    public HashMap<String, UniqueLabelData> getChartData(ImageDetectionResult result){
        ArrayList<ImageLabel> allLabels = new ArrayList<>();
        HashMap<String, ArrayList<ImageLabel>> dataMap = result.getFrameDataMap();
        for(HashMap.Entry<String, ArrayList<ImageLabel>> entry : dataMap.entrySet()){
            Log.v("hash", "entry: "+entry.toString());
            ArrayList<ImageLabel> labelsInAFrame = entry.getValue();
            // iterating over the image label objects in an list
            for(int i=0; i<labelsInAFrame.size(); i++){
                allLabels.add(labelsInAFrame.get(i));
            }
        }
        // got all the labels now, find out unique label names from this all labels list
        ArrayList<String> uniqueLabelNames = new ArrayList<>();
        ArrayList<String> listOfLabelNames = new ArrayList<>();
        // get labels names from ImageLabel Object
        for(ImageLabel label: allLabels){
            listOfLabelNames.add(label.getName());
        }
        // getting unique label names
        Set setOfLabelNames = new HashSet(listOfLabelNames);
        uniqueLabelNames = new ArrayList<>(setOfLabelNames);
        // now create a list of hashmap <String, UniqueLabelData>
        HashMap<String, UniqueLabelData> averageMap = new HashMap<>();
        // create map for label and its corresponding label list
        for(String label: uniqueLabelNames){
            UniqueLabelData labelData = new UniqueLabelData(label);
            averageMap.put(label, labelData);
        }
        // now add the labels to it
        for(ImageLabel label: allLabels){
            UniqueLabelData obj = averageMap.get(label.getName());
            obj.appendScoreToLabel(label.getScore());
        }
        Log.v("uni","Average Map: "+averageMap.toString());
        Log.v("all", "Unique Labels: "+uniqueLabelNames);
        HashMap<String, UniqueLabelData> chartData = calculateAverageOfAverageMap(averageMap);
        return chartData;
    }

    public HashMap<String, UniqueLabelData> calculateAverageOfAverageMap(HashMap<String, UniqueLabelData> map){
        // print the content of the map
        for(HashMap.Entry<String, UniqueLabelData> entry: map.entrySet()){
            Log.v("entry", entry.getValue().getLabelName()+ " : "+entry.getValue().getScoreList().toString());
            entry.getValue().calculateAverage();
            Log.v("entry", entry.getValue().getLabelName()+ " : "+entry.getValue().getAverage());
        }
        return map;
    }


    public String getFrameNameFromLocation(LatLng point){
        // look in time location map and get the frame no
        String frameNo = null;
        for(HashMap.Entry<Integer, LatLng> entry: timeLocationMap.entrySet()){
            Log.v("en", entry.getKey()+" : "+entry.getValue().toString());
            if(point.equals(entry.getValue())){
                frameNo = entry.getKey().toString();
                Log.v("frame_no", "Frame a point: "+point.toString()+ " is: "+frameNo);
            }
        }
        return frameNo;
    }

    /**
     * this fxn returns the list of image labels from image detection result based on given frame name
     * @param frameName name of the frame
     * @param result image detection result object
     * @return list of image labels
     */
    public ArrayList<ImageLabel> getImageLabelsFromFrameName(String frameName, ImageDetectionResult result){
        HashMap<String, ArrayList<ImageLabel>> map = result.getFrameDataMap();
        ArrayList<ImageLabel> labels = map.get(frameName);
        return labels;
    }

    /**
     * this fxn takes the frame name and return the absolute path of the corresponding image
     * @param frameName name of the frame
     * @return null
     */
    public String getAbsolutePathOfImageFromFrameName(String frameName){
        String videoName = getVideoName();
        Log.v("path", "Video Name: "+videoName);
        Log.v("path", "Frame Name: "+frameName);
        File f = new File(Environment.getExternalStorageDirectory()+"/RouteApp/"+videoName+"/"+frameName+".jpg");
        String absPath = f.getAbsolutePath();
        Log.v("abs_path", "Abs Path: "+absPath);
        return absPath;
    }

    /**
     * this fxn takes an Image Label and returns the image label which we want to see
     * @param label label passed which we get from the firebase image labeling
     * @return a valid allowed label or null if we are not supposed to show any label
     */
    public ImageLabel getDesiredLabelObjectFromSimpleImageLabel(ImageLabel label){
        // see if this label name is in the json file
        ImageLabel newImageLabel = null;
        // creating a input stream to read the json file
        InputStream is = null;
        try {
            is = context.getAssets().open("labels.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String myJson = new String(buffer, "UTF-8");
            // creating json object from that json file called labels.json in assets folder
            JSONObject obj = new JSONObject(myJson);
            // getting new label if there is any to be changed
            String newLabelName = obj.getString(label.getName());
            Log.v("json_labels", obj.getString(label.getName()));
            // creating new label object with the allowed name
            newImageLabel = new ImageLabel(label.getId(), newLabelName,label.getScore());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // returning either a new allowed image label or null is we are not supposed to show any label
        return newImageLabel;
    }

    /**
     * this fxn returns a new Image Detection Result by taking the old Image Detection Result Object changing the names of the Labels
     * @param result old Image Detectionn Result Object
     * @return New Image Detecttion Result Object
     */
    public ImageDetectionResult getNewImageDetectionResultFromOld(ImageDetectionResult result){
        HashMap<String, ArrayList<ImageLabel>> map = new HashMap<>();
        HashMap<String, ArrayList<ImageLabel>> newMap = new HashMap();
        for(Map.Entry<String, ArrayList<ImageLabel>> entry: map.entrySet()){
            String key = entry.getKey();
            ArrayList<ImageLabel> labels = entry.getValue();
            ArrayList<ImageLabel> newLabels = entry.getValue();
            for(ImageLabel label: labels){
                ImageLabel newLabel = getDesiredLabelObjectFromSimpleImageLabel(label);
                if(newLabel != null){
                    Log.v("new_label", "Label: "+newLabel.getName());
                    newLabels.add(newLabel);
                }else{
                    Log.v("new_label", "Label: null");
                }
            }
            newMap.put(key, newLabels);
        }
        ImageDetectionResult newResult = new ImageDetectionResult(result.getVideoName());
        newResult.setFrameDataMap(newMap);
        return newResult;
    }







}
