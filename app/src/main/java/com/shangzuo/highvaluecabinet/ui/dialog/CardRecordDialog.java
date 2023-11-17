package com.shangzuo.highvaluecabinet.ui.dialog;

import android.view.View;

import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.shangzuo.highvaluecabinet.R;
import com.shangzuo.highvaluecabinet.databinding.DialogCardRecordBinding;

public class CardRecordDialog extends CustomDialog {
    private OnBack onDismiss; // 用于接收参数的变量

    public CardRecordDialog(OnBack listener) {
        super(new OnBindView<>(R.layout.dialog_card_record) {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                DialogCardRecordBinding binding = DialogCardRecordBinding.bind(v);
                binding.btnCancel.setOnClickListener(view -> {
                    listener.onDismiss();
                    dialog.dismiss();
                });
            }
        });
        this.onDismiss = listener; // 在super()之后初始化
    }

    @Override
    public void onShow(CustomDialog dialog) {
        super.onShow(dialog);
        onDismiss.onShow();
    }

    public interface OnBack {
        void onDismiss();

        void onShow();
    }
}