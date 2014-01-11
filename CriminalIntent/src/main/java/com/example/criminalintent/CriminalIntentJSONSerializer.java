package com.example.criminalintent;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class CriminalIntentJSONSerializer {

    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(Context context, String fileName) {
        mContext = context;
        mFilename = fileName;
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try {
            // open and read the file into a stringbuilder
            InputStream inputStream = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // parse the json using jsontokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();
            // build the array of crimes from jsonobjects
            for (int i = 0; i < array.length(); i++)
                crimes.add(new Crime(array.getJSONObject(i)));
        } catch (FileNotFoundException e) {
            // ignore this; it happens when starting fresh
        } finally {
            if (reader != null)
                reader.close();
        }

        return crimes;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {

        //Build an array in json
        JSONArray array = new JSONArray();

        for (Crime crime : crimes)
            array.put(crime.toJSON());

        // write the file to disk
        OutputStreamWriter writer = null;
        try {
            OutputStream outputStream = mContext
                    .openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
