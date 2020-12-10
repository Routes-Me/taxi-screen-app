package com.routesme.taxi.MVVM.Model

data class Content ( val pagination: Pagination? = null, val data: List<Data>? = null, val message: String? = null, val status: Boolean? = null, val statusCode: Int? = null)
data class Data(val contentId: String? = null, val type: String? = null, val url: String? = null, val tintColor: Int? = null, val promotion: Promotion? = null)
data class Promotion (val title: String? = null, val subtitle: String? = null, val code: String? = null, val link: String? = null, val logoUrl: String? = null){
    val isExist: Boolean
        get() = !link.isNullOrEmpty()
}
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
