package studio.xmatrix.coffee.ui;

import android.view.View;
import studio.xmatrix.coffee.databinding.ListStatusBinding;

public class ListStatus {

    public enum StatusType {
        Loading,
        Done,
        Nothing,
        Error,
        NotLogin
    }

    public static void setStatus(ListStatusBinding binding, StatusType status) {
        binding.statusNothing.setVisibility(status == StatusType.Nothing ?  View.VISIBLE : View.GONE);
        binding.statusLoading.setVisibility(status == StatusType.Loading ?  View.VISIBLE : View.GONE);
        binding.statusError.setVisibility(status == StatusType.Error ?  View.VISIBLE : View.GONE);
        binding.statusLogin.setVisibility(status == StatusType.NotLogin ?  View.VISIBLE : View.GONE);

        binding.statusNothingText.setVisibility(status == StatusType.Nothing ?  View.VISIBLE : View.GONE);
        binding.statusLoadingText.setVisibility(status == StatusType.Loading ?  View.VISIBLE : View.GONE);
        binding.statusErrorText.setVisibility(status == StatusType.Error ?  View.VISIBLE : View.GONE);
        binding.statusLoginText.setVisibility(status == StatusType.NotLogin ?  View.VISIBLE : View.GONE);
    }
}