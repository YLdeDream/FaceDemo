package com.shangzuo.highvaluecabinet.ui.activity

import android.os.Bundle
import com.shangzuo.highvaluecabinet.app.FaceApp
import com.shangzuo.highvaluecabinet.app.base.BaseActivity
import com.shangzuo.highvaluecabinet.databinding.ActivityFaceManageBinding
import com.shangzuo.highvaluecabinet.ui.viewmodel.FaceManageViewModel
import com.shangzuo.highvaluecabinet.ui.widget.arcface.facedb.entity.FaceEntity
import java.util.LinkedList

class FaceManageActivity : BaseActivity<FaceManageViewModel, ActivityFaceManageBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
       val  app = FaceApp.instance
        val facePhotoAdapter = FacePhotoAdapter(this, object :
            FacePhotoAdapter.OnItemChangedListener {
            override fun onFaceItemRemoved(position: Int, faceEntity: FaceEntity?) {
                mViewModel.deleteFace(faceEntity)
            }

            override fun onFaceItemUpdated(position: Int, faceEntity: FaceEntity?) {
                mViewModel.updateFace(position, faceEntity!!)            }
        })
        mBind.rvFacePhoto.adapter = facePhotoAdapter

        mViewModel.faceList.observe(this){
            facePhotoAdapter.submitList(LinkedList(it))
            facePhotoAdapter.notifyDataSetChanged()
        }

        mViewModel.getFaceList()

    }

}