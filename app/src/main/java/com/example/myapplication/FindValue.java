package com.example.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;

import java.util.concurrent.CopyOnWriteArrayList;

public class FindValue {
    private final Activity activity;
    private takingvalues takingvalues;

    private final int readtime = 45;
    private final int ML = 45000;
    private final int bridge = 3500;

    private int numberofvalleys = 0;
    private int verified_ticks = 0;

    private final CopyOnWriteArrayList<Long> valleys = new CopyOnWriteArrayList<>();

    private CountDownTimer count;

    private final Handler mainHandler;

    FindValue(Activity activity, Handler mainHandler) {
        this.activity = activity;
        this.mainHandler = mainHandler;
    }

    private boolean checkturfs() {
        final int valleyWindowSize = 13;
        CopyOnWriteArrayList<taking<Integer>> subList = takingvalues.getLastStdValues(valleyWindowSize);
        if (subList.size() < valleyWindowSize) {
            return false;
        } else {
            Integer referenceValue = subList.get((int) Math.ceil(valleyWindowSize / 2f)).reading;

            for (taking<Integer> measurement : subList) {
                if (measurement.reading < referenceValue) return false;
            }

            return (!subList.get((int) Math.ceil(valleyWindowSize / 2f)).reading.equals(
                    subList.get((int) Math.ceil(valleyWindowSize / 2f) - 1).reading));
        }
    }
    void measurehr(TextureView textureView, Camera cameraService) {
        takingvalues = new takingvalues();

        numberofvalleys = 0;

        count = new CountDownTimer(ML, readtime) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (bridge > (++verified_ticks * readtime)) return;
                sendMessage(MainActivity.MTV, millisUntilFinished / 1000 + "s");


                Thread thread = new Thread(() -> {
                    Bitmap currentBitmap = textureView.getBitmap();
                    int pixelCount = textureView.getWidth() * textureView.getHeight();
                    int reading = 0;
                    int[] pixels = new int[pixelCount];

                    currentBitmap.getPixels(pixels, 0, textureView.getWidth(), 0, 0, textureView.getWidth(), textureView.getHeight());

                    for (int pixelIndex = 0; pixelIndex < pixelCount; pixelIndex++) {
                        reading += (pixels[pixelIndex] >> 16) & 0xff;
                    }
                    takingvalues.add(reading);

                    if (checkturfs()) {
                        numberofvalleys = numberofvalleys + 1;
                        valleys.add(takingvalues.getLastTimestamp().getTime());
                    }
                });
                thread.start();
            }

            @Override
            public void onFinish() {
                String currentValue = String.valueOf(60f * (numberofvalleys - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f)));
                sendMessage(MainActivity.HRF, currentValue);
                cameraService.stop();

            }
        };

        count.start();
    }

    void stop() {
        if (count != null) {
            count.cancel();
        }
    }

    void sendMessage(int what, Object message) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = message;
        mainHandler.sendMessage(msg);
    }
}
