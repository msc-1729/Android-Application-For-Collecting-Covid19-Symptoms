package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Collections;
import java.util.Objects;

public class Camera {
    private String cameraId;
    private final Activity activity;
    private CameraDevice cam_hehe;
    private CameraCaptureSession preview;


    private CaptureRequest.Builder hehe;

    Camera(Activity _activity) {
        activity = _activity;
    }

    void start(Surface previewSurface) {

        CameraManager camManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = Objects.requireNonNull(camManager).getCameraIdList()[0];
        } catch (CameraAccessException | NullPointerException e) {
            Log.println(Log.ERROR, "camera", "Access not granted for camera...");
        }
        try {

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.println(Log.ERROR, "camera", "permission denied to take photos");
            }
            Objects.requireNonNull(camManager).openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cam_hehe = camera;

                    CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            preview = session;
                            try {

                                hehe = cam_hehe.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                hehe.addTarget(previewSurface);
                                hehe.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);

                                HandlerThread thread = new HandlerThread("CameraPreview");
                                thread.start();

                                preview.setRepeatingRequest(hehe.build(), null, null);

                            } catch (CameraAccessException e) {
                                if (e.getMessage() != null) {
                                    Log.println(Log.ERROR, "camera", e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Log.println(Log.ERROR, "camera", "configuration failed");
                        }
                    };

                    try {
                        camera.createCaptureSession(Collections.singletonList(previewSurface), stateCallback, null); //1
                    } catch (CameraAccessException e) {
                        if (e.getMessage() != null) {
                            Log.println(Log.ERROR, "camera", e.getMessage());
                        }
                    }
                }
                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                }
            }, null);
        } catch (CameraAccessException | SecurityException e) {
            if (e.getMessage() != null) {
                Log.println(Log.ERROR, "camera", e.getMessage());
            }
        }
    }

    void stop() {
        try {
            cam_hehe.close();
        } catch (Exception e) {
            Log.println(Log.ERROR, "camera", "not able to close camera device" + e.getMessage());
        }
    }
}
