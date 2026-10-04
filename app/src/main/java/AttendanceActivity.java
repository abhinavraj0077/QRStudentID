package com.example.qrstudentid;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Colors
    private final int BG = Color.rgb(20, 19, 24);
    private final int CARD = Color.rgb(31, 30, 39);
    private final int PURPLE = Color.rgb(190, 145, 255);
    private final int GREEN = Color.rgb(70, 215, 135);
    private final int RED = Color.rgb(245, 90, 105);
    private final int WHITE = Color.WHITE;
    private final int GRAY = Color.rgb(175, 171, 185);

    // UI
    private TextView todayStatus;
    private TextView todayInTime;
    private TextView todayOutTime;
    private TextView presentCountText;
    private TextView absentCountText;
    private TextView percentageText;

    private LinearLayout chartContainer;
    private LinearLayout historyContainer;

    // Attendance data
    private final ArrayList<AttendanceRecord> records =
            new ArrayList<>();

    private int presentCount = 0;
    private int absentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        showAttendanceScreen();

        loadAttendance();
    }

    // =========================================================
    // ATTENDANCE SCREEN
    // =========================================================

    private void showAttendanceScreen() {

        ScrollView scrollView = new ScrollView(this);

        scrollView.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(20),
                dp(25),
                dp(20),
                dp(35)
        );

        scrollView.addView(root);

        // BACK BUTTON
        Button backButton = createButton("← BACK");

        root.addView(
                backButton,
                new LinearLayout.LayoutParams(
                        dp(100),
                        dp(48)
                )
        );

        backButton.setOnClickListener(
                v -> finish()
        );

        addSpace(root, 20);

        // TITLE
        TextView title =
                createTitle("ATTENDANCE");

        root.addView(title);

        addSpace(root, 5);

        // SUBTITLE
        TextView subtitle =
                createSubtitle(
                        "Track your attendance and daily records"
                );

        root.addView(subtitle);

        addSpace(root, 25);

        // =====================================================
        // TODAY CARD
        // =====================================================

        LinearLayout todayCard =
                createCard();

        root.addView(todayCard);

        TextView todayHeading =
                createHeading("TODAY");

        todayCard.addView(todayHeading);

        addSpace(todayCard, 8);

        String currentDate =
                new SimpleDateFormat(
                        "EEEE, dd MMMM yyyy",
                        Locale.getDefault()
                ).format(new Date());

        TextView dateText =
                createNormalText(currentDate);

        dateText.setTextSize(18);

        todayCard.addView(dateText);

        addSpace(todayCard, 18);

        todayStatus =
                createNormalText(
                        "Status: Loading..."
                );

        todayStatus.setTextSize(17);

        todayCard.addView(todayStatus);

        addSpace(todayCard, 12);

        todayInTime =
                createNormalText(
                        "In Time: --"
                );

        todayInTime.setTextSize(16);

        todayCard.addView(todayInTime);

        addSpace(todayCard, 8);

        todayOutTime =
                createNormalText(
                        "Out Time: --"
                );

        todayOutTime.setTextSize(16);

        todayCard.addView(todayOutTime);

        addSpace(root, 25);

        // =====================================================
        // SUMMARY
        // =====================================================

        TextView summaryTitle =
                createSectionTitle(
                        "ATTENDANCE SUMMARY"
                );

        root.addView(summaryTitle);

        addSpace(root, 12);

        LinearLayout summaryCard =
                createCard();

        root.addView(summaryCard);

        presentCountText =
                createNormalText(
                        "🟢 Present: --"
                );

        presentCountText.setTextSize(18);

        summaryCard.addView(
                presentCountText
        );

        addSpace(summaryCard, 15);

        absentCountText =
                createNormalText(
                        "🔴 Absent: --"
                );

        absentCountText.setTextSize(18);

        summaryCard.addView(
                absentCountText
        );

        addSpace(summaryCard, 15);

        percentageText =
                createNormalText(
                        "📊 Attendance: --"
                );

        percentageText.setTextSize(18);

        summaryCard.addView(
                percentageText
        );

        addSpace(root, 25);

        // =====================================================
        // WEEKLY CHART
        // =====================================================

        TextView chartTitle =
                createSectionTitle(
                        "WEEKLY ATTENDANCE"
                );

        root.addView(chartTitle);

        addSpace(root, 12);

        LinearLayout chartCard =
                createCard();

        root.addView(chartCard);

        chartContainer =
                new LinearLayout(this);

        chartContainer.setOrientation(
                LinearLayout.HORIZONTAL
        );

        chartContainer.setGravity(
                Gravity.BOTTOM
        );

        chartContainer.setPadding(
                dp(5),
                dp(10),
                dp(5),
                dp(5)
        );

        chartCard.addView(
                chartContainer,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(250)
                )
        );

        addSpace(root, 25);

        // =====================================================
        // HISTORY
        // =====================================================

        TextView historyTitle =
                createSectionTitle(
                        "ATTENDANCE HISTORY"
                );

        root.addView(historyTitle);

        addSpace(root, 12);

        historyContainer =
                new LinearLayout(this);

        historyContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        root.addView(historyContainer);

        addSpace(root, 20);

        TextView info =
                createSubtitle(
                        "Your attendance records are stored securely in Firebase."
                );

        root.addView(info);

        setContentView(scrollView);
    }

    // =========================================================
    // LOAD ATTENDANCE FROM FIREBASE
    // =========================================================

    private void loadAttendance() {

        if (auth.getCurrentUser() == null) {

            todayStatus.setText(
                    "Status: Please login again"
            );

            return;
        }

        String uid =
                auth.getCurrentUser().getUid();

        db.collection("attendance")
                .document(uid)
                .collection("daily")
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            records.clear();

                            for (
                                    DocumentSnapshot document :
                                    querySnapshot.getDocuments()
                            ) {

                                AttendanceRecord record =
                                        new AttendanceRecord();

                                record.date =
                                        document.getString(
                                                "date"
                                        );

                                if (
                                        record.date == null
                                                ||
                                                record.date.isEmpty()
                                ) {

                                    record.date =
                                            document.getId();
                                }

                                record.status =
                                        document.getString(
                                                "status"
                                        );

                                record.inTime =
                                        document.getString(
                                                "inTime"
                                        );

                                record.outTime =
                                        document.getString(
                                                "outTime"
                                        );

                                if (
                                        record.status == null
                                ) {
                                    record.status = "";
                                }

                                if (
                                        record.inTime == null
                                ) {
                                    record.inTime = "";
                                }

                                if (
                                        record.outTime == null
                                ) {
                                    record.outTime = "";
                                }

                                records.add(record);
                            }

                            // Sort latest first
                            sortRecords();

                            // Calculate summary
                            calculateSummary();

                            // Show today's attendance
                            showToday();

                            // Create weekly chart
                            createWeeklyChart();

                            // Show history
                            showHistory();

                            // Check 3 consecutive absent days
                            checkThreeConsecutiveAbsences();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            todayStatus.setText(
                                    "Status: Unable to load"
                            );

                            Toast.makeText(
                                    this,
                                    "Firebase error: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    // =========================================================
    // SORT RECORDS
    // =========================================================

    private void sortRecords() {

        Collections.sort(
                records,
                new Comparator<AttendanceRecord>() {

                    @Override
                    public int compare(
                            AttendanceRecord first,
                            AttendanceRecord second
                    ) {

                        return second.date.compareTo(
                                first.date
                        );
                    }
                }
        );
    }

    // =========================================================
    // SUMMARY
    // =========================================================

    private void calculateSummary() {

        presentCount = 0;
        absentCount = 0;

        for (
                AttendanceRecord record :
                records
        ) {

            if (
                    record.status.equalsIgnoreCase(
                            "Present"
                    )
            ) {

                presentCount++;

            } else if (
                    record.status.equalsIgnoreCase(
                            "Absent"
                    )
            ) {

                absentCount++;
            }
        }

        int total =
                presentCount + absentCount;

        double percentage = 0;

        if (total > 0) {

            percentage =
                    (
                            (double) presentCount
                                    / total
                    ) * 100;
        }

        presentCountText.setText(
                "🟢 Present: "
                        + presentCount
        );

        absentCountText.setText(
                "🔴 Absent: "
                        + absentCount
        );

        percentageText.setText(
                String.format(
                        Locale.getDefault(),
                        "📊 Attendance: %.1f%%",
                        percentage
                )
        );
    }

    // =========================================================
    // TODAY
    // =========================================================

    private void showToday() {

        String today =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        AttendanceRecord todayRecord =
                null;

        for (
                AttendanceRecord record :
                records
        ) {

            if (
                    record.date.equals(today)
            ) {

                todayRecord = record;

                break;
            }
        }

        if (todayRecord == null) {

            todayStatus.setText(
                    "Status: Not marked"
            );

            todayStatus.setTextColor(
                    GRAY
            );

            todayInTime.setText(
                    "In Time: --"
            );

            todayOutTime.setText(
                    "Out Time: --"
            );

            return;
        }

        String status =
                todayRecord.status;

        if (
                status == null
                        ||
                        status.isEmpty()
        ) {

            status = "Not marked";
        }

        todayStatus.setText(
                "Status: " + status
        );

        if (
                status.equalsIgnoreCase(
                        "Present"
                )
        ) {

            todayStatus.setTextColor(
                    GREEN
            );

        } else if (
                status.equalsIgnoreCase(
                        "Absent"
                )
        ) {

            todayStatus.setTextColor(
                    RED
            );

        } else {

            todayStatus.setTextColor(
                    GRAY
            );
        }

        if (
                todayRecord.inTime.isEmpty()
        ) {

            todayInTime.setText(
                    "In Time: --"
            );

        } else {

            todayInTime.setText(
                    "In Time: "
                            + todayRecord.inTime
            );
        }

        if (
                todayRecord.outTime.isEmpty()
        ) {

            todayOutTime.setText(
                    "Out Time: --"
            );

        } else {

            todayOutTime.setText(
                    "Out Time: "
                            + todayRecord.outTime
            );
        }
    }

    // =========================================================
    // WEEKLY CHART
    // =========================================================

    private void createWeeklyChart() {

        chartContainer.removeAllViews();

        int count =
                Math.min(
                        records.size(),
                        7
                );

        if (count == 0) {

            TextView empty =
                    createNormalText(
                            "No attendance records yet."
                    );

            empty.setGravity(
                    Gravity.CENTER
            );

            chartContainer.addView(
                    empty
            );

            return;
        }

        for (
                int i = count - 1;
                i >= 0;
                i--
        ) {

            AttendanceRecord record =
                    records.get(i);

            LinearLayout column =
                    new LinearLayout(this);

            column.setOrientation(
                    LinearLayout.VERTICAL
            );

            column.setGravity(
                    Gravity.BOTTOM
                            |
                            Gravity.CENTER_HORIZONTAL
            );

            LinearLayout.LayoutParams columnParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                    );

            chartContainer.addView(
                    column,
                    columnParams
            );

            TextView bar =
                    new TextView(this);

            int barHeight;

            if (
                    record.status.equalsIgnoreCase(
                            "Present"
                    )
            ) {

                barHeight = dp(145);

                bar.setBackgroundColor(
                        GREEN
                );

            } else if (
                    record.status.equalsIgnoreCase(
                            "Absent"
                    )
            ) {

                barHeight = dp(65);

                bar.setBackgroundColor(
                        RED
                );

            } else {

                barHeight = dp(30);

                bar.setBackgroundColor(
                        GRAY
                );
            }

            LinearLayout.LayoutParams barParams =
                    new LinearLayout.LayoutParams(
                            dp(30),
                            barHeight
                    );

            column.addView(
                    bar,
                    barParams
            );

            addSpace(
                    column,
                    6
            );

            TextView dayText =
                    new TextView(this);

            dayText.setText(
                    getDayName(
                            record.date
                    )
            );

            dayText.setTextColor(
                    WHITE
            );

            dayText.setTextSize(10);

            dayText.setGravity(
                    Gravity.CENTER
            );

            column.addView(
                    dayText
            );
        }
    }

    // =========================================================
    // GET DAY NAME
    // =========================================================

    private String getDayName(
            String dateString
    ) {

        try {

            Date date =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).parse(
                            dateString
                    );

            return new SimpleDateFormat(
                    "EEE",
                    Locale.getDefault()
            ).format(date);

        } catch (Exception e) {

            return dateString;
        }
    }

    // =========================================================
    // HISTORY
    // =========================================================

    private void showHistory() {

        historyContainer.removeAllViews();

        if (records.isEmpty()) {

            TextView empty =
                    createNormalText(
                            "No attendance records found."
                    );

            historyContainer.addView(
                    empty
            );

            return;
        }

        int count =
                Math.min(
                        records.size(),
                        15
                );

        for (
                int i = 0;
                i < count;
                i++
        ) {

            AttendanceRecord record =
                    records.get(i);

            LinearLayout card =
                    createHistoryCard(
                            record
                    );

            historyContainer.addView(
                    card
            );

            addSpace(
                    historyContainer,
                    10
            );
        }
    }

    // =========================================================
    // HISTORY CARD
    // =========================================================

    private LinearLayout createHistoryCard(
            AttendanceRecord record
    ) {

        LinearLayout card =
                createCard();

        TextView date =
                new TextView(this);

        date.setText(
                formatDate(
                        record.date
                )
        );

        date.setTextColor(
                WHITE
        );

        date.setTextSize(17);

        date.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        card.addView(date);

        addSpace(
                card,
                8
        );

        TextView status =
                new TextView(this);

        if (
                record.status.equalsIgnoreCase(
                        "Present"
                )
        ) {

            status.setText(
                    "● Present"
            );

            status.setTextColor(
                    GREEN
            );

        } else if (
                record.status.equalsIgnoreCase(
                        "Absent"
                )
        ) {

            status.setText(
                    "● Absent"
            );

            status.setTextColor(
                    RED
            );

        } else {

            status.setText(
                    "● Not marked"
            );

            status.setTextColor(
                    GRAY
            );
        }

        status.setTextSize(16);

        card.addView(status);

        addSpace(
                card,
                7
        );

        TextView time =
                new TextView(this);

        time.setText(
                "In: "
                        +
                        (
                                record.inTime.isEmpty()
                                        ? "--"
                                        : record.inTime
                        )
                        +
                        "\nOut: "
                        +
                        (
                                record.outTime.isEmpty()
                                        ? "--"
                                        : record.outTime
                        )
        );

        time.setTextColor(
                GRAY
        );

        time.setTextSize(14);

        card.addView(time);

        return card;
    }

    // =========================================================
    // FORMAT DATE
    // =========================================================

    private String formatDate(
            String dateString
    ) {

        try {

            Date date =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).parse(
                            dateString
                    );

            return new SimpleDateFormat(
                    "EEEE, dd MMM yyyy",
                    Locale.getDefault()
            ).format(date);

        } catch (Exception e) {

            return dateString;
        }
    }

    // =========================================================
    // 3 CONSECUTIVE ABSENT CHECK
    // =========================================================

    private void checkThreeConsecutiveAbsences() {

        if (records.size() < 3) {
            return;
        }

        /*
         * Records are sorted latest first.
         *
         * Example:
         *
         * 2026-10-04 Absent
         * 2026-10-03 Absent
         * 2026-10-02 Absent
         *
         * This is considered 3 consecutive absent days.
         */

        for (
                int i = 0;
                i < records.size() - 2;
                i++
        ) {

            AttendanceRecord first =
                    records.get(i);

            AttendanceRecord second =
                    records.get(i + 1);

            AttendanceRecord third =
                    records.get(i + 2);

            // Check all three are absent
            if (
                    !first.status.equalsIgnoreCase(
                            "Absent"
                    )
                            ||
                            !second.status.equalsIgnoreCase(
                                    "Absent"
                            )
                            ||
                            !third.status.equalsIgnoreCase(
                                    "Absent"
                            )
            ) {

                continue;
            }

            try {

                SimpleDateFormat format =
                        new SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                        );

                Date date1 =
                        format.parse(
                                first.date
                        );

                Date date2 =
                        format.parse(
                                second.date
                        );

                Date date3 =
                        format.parse(
                               third.date
                        );

                Calendar cal1 =
                        Calendar.getInstance();

                Calendar cal2 =
                        Calendar.getInstance();

                Calendar cal3 =
                        Calendar.getInstance();

                cal1.setTime(date1);
                cal2.setTime(date2);
                cal3.setTime(date3);

                /*
                 * Remove time part so that
                 * only the calendar date is compared.
                 */

                cal1.set(
                        Calendar.HOUR_OF_DAY,
                        0
                );

                cal1.set(
                        Calendar.MINUTE,
                        0
                );

                cal1.set(
                        Calendar.SECOND,
                        0
                );

                cal1.set(
                        Calendar.MILLISECOND,
                        0
                );

                cal2.set(
                        Calendar.HOUR_OF_DAY,
                        0
                );

                cal2.set(
                        Calendar.MINUTE,
                        0
                );

                cal2.set(
                        Calendar.SECOND,
                        0
                );

                cal2.set(
                        Calendar.MILLISECOND,
                        0
                );

                cal3.set(
                        Calendar.HOUR_OF_DAY,
                        0
                );

                cal3.set(
                        Calendar.MINUTE,
                        0
                );

                cal3.set(
                        Calendar.SECOND,
                        0
                );

                cal3.set(
                        Calendar.MILLISECOND,
                        0
                );

                long day1 =
                        cal1.getTimeInMillis();

                long day2 =
                        cal2.getTimeInMillis();

                long day3 =
                        cal3.getTimeInMillis();

                long oneDay =
                        24L
                                * 60L
                                * 60L
                                * 1000L;

                /*
                 * Latest date must be exactly
                 * one day after second date,
                 * and second must be one day
                 * after third date.
                 */

                if (
                        day1 - day2 == oneDay
                                &&
                                day2 - day3 == oneDay
                ) {

                    showAbsentAlert();

                    return;
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    // =========================================================
    // ABSENT ALERT
    // =========================================================

    private void showAbsentAlert() {

        new AlertDialog.Builder(this)

                .setTitle(
                        "⚠️ Attendance Alert"
                )

                .setMessage(
                        "You have been absent for "
                                + "3 consecutive days.\n\n"
                                + "Please contact your faculty "
                                + "or department."
                )

                .setPositiveButton(
                        "OK",
                        null
                )

                .show();
    }

    // =========================================================
    // CREATE CARD
    // =========================================================

    private LinearLayout createCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        card.setBackgroundColor(
                CARD
        );

        card.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        return card;
    }

    // =========================================================
    // TITLE
    // =========================================================

    private TextView createTitle(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                WHITE
        );

        view.setTextSize(30);

        view.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        view.setGravity(
                Gravity.CENTER
        );

        return view;
    }

    // =========================================================
    // SECTION TITLE
    // =========================================================

    private TextView createSectionTitle(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                WHITE
        );

        view.setTextSize(21);

        view.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        return view;
    }

    // =========================================================
    // HEADING
    // =========================================================

    private TextView createHeading(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                PURPLE
        );

        view.setTextSize(16);

        view.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        return view;
    }

    // =========================================================
    // NORMAL TEXT
    // =========================================================

    private TextView createNormalText(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                WHITE
        );

        view.setTextSize(16);

        return view;
    }

    // =========================================================
    // SUBTITLE
    // =========================================================

    private TextView createSubtitle(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                GRAY
        );

        view.setTextSize(15);

        view.setGravity(
                Gravity.CENTER
        );

        return view;
    }

    // =========================================================
    // BUTTON
    // =========================================================

    private Button createButton(
            String text
    ) {

        Button button =
                new Button(this);

        button.setText(text);

        button.setTextSize(14);

        button.setTextColor(
                Color.rgb(
                        45,
                        25,
                        80
                )
        );

        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setAllCaps(false);

        button.setBackgroundColor(
                PURPLE
        );

        return button;
    }

    // =========================================================
    // SPACE
    // =========================================================

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

    // =========================================================
    // DP
    // =========================================================

    private int dp(
            int value
    ) {

        return (int) (
                value
                        *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    // =========================================================
    // ATTENDANCE RECORD
    // =========================================================

    private static class AttendanceRecord {

        String date = "";

        String status = "";

        String inTime = "";

        String outTime = "";
    }
}