package com.shangzuo.highvaluecabinet.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.blankj.utilcode.util.ToastUtils
import com.shangzuo.highvaluecabinet.app.FaceApp
import com.shangzuo.highvaluecabinet.ui.widget.arcface.FaceRepository
import com.shangzuo.highvaluecabinet.ui.widget.arcface.facedb.FaceDatabase
import com.shangzuo.highvaluecabinet.ui.widget.arcface.facedb.entity.FaceEntity
import com.shangzuo.highvaluecabinet.ui.widget.arcface.faceserver.FaceServer
import com.shangzuo.mvvm.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FaceManageViewModel : BaseViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    val faceList = MutableLiveData<List<FaceEntity>>()
    private val faceDao = FaceDatabase.getInstance(FaceApp.instance).faceDao()
    private var instance = FaceServer.getInstance()
    private val faceRepository = FaceRepository(faceDao, instance)

    fun getFaceList() {
        viewModelScope.launch(Dispatchers.IO) {
            // 在后台线程执行数据库操作
            val list = faceDao.allFaces
            faceList.postValue(list)
        }
    }

    fun clearAllFace() {
        viewModelScope.launch {
            val count=faceRepository.clearAll()
            if (count==0){
                ToastUtils.showShort("清空成功")
            }else{
                ToastUtils.showShort("清空失败")
            }
        }
    }

     fun activeFaceEngine() {
        val result = FaceEngine.activeOnline(
            FaceApp.instance,
            "U5V1-11LL-2138-ZRRR",
            "6QGNd4rsgtoV4id2ToSpv95XTjrdvx9cVQBHkb2UWz2R",
            "Ce16RkgTbFBPTNLqEyLLmQWV7LV162e5MFn3aY9Nn2ZS"
        )
        val info = when (result) {
            ErrorInfo.MOK -> "激活引擎成功"
            ErrorInfo.MERR_ASF_ALREADY_ACTIVATED -> "已激活"
            ErrorInfo.MERR_ASF_ACTIVEKEY_ACTIVEKEY_ACTIVATED -> "该激活码已被其他设备使用"
            else -> "错误码：$result"
        }
        ToastUtils.showLong(info)
    }

    fun deleteFace(faceEntity: FaceEntity?) {
        viewModelScope.launch {
            if (faceList.value != null) {
                val currentList = faceList.value?.toMutableList() ?: mutableListOf()
                currentList.removeIf { it.faceId == faceEntity?.faceId }
                faceList.postValue(currentList)
            }
            FaceServer.getInstance().removeOneFace(faceEntity)
            faceRepository.delete(faceEntity)
            faceList.postValue(faceList.value)
        }
    }

    fun updateFace(position: Int, faceEntity: FaceEntity) {
        viewModelScope.launch {
            faceDao.updateFaceEntity(faceEntity)
            val faceEntityList: MutableList<FaceEntity>? = faceList.value?.toMutableList()
            if (faceEntityList != null) {
                faceEntityList[position] = faceEntity
                faceList.postValue(faceEntityList)
            }
        }
    }
}