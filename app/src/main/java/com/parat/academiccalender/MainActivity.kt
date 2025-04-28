package com.parat.academiccalender

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

data class AcademicEvent(
    val name: String,
    val date: LocalDate,
    val type: EventType
)

enum class EventType {
    HOLIDAY, EVENT, SCHEDULE, EXAM, CLASS_TEST, VACATION
}

// Generate Sundays as holidays for 2025
@RequiresApi(Build.VERSION_CODES.O)
fun generateSundayHolidays(year: Int): List<AcademicEvent> {
    val sundays = mutableListOf<AcademicEvent>()
    var date = LocalDate.of(year, 1, 1)
    while (date.year == year) {
        if (date.dayOfWeek.value == 7) { // Sunday
            sundays.add(AcademicEvent("Sunday Holiday", date, EventType.HOLIDAY))
        }
        date = date.plusDays(1)
    }
    return sundays
}

// Updated events list based on a typical academic calendar
@RequiresApi(Build.VERSION_CODES.O)
val eventsList = listOf(
    AcademicEvent("Academic Session Start", LocalDate.of(2025, 1, 2), EventType.SCHEDULE),
    AcademicEvent("Issuance of Library books Preparation of Students detail & Lesson Plans in Teacher Diaries and Interaction with students by respective Teachers to discuss Course Outcomes. ", LocalDate.of(2025, 1, 2), EventType.SCHEDULE),
    AcademicEvent("Semester Enrolment", LocalDate.of(2025, 2, 4), EventType.SCHEDULE),
    AcademicEvent("Semester Enrolment", LocalDate.of(2025, 2, 5), EventType.SCHEDULE),
    AcademicEvent("Semester Enrolment", LocalDate.of(2025, 2, 6), EventType.SCHEDULE),
    AcademicEvent("Semester Enrolment", LocalDate.of(2025, 2, 7), EventType.SCHEDULE),
    AcademicEvent("National Science Day", LocalDate.of(2025, 2, 28), EventType.EVENT),
    AcademicEvent("1st Class Test", LocalDate.of(2025, 2, 1), EventType.CLASS_TEST),
    AcademicEvent("1st Class Test", LocalDate.of(2025, 2, 3), EventType.CLASS_TEST),
    AcademicEvent("1st Class Test", LocalDate.of(2025, 2, 4), EventType.CLASS_TEST),
    AcademicEvent("1st Class Test", LocalDate.of(2025, 2, 5), EventType.CLASS_TEST),
    AcademicEvent("1st Class Test", LocalDate.of(2025, 2, 6), EventType.CLASS_TEST),
    AcademicEvent("1st Class Test", LocalDate.of(2025, 2, 7), EventType.CLASS_TEST),
    AcademicEvent("Display of 1st Class Test marks and identification of weak students for extra classes", LocalDate.of(2025, 2, 7), EventType.CLASS_TEST),
    AcademicEvent("1st Class Test", LocalDate.of(2025, 2, 8), EventType.CLASS_TEST),
    AcademicEvent("Display of 1st Class Test marks and identification of weak students for extra classes", LocalDate.of(2025, 2, 8), EventType.CLASS_TEST),
    AcademicEvent("Display of 1st Class Test marks and identification of weak students for extra classes", LocalDate.of(2025, 2, 10), EventType.CLASS_TEST),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students", LocalDate.of(2025, 2, 10), EventType.EVENT),
    AcademicEvent("Display of 1st Class Test marks and identification of weak students for extra classes", LocalDate.of(2025, 2, 11), EventType.CLASS_TEST),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students", LocalDate.of(2025, 2, 11), EventType.EVENT),
    AcademicEvent("Display of 1st Class Test marks and identification of weak students for extra classes", LocalDate.of(2025, 2, 12), EventType.CLASS_TEST),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students", LocalDate.of(2025, 2, 12), EventType.EVENT),
    AcademicEvent("Display of 1st Class Test marks and identification of weak students for extra classes", LocalDate.of(2025, 2, 13), EventType.CLASS_TEST),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students", LocalDate.of(2025, 2, 13), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students", LocalDate.of(2025, 2, 14), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students", LocalDate.of(2025, 2, 15), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students", LocalDate.of(2025, 2, 17), EventType.EVENT),
    AcademicEvent("2nd Class Test", LocalDate.of(2025, 3, 1), EventType.CLASS_TEST),
    AcademicEvent("2nd Class Test", LocalDate.of(2025, 3, 3), EventType.CLASS_TEST),
    AcademicEvent("2nd Class Test", LocalDate.of(2025, 3, 4), EventType.CLASS_TEST),
    AcademicEvent("2nd Class Test", LocalDate.of(2025, 3, 5), EventType.CLASS_TEST),
    AcademicEvent("2nd Class Test", LocalDate.of(2025, 3, 6), EventType.CLASS_TEST),
    AcademicEvent("2nd Class Test", LocalDate.of(2025, 3, 7), EventType.CLASS_TEST),
    AcademicEvent("2nd Class Test", LocalDate.of(2025, 3, 8), EventType.CLASS_TEST),
    AcademicEvent("Display of 2nd Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 3, 6), EventType.CLASS_TEST),
    AcademicEvent("Display of 2nd Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 3, 7), EventType.CLASS_TEST),
    AcademicEvent("Display of 2nd Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 3, 8), EventType.CLASS_TEST),
    AcademicEvent("Display of 2nd Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 3, 10), EventType.CLASS_TEST),
    AcademicEvent("Display of 2nd Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 3, 11), EventType.CLASS_TEST),
    AcademicEvent("Display of 2nd Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 3, 12), EventType.CLASS_TEST),
    AcademicEvent("Display of 2nd Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 3, 17), EventType.CLASS_TEST),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students.", LocalDate.of(2025, 3, 10), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students.", LocalDate.of(2025, 3, 11), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students.", LocalDate.of(2025, 3, 12), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students.", LocalDate.of(2025, 3, 17), EventType.EVENT),
    AcademicEvent("National Science Day", LocalDate.of(2025, 2, 28), EventType.EVENT),
    AcademicEvent("Holi", LocalDate.of(2025, 3, 13), EventType.HOLIDAY),
    AcademicEvent("Holi", LocalDate.of(2025, 3, 14), EventType.HOLIDAY),
    AcademicEvent("Holi", LocalDate.of(2025, 3, 15), EventType.HOLIDAY),
    AcademicEvent("3rd Class Test", LocalDate.of(2025, 3, 29), EventType.CLASS_TEST),
    AcademicEvent("3rd Class Test", LocalDate.of(2025, 3, 31), EventType.CLASS_TEST),
    AcademicEvent("3rd Class Test", LocalDate.of(2025, 4, 1), EventType.CLASS_TEST),
    AcademicEvent("3rd Class Test", LocalDate.of(2025, 4, 3), EventType.CLASS_TEST),
    AcademicEvent("3rd Class Test", LocalDate.of(2025, 4, 4), EventType.CLASS_TEST),
    AcademicEvent("3rd Class Test", LocalDate.of(2025, 4, 5), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 3, 31), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 1), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 3), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 4), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 5), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 6), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 7), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 8), EventType.CLASS_TEST),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 1), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 3), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 4), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 5), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 6), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 7), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 8), EventType.EVENT),


    AcademicEvent("4rd Class Test", LocalDate.of(2025, 4, 19), EventType.CLASS_TEST),
    AcademicEvent("4rd Class Test", LocalDate.of(2025, 4, 21), EventType.CLASS_TEST),
    AcademicEvent("4rd Class Test", LocalDate.of(2025, 4, 22), EventType.CLASS_TEST),
    AcademicEvent("4rd Class Test", LocalDate.of(2025, 4, 23), EventType.CLASS_TEST),
    AcademicEvent("4rd Class Test", LocalDate.of(2025, 4, 24), EventType.CLASS_TEST),
    AcademicEvent("4rd Class Test", LocalDate.of(2025, 4, 25), EventType.CLASS_TEST),
    AcademicEvent("4rd Class Test", LocalDate.of(2025, 4, 26), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 21), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 22), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 23), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 24), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 25), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 26), EventType.CLASS_TEST),
    AcademicEvent("Display of Class Test marks and identification of weak students for extra classes.", LocalDate.of(2025, 4, 28), EventType.CLASS_TEST),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 25), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 26), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 28), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 29), EventType.EVENT),
    AcademicEvent("Submission of Concrete Plan by Concerned HOD’s to the Dean for conducting remedial classes for weak students ", LocalDate.of(2025, 4, 30), EventType.EVENT),
    AcademicEvent("Summer Vacation Start", LocalDate.of(2025, 6, 1), EventType.VACATION),
    AcademicEvent("Mid-Semester Exam", LocalDate.of(2025, 4, 10), EventType.EXAM),
    AcademicEvent("Independence Day", LocalDate.of(2025, 8, 15), EventType.HOLIDAY),
    AcademicEvent("Teacher's Day", LocalDate.of(2025, 9, 5), EventType.EVENT),
    AcademicEvent("Diwali", LocalDate.of(2025, 10, 20), EventType.HOLIDAY),
    AcademicEvent("Final Exam", LocalDate.of(2025, 11, 15), EventType.EXAM),
    AcademicEvent("Winter Vacation Start", LocalDate.of(2025, 12, 20), EventType.VACATION),
    AcademicEvent("Christmas", LocalDate.of(2025, 12, 25), EventType.HOLIDAY)
) + generateSundayHolidays(2025)

