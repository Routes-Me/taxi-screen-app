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

    private fun getData(): MutableList<Data> {
       // val qrCode1 = Promotion("30% off at place 2  30% off at place 2  ","Grab it now!","8236249","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Qrcodes%2Flogo%2Fhuawei.svg?alt=media&token=ca5bd550-e6c1-4e18-a331-a568ae62360b", type ="coupons")
       // val qrCode2 = Promotion("McDonald's offers a 30% discount, valid till 20/11/2020 ","Available Now !","cskJhlOmpq","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Qrcodes%2Flogo%2Fmac2.png?alt=media&token=b93837ce-a860-4092-bd2f-c1774b0a1eb5", type ="places")
       // val qrCode3 = Promotion("Max offers a 70% discount, valid till 20/11/2020","Find it out in 360 Mall","bDoimLknqD","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Qrcodes%2Flogo%2Fmax1.png?alt=media&token=beb4b2d6-a61e-4f3e-87c1-a627ae05ec62", type ="links")
       // val qrCode4 = Promotion("SUBWAY offers a 60% discount, valid till 16/12/2020","Scan Now!","hfDdLkjmIk","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/Qrcodes%2Flogo%2Fsubway1.png?alt=media&token=c568a0a7-278a-4b37-8891-d319ba2cf2e7",type = null)
      //  val image1 = Data("1","image","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/Routes%2Fnew%2Fbanner2.jpg?alt=media&token=b89fbdc6-35a3-4cf1-90b2-03999d3862f5",qrCode1)
       // val image2 = Data("2","image","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/Routes%2Fnew%2Fbanner3.jpg?alt=media&token=67e1e066-c98e-4c0f-b73a-7db25675fa08",qrCode2)
         // val image3 = Data("3","image","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/Routes%2Fbanner6.jpg?alt=media&token=f7a8403f-eb52-456a-a5c6-95dc5a04634a",qrCode3)
       //   val image4 = Data("4","image","https://firebasestorage.googleapis.com/v0/b/usingfirebasefirestore.appspot.com/o/000000000%2Funnamed.jpg?alt=media&token=ff4adc90-1e6a-487b-8774-1eb3152c60d5",qrCode4)
        //val video1 = Data("5","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/Routes%2Fnew%2FHuawei_db3014e9-6f0d-409d-8f4e-bce22b7bfaaf.mp4?alt=media&token=da43d46b-fe1b-414d-91a3-28ea2ea03911",qrCode1)
       // val video2 = Data("6","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/Routes%2Fnew%2FKFC_a770a23c-0f1f-46b8-bc24-963b4d9e0838.mp4?alt=media&token=ca28c382-62b1-4c63-b544-9a3c16fee39f",qrCode2)
       // val video3 = Data("7","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/Routes%2Fnew%2FMcDonald's_321c3000-3c77-452b-a44a-d88a47097654.mp4?alt=media&token=41d191f7-8214-4fd4-8ea7-a189f4e97e14",qrCode3)
        // val video4 = Data("8","video","https://firebasestorage.googleapis.com/v0/b/wdeniapp.appspot.com/o/000000%2FKuwait%20National%20Day.mp4?alt=media&token=fd4c77c5-1d5c-4aed-bb77-a6de9acb00b3",qrCode1)
        // val video5 = Data("9","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/RoutesVideos%2F1.mp4?alt=media&token=a4033229-2e76-4d5e-9112-1b3f50f310b8",qrCode1)
        // val video6 = Data("10","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/RoutesVideos%2F2.mp4?alt=media&token=b96f4f61-1f4a-4c22-9bb0-9024784199f9",qrCode1)
        // val video7 = Data("11","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/RoutesVideos%2F3.mp4?alt=media&token=07a1263f-7117-4ee4-80f7-58373f697aac",qrCode1)
        // val video8 = Data("12","video","https://firebasestorage.googleapis.com/v0/b/buy-me-a3db2.appspot.com/o/RoutesVideos%2F4.mp4?alt=media&token=69009b67-5230-4a76-8f16-f5d076e92b09",qrCode1)

        val video1 = Data("1","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F1.mp4?alt=media&token=221ba36c-8846-4755-b0f2-3a4e961711b3",null)
        val video2 = Data("2","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F2.mp4?alt=media&token=75697160-6d75-4a4c-9118-3b466c14f19a",null)
        val video3 = Data("3","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F3.mp4?alt=media&token=63042953-f2e1-471e-9350-e7690908e72d",null)
        val video4 = Data("4","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F4.mp4?alt=media&token=f9f344e8-2422-48ae-85ce-a5ddb4b8dc2e",null)
        val video5 = Data("5","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F5.mp4?alt=media&token=d1966a90-84f7-479f-a588-562124177485",null)
        val video6 = Data("6","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F6.mp4?alt=media&token=861c3a23-0290-41c3-b1a5-ab22f3fd9bf3",null)
        val video7 = Data("7","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F7.mp4?alt=media&token=25d30140-f456-41d1-982f-5eba4d598edb",null)
        val video8 = Data("8","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F8.mp4?alt=media&token=06e18c9e-362a-4647-912c-bbcdd92b5e3d",null)
        val video9 = Data("9","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F9.mp4?alt=media&token=9c0dfb5a-8485-4cdd-9687-c2a8c1ada73d",null)
        val video10 = Data("10","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F10.mp4?alt=media&token=56ae55b5-0eb9-4fa8-bd01-2c591265b0fd",null)
        val video11 = Data("11","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F11.mp4?alt=media&token=68d6a3ae-7dd7-4682-a242-2e4050f850df",null)
        val video12 = Data("12","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F12.mp4?alt=media&token=e2a9d688-5595-43bb-8147-974aeed36a1b",null)
        val video13 = Data("13","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F13.mp4?alt=media&token=ee2a21c6-d243-4d3e-850a-0a5eaa7d93f7",null)
        val video14 = Data("14","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F14.mp4?alt=media&token=79fb7e55-96e2-48b6-be8a-e1a26ef56011",null)
        val video15 = Data("15","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F15.mp4?alt=media&token=fffd3623-2ac4-4db6-9f59-598157218ad1",null)
        val video16 = Data("16","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F16.mp4?alt=media&token=40763a37-83bc-4b6b-9f1f-b37b5d02d798",null)
        val video17 = Data("17","video","https://firebasestorage.googleapis.com/v0/b/kidsedu-69ac7.appspot.com/o/0000000000000000000000000000000000000000000000000000000000000%2F17.mp4?alt=media&token=bd148ee6-9233-40ef-a101-e02a7b57564a",null)


        val data = mutableListOf<Data>().apply {
            add(video1)
            add(video2)
            add(video3)
            add(video4)
            add(video5)
            add(video6)
            add(video7)
            add(video8)
            add(video9)
            add(video10)
            add(video11)
            add(video12)
            add(video13)
            add(video14)
            add(video15)
            add(video16)
            add(video17)
        }
        return data
    }

}