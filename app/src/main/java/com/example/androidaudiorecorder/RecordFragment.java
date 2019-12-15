package com.example.androidaudiorecorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    //Declare Variables
    Button btnRecord1, btnStopRecord1, btnPlay1, btnStop1, btnRecord2, btnStopRecord2, btnPlay2, btnStop2, btnPlayAll;
    private String pathSave1 = "";
    private String pathSave2 = "";
    private String fileName1 = "";
    private String fileName2 = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    private DBHelper database;
    final int REQUEST_PERMISSION_CODE = 1000;
    final int RECORDER1 = 1;
    final int RECORDER2 = 2;

    private static final String LOG_TAG = "RecordFragment";

    //반복횟수
    int Repnum = 0;
    int cnt = 0;

    //toast용
    private Context context;

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();
    // 현재시간을 date 변수에 저장한다.
    Date date = new Date(now);
    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH_mm_ss");
    // nowDate 변수에 값을 저장한다.
    String formatDate = sdfNow.format(date);

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DBHelper(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);
        //requset runtime permission
        if (!checkPermissionFromDevice())
            requestPermissions();
        //init VIew
        btnPlay1 = (Button) recordView.findViewById(R.id.btnPlay1);
        btnRecord1 = (Button) recordView.findViewById(R.id.btnStartRecord1);
        btnStop1 = (Button) recordView.findViewById(R.id.btnStop1);
        btnStopRecord1 = (Button) recordView.findViewById(R.id.btnStopRecord1);
        btnPlay2 = (Button) recordView.findViewById(R.id.btnPlay2);
        btnRecord2 = (Button) recordView.findViewById(R.id.btnStartRecord2);
        btnStop2 = (Button) recordView.findViewById(R.id.btnStop2);
        btnStopRecord2 = (Button)recordView.findViewById(R.id.btnStopRecord2);
        btnPlayAll = (Button) recordView.findViewById(R.id.btnPlayAll);

        //폴더만들기
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "AndroidRecorder");
        // 폴더가 제대로 만들어졌는지 체크 ======
        if (!dir.mkdirs()) {
            Log.e("FILE", "Directory not created");
        } else {
            Toast.makeText(context, "폴더 생성 SUCCESS", Toast.LENGTH_SHORT).show();
        }


        //From Android M , you need to request Run-time permission
        btnRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionFromDevice()) {
                    //경로설정
                    pathSave1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AndroidRecorder/" + "KEY_" + (database.getCount() + 1) + ".mp3";
                    fileName1 = "KEY_" + (database.getCount() + 1) + ".mp3";
                    setupMediaRecorder(RECORDER1);
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnRecord1.setEnabled(false);
                    btnStopRecord1.setEnabled(true);
                    btnPlay1.setEnabled(false);
                    btnStop1.setEnabled(false);

                    Toast.makeText(context, pathSave1, Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions();
                }

            }
        });

        btnStopRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStopRecord1.setEnabled(false);
                btnPlay1.setEnabled(true);
                btnRecord1.setEnabled(true);
                btnStop1.setEnabled(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    database.addRecording(fileName1, pathSave1, 0);

                } catch (Exception e){
                    Log.e(LOG_TAG, "exception", e);
                }
                mediaRecorder.stop();
            }
        });

        btnPlay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStop1.setEnabled(true);
                btnStopRecord1.setEnabled(false);
                btnRecord1.setEnabled(false);
                // play as many as repeatCount
                String res = "0";

                int num = Integer.parseInt(res) - 1;
                if (num <= -1)
                    num = 0;
                Repnum = num;
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave1);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();

                Toast.makeText(context, "전체 길이 : " + mediaPlayer.getDuration() + " 현재 : " + mediaPlayer.getCurrentPosition(), Toast.LENGTH_LONG).show();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    }
                });
            }
        });

        btnStop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord1.setEnabled(false);
                btnPlay1.setEnabled(true);
                btnRecord1.setEnabled(true);
                btnStop1.setEnabled(false);
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    setupMediaRecorder(RECORDER1);
                }
            }
        });
        /////////////////////////////////////**PRACTICE AREA**///////////////////////////////////////////////////////////////////////////////////

        btnRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionFromDevice()) {
                    //경로설정
                    pathSave2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AndroidRecorder/" + "PRACTICE_" + (database.getCount() + 1) + ".mp3";
                    fileName2 = "PRACTICE_" + (database.getCount() + 1) + ".mp3";
                    setupMediaRecorder(RECORDER2);
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnRecord2.setEnabled(false);
                    btnStopRecord2.setEnabled(true);
                    btnPlay2.setEnabled(false);
                    btnStop2.setEnabled(false);

                    Toast.makeText(context, pathSave2, Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions();
                }
            }
        });

        btnStopRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStopRecord2.setEnabled(false);
                btnPlay2.setEnabled(true);
                btnRecord2.setEnabled(true);
                btnStop2.setEnabled(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    database.addRecording(fileName2, pathSave2, 0);

                } catch (Exception e){
                    Log.e(LOG_TAG, "exception", e);
                }
                mediaRecorder.stop();
            }
        });

        btnPlay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStop2.setEnabled(true);
                btnStopRecord2.setEnabled(false);
                btnRecord2.setEnabled(false);
                // play as many as repeatCount
                String res = "0";

                int num = Integer.parseInt(res) - 1;
                if (num <= -1)
                    num = 0;
                Repnum = num;
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave2);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();

                Toast.makeText(context, "전체 길이 : " + mediaPlayer.getDuration() + " 현재 : " + mediaPlayer.getCurrentPosition(), Toast.LENGTH_LONG).show();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    }
                });
            }
        });

        btnStop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord2.setEnabled(false);
                btnPlay2.setEnabled(true);
                btnRecord2.setEnabled(true);
                btnStop2.setEnabled(false);
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    setupMediaRecorder(RECORDER2);
                }
            }
        });

        btnPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave1);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();

                Toast.makeText(context, "전체 길이 : " + mediaPlayer.getDuration() + " 현재 : " + mediaPlayer.getCurrentPosition(), Toast.LENGTH_LONG).show();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(pathSave2);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (mediaPlayer != null) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                }
                            }
                        });
                    }
                });

            }
        });

        return recordView;
    }
    private void setupMediaRecorder(int whichRECORDER) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        if (whichRECORDER == RECORDER1)
            mediaRecorder.setOutputFile(pathSave1);
        else
            mediaRecorder.setOutputFile(pathSave2);
    }

    private void requestPermissions() {
        requestPermissions(new String[]{
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
                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
