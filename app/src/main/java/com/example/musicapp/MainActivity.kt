package com.example.musicapp

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ticket.view.*

class MainActivity : AppCompatActivity() {
    var sermonlist = ArrayList<songinfo>()
    var adapter: songadapter? = null

    var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //function call
        //next line isnt a comment
        // getsermons()

        //calling a function to get sermons from the phone memory
        CheckUserPermsions()

        //this is to display songs
        adapter = songadapter(sermonlist)
        //list is the id from activity main
        list.adapter = adapter

        //this loads an online url
        var sermontrack = track()
        sermontrack.start()
    }

    /*
    fun getsermons() {
        sermonlist.add(songinfo("Constrained By Love", "Apostle Grace Lubega", "https://m.facebook.com/story.php?story_fbid=506483953432811&id=899416403409055&anchor_composer=false"))
        sermonlist.add(songinfo("Love, The Experience", "Apostle Grace Lubega", "www.ap.o"))
        sermonlist.add(songinfo("Agape The God Of Love", "Apostle Grace Lubega", "www.ap.s"))
        sermonlist.add(songinfo("Agape and Phileo", "Apostle Grace Lubega", "www.ap.t"))
        sermonlist.add(songinfo("Righteousness By Faith", "Apostle Grace Lubega", "www.ap.t"))
        sermonlist.add(songinfo("Called Alone", "Apostle Grace Lubega", "www.ap.l"))
    }
    */

    inner class songadapter : BaseAdapter {
        var songlist = ArrayList<songinfo>()

        constructor(songlist: ArrayList<songinfo>) : super() {
            this.songlist = songlist

        }

        override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
            val myview = layoutInflater.inflate(R.layout.ticket, null)
            val sermonview = this.songlist[position]
            myview.sermon.setText(sermonview.sermon)
            myview.teacher.setText(sermonview.teacher)
            // myview.sermon.setText(sermonview.sermonurl)

            myview.play.setOnClickListener(View.OnClickListener {
                //TODO():play

                if (myview.play.text.equals("Stop")) {
                    mp!!.stop()
                    myview.play.text = "Start"
                } else {
                    mp = MediaPlayer()
                    try {
                        mp!!.setDataSource(sermonview.sermonurl)
                        mp!!.prepare()
                        mp!!.start()

                        myview.play.text = "Stop"
                        seek.max = mp!!.duration

                    } catch (ex: Exception) {
                    }

                }
            })

            return myview;

        }

        override fun getItem(song: Int): Any {
            return this.songlist[song]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return this.songlist.size

        }

    }


    inner class track() : Thread() {

        override fun run() {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (ex: Exception) {
                }

                runOnUiThread {
                    if (mp != null) {
                        seek.progress = mp!!.currentPosition

                    }
                }
            }
        }
    }

    //phone momory function call
    fun CheckUserPermsions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_ASK_PERMISSIONS)
                return
            }
        }

        LoadSong()

    }

    //get acces to location permsion
    private val REQUEST_CODE_ASK_PERMISSIONS = 123


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LoadSong()
            } else {
                // Permission Denied
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun   LoadSong() {
        val allSongsURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = contentResolver.query(allSongsURI, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                do {

                    val songURL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val SongAuthor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val SongName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    sermonlist.add(songinfo(SongName, SongAuthor, songURL))
                } while (cursor.moveToNext())


            }
            cursor.close()

            adapter=songadapter(sermonlist)
            list.adapter=adapter
        }
    }

}
