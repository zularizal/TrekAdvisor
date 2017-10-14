package com.peterlaurence.trekadvisor.core.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * This {@link Thread} downloads a file from the given url. <br>
 * The parameters are :
 * <p>
 * <ul>
 * <li>The URL of the file to download</li>
 * <li>The name of the file to write</li>
 * <li>The {@link UrlDownloadListener} object</li>
 * </ul>
 * <p>
 * The {@link UrlDownloadListener#onDownloadProgress(int)} is called from this created thread, so
 * synchronization is left to the caller.
 *
 * @author peterLaurence on 14/10/17.
 */
public class DownloadTask extends Thread {
    private String mUrlToDownload;
    private File mOutputFile;
    private UrlDownloadListener mUrlDownloadListener;

    public DownloadTask(String urlToDownload, File outputFile, UrlDownloadListener listener) {
        mUrlToDownload = urlToDownload;
        mOutputFile = outputFile;
        mUrlDownloadListener = listener;
    }

    @Override
    public void run() {
        int percentProgress = 0;
        try {
            URL url = new URL(mUrlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            /* This will be useful so that you can show a typical 0-100% progress bar */
            int fileLength = connection.getContentLength();

            /* Download the file */
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(mOutputFile);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);

                /* Publishing the progress... */
                percentProgress = (int) (total * 100 / fileLength);
                mUrlDownloadListener.onDownloadProgress(percentProgress);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (percentProgress != 100) {
            //TODO : alert user, send an error signal to the receiver.
        }
    }

    public interface UrlDownloadListener {
        void onDownloadProgress(int percent);
    }
}