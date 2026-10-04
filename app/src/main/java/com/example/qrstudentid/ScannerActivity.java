package com.example.qrstudentid;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.ScanContract;

public class ScannerActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout layout;

    private final androidx.activity.result.ActivityResultLauncher<ScanOptions>
            barcodeLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {

                        if (result.getContents() != null) {

                            String studentId =
                                    result.getContents().trim();

                            verifyStudent(studentId);

                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        showScannerScreen();
    }

    private void showScannerScreen() {

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("QR Code Scanner");
        title.setTextSize(30);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 30, 0, 50);

        Button scanButton = new Button(this);
        scanButton.setText("SCAN QR CODE");
        scanButton.setTextSize(18);

        layout.addView(title);
        layout.addView(scanButton);

        setContentView(layout);

        scanButton.setOnClickListener(v -> startScanner());
    }

    private void startScanner() {

        ScanOptions options = new ScanOptions();

        options.setDesiredBarcodeFormats(
                ScanOptions.QR_CODE
        );

        options.setPrompt(
                "Point the camera at the student's QR code"
        );

        options.setBeepEnabled(true);
        options.setOrientationLocked(false);

        barcodeLauncher.launch(options);
    }

    private void verifyStudent(String studentId) {

        db.collection("students")
                .whereEqualTo("studentId", studentId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!querySnapshot.isEmpty()) {

                        DocumentSnapshot student =
                                querySnapshot
                                        .getDocuments()
                                        .get(0);

                        showVerifiedStudent(student);

                    } else {

                        showNotVerified(studentId);
                    }
                })
                .addOnFailureListener(e -> {

                    showError(
                            "Database error: " + e.getMessage()
                    );
                });
    }

    private void showVerifiedStudent(
            DocumentSnapshot student) {

        layout.removeAllViews();

        TextView status = new TextView(this);
        status.setText("✓ STUDENT VERIFIED");
        status.setTextSize(28);
        status.setTextColor(Color.GREEN);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 30, 0, 40);

        layout.addView(status);

        addDetail(
                "Name",
                student.getString("name")
        );

        addDetail(
                "Student ID",
                student.getString("studentId")
        );

        addDetail(
                "Course",
                student.getString("course")
        );

        addDetail(
                "Year",
                student.getString("year")
        );

        addDetail(
                "Phone",
                student.getString("phone")
        );

        addDetail(
                "Email",
                student.getString("email")
        );

        Button scanAgain = new Button(this);
        scanAgain.setText("SCAN ANOTHER QR");
        scanAgain.setTextSize(18);

        layout.addView(scanAgain);

        scanAgain.setOnClickListener(
                v -> showScannerScreen()
        );
    }

    private void showNotVerified(String studentId) {

        layout.removeAllViews();

        TextView status = new TextView(this);
        status.setText("✗ STUDENT NOT VERIFIED");
        status.setTextSize(26);
        status.setTextColor(Color.RED);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 30, 0, 30);

        layout.addView(status);

        TextView message = new TextView(this);
        message.setText(
                "Student ID not found: " + studentId
        );
        message.setTextSize(18);
        message.setTextColor(Color.WHITE);
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, 10, 0, 30);

        layout.addView(message);

        Button tryAgain = new Button(this);
        tryAgain.setText("TRY AGAIN");
        tryAgain.setTextSize(18);

        layout.addView(tryAgain);

        tryAgain.setOnClickListener(
                v -> showScannerScreen()
        );
    }

    private void showError(String message) {

        layout.removeAllViews();

        TextView error = new TextView(this);
        error.setText("ERROR\n\n" + message);
        error.setTextSize(20);
        error.setTextColor(Color.RED);
        error.setGravity(Gravity.CENTER);

        layout.addView(error);

        Button backButton = new Button(this);
        backButton.setText("BACK");

        layout.addView(backButton);

        backButton.setOnClickListener(
                v -> finish()
        );
    }

    private void addDetail(
            String label,
            String value) {

        TextView textView = new TextView(this);

        if (value == null) {
            value = "";
        }

        textView.setText(
                label + ": " + value
        );

        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(0, 10, 0, 10);

        layout.addView(textView);
    }
}