package studio.xmatrix.coffee.ui.person;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.databinding.PersonalNameDialogBinding;

class NameDialog extends Dialog {

    private PersonalNameDialogBinding binding;

    NameDialog(PersonActivity activity, String name,PersonHandler.MyInterface myListener) {
        super(activity, R.style.NameDialog);
        binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.personal_name_dialog, null, false);
        setContentView(binding.getRoot());
        binding.setHandler(new NameDialogHandler(activity, binding, name, myListener));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}
