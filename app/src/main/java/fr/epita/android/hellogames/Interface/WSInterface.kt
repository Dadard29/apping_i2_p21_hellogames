package fr.epita.android.hellogames.Interface

import fr.epita.android.hellogames.Models.GameDetailObject
import fr.epita.android.hellogames.Models.GameObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WSInterface {

    @GET("game/list")
    fun listGames() : Call<MutableList<GameObject>>

    @GET("game/details")
    fun getGameDetail(@Query("game_id") gameId: Int) : Call<GameDetailObject>
}