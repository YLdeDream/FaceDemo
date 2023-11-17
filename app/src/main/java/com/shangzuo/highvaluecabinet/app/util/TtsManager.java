package com.shangzuo.highvaluecabinet.app.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;



import java.util.Locale;

public class TtsManager {
    private TtsManager() {
    }

    private static class SingletonHoler {
        public static final TtsManager INSTANCE = new TtsManager();
    }

    public static TtsManager getInstance() {
        return SingletonHoler.INSTANCE;
    }

    private TextToSpeech mSpeech;
    private boolean mIsInited;
    private UtteranceProgressListener mSpeedListener;

    public void init(Context context) {
        destory();
        mSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Log.e("MainActivity", "init: "+status );
                int result = mSpeech.setLanguage(Locale.getDefault());
                 mSpeech.setPitch(1.0f); // 设置音调
                 mSpeech.setSpeechRate(1.0f); // 设置语速
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    //语音合成初始化失败,不支持语种
                    mIsInited = false;
                } else {
                    mIsInited = true;
                }
                mSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        //======语音合成 Start
                        if (mSpeedListener != null)
                            mSpeedListener.onStart(utteranceId);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        //======语音合成 Done
                        if (mSpeedListener != null)
                            mSpeedListener.onDone(utteranceId);
                    }

                    @Override
                    public void onError(String utteranceId) {
                        //======语音合成 Error
                        if (mSpeedListener != null)
                            mSpeedListener.onError(utteranceId);
                    }
                });
            }
        });
    }

    public void setSpeechListener(UtteranceProgressListener listener) {
        this.mSpeedListener = listener;
    }

    public boolean speakText(String text,Context context) {
        if (!mIsInited) {
            //语音合成失败，未初始化成功
            init(context);
            Log.e("MainActivity", "speakText: 未初始化成功" );
            return false;
        }
        if (mSpeech != null) {
            int result = mSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, "");
            Log.e("MainActivity", "result: ==="+result );
            return result == TextToSpeech.SUCCESS;
        }
        return false;
    }

    public void stop() {
        if (mSpeech != null && mSpeech.isSpeaking()) {
            mSpeech.stop();
        }
    }

    public boolean isSpeaking() {
        if (mSpeech == null)
            return false;
        return mSpeech.isSpeaking();
    }


    public void destory() {
        if (mSpeech != null) {
            mSpeech.stop();
            mSpeech.shutdown();
        }
    }
}
