
package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int BACKGROUND = Color.rgb(18, 20, 27);
    private static final int CARD = Color.rgb(34, 38, 48);
    private static final int BLUE = Color.rgb(65, 120, 230);
    private static final int WHITE = Color.WHITE;
    private static final int GREY = Color.LTGRAY;
    private static final int GREEN = Color.rgb(100, 210, 140);
    private static final int ORANGE = Color.rgb(255, 190, 90);

    private LinearLayout appLayout;
    private LinearLayout screenLayout;

    private String currentScreen = "login";

    private String selectedDate;
    private String startTime = "14:00";
    private String endTime = "15:00";
    private String roomFilter = "All";
    private String bookingTab = "Upcoming";

    private final ArrayList<RoomBooking> bookings = new ArrayList<>();

    private final String[] roomNames = {
            "Meeting Room 1",
            "Meeting Room 2",
            "Meeting Room 3",
            "Meeting Room 4",
            "Meeting Room 5",
            "Seminar Room A",
            "Seminar Room B"
    };

    private final String[] roomTypes = {
            "Meeting", "Meeting", "Meeting",
            "Meeting", "Meeting", "Seminar", "Seminar"
    };

    private final int[] roomCapacities = {
            4, 4, 6, 8, 10, 30, 30
    };

    // Booking information
    private static class RoomBooking {

        String room;
        String date;
        String time;
        String status;
        String purpose;

        RoomBooking(String room, String date, String time,
                    String status, String purpose) {

            this.room = room;
            this.date = date;
            this.time = time;
            this.status = status;
            this.purpose = purpose;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });

        Calendar calendar = Calendar.getInstance();

        selectedDate = formatDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        if (savedInstanceState != null) {

            currentScreen = savedInstanceState.getString(
                    "screen", "login");

            selectedDate = savedInstanceState.getString(
                    "date", selectedDate);

            startTime = savedInstanceState.getString(
                    "start", "14:00");

            endTime = savedInstanceState.getString(
                    "end", "15:00");

            roomFilter = savedInstanceState.getString(
                    "filter", "All");

            bookingTab = savedInstanceState.getString(
                    "tab", "Upcoming");

            ArrayList<String> savedBookings =
                    savedInstanceState.getStringArrayList("bookings");

            if (savedBookings != null) {

                for (String item : savedBookings) {

                    String[] parts = item.split("\\|", -1);

                    if (parts.length == 5) {

                        bookings.add(new RoomBooking(
                                parts[0],
                                parts[1],
                                parts[2],
                                parts[3],
                                parts[4]
                        ));
                    }
                }
            }

        } else {

            addSampleBookings();
        }

        buildEmployeeUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putString("screen", currentScreen);
        outState.putString("date", selectedDate);
        outState.putString("start", startTime);
        outState.putString("end", endTime);
        outState.putString("filter", roomFilter);
        outState.putString("tab", bookingTab);

        ArrayList<String> saved = new ArrayList<>();

        for (RoomBooking booking : bookings) {

            saved.add(
                    booking.room + "|" +
                            booking.date + "|" +
                            booking.time + "|" +
                            booking.status + "|" +
                            booking.purpose
            );
        }

        outState.putStringArrayList("bookings", saved);
    }

    private void addSampleBookings() {

        bookings.add(new RoomBooking(
                "Meeting Room 2",
                selectedDate,
                "14:00-15:00",
                "Confirmed",
                "Team meeting"
        ));

        bookings.add(new RoomBooking(
                "Meeting Room 1",
                selectedDate,
                "10:00-11:00",
                "Awaiting approval",
                "Project discussion"
        ));
    }

    private String formatDate(int year, int month, int day) {

        return String.format(
                Locale.UK,
                "%02d/%02d/%04d",
                day, month + 1, year
        );
    }

    private void buildEmployeeUI() {

        View rootView = findViewById(R.id.main);

        if (!(rootView instanceof ViewGroup)) {
            Toast.makeText(this,
                    "Invalid main layout.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        ViewGroup host = (ViewGroup) rootView;

        appLayout = new LinearLayout(this);
        appLayout.setOrientation(LinearLayout.VERTICAL);
        appLayout.setBackgroundColor(BACKGROUND);

        host.addView(appLayout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        showEmployeeScreen(currentScreen);
    }

    private void showEmployeeScreen(String screen) {

        currentScreen = screen;

        appLayout.removeAllViews();

        screenLayout = new LinearLayout(this);
        screenLayout.setOrientation(LinearLayout.VERTICAL);
        screenLayout.setPadding(16, 12, 16, 8);

        appLayout.addView(screenLayout,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                ));

        if (screen.equals("login")) {

            showEmployeeLogin();

        } else if (screen.equals("find")) {

            showRoomSearch();

        } else if (screen.equals("bookings")) {

            showEmployeeBookings();

        } else {

            showEmployeeLogin();
        }
    }

    private TextView createLabel(
            String value, int size, boolean bold) {

        TextView label = new TextView(this);

        label.setText(value);
        label.setTextColor(WHITE);
        label.setTextSize(size);
        label.setPadding(4, 7, 4, 7);

        if (bold) {
            label.setTypeface(null, Typeface.BOLD);
        }

        return label;
    }

    private Button createButton(
            String value, Runnable action) {

        Button button = new Button(this);

        button.setText(value);
        button.setAllCaps(false);
        button.setTextColor(WHITE);
        button.setTextSize(14);

        button.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(BLUE)
        );

        button.setOnClickListener(v -> action.run());

        return button;
    }

    private Button createSecondaryButton(
            String value, Runnable action) {

        Button button = new Button(this);

        button.setText(value);
        button.setAllCaps(false);
        button.setTextColor(WHITE);
        button.setTextSize(12);

        button.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(CARD)
        );

        button.setOnClickListener(v -> action.run());

        return button;
    }

    private EditText createInput(String hint) {

        EditText input = new EditText(this);

        input.setHint(hint);
        input.setHintTextColor(GREY);
        input.setTextColor(WHITE);
        input.setSingleLine(true);
        input.setPadding(12, 8, 12, 8);

        GradientDrawable shape = new GradientDrawable();

        shape.setColor(CARD);
        shape.setCornerRadius(12);

        input.setBackground(shape);

        return input;
    }

    private GradientDrawable createCardBackground() {

        GradientDrawable shape = new GradientDrawable();

        shape.setColor(CARD);
        shape.setCornerRadius(14);

        return shape;
    }

    // HEADER
    private void createHeader() {

        LinearLayout header = new LinearLayout(this);

        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = createLabel("ROOM DESK", 22, true);

        header.addView(title,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                ));

        header.addView(createSecondaryButton(
                "Sign out",
                () -> showEmployeeScreen("login")
        ));

        screenLayout.addView(header);

        View divider = new View(this);

        divider.setBackgroundColor(Color.DKGRAY);

        screenLayout.addView(divider,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1
                ));
    }

    private void showEmployeeLogin() {

        ScrollView scroll = new ScrollView(this);

        screenLayout.addView(scroll,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));

        LinearLayout form = new LinearLayout(this);

        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(12, 30, 12, 20);

        scroll.addView(form);

        TextView title = createLabel("ROOM DESK", 32, true);

        title.setGravity(Gravity.CENTER);

        form.addView(title);

        TextView subtitle = createLabel(
                "Employee room booking system", 15, false);

        subtitle.setGravity(Gravity.CENTER);
        subtitle.setTextColor(GREY);

        form.addView(subtitle);

        addGap(form, 35);

        form.addView(createLabel("Welcome back", 24, true));

        form.addView(createLabel(
                "Sign in to access your employee account.",
                14, false
        ));

        EditText email = createInput("Email address");

        email.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        form.addView(email);

        EditText password = createInput("Password");

        password.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        form.addView(password);

        TextView error = createLabel("", 13, false);

        error.setTextColor(Color.RED);

        form.addView(error);

        form.addView(createButton("Sign in", () -> {

            if (email.getText().toString().trim()
                    .equals("sam@officeco.example")
                    && password.getText().toString()
                    .equals("employee123")) {

                showEmployeeScreen("find");

            } else {

                error.setText("Incorrect email or password.");
            }
        }));

        addGap(form, 8);

        form.addView(createSecondaryButton(
                "Fill demo account",
                () -> {
                    email.setText("sam@officeco.example");
                    password.setText("employee123");
                }
        ));

        form.addView(createLabel(
                "Demo email: sam@officeco.example\n" +
                        "Demo password: employee123",
                13, false
        ));
    }

    private void showRoomSearch() {

        createHeader();

        screenLayout.addView(
                createLabel("Find a room", 24, true));

        screenLayout.addView(
                createLabel(
                        "Choose your preferred date and time.",
                        14, false
                ));

        screenLayout.addView(createSecondaryButton(
                "Date: " + selectedDate,
                this::chooseDate
        ));

        LinearLayout timeRow = new LinearLayout(this);

        timeRow.setOrientation(LinearLayout.HORIZONTAL);

        screenLayout.addView(timeRow);

        timeRow.addView(createSecondaryButton(
                "From: " + startTime,
                () -> chooseTime(true)
        ), new LinearLayout.LayoutParams(0, 50, 1));

        timeRow.addView(createSecondaryButton(
                "To: " + endTime,
                () -> chooseTime(false)
        ), new LinearLayout.LayoutParams(0, 50, 1));

        screenLayout.addView(
                createLabel("Room type", 15, true));

        LinearLayout filters = new LinearLayout(this);

        filters.setOrientation(LinearLayout.HORIZONTAL);

        screenLayout.addView(filters);

        for (String filter :
                new String[]{"All", "Meeting", "Seminar"}) {

            filters.addView(createSecondaryButton(
                    filter.equals(roomFilter)
                            ? "✓ " + filter : filter,
                    () -> {
                        roomFilter = filter;
                        showEmployeeScreen("find");
                    }
            ), new LinearLayout.LayoutParams(0, 48, 1));
        }

        ScrollView scroll = new ScrollView(this);

        screenLayout.addView(scroll,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                ));

        LinearLayout roomList = new LinearLayout(this);

        roomList.setOrientation(LinearLayout.VERTICAL);

        scroll.addView(roomList);

        boolean wide = getResources()
                .getConfiguration().screenWidthDp >= 600;

        LinearLayout row = null;

        int count = 0;

        for (int i = 0; i < roomNames.length; i++) {

            if (!roomFilter.equals("All")
                    && !roomTypes[i].equals(roomFilter)) {
                continue;
            }

            if (!isRoomAvailable(i)) {
                continue;
            }

            if (row == null) {

                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                roomList.addView(row);
            }

            addRoomCard(row, i);

            count++;

            if (!wide || count % 2 == 0) {
                row = null;
            }
        }

        screenLayout.addView(
                createLabel(count + " available rooms", 14, true),
                2
        );

        if (count == 0) {

            roomList.addView(createLabel(
                    "No rooms available for this selection.",
                    15, false
            ));
        }

        createNavigation();
    }

    private boolean isRoomAvailable(int index) {

        String slot = startTime + "-" + endTime;

        for (RoomBooking booking : bookings) {

            if (booking.room.equals(roomNames[index])
                    && booking.date.equals(selectedDate)
                    && booking.time.equals(slot)
                    && !booking.status.equals("Cancelled")) {

                return false;
            }
        }

        return true;
    }

    private void addRoomCard(
            LinearLayout row, int index) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(12, 10, 12, 10);
        card.setBackground(createCardBackground());

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );

        params.setMargins(4, 5, 4, 5);

        row.addView(card, params);

        card.addView(createLabel(
                roomNames[index], 17, true));

        card.addView(createLabel(
                roomTypes[index] + " room",
                13, false));

        card.addView(createLabel(
                roomCapacities[index] + " seats",
                13, false));

        TextView available = createLabel(
                "Available", 13, true);

        available.setTextColor(GREEN);

        card.addView(available);

        card.addView(createButton(
                "Request booking",
                () -> showBookingRequest(index)
        ));
    }

    private void showBookingRequest(int index) {

        LinearLayout form = new LinearLayout(this);

        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(12, 8, 12, 8);

        form.addView(createLabel(
                roomNames[index] + "\n" +
                        selectedDate + "\n" +
                        startTime + " - " + endTime + "\n" +
                        roomCapacities[index] + " seats",
                16, true
        ));

        EditText purpose = createInput("Meeting purpose");

        form.addView(purpose);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Request booking")
                .setView(form)
                .setMessage("Your request will need manager approval.")
                .setPositiveButton("Send request", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        if (startTime.compareTo(endTime) >= 0) {

                            Toast.makeText(this,
                                    "End time must be after start time.",
                                    Toast.LENGTH_SHORT).show();

                            return;
                        }

                        String time = startTime + "-" + endTime;

                        for (RoomBooking b : bookings) {

                            if (b.room.equals(roomNames[index])
                                    && b.date.equals(selectedDate)
                                    && b.time.equals(time)
                                    && !b.status.equals("Cancelled")) {

                                Toast.makeText(this,
                                        "Room is already booked.",
                                        Toast.LENGTH_SHORT).show();

                                return;
                            }
                        }

                        bookings.add(new RoomBooking(
                                roomNames[index],
                                selectedDate,
                                time,
                                "Awaiting approval",
                                purpose.getText().toString()
                        ));

                        dialog.dismiss();

                        new AlertDialog.Builder(this)
                                .setTitle("Request submitted")
                                .setMessage(
                                        "Your booking request has been sent."
                                )
                                .setPositiveButton(
                                        "My bookings",
                                        (a, b) ->
                                                showEmployeeScreen("bookings")
                                )
                                .show();
                    });
        });

        dialog.show();
    }

    private void showEmployeeBookings() {

        createHeader();

        screenLayout.addView(
                createLabel("My bookings", 24, true));

        LinearLayout tabs = new LinearLayout(this);

        tabs.setOrientation(LinearLayout.HORIZONTAL);

        screenLayout.addView(tabs);

        tabs.addView(createSecondaryButton(
                bookingTab.equals("Upcoming")
                        ? "✓ Upcoming" : "Upcoming",
                () -> {
                    bookingTab = "Upcoming";
                    showEmployeeScreen("bookings");
                }
        ), new LinearLayout.LayoutParams(0, 48, 1));

        tabs.addView(createSecondaryButton(
                bookingTab.equals("History")
                        ? "✓ History" : "History",
                () -> {
                    bookingTab = "History";
                    showEmployeeScreen("bookings");
                }
        ), new LinearLayout.LayoutParams(0, 48, 1));

        ScrollView scroll = new ScrollView(this);

        screenLayout.addView(scroll,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                ));

        LinearLayout list = new LinearLayout(this);

        list.setOrientation(LinearLayout.VERTICAL);

        scroll.addView(list);

        int count = 0;

        for (int i = bookings.size() - 1; i >= 0; i--) {

            RoomBooking booking = bookings.get(i);

            boolean history = booking.status.equals("Cancelled");

            if (bookingTab.equals("History") == history) {

                addBookingCard(list, booking);

                count++;
            }
        }

        if (count == 0) {

            list.addView(createLabel(
                    "No bookings found.",
                    15, false
            ));
        }

        createNavigation();
    }

    private void addBookingCard(
            LinearLayout list, RoomBooking booking) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(12, 10, 12, 10);
        card.setBackground(createCardBackground());

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 6, 0, 6);

        list.addView(card, params);

        card.addView(createLabel(
                booking.room, 18, true));

        card.addView(createLabel(
                booking.date + " • " + booking.time,
                14, false));

        TextView status = createLabel(
                "Status: " + booking.status,
                14, true);

        status.setTextColor(
                booking.status.equals("Confirmed")
                        ? GREEN : ORANGE
        );

        card.addView(status);

        if (!booking.purpose.isEmpty()) {

            card.addView(createLabel(
                    "Purpose: " + booking.purpose,
                    13, false));
        }

        if (!booking.status.equals("Cancelled")
                && !booking.status.equals("Cancellation requested")) {

            card.addView(createButton(
                    booking.status.equals("Awaiting approval")
                            ? "Withdraw request"
                            : "Request cancellation",
                    () -> confirmCancellation(booking)
            ));
        }
    }

    private void confirmCancellation(RoomBooking booking) {

        boolean pending =
                booking.status.equals("Awaiting approval");

        new AlertDialog.Builder(this)
                .setTitle("Cancel booking?")
                .setMessage(
                        pending
                                ? "Withdraw your booking request?"
                                : "Send a cancellation request to the manager?"
                )
                .setNegativeButton("Keep booking", null)
                .setPositiveButton("Confirm", (dialog, which) -> {

                    if (pending) {

                        booking.status = "Cancelled";

                    } else {

                        booking.status = "Cancellation requested";
                    }

                    showEmployeeScreen("bookings");
                })
                .show();
    }

    private void chooseDate() {

        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    selectedDate = formatDate(year, month, day);

                    showEmployeeScreen("find");
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void chooseTime(boolean start) {

        String current = start ? startTime : endTime;

        String[] parts = current.split(":");

        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {

                    String result = String.format(
                            Locale.UK,
                            "%02d:%02d",
                            selectedHour,
                            selectedMinute
                    );

                    if (start) {
                        startTime = result;
                    } else {
                        endTime = result;
                    }

                    showEmployeeScreen("find");
                },
                hour,
                minute,
                true
        ).show();
    }

    private void createNavigation() {

        LinearLayout navigation = new LinearLayout(this);

        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setBackgroundColor(CARD);

        appLayout.addView(navigation,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        58
                ));

        navigation.addView(createSecondaryButton(
                "Find a room",
                () -> showEmployeeScreen("find")
        ), new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1
        ));

        navigation.addView(createSecondaryButton(
                "My bookings",
                () -> showEmployeeScreen("bookings")
        ), new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1
        ));
    }

    private void addGap(LinearLayout layout, int height) {

        View gap = new View(this);

        layout.addView(gap,
                new LinearLayout.LayoutParams(1, height));
    }
}

