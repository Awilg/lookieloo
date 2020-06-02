package com.lookieloo.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.lookieloo.model.Loo
import com.lookieloo.model.LooLocationRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


// This is the local address for the emulator
private const val BASE_URL = "http://10.0.2.2:8080/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
	.build()


/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
	.addConverterFactory(MoshiConverterFactory.create(moshi))
	.addCallAdapterFactory(CoroutineCallAdapterFactory())
	.baseUrl(BASE_URL)
	.build()

/**
 * A public interface that exposes the methods of the backend user service/
 */
interface LooService {
	/**
	 * Returns a Coroutine [Deferred] of [User] which can be fetched with await() if
	 * in a Coroutine scope.
	 */
	@POST("loo/findNearby")
	fun getNearbyLoos(@Body looLocationRequest: LooLocationRequest): Deferred<List<Loo>>
}


/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object LooApi {
	val looService: LooService by lazy { retrofit.create(LooService::class.java) }
}
