package com.darkfetch.browser

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listView = ListView(this)
        setContentView(listView)

        val prefs = getSharedPreferences("history", MODE_PRIVATE)
        val items = prefs.all.values.map { it.toString() }

        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            items
        )
    }
}
