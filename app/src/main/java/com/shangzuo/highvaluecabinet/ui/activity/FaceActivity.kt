package com.shangzuo.highvaluecabinet.ui.activity


import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Point
import android.hardware.Camera
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import android.widget.TextView
import com.arcsoft.face.ErrorInfo
import com.blankj.utilcode.util.ToastUtils
import com.kongzue.dialogx.dialogs.PopTip
import com.shangzuo.highvaluecabinet.R
import com.shangzuo.highvaluecabinet.app.base.BaseActivity
import com.shangzuo.highvaluecabinet.app.base.UserInfo
import com.shangzuo.highvaluecabinet.databinding.ActivityFaceBinding
import com.shangzuo.highvaluecabinet.ui.viewmodel.RecognizeViewModel
import com.shangzuo.highvaluecabinet.ui.widget.arcface.ConfigUtil
import com.shangzuo.highvaluecabinet.ui.widget.arcface.ErrorCodeUtil
import com.shangzuo.highvaluecabinet.ui.widget.arcface.FaceRectTransformer
import com.shangzuo.highvaluecabinet.ui.widget.arcface.FaceRectView
import com.shangzuo.highvaluecabinet.ui.widget.arcface.PreviewConfig
import com.shangzuo.highvaluecabinet.ui.widget.arcface.RecognizeAreaView
import com.shangzuo.highvaluecabinet.ui.widget.arcface.camera.CameraListener
import com.shangzuo.highvaluecabinet.ui.widget.arcface.camera.DualCameraHelper
import com.shangzuo.highvaluecabinet.ui.widget.arcface.face.constants.LivenessType
import com.shangzuo.highvaluecabinet.ui.widget.arcface.face.model.FacePreviewInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FaceActivity : BaseActivity<RecognizeViewModel, ActivityFaceBinding>(),
    OnGlobalLayoutListener {
    private var rgbCameraHelper: DualCameraHelper? = null
    private var rgbFaceRectTransformer: FaceRectTransformer? = null
    private var textViewRgb: TextView? = null
    private var recognizeAreaView: RecognizeAreaView? = null
    private val openRectInfoDraw = false
    private var isStart = false
    var userInfo: UserInfo? = null
    override fun initView(savedInstanceState: Bundle?) {

        // Activity启动后就锁定为启动时的方向
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        userInfo = intent.getParcelableExtra<UserInfo>("user")
        mViewModel.setUserInfo(userInfo)
        if (userInfo!=null){
            mBind.btRegister.visibility=View.VISIBLE
        }else{
            mBind.btRegister.visibility=View.GONE
        }
        mViewModel.setLiveType(LivenessType.RGB)
        initViewModel()
        //在布局结束后才做初始化操作
        mBind.dualCameraTexturePreviewRgb.viewTreeObserver.addOnGlobalLayoutListener(this)
        mBind.compareResultList = mViewModel.compareResultList.value
        mViewModel.setDrawRectInfoTextValue(true)

        //mViewModel.prepareRegister()

    }

    override fun onStart() {
        super.onStart()
        Log.e("initView", "onStart: ")

    }

    private fun initViewModel() {

        mViewModel.ftInitCode.observe(this) { ftInitCode ->
            if (ftInitCode !== ErrorInfo.MOK) {
                val error = getString(
                    R.string.specific_engine_init_failed,
                    "ftEngine",
                    ftInitCode,
                    ErrorCodeUtil.arcFaceErrorCodeToFieldName(ftInitCode)
                )
                Log.i("FaceActivity", "initEngine: $error")
                ToastUtils.showShort(error)
            }
        }
        mViewModel.frInitCode.observe(this) { frInitCode ->
            if (frInitCode !== ErrorInfo.MOK) {
                val error = getString(
                    R.string.specific_engine_init_failed, "frEngine",
                    frInitCode, ErrorCodeUtil.arcFaceErrorCodeToFieldName(frInitCode)
                )
                Log.i("FaceActivity", "initEngine: $error")
                ToastUtils.showShort(error)
            }
        }
        mViewModel.flInitCode.observe(this) { flInitCode ->
            if (flInitCode !== ErrorInfo.MOK) {
                val error = getString(
                    R.string.specific_engine_init_failed, "flEngine",
                    flInitCode, ErrorCodeUtil.arcFaceErrorCodeToFieldName(flInitCode)
                )
                Log.i("FaceActivity", "initEngine: $error")
                ToastUtils.showShort(error)
            }
        }
        mViewModel.faceItemEventMutableLiveData.observe(this) { faceItemEvent ->
            val adapter = mBind.dualCameraRecyclerViewPerson.getAdapter()
            when (faceItemEvent.getEventType()) {
                RecognizeViewModel.EventType.REMOVED -> adapter?.notifyItemRemoved(faceItemEvent.index)

                RecognizeViewModel.EventType.INSERTED -> adapter?.notifyItemInserted(faceItemEvent.index)

                else -> {}
            }
        }
        mViewModel.recognizeConfiguration.observe(this) { recognizeConfiguration ->
            Log.i("FaceActivity", " recognizeConfiguration:====== $recognizeConfiguration")
        }
        mViewModel.setOnRegisterFinishedCallback { facePreviewInfo, success ->
            PopTip.show(if (success) "注册成功" else "注册失败，请重新注册")
            if (success){
                val resultIntent = Intent()
                resultIntent.putExtra("faceId", facePreviewInfo.faceInfoRgb.faceId)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
        mViewModel.recognizeNotice.observe(this) { notice ->
            //ToastUtils.showShort(notice)
            Log.e("FaceActivity", "notice=====: $notice")
        }

        mViewModel.recognizeTrackId.observe(this) {
            Log.e("FaceActivity", "认证通过，faceID=====: $it")
            if (userInfo == null)
                runOnUiThread {
                    if (!isStart) {
//                        startActivity(Intent(this@FaceActivity, ChooseCabinetActivity::class.java))
//                        finish()
                    }
                    isStart = true
                }
        }
    }



    fun finish(view: View?) {
        finish()
    }

    /**
     * 将准备注册的状态置为待注册
     *
     * @param view 注册按钮
     */
    fun register(view: View?) {
        mViewModel.prepareRegister()
    }


    override fun onGlobalLayout() {
        Log.e("initView", "onGlobalLayout: ")

        mBind.dualCameraTexturePreviewRgb.viewTreeObserver.removeOnGlobalLayoutListener(this)
        GlobalScope.launch {
            mViewModel.init()
            initRgbCamera()
        }
    }

    private fun initRgbCamera() {
        val cameraListener: CameraListener = object : CameraListener {
            override fun onCameraOpened(
                camera: Camera,
                cameraId: Int,
                displayOrientation: Int,
                isMirror: Boolean
            ) {

                runOnUiThread {
                    val previewSizeRgb =
                        camera.parameters.previewSize
                    val layoutParams: ViewGroup.LayoutParams? = adjustPreviewViewSize(
                        mBind.dualCameraTexturePreviewRgb,
                        mBind.dualCameraTexturePreviewRgb, mBind.dualCameraFaceRectView,
                        previewSizeRgb, displayOrientation, 3.0f
                    )
                    if (layoutParams != null) {
                        rgbFaceRectTransformer = FaceRectTransformer(
                            previewSizeRgb.width,
                            previewSizeRgb.height,
                            layoutParams.width,
                            layoutParams.height,
                            displayOrientation,
                            cameraId,
                            isMirror,
                            ConfigUtil.isDrawRgbRectHorizontalMirror(this@FaceActivity),
                            ConfigUtil.isDrawRgbRectVerticalMirror(this@FaceActivity)
                        )
                    }
                    val parentView = mBind.dualCameraTexturePreviewRgb.getParent() as FrameLayout
                    if (textViewRgb == null) {
                        textViewRgb = TextView(this@FaceActivity, null)
                    } else {
                        parentView.removeView(textViewRgb)
                    }
                    textViewRgb!!.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    textViewRgb!!.text = getString(
                        R.string.camera_rgb_preview_size,
                        previewSizeRgb.width,
                        previewSizeRgb.height
                    )
                    textViewRgb!!.setTextColor(Color.WHITE)
                    textViewRgb!!.setBackgroundColor(resources.getColor(R.color.color_bg_notification))
                    parentView.addView(textViewRgb)
                    // 父View宽度和子View一致，保持居中
                    val parentLayoutParams = parentView.layoutParams
                    parentLayoutParams.width = layoutParams!!.width
                    parentView.layoutParams = parentLayoutParams

                    // 添加recognizeAreaView，在识别区域发生变更时，更新数据给FaceHelper
                    if (ConfigUtil.isRecognizeAreaLimited(this@FaceActivity)) {
                        if (recognizeAreaView == null) {
                            recognizeAreaView = RecognizeAreaView(this@FaceActivity)
                            recognizeAreaView!!.layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        } else {
                            parentView.removeView(recognizeAreaView)
                        }
                        recognizeAreaView!!.setOnRecognizeAreaChangedListener { recognizeArea ->
                            mViewModel.setRecognizeArea(
                                recognizeArea
                            )
                        }
                        parentView.addView(recognizeAreaView)
                    }
                    mViewModel.onRgbCameraOpened(camera)
                    mViewModel.setRgbFaceRectTransformer(rgbFaceRectTransformer)
                }
            }

            override fun onPreview(nv21: ByteArray?, camera: Camera?) {
                mBind.dualCameraFaceRectView.clearFaceInfo()
                if (nv21 != null) {
                    val facePreviewInfoList: List<FacePreviewInfo> =
                        mViewModel.onPreviewFrame(nv21, true)
                    if (facePreviewInfoList != null && rgbFaceRectTransformer != null) {
                        drawPreviewInfo(facePreviewInfoList)
                    }
                    mViewModel.clearLeftFace(facePreviewInfoList)
                }
            }

            override fun onCameraClosed() {
                Log.i("FaceActivity", "onCameraClosed: ")
            }

            override fun onCameraError(e: Exception) {
                Log.i("FaceActivity", "onCameraError: " + e.message)
                e.printStackTrace()
            }

            override fun onCameraConfigurationChanged(cameraID: Int, displayOrientation: Int) {
                Log.i("FaceActivity", "onCameraConfigurationChanged:" + Thread.currentThread().name)
                if (rgbFaceRectTransformer != null) {
                    rgbFaceRectTransformer!!.setCameraDisplayOrientation(displayOrientation)
                }
                Log.i(
                    "FaceActivity",
                    "onCameraConfigurationChanged: $cameraID  $displayOrientation"
                )
            }
        }
        val previewConfig: PreviewConfig = mViewModel.getPreviewConfig()
        rgbCameraHelper = DualCameraHelper.Builder()
            .previewViewSize(
                Point(
                    mBind.dualCameraTexturePreviewRgb.getMeasuredWidth(),
                    mBind.dualCameraTexturePreviewRgb.getMeasuredHeight()
                )
            )
            .rotation(windowManager.defaultDisplay.rotation)
            .additionalRotation(previewConfig.getRgbAdditionalDisplayOrientation())
            .previewSize(mViewModel.loadPreviewSize())
            .specificCameraId(previewConfig.getRgbCameraId())
            .isMirror(ConfigUtil.isDrawRgbPreviewHorizontalMirror(this))
            .previewOn(mBind.dualCameraTexturePreviewRgb)
            .cameraListener(cameraListener)
            .build()
        rgbCameraHelper!!.init()
        rgbCameraHelper!!.start()
    }

    /**
     * 调整View的宽高，使2个预览同时显示
     *
     * @param previewView        显示预览数据的view
     * @param faceRectView       画框的view
     * @param previewSize        预览大小
     * @param displayOrientation 相机旋转角度
     * @return 调整后的LayoutParams
     */
    private fun adjustPreviewViewSize(
        rgbPreview: View,
        previewView: View,
        faceRectView: FaceRectView,
        previewSize: Camera.Size,
        displayOrientation: Int,
        scale: Float
    ): ViewGroup.LayoutParams? {
        val layoutParams = previewView.layoutParams
        val measuredWidth = previewView.measuredWidth
        val measuredHeight = previewView.measuredHeight
        var ratio = previewSize.height.toFloat() / previewSize.width.toFloat()
        if (ratio > 1) {
            ratio = 1 / ratio
        }
        if (displayOrientation % 180 == 0) {
            layoutParams.width = rgbPreview.width  // 将宽度设置为与rgbPreview相匹配
            layoutParams.height = (rgbPreview.width * ratio).toInt()  // 根据纵横比调整高度
        } else {
            layoutParams.height = rgbPreview.width  // 将高度设置为与rgbPreview相匹配
            layoutParams.width = (rgbPreview.width * ratio).toInt()  // 根据纵横比调整宽度
        }
        if (scale < 1f) {
            // 根据比例调整宽度和高度
            layoutParams.width = (layoutParams.width * scale).toInt()
            layoutParams.height = (layoutParams.height * scale).toInt()
        } else {
            layoutParams.width *= scale.toInt()
            layoutParams.height *= scale.toInt()
        }
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        if (layoutParams.width >= metrics.widthPixels) {
            val viewRatio = layoutParams.width / metrics.widthPixels.toFloat()
            layoutParams.width /= viewRatio.toInt()
            layoutParams.height /= viewRatio.toInt()
        }
        if (layoutParams.height >= metrics.heightPixels) {
            val viewRatio = layoutParams.height / metrics.heightPixels.toFloat()
            layoutParams.width /= viewRatio.toInt()
            layoutParams.height /= viewRatio.toInt()
        }
        previewView.layoutParams = layoutParams
        faceRectView.layoutParams = layoutParams  // 同时更新faceRectView的布局参数
        return layoutParams
    }

    private fun drawPreviewInfo(facePreviewInfoList: List<FacePreviewInfo>) {
        if (rgbFaceRectTransformer != null) {
            val rgbDrawInfoList: List<FaceRectView.DrawInfo> = mViewModel.getDrawInfo(
                facePreviewInfoList,
                LivenessType.RGB,
                openRectInfoDraw
            )
            mBind.dualCameraFaceRectView.drawRealtimeFaceInfo(rgbDrawInfoList)
        }
    }

    override fun onDestroy() {

        if (rgbCameraHelper != null) {
            rgbCameraHelper!!.release()
            rgbCameraHelper = null
        }
        mViewModel.destroy()
        super.onDestroy()
    }
}