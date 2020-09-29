package com.routesme.taxi_screen.MVVM.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.routesme.taxi_screen.MVVM.API.RestApiService
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
        val image1 = Data("1588","image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957")
       // val video1 = Data("2878","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Best%2Fbig%20sale%20sep%20video.mp4?alt=media&token=9dae3b12-028b-41e7-86b2-961e9d30cc0f",qrCode1)
       // val video2 = Data("5822","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Best%2FShukran%204th%20Ring%20Screen.mp4?alt=media&token=d2f5cf31-6a86-4409-bfff-9830d71d2291",null)
        val image2 = Data("5252","image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",qrCode2)
        val image3 = Data("8577","image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2F160x600.jpg?alt=media&token=b6b8006d-c1cd-4bf3-b377-55e725c66957",null)
       // val video3 = Data("2875","video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FEid%20Alfiter.mp4?alt=media&token=f8ddfe58-d812-456c-bf4c-37fdcafa731c",null)
       // val video4 = Data("6898","video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3",qrCode2)
        val image4 = Data("9897","image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",qrCode4)

        val video1 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F1.mp4?alt=media&token=81c64e8f-a16d-4970-b628-abfc05336c19",qrCode2)
        val video2 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F2.mp4?alt=media&token=75a1ae14-4a5f-4851-9eb5-249e013be459",null)
        val video3 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F3.mp4?alt=media&token=8e1760a8-3ae6-401c-854e-7a2dc7203c95",qrCode1)
        val video4 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F4.mp4?alt=media&token=99381fe6-dfef-4f59-b3d3-125faa31c667")
        val video5 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F5.mp4?alt=media&token=01d0b0dc-6673-4c7b-9ea3-257bf9f16ad8",qrCode4)
        val video6 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F6.mp4?alt=media&token=56aa128f-7fef-4fc3-aef3-08c9a3cbeb14",qrCode2)
        val video7 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F7.mp4?alt=media&token=ffd9f9bf-ef3f-4c83-bb46-d06df080e5e8")
        val video8 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F8.mp4?alt=media&token=4a0af3fe-64b3-4429-8364-ecd2b1ab6ccf",qrCode3)
        val video9 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F9.mp4?alt=media&token=9a3994f3-614b-4dc3-a5eb-ed5a3c56aab0")
        val video10 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F10.mp4?alt=media&token=d6d42212-1038-4831-b53e-5c94d68ca5fa",null)
        val video11 = Data("8r37hhs","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/RoutesVideos%2F11.mp4?alt=media&token=25525f76-c9d8-44a2-8d66-fa599cc29758",qrCode4)

        val data = mutableListOf<Data>().apply {
            add(image1)
            add(video1)
            add(video2)
            add(image2)
            add(image3)
            add(video3)
            add(video4)
            add(video5)
            add(video6)
            add(video7)
            add(video8)
            add(video9)
            add(video10)
            add(video11)
        }

        return data
    }
}
