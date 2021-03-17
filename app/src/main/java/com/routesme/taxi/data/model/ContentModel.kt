package com.routesme.taxi.data.model

import android.os.Parcel
import android.os.Parcelable

data class Content (val pagination: Pagination? = null, val data: List<Data>? = null, val message: String? = null, val status: Boolean? = null, val statusCode: Int? = null)

data class Data(val contentId: String? = null, val type: String? = null, val url: String? = null, val tintColor: Int? = null, val promotion: Promotion? = null):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readParcelable(Promotion::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(contentId)
        parcel.writeString(type)
        parcel.writeString(url)
        parcel.writeValue(tintColor)
        parcel.writeParcelable(promotion, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }

}


data class Promotion (val title: String? = null, val subtitle: String? = null, val code: String? = null, val link: String? = null, val logoUrl: String? = null):Parcelable{
    val isExist: Boolean
        get() = !link.isNullOrEmpty()

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(subtitle)
        parcel.writeString(code)
        parcel.writeString(link)
        parcel.writeString(logoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Promotion> {
        override fun createFromParcel(parcel: Parcel): Promotion {
            return Promotion(parcel)
        }

        override fun newArray(size: Int): Array<Promotion?> {
            return arrayOfNulls(size)
        }
    }
}
enum class ContentType(val value: String) { Image("image"), Video("video") }

class Report(val status:Boolean?=null,val message:String?=null,val statusCode:Int?=null)



class ReportResponse(val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {
    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}

class ContentResponse(val data: List<Data>? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    val imageList = mutableListOf<Data>()
    val videoList = mutableListOf<Data>()

    init {
        if (!data.isNullOrEmpty()){
            for (d in data){
                when(d.type){
                    ContentType.Image.value -> {
                        imageList.add(d)

                    }
                    else -> videoList.add(d)
                }
            }
        }
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}
