package com.routesme.taxi_screen.MVVM.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.routesme.taxi_screen.MVVM.API.RestApiService
import com.routesme.taxi_screen.MVVM.Model.ContentResponse
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi_screen.MVVM.Model.Content
import com.routesme.taxi_screen.MVVM.Model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentRepository(val context: Context) {

    private val contentResponse = MutableLiveData<ContentResponse>()

    private val thisApiCorService by lazy {
        RestApiService.createCorService(context)
    }

    fun getContent(offset: Int, limit: Int): MutableLiveData<ContentResponse> {

        val call = thisApiCorService.getContent(offset,limit)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val content = Gson().fromJson<Content>(response.body(), Content::class.java)
                    contentResponse.value = ContentResponse(data = content.data)
                } else{
                    if (response.body() != null){
                        val errors = Gson().fromJson<ResponseErrors>(response.body(), ResponseErrors::class.java)
                        contentResponse.value = ContentResponse(mResponseErrors = errors)
                    }else{


                        val error = Error(detail = response.message(),status = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()

                        val responseErrors = ResponseErrors(errors)
                        contentResponse.value = ContentResponse(mResponseErrors = null, data = getData())
                    }
                }
            }
            override fun onFailure(call: Call<JsonElement>, throwable: Throwable) {
                contentResponse.value = ContentResponse(mThrowable = throwable)
            }
        })
        return contentResponse
    }

    private fun getData (): MutableList<Data> {
        val qrCode1 = Promotion("Big Sale !","Find it out in Avenus Mall","vci3wwiahX","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Qrcodes%2Flogo%2Fhuawei.svg?alt=media&token=ca5bd550-e6c1-4e18-a331-a568ae62360b")
        val qrCode2 = Promotion("McDonald's offers a 30% discount, valid till 20/11/2020","Scan Now!","cskJhlOmpq","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Qrcodes%2Flogo%2Fmac2.png?alt=media&token=b93837ce-a860-4092-bd2f-c1774b0a1eb5")
        val qrCode3 = Promotion("Max offers a 70% discount, valid till 20/11/2020","Find it out in 360 Mall","bDoimLknqD","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Qrcodes%2Flogo%2Fmax1.png?alt=media&token=beb4b2d6-a61e-4f3e-87c1-a627ae05ec62")
        val qrCode4 = Promotion("SUBWAY offers a 60% discount, valid till 16/12/2020","Scan Now!","hfDdLkjmIk","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Qrcodes%2Flogo%2Fsubway1.png?alt=media&token=c568a0a7-278a-4b37-8891-d319ba2cf2e7")
        val image1 = Data(0,"image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957",qrCode1)
        val video1 = Data(1,"video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Best%2Fbig%20sale%20sep%20video.mp4?alt=media&token=9dae3b12-028b-41e7-86b2-961e9d30cc0f",qrCode1)
        val video2 = Data(2,"video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3",qrCode1)
        val image2 = Data(3,"image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",qrCode1)
        val image3 = Data(4,"image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957",qrCode1)
        val video3 = Data(5,"video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FEid%20Alfiter.mp4?alt=media&token=f8ddfe58-d812-456c-bf4c-37fdcafa731c",qrCode1)
        val video4 = Data(6,"video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3",qrCode1)
        val image4 = Data(7,"image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",qrCode1)

        val data = mutableListOf<Data>().apply {
            add(image1)
            add(video1)
            add(video2)
            add(image2)
            add(image3)
            add(video3)
            add(video4)
            add(image4)
        }

        return data
    }
}
