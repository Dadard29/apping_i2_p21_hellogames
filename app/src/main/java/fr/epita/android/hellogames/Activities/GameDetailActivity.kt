package fr.epita.android.hellogames.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import fr.epita.android.hellogames.Models.GameDetailObject
import fr.epita.android.hellogames.R
import fr.epita.android.hellogames.Interface.WSInterface
import kotlinx.android.synthetic.main.activity_game_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameDetailActivity : AppCompatActivity() {

    var baseUrl = ""
    var gameDetail : GameDetailObject =
        GameDetailObject(
            0, "", "", 0,
            0, "", "", "", ""
        )


    private fun initGameDetailUI() {
        game_name.text = this.gameDetail.name
        game_type.text = this.gameDetail.type
        game_nbplayers.text = this.gameDetail.players.toString()
        game_year.text = this.gameDetail.year.toString()
        game_description.text = this.gameDetail.description_en

        Glide
            .with(this)
            .load(this.gameDetail.picture)
            .error(resources.getDrawable(R.drawable.error_loading))
            .into(game_picture)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        // retrieve the intent infos
        val originIntent = intent
        this.baseUrl = originIntent.getStringExtra("GAME_BASE_URL")
        val gameId = originIntent.getIntExtra("GAME_ID", 0)


        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(this.baseUrl)
            .addConverterFactory(jsonConverter)
            .build()

        val service: WSInterface = retrofit.create(WSInterface::class.java)

        val callback = object : Callback<GameDetailObject> {
            override fun onFailure(call: Call<GameDetailObject>, t: Throwable) {
                Log.d("HTTP_ERROR", "Failed to retrieve game detail")
            }

            override fun onResponse(
                call: Call<GameDetailObject>,
                response: Response<GameDetailObject>
            ) {
                val rCode = response.code()
                if (rCode == 200) {
                    if (response.body() != null) {
                        gameDetail = response.body()!!
                        initGameDetailUI()
                        Log.d("HTTP_SUCCESS", "retrieve game details")
                    } else {
                        Log.d("HTTP_ERROR", "empty response")
                    }
                } else {
                    Log.d("HTTP_ERROR", "bad response code received: $rCode")
                }
            }

        }

        service.getGameDetail(gameId).enqueue(callback)

        game_more_button.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(this.gameDetail.url)
            startActivity(openURL)
        }
    }
}
