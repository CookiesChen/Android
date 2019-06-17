package studio.xmatrix.coffee.ui.person;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import studio.xmatrix.coffee.R;
import studio.xmatrix.coffee.ui.BaseActionBarActivity;
import studio.xmatrix.coffee.databinding.PersonActivityBinding;

public class PersonActivity extends BaseActionBarActivity {
    private PersonActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.person_activity);
        PersonHandler handler = new PersonHandler(this, binding);
    }
}
