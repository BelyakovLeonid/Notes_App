package com.example.lab.noteapp.View

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.lifecycle.ViewModelProviders
import com.example.lab.noteapp.Model.Note
import com.example.lab.noteapp.R
import com.example.lab.noteapp.ViewModel.NoteViewModel
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddActivity: AppCompatActivity()  {

    private val TAKE_PHOTO_REQUEST = 1
    private val TAKE_GALLERY_REQUEST = 2
    private var uri: Uri? = null
    private var colorRes: Int = R.color.color1
    private var noteId: Int = -1
    private lateinit var noteViewModel: NoteViewModel
    private val CHANNEL_ID = "MyFirstChannel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar)

        noteId = intent.getIntExtra("noteId", -1)

        if(noteId != -1){
            noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
            noteViewModel.getNoteById(noteId).observe(this, androidx.lifecycle.Observer{
                if (it != null)
                    refreshUI(it)
            })
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            var res: Int = 0
            Log.d("MyTag", "$checkedId ____ ${group[1].id}")
            when(checkedId){
                group[0].id -> res = R.color.color1
                group[1].id -> res = R.color.color2
                group[2].id -> res = R.color.color3
                group[3].id -> res = R.color.color4
                group[4].id -> res = R.color.color5
            }

            layout.setBackgroundResource(res)
            radioGroup.visibility = View.INVISIBLE
            colorRes = res
        }

        button.setOnClickListener {
            //если ничего не ввели, то создать заметку невозможно
            if(titleView.text.isEmpty() && textView.text.isEmpty() && uri == null){
                Toast.makeText(this, "Вы ничего не ввели", Toast.LENGTH_SHORT).show()
            }else {
                val resIntent = Intent()
                with(resIntent) {
                    putExtra("title", titleView.text.toString())
                    putExtra("text", textView.text.toString())
                    putExtra("image", uri?.toString())
                    putExtra("color", colorRes)
                    if (noteId != -1) putExtra("noteId", noteId)
                }
                setResult(Activity.RESULT_OK, resIntent)
                finish()
            }
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Создание NotificationChannel требуется только для API 26+
        // потому что класс NotificationChannel новый и находится не в support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NAME"
            val descriptionText = "DESCRIPTION"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun refreshUI(note: Note){
        uri = note.image?.toUri()
        colorRes = note.colorRes

        when(colorRes){
            R.color.color2 -> button2.isChecked = true
            R.color.color3 -> button3.isChecked = true
            R.color.color4 -> button4.isChecked = true
            R.color.color5 -> button5.isChecked = true
            else -> button1.isChecked = true
        }

        layout.setBackgroundResource(colorRes)
        titleView.setText(note.title)
        textView.setText(note.text)
        image.setImageURI(uri)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Создаем уникальное имя для файла
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* префикс */
            ".jpg", /* суффикс */
            storageDir /* расположение */
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
         menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         return when (item.itemId) {
            //назад
            android.R.id.home->{
                finish()
                true
            }

            //уведомления
            R.id.action_notification ->{

                //создаем интент, который вызовется при нажатии на уведомление
                val notificationIntent = Intent(this, DetailActivity::class.java)
                notificationIntent.putExtra("noteId", noteId)

                //создаем "оболочку" для нашего интента
                val pendingIntent2 = PendingIntent.getActivity(this, 0 , notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT)

                val pendingIntent = TaskStackBuilder.create(this).run{
                    addNextIntentWithParentStack(notificationIntent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                val time = (Date().time - 1000000L)
                //создаем уведомление
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                                                            .setSmallIcon(R.drawable.ic_notifications)
                                                            .setContentTitle("MyFirstNotification")
                                                            .setContentText("ReallyFirst")
                                                            .setWhen(time)
                                                            .setShowWhen(true)
                                                            .setContentIntent(pendingIntent)
                                                            .setAutoCancel(true)
                                                            .build()

                NotificationManagerCompat.from(this).notify(1, notification)

                true
            }

            //фото с камеры
            R.id.action_photo -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{ picIntent->
                    picIntent.resolveActivity(packageManager)?.also{
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            null
                        }
                        // Код ниже запустится лишь когда photoFile != null
                        photoFile?.also {
                            uri = FileProvider.getUriForFile(
                                this,
                                "com.example.lab.noteapp",
                                 it
                            )
                            picIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                            startActivityForResult(picIntent, TAKE_PHOTO_REQUEST)
                        }
                    }
                }
                true
            }

            //фото из галереи
            R.id.action_image ->{

                val picIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                picIntent.addCategory(Intent.CATEGORY_OPENABLE)
                picIntent.setType("image/*")
                startActivityForResult(picIntent, TAKE_GALLERY_REQUEST)

                true
            }

            //палитра
            R.id.action_color ->{
                if(radioGroup.visibility == View.VISIBLE)
                    radioGroup.visibility = View.INVISIBLE
                else
                    radioGroup.visibility = View.VISIBLE
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            image.setImageURI(uri)
        }

        if (requestCode == TAKE_GALLERY_REQUEST && resultCode == RESULT_OK) {
            val temporalURI = data?.data
            val bitmap = getBitmap(this.contentResolver, temporalURI)

            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                uri = FileProvider.getUriForFile(
                    this,
                    "com.example.lab.noteapp",
                    it
                )
                val fOut = FileOutputStream(it)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
                fOut.close()
            }
            image.setImageURI(uri)
        }
    }

}