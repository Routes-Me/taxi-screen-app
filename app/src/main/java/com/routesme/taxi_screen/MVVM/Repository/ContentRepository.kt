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
        val qrcode1 = QrCode("Macdonalds offers a 30% discount \n Scan Now!","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F1.png?alt=media&token=24e4ed47-e77f-489a-bb87-36955ba85b84")
        val qrcode2 = QrCode("KFC offers a 20% discount \n Scan Now!","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F2.png?alt=media&token=071c6c0d-0959-4a5e-99fe-49b01eb21977")
        val qrcode3 = QrCode("Burger King offers a 70% discount \n Scan Now!","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F1.png?alt=media&token=24e4ed47-e77f-489a-bb87-36955ba85b84")
        val qrcode4 = QrCode("STC offers a 60% discount \n Scan Now!","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F2.png?alt=media&token=071c6c0d-0959-4a5e-99fe-49b01eb21977")
        val image1 = Data(0,"image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957",qrcode1)
        val video1 = Data(1,"video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FEid%20Alfiter.mp4?alt=media&token=f8ddfe58-d812-456c-bf4c-37fdcafa731c",qrcode1)
        val video2 = Data(2,"video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3",null)
        val image2 = Data(3,"image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",null)
        val image3 = Data(4,"image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957",qrcode1)
        val video3 = Data(5,"video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FEid%20Alfiter.mp4?alt=media&token=f8ddfe58-d812-456c-bf4c-37fdcafa731c",qrcode3)
        val video4 = Data(6,"video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3",null)
        val image4 = Data(7,"image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",null)

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
