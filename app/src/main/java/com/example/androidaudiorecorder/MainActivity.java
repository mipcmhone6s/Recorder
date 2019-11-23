package com.example.androidaudiorecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Declare Variables
    Button btnRecord, btnStopRecord, btnPlay, btnStop;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    EditText repeatCount;

    final int REQUEST_PERMISSION_CODE = 1000;

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();
    // 현재시간을 date 변수에 저장한다.
    Date date = new Date(now);
    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH_mm_ss");
    // nowDate 변수에 값을 저장한다.
    String formatDate = sdfNow.format(date);

    SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //requset runtime permission
        if (!checkPermissionFromDevice())
            requestPermissions();
        //init VIew
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnRecord = (Button) findViewById(R.id.btnStartRecord);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStopRecord = (Button) findViewById(R.id.btnStopRecord);
        repeatCount = (EditText) findViewById(R.id.repeatCount);

        //create soundpool
        SoundPool tmp = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final SoundPool soundPool = new SoundPool.Builder().setMaxStreams(10).build();
            tmp = soundPool;
        } else {
            final SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
            tmp = soundPool;
        }
        soundPool = tmp;

        //From Android M , you need to request Run-time permission
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionFromDevice()) {


                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "_audio_record.mp3";
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnRecord.setEnabled(false);
                    btnStopRecord.setEnabled(true);
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);

                    Toast.makeText(MainActivity.this, pathSave, Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions();
                }

            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnRecord.setEnabled(true);
                btnStop.setEnabled(true);

                // play as many as repeatCount
                String res = repeatCount.getText().toString();
                int num = Integer.parseInt(res) - 1;
                if(num <= -1)
                    num = 0;
                final int cnt = num;
                //soundpool로 play
                //soundpool사용 load
                final int soundID = soundPool.load(pathSave, 1);
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        soundPool.play(soundID, 1, 1, 0, cnt, 1.0f);
                    }
                });


                Toast.makeText(MainActivity.this, "Playing...", Toast.LENGTH_LONG).show();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStop.setEnabled(true);
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(false);

                // play as many as repeatCount
                String res = repeatCount.getText().toString();
                int num = Integer.parseInt(res) - 1;
                if(num <= -1)
                    num = 0;
                final int cnt = num;
                //soundpool로 play
                //soundpool사용 load
                final int soundID = soundPool.load(pathSave, 1);
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        soundPool.play(soundID, 1, 1, 0, cnt, 1.0f);
                    }
                });


                Toast.makeText(MainActivity.this, "Playing...", Toast.LENGTH_LONG).show();

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnRecord.setEnabled(true);
                btnStop.setEnabled(false);


                //soundpool release
                soundPool.release();
                soundPool=null;
            }
        });

    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    //press Ctrl+O


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

}

