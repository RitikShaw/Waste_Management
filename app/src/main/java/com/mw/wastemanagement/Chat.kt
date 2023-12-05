package com.mw.wastemanagement

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mw.utills.SharedPrefs
import java.text.SimpleDateFormat
import java.util.Date

class Chat : AppCompatActivity() {
    private val messageList = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var sendButton: Button
    lateinit var messageEditText: EditText
    lateinit var back: ImageView
    lateinit var dialog : ProgressDialog
    lateinit var prefs: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.hide()

        dialog = ProgressDialog(this)
        dialog.setTitle("Please Wait...")
        dialog.setCancelable(true)

        prefs = SharedPrefs(this)

        recyclerView = findViewById(R.id.recyclerViewmsg)
        sendButton = findViewById(R.id.sendButton)
        messageEditText = findViewById(R.id.messageEditText)
        back = findViewById(R.id.back)

        back.setOnClickListener {
            navigateBack()
        }

        messageAdapter = MessageAdapter(messageList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = messageAdapter

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            val dateFormat =
                SimpleDateFormat("yyyy-MM-dd") // You can change the format as per your requirement
            val currentDate = Date()
            val date = dateFormat.format(currentDate)

            val timeFormat =
                SimpleDateFormat("HH:mm:ss") // You can change the format as per your requirement
            val currentTime = Date()
            val time = timeFormat.format(currentTime)

            if (messageText.isNotEmpty()) {
                val isSender = true // Assume the user is the sender
                val message = Message(messageText, isSender, date, time)
                messageList.add(message)
                messageAdapter.notifyItemInserted(messageList.size - 1)
                messageEditText.text.clear()
                recyclerView.smoothScrollToPosition(messageList.size - 1)
            }

        }

    }

    data class Message(val senttext: String, val isSender: Boolean, val sentdate: String, val sendTime: String)

    class MessageAdapter(private val messages: List<Message>) :
        RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

        class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val senderMessageLayout = itemView.findViewById<LinearLayout>(R.id.senderMessageLayout)
            val senderMessageTextView = itemView.findViewById<TextView>(R.id.senderMessageTextView)
            val senderdatetime = itemView.findViewById<TextView>(R.id.senderMessageDatetime)
            val senderdate = itemView.findViewById<TextView>(R.id.senderMessageDate)
            val receiverMessageLayout =
                itemView.findViewById<LinearLayout>(R.id.receiverMessageLayout)
            val receiverMessageTextView =
                itemView.findViewById<TextView>(R.id.receiverMessageTextView)
            val receiverdatetime = itemView.findViewById<TextView>(R.id.receiverMessageDatetime)
            val receiverdate = itemView.findViewById<TextView>(R.id.receiverMessagedate)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            return MessageViewHolder(view)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val message = messages[position]

            if (message.isSender) {
                holder.senderMessageLayout.visibility = View.VISIBLE
                holder.receiverMessageLayout.visibility = View.GONE
                holder.senderMessageTextView.text = message.senttext
                holder.senderdatetime.text = message.sentdate
                holder.senderdate.text = message.sendTime
//                val readtext = message.read
//                if(readtext=="yes"){
//                    if(message.senttext == message.readtext && message.sentdate == message.readdate){
//                        holder.senderread.visibility = View.VISIBLE
//                    }
//                    else{
//                        holder.senderread.visibility = View.GONE
//                    }
//                }
            } else {
                holder.senderMessageLayout.visibility = View.GONE
                holder.receiverMessageLayout.visibility = View.VISIBLE
                holder.receiverMessageTextView.text = message.senttext
                holder.receiverdatetime.text = message.sentdate
                holder.receiverdate.text = message.sendTime
            }
        }

        override fun getItemCount(): Int = messages.size

    }
    override fun onBackPressed() {
        navigateBack()
    }

    private fun navigateBack() {
        var intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.anim_slide_in_left,
            R.anim.anim_slide_in_right
        )
        finish()
    }
}