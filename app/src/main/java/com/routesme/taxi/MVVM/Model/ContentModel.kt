package com.routesme.taxi.MVVM.Model

data class Content ( val pagination: Pagination? = null, val data: List<Data>? = null, val message: String? = null, val status: Boolean? = null, val statusCode: Int? = null)
data class Data(val contentId: String? = null, val type: String? = null, val url: String? = null, val promotion: Promotion? = null)
data class Promotion (val title: String? = null, val subtitle: String? = null, val promotionId: String? = null, val logoUrl: String? = null)
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

/*
                if (d.url != null){
                    if (d.url.contains(".")) {
                        val extension: String = d.url.substring(d.url.lastIndexOf("."))
                        if (extension == ".mp4"){
                            videoList.add(d)
                        }else if (extension == ".jpg"){
                            imageList.add(d)
                        }
                    }
                }
*/
            }
        }
    }

    val isSuccess: Boolean
        get() = (mResponseErrors == null && mThrowable == null)
}
