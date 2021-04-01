package com.routesme.taxi.api.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class RedirectInterceptor : Interceptor {
   override fun intercept(chain: Interceptor.Chain): Response {
       var request = chain.request()
       var response = chain.proceed(request)
       if (response.isRedirect){
           val newUrl = response.header("Location")
           newUrl?.let {
               response.body()?.close()
               request = request.newBuilder().url(it).build()
               response = chain.proceed(request)
           }
       }
       return response
   }
}