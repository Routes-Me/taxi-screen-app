package com.routesme.taxi.MVVM.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi.MVVM.API.RestApiService
import com.routesme.taxi.MVVM.Model.*
import org.json.JSONObject
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
                    contentResponse.value = ContentResponse(data = getData())
                } else{
                    if (response.errorBody() != null){
                        val objError = JSONObject(response.errorBody()!!.string())
                        val errors = Gson().fromJson<ResponseErrors>(objError.toString(), ResponseErrors::class.java)
                        contentResponse.value = ContentResponse(mResponseErrors = errors)
                    }else{
                        val error = Error(detail = response.message(),statusCode = response.code())
                        val errors = mutableListOf<Error>().apply { add(error)  }.toList()
                        val responseErrors = ResponseErrors(errors)
                        contentResponse.value = ContentResponse(mResponseErrors = responseErrors)
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
        val image1 = Data("1","image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957")
        val image2 = Data("2","image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",qrCode2)
        val image3 = Data("3","image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957",null)
        val image4 = Data("4","image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",qrCode4)
        val video1 = Data("5","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Best%2Fbig%20sale%20sep%20video.mp4?alt=media&token=9dae3b12-028b-41e7-86b2-961e9d30cc0f",qrCode1)
        val video2 = Data("6","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Best%2FShukran%204th%20Ring%20Screen.mp4?alt=media&token=d2f5cf31-6a86-4409-bfff-9830d71d2291",null)
        val video3 = Data("7","video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FEid%20Alfiter.mp4?alt=media&token=f8ddfe58-d812-456c-bf4c-37fdcafa731c",qrCode3)
        val video4 = Data("8","video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3")
        val video5 = Data("9","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/RoutesVideos%2F1.mp4?alt=media&token=a4033229-2e76-4d5e-9112-1b3f50f310b8",qrCode3)
        val video6 = Data("10","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/RoutesVideos%2F2.mp4?alt=media&token=b96f4f61-1f4a-4c22-9bb0-9024784199f9",qrCode1)
        val video7 = Data("11","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/RoutesVideos%2F3.mp4?alt=media&token=07a1263f-7117-4ee4-80f7-58373f697aac",qrCode3)
        val video8 = Data("12","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/RoutesVideos%2F4.mp4?alt=media&token=69009b67-5230-4a76-8f16-f5d076e92b09")
        val data = mutableListOf<Data>().apply {
            add(image1)
            add(video1)
            add(video7)
            add(image2)
            add(image3)
            add(video2)
            add(video3)
            add(image4)
        }
        return data
    }
}