class MainActivity : ComponentActivity() {
    private val CHANNEL_ID = "academic_channel"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        scheduleDailyNotificationCheck()

        setContent {
            AcademicCalenderTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AcademicCalendarScreen(events = eventsList)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Academic Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Calendar event reminders"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun scheduleDailyNotificationCheck() {
        val request = PeriodicWorkRequestBuilder<NotificationWorker>(
            24, TimeUnit.HOURS
        ).build()
        WorkManager.getInstance(this).enqueue(request)
    }
}

@Composable
fun AcademicCalenderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AcademicCalendarScreen(events: List<AcademicEvent>) {
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var showMonthSelector by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val currentDate = LocalDate.now()
    val yearMonth = YearMonth.of(2025, selectedMonth)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDay = yearMonth.atDay(1).dayOfWeek.value % 7

    Column(modifier = Modifier.padding(16.dp)) {
        // Header with Current Date
        Text(
            text = "Today: ${currentDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Month Selector
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Academic Calendar 2025",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = months[selectedMonth - 1],
                fontSize = 18.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            IconButton(onClick = { showMonthSelector = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Month")
            }

            DropdownMenu(
                expanded = showMonthSelector,
                onDismissRequest = { showMonthSelector = false }
            ) {
                months.forEachIndexed { index, month ->
                    DropdownMenuItem(
                        text = { Text(month) },
                        onClick = {
                            selectedMonth = index + 1
                            showMonthSelector = false
                            selectedDate = null // Reset selected date when month changes
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days Header
        Row {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp)
        ) {
            items(42) { index ->
                val day = index - firstDay + 1
                val date = if (day in 1..daysInMonth) yearMonth.atDay(day) else null
                val dayEvents = date?.let { d -> events.filter { it.date == d } } ?: emptyList()
                val isCurrentDate = date == currentDate
                val isSelectedDate = date == selectedDate

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .border(0.5.dp, if (isSelectedDate) Color.Black else Color.LightGray)
                        .background(
                            when {
                                isCurrentDate -> Color(0xFF80CBC4) // Teal for current date
                                else -> getEventColor(dayEvents)
                            }
                        )
                        .clickable(enabled = date != null) {
                            selectedDate = date
                        }
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        Text(
                            text = date?.dayOfMonth?.toString() ?: "",
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.End),
                            color = if (isCurrentDate) Color.White else Color.Black
                        )
                        dayEvents.take(1).forEach { event ->
                            Text(
                                text = event.type.name.take(3),
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Events List
        Text(
            text = if (selectedDate != null)
                "Events on ${selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}"
            else
                "Events in ${months[selectedMonth - 1]}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val filteredEvents = if (selectedDate != null) {
            events.filter { it.date == selectedDate }
        } else {
            events.filter { it.date.monthValue == selectedMonth }
                .sortedBy { it.date.dayOfMonth }
        }

        if (filteredEvents.isEmpty()) {
            Text(
                text = "No events for this ${if (selectedDate != null) "date" else "month"}",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredEvents) { event ->
                    EventItem(event = event)
                }
            }
        }
    }
}

@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.DropdownMenuItem(
        text = text,
        onClick = onClick,
        modifier = modifier
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventItem(event: AcademicEvent) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getEventColor(listOf(event))
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = event.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = event.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = event.type.name,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

fun getEventColor(events: List<AcademicEvent>): Color {
    return when {
        events.any { it.type == EventType.HOLIDAY } -> Color(0xFFFFCDD2) // Red
        events.any { it.type == EventType.EVENT } -> Color(0xFFC8E6C9) // Green
        events.any { it.type == EventType.CLASS_TEST } -> Color(0xFFBBDEFB) // Blue
        events.any { it.type == EventType.VACATION } -> Color(0xFFFFF9C4) // Yellow
        events.any { it.type == EventType.SCHEDULE } -> Color(0xFFE1BEE7) // Purple
        events.any { it.type == EventType.EXAM } -> Color(0xFFFFB300) // Amber
        else -> Color.White
    }
}

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val tomorrow = LocalDate.now().plusDays(1)
        val upcomingEvents = eventsList.filter { it.date == tomorrow }

        if (upcomingEvents.isNotEmpty()) {
            upcomingEvents.forEach { event ->
                sendNotification(event)
            }
        }
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun sendNotification(event: AcademicEvent) {
        val builder = NotificationCompat.Builder(applicationContext, "academic_channel")
            .setContentTitle("Upcoming Event: ${event.name}")
            .setContentText("${event.type} on ${event.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(event.hashCode(), builder.build())
        }
    }
}