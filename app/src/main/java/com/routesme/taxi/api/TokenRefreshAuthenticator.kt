package com.routesme.taxi.api

import android.content.Context
import android.util.Log
import com.routesme.taxi.R
import com.routesme.taxi.data.repository.TokenRepository
import com.routesme.taxi.helper.Helper
import com.routesme.taxi.uplevels.Account
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.net.HttpURLConnection

class TokenRefreshAuthenticator(private val context: Context): Authenticator{
    private val baseUrl = Helper.getConfigValue("baseUrl", R.raw.config)!!
    override fun authenticate(route: Route?, response: Response): Request? = when {

        response.networkResponse()?.request()?.url().toString() == baseUrl + "authentications/renewals" && response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE -> {
           // logOutAuthenticator()
            null
        }

        response.networkResponse()?.request()?.url().toString() == baseUrl + "authentications" -> null


        retryCount(response.request()) == 1 -> null

       // else -> response.createSignedRequest()

        else -> {
            //val previousRetryCount = retryCount(response)
           // increaseRetryCount(response, retryCount(response)+1)
            response.createSignedRequest()
            // Attempt to reauthenticate using the refresh token!
           // return reAuthenticateRequestUsingRefreshToken(response, previousRetryCount + 1)
        }
    }

    private fun retryCount(request: Request?): Int {
        val count = request?.header(Constants.httpHeaderRetryCount)?.toInt() ?: 0
        //Log.d("Retry-Count", "Request: ${request?.url()}, Count times: $count")
       return count
    }


/*
    @Synchronized
    // We synchronize this request, so that multiple concurrent failures
    // don't all try to use the same refresh token!
    private fun reAuthenticateRequestUsingRefreshToken(staleRequest: Response, retryCount: Int): Request? {

        // See if we have gone too far:
        if (retryCount > BuildConfig.OAUTH_RE_AUTH_RETRY_LIMIT) {
            // Yup!
            Log.d("RefreshToken","Retry count exceeded! Giving up.")
            // Don't try to re-authenticate any more.
            return null
        }

        // We have some retries left!
       // Timber.d("Attempting to fetch a new token...")

        Log.d("RefreshToken","Attempting to fetch a new token...")

        // Try for the new token:

        response.createSignedRequest()
        /*
        delegate.userOauthRefreshedBearerToken()?.let { newBearerToken ->
            Timber.d("Retreived new token, re-authenticating...")
            return rewriteRequest(staleRequest, retryCount, newBearerToken)
        }
*/
        // Could not retrieve new token! Unable to re-authenticate!
        Timber.w("Failed to retrieve new token, unable to re-authenticate!")
        return null
    }

    private fun rewriteRequest(staleRequest: Request?, retryCount: Int, authToken: String): Request? {
        return staleRequest?.newBuilder()
                ?.header(
                        Constants.httpHeaderAuthorization,
                        authToken.withBearerAuthTokenPrefix()
                )
                ?.header(
                        Constants.httpHeaderRetryCount,
                        "$retryCount"
                )
                ?.build()
    }
*/
/*
    private fun retryCount(response: Response?): Int {

        val times = response?.request()?.header(Constants.httpHeaderRetryCount)?.toInt() ?: 0
        Log.d("Retry-Times", "Times: $times")
        return times
    }
*/
    private fun Response.createSignedRequest(): Request? {
        val refreshTokenResponse = TokenRepository(context).refreshToken()//tokenRepository.refreshToken()
        val accessToken = refreshTokenResponse.value?.accessToken
        val refreshToken = refreshTokenResponse.value?.refreshToken
        accessToken?.let { Account().accessToken = it} //; increaseRetryCount(request(), retryCount(request())+1) }
        refreshToken?.let { Account().refreshToken = it }
        return request().signWithToken()
    }
/*
    private fun increaseRetryCount(request: Request, retryCount: Int ) {
        request.newBuilder().header(Constants.httpHeaderRetryCount,"$retryCount").build()

        // response?.request()?.header(Constants.httpHeaderRetryCount)?.toInt() ?: 0
    }
*/
    private fun Request.signWithToken(): Request {
    Log.d("Retry-Count", "Request: ${this.url()}, Count times: ${retryCount(this)}")
   return newBuilder()
            .header(Constants.httpHeaderRetryCount, "${retryCount(this) + 1}")
            .removeHeader(Header.Authorization.toString())
            .addHeader(Header.Authorization.toString(), Account().accessToken.toString())
            .build()
}
}