package com.hanyeop.songforyou.view.main.home

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanyeop.songforyou.model.response.SongResponse
import com.hanyeop.songforyou.model.response.Weather
import com.hanyeop.songforyou.usecase.ib_recommend.GetIbRecommendMyListUseCase
import com.hanyeop.songforyou.usecase.ib_recommend.GetIbRecommendMyRecordUseCase
import com.hanyeop.songforyou.usecase.sb_recommend.GetAgeRecommendUseCase
import com.hanyeop.songforyou.usecase.sb_recommend.GetSbRecommendRandomUseCase
import com.hanyeop.songforyou.usecase.sb_recommend.GetSbRecommendUseCase
import com.hanyeop.songforyou.usecase.sb_recommend.GetWeatherRecommendUseCase
import com.hanyeop.songforyou.usecase.ub_recommend.GetUbRecommendMySoundUseCase
import com.hanyeop.songforyou.usecase.user_state.GetUserInfoUseCase
import com.hanyeop.songforyou.usecase.weather.GetWeatherUseCase
import com.hanyeop.songforyou.utils.BIRTHDAY
import com.hanyeop.songforyou.utils.NICKNAME
import com.hanyeop.songforyou.utils.ResultType
import com.hanyeop.songforyou.utils.SEQ
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel___"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getIbRecommendMyListUseCase: GetIbRecommendMyListUseCase,
    private val getIbRecommendMyRecordUseCase: GetIbRecommendMyRecordUseCase,
    private val getSbRecommendRandomUseCase: GetSbRecommendRandomUseCase,
    private val getSbRecommendUseCase: GetSbRecommendUseCase,
    private val getUbRecommendMySoundUseCase: GetUbRecommendMySoundUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val sharedPreferences: SharedPreferences,
    private val getWeatherRecommendUseCase: GetWeatherRecommendUseCase,
    private val getAgeRecommendUseCase: GetAgeRecommendUseCase
): ViewModel() {
    private val _userAge: MutableStateFlow<Int> = MutableStateFlow(0)
    val userAge get() = _userAge.asStateFlow()

    private val _recommendMyList: MutableStateFlow<List<SongResponse>> = MutableStateFlow(listOf())
    val recommendMyList get() = _recommendMyList.asStateFlow()

    private val _recommendMyRecord: MutableStateFlow<List<SongResponse>> = MutableStateFlow(listOf())
    val recommendMyRecord get() = _recommendMyRecord.asStateFlow()

    private val _recommendRandomList: MutableStateFlow<List<SongResponse>> = MutableStateFlow(listOf())
    val recommendRandomList get() = _recommendRandomList.asStateFlow()

    private val _weatherRecommendList: MutableStateFlow<List<SongResponse>> = MutableStateFlow(listOf())
    val weatherRecommendList get() = _weatherRecommendList.asStateFlow()

    private val _genreRecommendList: MutableStateFlow<List<SongResponse>> = MutableStateFlow(listOf())
    val genreRecommendList get() = _genreRecommendList.asStateFlow()

    private val _ageRecommendList: MutableStateFlow<List<SongResponse>> = MutableStateFlow(listOf())
    val ageRecommendList get() = _ageRecommendList.asStateFlow()

    private val _genderRecommendList: MutableStateFlow<List<SongResponse>> = MutableStateFlow(listOf())
    val genderRecommendList get() = _genderRecommendList.asStateFlow()

    private val _weatherResponse : MutableStateFlow<ResultType<Weather>> = MutableStateFlow(ResultType.Uninitialized)
    val weatherResponse get() = _weatherResponse.asStateFlow()

    private val _ubRecommendList: MutableStateFlow<List<SongResponse>> = MutableStateFlow(listOf())
    val ubRecommendList get() = _ubRecommendList.asStateFlow()



    fun getWeather(dataType : String, numOfRows : Int, pageNo : Int,
                   baseDate : String, baseTime : String, nx : Int, ny : Int){
        viewModelScope.launch(Dispatchers.IO) {
            getWeatherUseCase.execute(dataType, numOfRows, pageNo, baseDate, baseTime, nx, ny).collectLatest {
                Log.d("test5", "getWeather: $it")
                _weatherResponse.value = it
            }
        }
    }

    fun getIbRecommendMyList(){
        viewModelScope.launch(Dispatchers.IO) {
            getIbRecommendMyListUseCase.execute().collectLatest {
                if(it is ResultType.Success){
                    _recommendMyList.value = it.data.data
                }
            }
        }
    }


    fun getIbRecommendMyRecord(){
        viewModelScope.launch(Dispatchers.IO) {
            getIbRecommendMyRecordUseCase.execute(7).collectLatest {
                Log.d("test5", "getIbRecommendMyRecord: $it")
                if(it is ResultType.Success){
                    _recommendMyRecord.value = it.data.data
                }
            }
        }
    }

    fun getWeatherRecommendList(weather: Int){
        viewModelScope.launch(Dispatchers.IO) {
            getWeatherRecommendUseCase.execute(weather).collectLatest {
                Log.d("test5", "getWeatherRecommendList: $it")
                if(it is ResultType.Success){
                    _weatherRecommendList.value = it.data.data
                }
            }
        }
    }

    fun getAgeRecommendList(age: Int){
        viewModelScope.launch(Dispatchers.IO) {
            getAgeRecommendUseCase.execute(age).collectLatest {
                Log.d("test5", "getAgeRecommendList: $it")
                if(it is ResultType.Success){
                    _ageRecommendList.value = it.data.data
                }
            }
        }
    }

//    fun getGenreRecommendList(genre: String, age: Int, gender: String, weather: Int){
//        viewModelScope.launch(Dispatchers.IO) {
//            getSbRecommendUseCase.execute(genre, age, gender, weather).collectLatest {
//                Log.d("test5", "getSbRecommendList: $it")
//                if(it is ResultType.Success){
//                    it.data.data.filter {
//                        it.SongGenre.startsWith(genre)
//                    }
//                    Log.d("test5", "getSbRecommendList_filter: $it")
//                    _genreRecommendList.value = it.data.data
//                }
//            }
//        }
//    }
    fun getSbRecommendRandomList(){
        viewModelScope.launch(Dispatchers.IO) {
            getSbRecommendRandomUseCase.execute().collectLatest {
                Log.d(TAG, "getSbRecommendRandomList:$it")
                if(it is ResultType.Success){
                    _recommendRandomList.value = it.data.data
                    Log.d(TAG, _recommendRandomList.toString())
                }else{
                    Log.d(TAG,"else")
                }
            }
        }
    }

    fun getUbRecommendList(){
        viewModelScope.launch(Dispatchers.IO) {
            getUbRecommendMySoundUseCase.execute().collectLatest {
                if(it is ResultType.Success){
                    _ubRecommendList.value = it.data.data
                    Log.d(TAG, _ubRecommendList.toString())
                }
            }
        }
    }

    fun getUserInfo(){
        viewModelScope.launch(Dispatchers.IO) {
            getUserInfoUseCase.execute().collectLatest {
                if(it is ResultType.Success){
                    sharedPreferences.edit().putInt(SEQ, it.data.data.userSeq).apply()
                    sharedPreferences.edit().putString(NICKNAME, it.data.data.userNickname).apply()
                    Log.d(TAG, it.data.data.userBirthday.toString())
                    _userAge.value = 2022 - it.data.data.userBirthday
                    sharedPreferences.edit().putInt(BIRTHDAY, it.data.data.userBirthday).apply()
                }
            }
        }
    }
}