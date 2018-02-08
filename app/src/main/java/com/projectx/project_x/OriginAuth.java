package com.projectx.project_x;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;


class BarcodeTracker extends Tracker<Barcode> implements MultiProcessor.Factory<Barcode> {
       private static final String TAG = "DETECTED!";
    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode barcode) {
//       if(barcode.rawValue!=OriginAuth.last_val) {
           OriginAuth.count=OriginAuth.count+1;

           OriginAuth.txtResult.setText(String.valueOf(OriginAuth.count)+"   "+String.valueOf(barcode.rawValue));
        if(OriginAuth.count==2) {
            Toast.makeText(this,getString(R.string.HERE),Toast.LENGTH_LONG).show();
        }
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                OriginAuth.cameraSource.release();
//            }
//        });
//
//           OriginAuth.last_val=barcode.rawValue;
//       }
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        return new BarcodeTracker();
    }

}


public class OriginAuth extends AppCompatActivity implements BarcodeGraphicTracker.BarcodeUpdateListener{

    public static final String BarcodeObject = "Barcode";
    private static final String TAG = "BarcodeReader";
    public static String last_val = "Never";
    public static int count = 0;

    SurfaceView cameraPreview;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtResult;
    public static TextView txtResult2;
    public static BarcodeDetector barcodeDetector;
    public static CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    ImageView tick;
    ProgressBar progressBar;
    SparseArray<Barcode> qrcodes;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private GraphicOverlay<BarcodeGraphic> GraphicOverlay;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin_auth);

        progressBar = findViewById(R.id.progress);
        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtResult2 = (TextView) findViewById(R.id.txtResult2);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void  createCameraSource() {

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true).setFacing(CAMERA_FACING_BACK).build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(OriginAuth.this,
                                new String[]{android.Manifest.permission.CAMERA}, RequestCameraPermissionID);
                        return;
                    }
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        BarcodeTracker barcodes = new BarcodeTracker();
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodes).build()
        );

        if (!barcodeDetector.isOperational()) {

            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestCameraPermission() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                cameraSource.start();
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private boolean onTap(float rawX, float rawY) {
        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        GraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / GraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / GraphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : GraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);  // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestDistance = distance;
            }
        }

        if (best != null) {
            Intent data = new Intent();
            data.putExtra(BarcodeObject, best);
            setResult(CommonStatusCodes.SUCCESS, data);
            finish();
            return true;
        }
        return false;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {

//        Intent data = new Intent();
//       // data.putExtra(BarcodeObject, best);
//        data.putExtra(BarcodeObject,barcode.rawValue);
//        setResult(CommonStatusCodes.SUCCESS, data);
//        finish();
//        return true;
//        Intent intent = new Intent(getApplicationContext(),Detected_I.class);
//        startActivity(intent);
//        txtResult.setText(barcode.displayValue);

    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        startCameraSource();
//    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//        if (cameraPreview != null) {
//            cameraSource.stop();
//        }
//    }
//
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraPreview!= null) {
            cameraSource.release();
        }
    }

}


































//    @Override
//    protected void onStart() {
//        super.onStart();



//        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//            @Override
//            public void release() {
//            }
//
//            @Override
//            public void receiveDetections(Detector.Detections<Barcode> detections) {
//                qrcodes = detections.getDetectedItems();
//                if(qrcodes.size()!= 0) {
//                   codeDetected();
//                }
//            }
//        });
//
//    }


//    private void onTouchEvent(){}


//    public void onBarcodeDetected(Barcode barcode) {
//        //do something with barcode data returned
//    }

//    private void codeDetected() {
//            txtResult.setText(qrcodes.valueAt(0).displayValue);
//            cameraSource.release();
//            barcodeDetector.release();
//        Intent intent = new Intent(getApplicationContext(),Detected_I.class);
//          startActivity(intent);
//        }