package com.example.miniproject

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Main_Forum : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_forum)

        val moreImageView: ImageView = findViewById(R.id.More)
        val notify: ImageView = findViewById(R.id.imageButton7)
        val search: ImageView = findViewById(R.id.search)
        val book: ImageView = findViewById(R.id.books)

        moreImageView.setOnClickListener { view ->
            showPopupMenu(view)
        }

        notify.setOnClickListener {
            intent = Intent(this, calendarView::class.java)
            startActivity(intent)
        }

        book.setOnClickListener {
            intent = Intent(this, Book::class.java)
            startActivity(intent)
        }
    }

    private fun showPopupMenu(view: View?) {
        auth = FirebaseAuth.getInstance()
        Log.d("Debug", "More ImageView clicked")
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_item, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item -> {
                    if (auth.currentUser != null) {
                        auth.signOut()
                        val out = Intent(this, MainActivity::class.java)
                        startActivity(out)
                    }
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}

data class Message(
    val userId: String? = null,
    val displayName: String? = null,
    val messageText: String? = null
)

class MessagesAdapter(private val context: Context, private val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val userName: TextView = itemView.findViewById(R.id.user_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.userName.text = message.displayName
        holder.messageText.text = message.messageText
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun add(message: Message?) {
        if (message != null) {
            messages.add(message)
            notifyDataSetChanged()
        }
    }
}

class GroupChatActivity : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_forum)

        messageRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.message_input)
        sendButton = findViewById(R.id.send_button)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("messages")

        messagesAdapter = MessagesAdapter(this, mutableListOf())
        messageRecyclerView.adapter = messagesAdapter

        sendButton.setOnClickListener {
            sendMessage()
        }

        // Listen for changes in the database and update the UI accordingly
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                messagesAdapter.add(message)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()
        if (TextUtils.isEmpty(messageText)) {
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("SendMessage", "Current user is null")
            return
        }

        val userId = currentUser.uid

        val message = Message(userId, currentUser.displayName, messageText)
        val messageKey = databaseReference.push().key

        if (messageKey != null) {
            databaseReference.child(messageKey).setValue(message)
                .addOnSuccessListener {
                    Log.d("SendMessage", "Message sent successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("SendMessage", "Error sending message", e)
                }
        } else {
            Log.e("SendMessage", "Message key is null")
        }

        // Clear the message input field
        messageEditText.text.clear()
    }

}
