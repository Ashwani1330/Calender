package com.example.Calender

import android.icu.util.Calendar
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.PopupWindow
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_layouts.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var popupWindow: PopupWindow
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var eventsRecyclerView: RecyclerView
    private val events = mutableListOf<String>()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        updateToolbarTitle(Calendar.getInstance().timeInMillis)

        val calendarView: CalendarView = findViewById(R.id.calendarView)

        calendarView.setOnDateChangeListener { _, year, month, _ ->
            val cal = Calendar.getInstance()
            cal.set(year, month, 1)
            updateToolbarTitle(cal.timeInMillis)
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showEventCreationBox()
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView)
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter(events)
        eventsRecyclerView.adapter = eventAdapter
    }

    private fun addEventToSidebar(title: String) {
        events.add(title)
        eventAdapter.notifyItemInserted(events.size - 1)
    }

    private fun updateToolbarTitle(timeInMillis: Long) {
        val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        supportActionBar?.title = dateFormat.format(timeInMillis)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_today -> {
                val calendarView: CalendarView = findViewById(R.id.calendarView)
                val currentDate = Calendar.getInstance().timeInMillis
                calendarView.date = currentDate
                updateToolbarTitle(currentDate)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showEventCreationBox() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.event_creation_box, null)

        popupWindow = PopupWindow(popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true)

        popupWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0)

        val saveButton: Button = popupView.findViewById(R.id.saveEventButton)
        val eventTitle: EditText = popupView.findViewById(R.id.eventTitle)
        val eventDatePicker: DatePicker = popupView.findViewById(R.id.eventDatePicker)

        saveButton.setOnClickListener {
            val title = eventTitle.text.toString()
            val day = eventDatePicker.dayOfMonth
            val month = eventDatePicker.month
            val year = eventDatePicker.year
            val eventDate = "$day/${month + 1}/$year"
            addEventToSidebar("$title - $eventDate")
            popupWindow.dismiss()
        }
    }
}