package com.example.criminalintent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CrimeCameraFragment extends Fragment {

    public static final String EXTRA_PHOTO_FILENAME =
            "com.example.criminalintent.photo_filename";

    private static final String TAG = "CrimeCameraFragment";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressBarContainer;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // display the progress indicator
            mProgressBarContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // create a filename
            String filename = UUID.randomUUID().toString() + ".jpg";
            // save the jpeg data to dis
            FileOutputStream outputStream = null;
            boolean success = true;

            try {
                outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(data);
            } catch (Exception e) {
                Log.e(TAG, "Error writing to file " + filename, e);
                success = false;
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing file " + filename, e);
                    success = false;
                }
            }

            if (success) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, intent);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }

            getActivity().finish();
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater. inflate(R.layout.fragment_crime_camera, parent, false);

        mProgressBarContainer = view.findViewById(R.id.crime_camera_progressContainer);
        mProgressBarContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button)view.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mCamera != null)
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
            }
        });

        mSurfaceView = (SurfaceView)view.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                // Tell the camera to use this surface as its preview area.
                try {
                    if (mCamera != null)
                        mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview display", e);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // we can no longer display on this surface, so stop the preview
                if (mCamera != null)
                    mCamera.stopPreview();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if (mCamera == null) return;

                // The surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size size = getBestSupportedSize(parameters
                        .getSupportedPreviewSizes(), w, h);
                parameters.setPreviewSize(size.width, size.height);
                size = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
                parameters.setPictureSize(size.width, size.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCamera = Camera.open(0);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    // Returns the largest size available for the surface to use.
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;

        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }

        return bestSize;
    }
}
