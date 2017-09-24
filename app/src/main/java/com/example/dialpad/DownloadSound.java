package com.example.dialpad;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadSound extends AppCompatActivity {

    public static final String DOWNLOADED_VOICES = "DownloadedVoices"; // preferences file

    String urlAddress;
    String destinationDir;

    ProgressDialog mProgressDialog;
    DownloadTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_sound);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        urlAddress = intent.getStringExtra("urlAddress");
        destinationDir = intent.getStringExtra("destinationDir");

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl(urlAddress);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                downloadFile(url);
            }
        });

        mProgressDialog = new ProgressDialog(DownloadSound.this);
        mProgressDialog.setMessage("Downloading file...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        downloadTask = new DownloadTask(DownloadSound.this);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }


    private void downloadFile(String url) {
        downloadTask.execute(url);

        // Create DT obj again in order to execute multiple times
        downloadTask = new DownloadTask(DownloadSound.this);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // This will be useful to display download percentage
                // Might be -1: server did not report the length
                int fileLength = connection.getContentLength();


                String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                        + "dialpad" + File.separator + "sounds" + File.separator + "zipFiles";
                File zipDirectory = new File(directoryPath);

                // Create zip directory if it not exists
                if (!zipDirectory.exists()) {
                    zipDirectory.mkdir();
                }

                // Get name of voice file
                String fileName = sUrl[0].substring(sUrl[0].lastIndexOf('/') + 1);
                String filePath = zipDirectory + File.separator + fileName;

                // Download the file
                input = connection.getInputStream();
                output = new FileOutputStream(filePath);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // Allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // Publishing the progress....
                    if (fileLength > 0) // Only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

                unzipFile(filePath, zipDirectory);
                addToPreferences(fileName);

            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Take CPU lock to prevent CPU from going off if the user presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // If we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Couldn't download file", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    // Unzip the file
    private void unzipFile(String filePath, File zipDirectory) {
        ZIP zip = new ZIP();
        zip.decompress(filePath, destinationDir);

        // Clean up zipFolder and delete its content to save memory
        String[] children = zipDirectory.list();
        for (int i = 0; i < children.length; i++)
        {
            new File(zipDirectory, children[i]).delete();
        }
    }

    // Add voice to pref so we could find it in settings
    private void addToPreferences(String name) {
        // Get SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(DOWNLOADED_VOICES, 0);
        SharedPreferences.Editor editor = sharedPref.edit();

        name = name.substring(0,name.length()-4);

        // Path to sound file
        String filePath = destinationDir + name;

        // Put number from text field in shared preferences
        editor.putString(name, filePath);
        editor.commit();
    }

    // Web view client class
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}