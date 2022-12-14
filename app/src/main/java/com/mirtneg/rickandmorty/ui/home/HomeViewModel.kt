package com.mirtneg.rickandmorty.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mirtneg.rickandmorty.data.models.Character
import com.mirtneg.rickandmorty.data.models.CharacterResponse
import com.mirtneg.rickandmorty.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    val charactersList : MutableLiveData<List<Character>> = MutableLiveData<List<Character>>()
    val filterList : MutableLiveData<List<Character>> = MutableLiveData<List<Character>>()
    val repository = Repository()
    var showingSearchResult = false
        get() = field
        set(value) {
            field = value
        }


    fun getCharacters(){
        repository.apiService.getCharacters()
            .enqueue(object : Callback<CharacterResponse>{
                override fun onResponse(
                    call: Call<CharacterResponse>,
                    response: Response<CharacterResponse>
                ) {
                    charactersList.value = response.body()?.results
                }

                override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                    t.printStackTrace()
                }

            })
    }

    fun filterCharacters(
        species: String? = null,
        gender: String? = null,
        status: String? = null){
        var url = "https://rickandmortyapi.com/api/character/?"
            url +="species =$species"
            url +="&gender=$gender"
            url +="&status =$status"

        repository.apiService.filterCharacters(url)
            .enqueue(object : Callback<CharacterResponse>{
                override fun onResponse(
                    call: Call<CharacterResponse>,
                    response: Response<CharacterResponse>
                ) {
                    showingSearchResult = true
                    filterList.value = response.body()?.results
                }

                override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                    t.printStackTrace()
                }

            })
    }
}