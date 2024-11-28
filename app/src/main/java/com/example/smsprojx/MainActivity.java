package com.example.smsprojx;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 101;
    private static final String PREFS_NAME = "SMSForwarderPrefs";
    private static final String API_URL_KEY = "api_url";
    private static final String API_METHOD_KEY = "api_method";
    private static final String REGEX_PATTERN_KEY = "regex_pattern";
    private static final String DEFAULT_API_URL = "https://sms-listener-api.onrender.com/api/data";
    private static final String DEFAULT_REGEX_PATTERN = "(?<=:)\\s*(\\d{6})"; // Default pattern for 6 digits after colon

    private TextInputEditText apiUrlInput;
    private TextInputEditText regexInput;
    private TextInputLayout regexInputLayout;
    private AutoCompleteTextView methodDropdown;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        initializeViews();
        setupMethodDropdown();
        loadSavedConfig();
        setupSaveButton();
        setupRegexHint();
        checkSmsPermission();
    }

    private void initializeViews() {
        apiUrlInput = findViewById(R.id.apiUrlInput);
        regexInput = findViewById(R.id.regexInput);
        regexInputLayout = findViewById(R.id.regexInputLayout);
        methodDropdown = findViewById(R.id.methodDropdown);
    }

    private void setupRegexHint() {
        regexInputLayout.setHelperText("Example: (?<=:)\\s*(\\d{6}) - extracts 6 digits after colon");
    }

    private void setupMethodDropdown() {
        String[] methods = new String[]{"POST", "PUT", "PATCH"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, methods);
        methodDropdown.setAdapter(adapter);
    }

    private void loadSavedConfig() {
        String savedUrl = prefs.getString(API_URL_KEY, DEFAULT_API_URL);
        String savedMethod = prefs.getString(API_METHOD_KEY, "POST");
        String savedRegex = prefs.getString(REGEX_PATTERN_KEY, DEFAULT_REGEX_PATTERN);
        
        apiUrlInput.setText(savedUrl);
        methodDropdown.setText(savedMethod, false);
        regexInput.setText(savedRegex);
    }

    private void setupSaveButton() {
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveConfiguration());
    }

    private void saveConfiguration() {
        String apiUrl = apiUrlInput.getText().toString().trim();
        String method = methodDropdown.getText().toString().trim();
        String regex = regexInput.getText().toString().trim();

        if (apiUrl.isEmpty()) {
            Toast.makeText(this, "Please enter API URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (method.isEmpty()) {
            Toast.makeText(this, "Please select HTTP method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use default pattern if empty
        if (regex.isEmpty()) {
            regex = DEFAULT_REGEX_PATTERN;
            regexInput.setText(regex);
        }

        // Validate regex pattern
        try {
            java.util.regex.Pattern.compile(regex);
        } catch (java.util.regex.PatternSyntaxException e) {
            Toast.makeText(this, "Invalid regex pattern: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(API_URL_KEY, apiUrl);
        editor.putString(API_METHOD_KEY, method);
        editor.putString(REGEX_PATTERN_KEY, regex);
        editor.apply();

        Toast.makeText(this, "Configuration saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
