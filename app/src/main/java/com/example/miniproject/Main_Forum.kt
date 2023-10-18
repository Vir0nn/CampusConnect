package com.example.miniproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.ProgressBar
import android.widget.LinearLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu

class Main_Forum : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_forum)

        val moreImageView: ImageView = findViewById(R.id.More)

        moreImageView.setOnClickListener{  view ->
            showPopupMenu(view)
        }
        auth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        database =
            FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("messages")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.menu_item -> {

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    private fun showPopupMenu(view: View?) {

    }
}

