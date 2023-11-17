package com.shangzuo.highvaluecabinet.ui.widget.arcface;

import android.content.Context;
import android.util.Log;


import com.shangzuo.highvaluecabinet.ui.widget.arcface.facedb.dao.FaceDao;
import com.shangzuo.highvaluecabinet.ui.widget.arcface.facedb.entity.FaceEntity;
import com.shangzuo.highvaluecabinet.ui.widget.arcface.faceserver.FaceServer;
import com.shangzuo.highvaluecabinet.ui.widget.arcface.faceserver.RegisterFailedException;

import java.io.File;
import java.util.List;

public class FaceRepository {
    private FaceDao faceDao;
    private static final String TAG = "FaceRepository";
    private FaceServer faceServer;

    public FaceRepository( FaceDao faceDao, FaceServer faceServer) {
        this.faceDao = faceDao;
        this.faceServer = faceServer;
    }

    public List<FaceEntity> getAllFace() {
        List<FaceEntity> faceEntities = faceDao.getAllFaces();
        return faceEntities;
    }



    public int clearAll() {
        // 由于涉及到文件删除操作，所以使用faceServer
        int faceCount = faceServer.clearAllFaces();
        return faceCount;
    }

    public int delete(FaceEntity faceEntity) {
        int index = faceDao.deleteFace(faceEntity);
        boolean delete = new File(faceEntity.getImagePath()).delete();
        if (!delete) {
            Log.w(TAG, "deleteFace: failed to delete headImageFile '" + faceEntity.getImagePath() + "'");
        }
        return index;
    }


    public FaceEntity registerJpeg(Context context, byte[] bytes, String name) throws RegisterFailedException {
        return faceServer.registerJpeg(context, bytes, name);
    }

    public FaceEntity registerBgr24(Context context, byte[] bgr24Data, int width, int height, String name) {
        return faceServer.registerBgr24(context, bgr24Data, width, height, name);
    }

    public int getTotalFaceCount() {
        return faceDao.getFaceCount();
    }
}
