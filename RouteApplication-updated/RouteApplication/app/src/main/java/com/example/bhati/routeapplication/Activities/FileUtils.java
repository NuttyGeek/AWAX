package com.example.bhati.routeapplication.Activities;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

public class FileUtils
{
    public static ArrayList<String> getFileNames
    (final String folder, final String fileNameFilterPattern, final int sort)
    {
        try {
            ArrayList<String> myData = new ArrayList<String>();
            File fileDir = new File(folder);
            if (!fileDir.exists() || !fileDir.isDirectory()) {
                return null;
            }

            String[] files = fileDir.list();

            if (files.length == 0) {
                return null;
            }
            for (int i = 0; i < files.length; i++) {
                if (fileNameFilterPattern == null ||
                        files[i].contains(fileNameFilterPattern))
                    myData.add(files[i]);
            }
            if (myData.size() == 0)
                return null;

            if (sort != 0) {
                Collections.sort(myData, String.CASE_INSENSITIVE_ORDER);
                if (sort < 0)
                    Collections.reverse(myData);
            }

            return myData;
        }catch (Exception ex)
        {
            ex.printStackTrace();
            Log.d("EXPCHUMK",ex.getMessage(),ex);
            return null;
        }
    }

    /**
     * Copy a file's contents.
     * @param fromFilePath  Full path to source file
     * @param toFilePath    Full path to destination file
     * @param overwriteExisting if true, toFile will be deleted before the copy
     * @return true if OK, false if couldn't copy (SecurityException, etc)
     * @throws IOException if an error occurred when opening, closing, reading, or writing;
     *     even after an exception, copyFile will close the files before returning.
     */
    public static boolean copyFile
    (String fromFilePath, String toFilePath, final boolean overwriteExisting)
            throws IOException
    {
        try{
            File fromFile = new File(fromFilePath);
            File toFile = new File(toFilePath);
            if(overwriteExisting && toFile.exists())
                toFile.delete();
            return copyFile(fromFile, toFile);
        }
        catch(SecurityException e){
            return false;
        }
    }


    public static boolean copyFile(File source, File dest)
            throws IOException
    {
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(dest).getChannel();

            long size = in.size();
            MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);

            out.write(buf);

            if (in != null)
                in.close();
            if (out != null)
                out.close();
            return true;
        }
        catch(IOException e){
            try {
                if (in != null)
                    in.close();
            } catch (IOException e2) {}
            try {
                if (out != null)
                    out.close();
            } catch (IOException e2) {}
            throw e;
        }
    }

}