package fr.epita.android.hellogames.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import fr.epita.android.hellogames.Adapters.GameListAdapter
import fr.epita.android.hellogames.Interface.WSInterface
import fr.epita.android.hellogames.Models.GameObject
import fr.epita.android.hellogames.R
import kotlinx.android.synthetic.main.activity_list_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListViewActivity : AppCompatActivity() {


    private var baseUrl: String = ""

    private fun goToDetails(gameId: Int) {
        val explicitIntent = Intent(this, GameDetailActivity::class.java)
        explicitIntent.putExtra("GAME_ID", gameId)
        explicitIntent.putExtra("GAME_BASE_URL", this.baseUrl)

        startActivity(explicitIntent)
    }

    private fun initListView(gameListObject: MutableList<GameObject>) {

        val itemClickListener = View.OnClickListener {
            val gameId = it.tag as Int

            this.goToDetails(gameId)
        }

        gameList.addItemDecoration(DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL))

        gameList.setHasFixedSize(true)
        gameList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        gameList.adapter = GameListAdapter(this, gameListObject, itemClickListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mymenu_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.changeToGrid) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        val originIntent = intent
        this.baseUrl = originIntent.getStringExtra("GAME_BASE_URL")

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
                        val gameListObject = response.body()!!

                        initListView(gameListObject)

                        Log.d("HTTP_SUCCESS", "retrieve game list")
                    } else {
                        Log.d("HTTP_ERROR", "empty response")
                    }
                } else {
                    Log.d("HTTP_ERROR", "bad response code received: $rCode")
                }
            }
        }

        service.listGames().enqueue(callback)
    }
}
