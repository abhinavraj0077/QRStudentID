package com.example.qrstudentid;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileUpdateActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText nameInput;
    private EditText studentIdInput;
    private EditText courseInput;
    private EditText yearInput;
    private EditText phoneInput;
    private EditText emailInput;
    private EditText alternatePhoneInput;

    private Button saveButton;

    private final int BG = Color.rgb(20, 19, 24);
    private final int CARD = Color.rgb(31, 30, 39);
    private final int INPUT = Color.rgb(45, 44, 56);
    private final int PURPLE = Color.rgb(170, 125, 255);
    private final int WHITE = Color.WHITE;
    private final int GRAY = Color.rgb(175, 171, 185);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        showProfileScreen();
        loadStudentProfile();
    }

    // =========================================================
    // PROFILE SCREEN
    // =========================================================

    private void showProfileScreen() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(
                dp(22),
                dp(35),
                dp(22),
                dp(35)
        );

        scrollView.addView(root);

        // BACK BUTTON
        Button backButton = new Button(this);

        backButton.setText("← BACK");
        backButton.setTextSize(15);
        backButton.setTextColor(Color.rgb(45, 25, 80));
        backButton.setAllCaps(false);
        backButton.setBackgroundColor(PURPLE);

        root.addView(
                backButton,
                new LinearLayout.LayoutParams(
                        dp(110),
                        dp(48)
                )
        );

        backButton.setOnClickListener(v -> finish());

        addSpace(root, 20);

        // TITLE
        TextView title = new TextView(this);

        title.setText("Update Profile");
        title.setTextColor(WHITE);
        title.setTextSize(30);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        title.setGravity(Gravity.CENTER);

        root.addView(title);

        addSpace(root, 5);

        TextView subtitle = new TextView(this);

        subtitle.setText(
                "Manage your student information"
        );

        subtitle.setTextColor(GRAY);
        subtitle.setTextSize(16);
        subtitle.setGravity(Gravity.CENTER);

        root.addView(subtitle);

        addSpace(root, 25);

        // PROFILE CARD
        LinearLayout card = new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(23),
                dp(25),
                dp(23),
                dp(25)
        );

        card.setBackgroundColor(CARD);

        root.addView(
                card,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        // NAME
        addLabel(card, "STUDENT NAME");

        nameInput = createInput(
                "Enter student name"
        );

        card.addView(nameInput);

        addSpace(card, 16);

        // STUDENT ID
        addLabel(card, "STUDENT ID");

        studentIdInput = createInput(
                "Enter student ID"
        );

        card.addView(studentIdInput);

        addSpace(card, 16);

        // COURSE
        addLabel(card, "COURSE");

        courseInput = createInput(
                "Enter course"
        );

        card.addView(courseInput);

        addSpace(card, 16);

        // YEAR
        addLabel(card, "YEAR");

        yearInput = createInput(
                "Enter year"
        );

        yearInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        card.addView(yearInput);

        addSpace(card, 16);

        // PHONE
        addLabel(card, "PHONE NUMBER");

        phoneInput = createInput(
                "Enter phone number"
        );

        phoneInput.setInputType(
                InputType.TYPE_CLASS_PHONE
        );

        card.addView(phoneInput);

        addSpace(card, 16);

        // EMAIL
        addLabel(card, "EMAIL ADDRESS");

        emailInput = createInput(
                "Enter email"
        );

        emailInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        card.addView(emailInput);

        addSpace(card, 16);

        // ALTERNATE PHONE
        addLabel(
                card,
                "ALTERNATE PHONE NUMBER"
        );

        alternatePhoneInput = createInput(
                "Enter alternate number"
        );

        alternatePhoneInput.setInputType(
                InputType.TYPE_CLASS_PHONE
        );

        card.addView(alternatePhoneInput);

        addSpace(card, 25);

        // SAVE BUTTON
        saveButton = new Button(this);

        saveButton.setText("SAVE PROFILE");
        saveButton.setTextSize(17);
        saveButton.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        saveButton.setTextColor(
                Color.rgb(45, 25, 80)
        );
        saveButton.setAllCaps(false);
        saveButton.setBackgroundColor(PURPLE);

        card.addView(
                saveButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58)
                )
        );

        saveButton.setOnClickListener(
                v -> saveProfile()
        );

        addSpace(root, 20);

        // INFORMATION
        TextView info = new TextView(this);

        info.setText(
                "Your profile information is securely stored in Firebase."
        );

        info.setTextColor(GRAY);
        info.setTextSize(14);
        info.setGravity(Gravity.CENTER);

        root.addView(info);

        setContentView(scrollView);
    }

    // =========================================================
    // LOAD PROFILE
    // =========================================================

    private void loadStudentProfile() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        String uid =
                auth.getCurrentUser().getUid();

        db.collection("students")
                .document(uid)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (document.exists()) {

                                fillProfile(document);

                            } else {

                                Toast.makeText(
                                        this,
                                        "Student profile not found",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    this,
                                    "Unable to load profile: " +
                                            e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    // =========================================================
    // FILL PROFILE
    // =========================================================

    private void fillProfile(
            DocumentSnapshot document
    ) {

        nameInput.setText(
                getValue(document, "name")
        );

        studentIdInput.setText(
                getValue(document, "studentId")
        );

        courseInput.setText(
                getValue(document, "course")
        );

        yearInput.setText(
                getValue(document, "year")
        );

        phoneInput.setText(
                getValue(document, "phone")
        );

        emailInput.setText(
                getValue(document, "email")
        );

        alternatePhoneInput.setText(
                getValue(document, "alternatePhone")
        );
    }

    // =========================================================
    // SAVE PROFILE
    // =========================================================

    private void saveProfile() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String name =
                nameInput.getText()
                        .toString()
                        .trim();

        String studentId =
                studentIdInput.getText()
                        .toString()
                        .trim();

        String course =
                courseInput.getText()
                        .toString()
                        .trim();

        String year =
                yearInput.getText()
                        .toString()
                        .trim();

        String phone =
                phoneInput.getText()
                        .toString()
                        .trim();

        String email =
                emailInput.getText()
                        .toString()
                        .trim();

        String alternatePhone =
                alternatePhoneInput.getText()
                        .toString()
                        .trim();

        // VALIDATION

        if (name.isEmpty()) {

            nameInput.setError(
                    "Enter student name"
            );

            return;
        }

        if (studentId.isEmpty()) {

            studentIdInput.setError(
                    "Enter student ID"
            );

            return;
        }

        if (course.isEmpty()) {

            courseInput.setError(
                    "Enter course"
            );

            return;
        }

        if (year.isEmpty()) {

            yearInput.setError(
                    "Enter year"
            );

            return;
        }

        if (phone.isEmpty()) {

            phoneInput.setError(
                    "Enter phone number"
            );

            return;
        }

        if (email.isEmpty()) {

            emailInput.setError(
                    "Enter email"
            );

            return;
        }

        if (alternatePhone.isEmpty()) {

            alternatePhoneInput.setError(
                    "Enter alternate phone number"
            );

            return;
        }

        saveButton.setEnabled(false);
        saveButton.setText("SAVING...");

        String uid =
                auth.getCurrentUser().getUid();

        Map<String, Object> updates =
                new HashMap<>();

        updates.put("name", name);
        updates.put("studentId", studentId);
        updates.put("course", course);
        updates.put("year", year);
        updates.put("phone", phone);
        updates.put("email", email);
        updates.put(
                "alternatePhone",
                alternatePhone
        );

        db.collection("students")
                .document(uid)
                .update(updates)
                .addOnSuccessListener(
                        unused -> {

                            Toast.makeText(
                                    this,
                                    "Profile updated successfully",
                                    Toast.LENGTH_LONG
                            ).show();

                            saveButton.setEnabled(true);
                            saveButton.setText(
                                    "SAVE PROFILE"
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    this,
                                    "Profile update failed: " +
                                            e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            saveButton.setEnabled(true);
                            saveButton.setText(
                                    "SAVE PROFILE"
                            );
                        }
                );
    }

    // =========================================================
    // UI HELPERS
    // =========================================================

    private void addLabel(
            LinearLayout parent,
            String text
    ) {

        TextView label = new TextView(this);

        label.setText(text);
        label.setTextColor(
                Color.rgb(220, 216, 230)
        );
        label.setTextSize(14);
        label.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        parent.addView(label);
    }

    private EditText createInput(
            String hint
    ) {

        EditText input =
                new EditText(this);

        input.setHint(hint);
        input.setHintTextColor(
                Color.rgb(150, 147, 160)
        );
        input.setTextColor(WHITE);
        input.setTextSize(17);
        input.setSingleLine(true);

        input.setPadding(
                dp(18),
                0,
                dp(18),
                0
        );

        input.setBackgroundColor(INPUT);

        input.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                )
        );

        return input;
    }

    private String getValue(
            DocumentSnapshot document,
            String field
    ) {

        String value =
                document.getString(field);

        if (value == null) {
            return "";
        }

        return value;
    }

    private void addSpace(
            LinearLayout parent,
            int value
    ) {

        TextView space =
                new TextView(this);

        parent.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        dp(value)
                )
        );
    }

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}