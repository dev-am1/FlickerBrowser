package com.amirmoradi.flickerbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev_am1 on 11/14/2019
 */
class GetFlickrJsonData extends AsyncTask<String ,Void , List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList = null;
    private String mBaseURL;
    private String mLanguage;
    private boolean mMatchAll;

    private final OnDataAvailable mCallBack;
    private boolean runnigOnSameThread = false;
    interface OnDataAvailable {
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    GetFlickrJsonData(OnDataAvailable callBack, String baseURL, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData: called");
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
        mCallBack = callBack;
    }

    void executeOnSameThread(String searchCriteria) {
        Log.d(TAG, "ExecuteOnSameThread: Starts");
        runnigOnSameThread = true;
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "ExecuteOnSameThread ends");

    }


    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");

        if (mCallBack != null){
            mCallBack.onDataAvailable(mPhotoList,DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute ends");


    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: starts");
        String destinationUri = createUri(params[0],mLanguage,mMatchAll);
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");
        return mPhotoList;
    }

    private String createUri(String searchCriteria, String language, boolean matchAll) {
        Log.d(TAG, "createUri starts");

        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("lang", language)
                .appendQueryParameter("tagmode", matchAll ? "All" : "ANY")
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
    }


    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete starts, status : " + status);

        if (status == DownloadStatus.OK) {
            mPhotoList = new ArrayList<>();
            try {

                JSONObject jsonData = new JSONObject(data);
                JSONArray itemsArray = jsonData.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");


                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: " + photoObject.toString());
                }
            } catch (JSONException e) {
                e.getStackTrace();
                Log.e(TAG, "onDownloadComplete: Error Downloading Json Data " + e.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }
        if ((mCallBack != null) && (runnigOnSameThread)) {
            mCallBack.onDataAvailable(mPhotoList, status);
        }
        Log.d(TAG, "onDownloadComplete ends");
    }
}
