package studio.xmatrix.coffee.ui.person;

import android.widget.Toast;
import studio.xmatrix.coffee.databinding.PersonalNameDialogBinding;

public class NameDialogHandler {

    private PersonActivity activity;
    private PersonalNameDialogBinding binding;
    private String name;
    private PersonHandler.MyInterface myListener;

    NameDialogHandler(PersonActivity activity, PersonalNameDialogBinding binding, String name, PersonHandler.MyInterface myListener){
        this.activity = activity;
        this.binding = binding;
        this.name = name;
        this.myListener = myListener;

        initView();
    }

    private void initView() {
        binding.name.setText(name);
        binding.button.setOnClickListener(v -> save());
    }

    private void save() {
        String saveName = binding.name.getText().toString();
        if (saveName.equals(name)){
            Toast.makeText(activity, "请修改昵称", Toast.LENGTH_SHORT).show();
        } else {
            myListener.changName(saveName);
        }
    }
}
