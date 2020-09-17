package com.routesme.taxi_screen.MVVM.Model

data class Content ( val pagination: Pagination? = null, val data: List<Data>? = null, val message: String? = null, val status: Boolean? = null, val responseCode: Int? = null)
data class Data(val contentId: Int? = null, val type: String? = null, val url: String? = null, val qrCode: QrCode? = null)
data class QrCode (val title: String? = null, val subTitle: String? = null, val url: String? = null)
enum class ContentType(val value: String) { Image("image"), Video("video") }

class ContentResponse(data: List<Data>? = null, val mResponseErrors: ResponseErrors? = null, val mThrowable: Throwable? = null): Response(mResponseErrors, mThrowable) {

    val imageList = mutableSetOf<Data>()
    val videoList = mutableSetOf<Data>()

    init {
        if (!data.isNullOrEmpty()){
            for (d in data){
                when(d.type){
                    ContentType.Image.value -> imageList.add(d)
                    else -> videoList.add(d)
                }
            }
        }
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}
