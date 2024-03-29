package app.flaminius.flaminius2k19;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.util.PatternsCompat;

import com.jaiselrahman.hintspinner.HintSpinner;
import com.jaiselrahman.hintspinner.HintSpinnerAdapter;

import app.flaminius.flaminius2k19.util.RegisterTask;
import app.flaminius.flaminius2k19.util.ReverseInterpolator;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {
    private EditText name, email, phone, college;

    private String department, foodPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        college = findViewById(R.id.college);

        setUpDepartmentSpinner();

        setUpFoodPrefSpinner();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_registration_fee) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.registration_fee)
                    .setMessage(R.string.registration_fee_info)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpFoodPrefSpinner() {
        final String[] foodPrefs = getResources().getStringArray(R.array.food_prefs);

        HintSpinner foodPrefSpinner = findViewById(R.id.foodPrefs);
        foodPrefSpinner.setAdapter(new HintSpinnerAdapter<>(this, foodPrefs, "Veg./Non. Veg."));
        foodPrefSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RegisterActivity.this.foodPref = foodPrefs[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setUpDepartmentSpinner() {
        final String[] departments = getResources().getStringArray(R.array.departments);

        HintSpinner departmentSpinner = findViewById(R.id.department);
        departmentSpinner.setAdapter(new HintSpinnerAdapter<>(this, departments, "Select Department"));
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RegisterActivity.this.department = departments[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void onRegisterClick(View view) {
        if (!validate()) return;

        final CircularProgressButton btn = (CircularProgressButton) view;

        btn.startMorphAnimation();

        new RegisterTask().setName(name.getText().toString())
                .setEmail(email.getText().toString())
                .setPhone(phone.getText().toString())
                .setCollege(college.getText().toString())
                .setDepartment(department)
                .setFoodPreference(foodPref)
                .register(this, new RegisterTask.OnCompletionListener() {
                    @Override
                    public void onSuccess() {
                        btn.doneLoadingAnimation(ContextCompat.getColor(RegisterActivity.this, R.color.colorPrimary), BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

                        btn.animate().alpha(0)
                                .scaleYBy(10)
                                .scaleXBy(10)
                                .translationYBy(-200)
                                .setDuration(1000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        animation.removeListener(this);
                                        animation.setDuration(0);
                                        animation.setInterpolator(new ReverseInterpolator());
                                        animation.start();
                                    }
                                });
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        btn.startMorphRevertAnimation();
                    }
                });
    }

    private boolean validate() {
        String name = this.name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            this.name.setError(getString(R.string.error_enter_name));
            return false;
        } else {
            this.name.setError(null);
        }

        String email = this.email.getText().toString();
        if (TextUtils.isEmpty(email) || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError(getString(R.string.error_enter_email));
            return false;
        } else {
            this.email.setError(null);
        }

        String phone = this.phone.getText().toString();
        if (TextUtils.isEmpty(phone) || phone.length() < 10 || phone.length() > 13 || !Patterns.PHONE.matcher(phone).matches()) {
            this.phone.setError(getString(R.string.error_enter_phone));
            return false;
        } else {
            this.phone.setError(null);
        }

        String college = this.college.getText().toString();
        if (TextUtils.isEmpty(college)) {
            this.college.setError(getString(R.string.error_enter_college));
            return false;
        } else {
            this.college.setError(null);
        }

        if (TextUtils.isEmpty(department)) {
            Toast.makeText(this, R.string.error_select_valid_department, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(foodPref)) {
            Toast.makeText(this, R.string.error_select_valid_food_pref, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
