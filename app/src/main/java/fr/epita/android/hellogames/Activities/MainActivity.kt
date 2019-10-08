package fr.epita.android.hellogames.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
// import fr.epita.android.hellogames.R
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import fr.epita.android.hellogames.Models.GameObject
import fr.epita.android.hellogames.R
import fr.epita.android.hellogames.Interface.WSInterface


class MainActivity : AppCompatActivity() {

    var gameList: MutableList<GameObject> = arrayListOf()
    var gameIndexes: Array<Int> = arrayOf(0, 1, 3, 4)
    var baseUrl = "https://androidlessonsapi.herokuapp.com/api/"
    var initialized = false

    private fun goToDetails(gameId: Int) {
        val explicitIntent = Intent(this, GameDetailActivity::class.java)
        explicitIntent.putExtra("GAME_ID", gameId)
        explicitIntent.putExtra("GAME_BASE_URL", this.baseUrl)

        startActivity(explicitIntent)
    }

    private fun goToListView() {
        if (this.initialized) {
            val explicitIntent = Intent(this, ListViewActivity::class.java)

            explicitIntent.putExtra("GAME_BASE_URL", this.baseUrl)

            startActivity(explicitIntent)
        }
    }


    private fun initImageGamesPreview() {
        for (i in 0..3) {
            val id = resources.getIdentifier("game_$i", "id", packageName)
            val game : ImageView = findViewById(id)


            Glide
                .with(this)
                .load(this.gameList[this.gameIndexes[i]].picture)
                .error(resources.getDrawable(R.drawable.error_loading))
                .into(game)
        }
    }

    private fun initRandomGameIndexes() {
        for (i in 0..3) {
            var r = Random().nextInt(this.gameList.size)
            while (r in this.gameIndexes) {
                r = Random().nextInt(this.gameList.size)
            }

            this.gameIndexes[i] = r
        }

        Log.d("INIT", this.gameIndexes.toString())
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.changeToListView) {
            this.goToListView()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(this.baseUrl)
            .addConverterFactory(jsonConverter)
            .build()

        val service: WSInterface = retrofit.create(WSInterface::class.java)

        val callback = object : Callback<MutableList<GameObject>> {
            override fun onFailure(call: Call<MutableList<GameObject>>, t: Throwable) {
                Toast.makeText(applicationContext, "network error", Toast.LENGTH_LONG).show()
                Log.d("HTTP_ERROR", "Failed to list the games")
            }

            override fun onResponse(
                call: Call<MutableList<GameObject>>,
                response: Response<MutableList<GameObject>>
            ) {
                val rCode = response.code()
                if (rCode == 200) {
                    if (response.body() != null) {
                        gameList = response.body()!!

                        initRandomGameIndexes()

                        initialized = true
                        Log.d("HTTP_SUCCESS", "retrieve game list")
                        Toast.makeText(applicationContext, "game list retrieved", Toast.LENGTH_LONG).show()

                        initImageGamesPreview()
                    } else {
                        Log.d("HTTP_ERROR", "empty response")
                    }
                } else {
                    Log.d("HTTP_ERROR", "bad response code received: $rCode")
                }
            }
        }

        service.listGames().enqueue(callback)

        game_0.setOnClickListener {
            if (this.initialized) {
                val i = this.gameIndexes[0]
                if (i < this.gameList.size) {
                    val game: GameObject = this.gameList[i]
                    this.goToDetails(game.id)
                }
            }
        }

        game_1.setOnClickListener {
            if (this.initialized) {
                val i = this.gameIndexes[1]
                if (i < this.gameList.size) {
                    val game: GameObject = this.gameList[i]
                    this.goToDetails(game.id)
                }
            }
        }

        game_2.setOnClickListener {
            if (this.initialized) {
                val i = this.gameIndexes[2]
                if (i < this.gameList.size) {
                    val game: GameObject = this.gameList[i]
                    this.goToDetails(game.id)
                }
            }
        }

        game_3.setOnClickListener {
            if (this.initialized) {
                val i = this.gameIndexes[3]
                if (i < this.gameList.size) {
                    val game: GameObject = this.gameList[i]
                    this.goToDetails(game.id)
                }
            }
        }
    }
}
