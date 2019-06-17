package studio.xmatrix.coffee.ui.detail;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.databinding.CommentFragmentBinding;

import java.util.Objects;

@SuppressLint("ValidFragment")
public class CommentFragment extends BottomSheetDialogFragment {
    CommentFragmentBinding binding;
    AfterBinding inter;

    public interface AfterBinding {
        void getBinding(CommentFragmentBinding binding);
    }

    @SuppressLint("ValidFragment")
    public CommentFragment(AfterBinding inter) {
        this.inter = inter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.comment_fragment, null);
        dialog.setContentView(view);
        binding = DataBindingUtil.bind(view);
        inter.getBinding(binding);

        return dialog;
    }
}
