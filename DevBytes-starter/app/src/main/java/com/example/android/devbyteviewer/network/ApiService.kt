/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.devbyteviewer.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

/**
 * A retrofit service to fetch a devbyte playlist.
 * //TODO add query pagesize
 */
interface DevbyteService {
//    @GET("devbytes")
//    fun getPlaylist(): Deferred<FlickrNetworkPhotoContainer>

    @GET("?method=flickr.photos.search&api_key=949e98778755d1982f537d56236bbb42&is_getty=&format=json&nojsoncallback=1&content_type=1&media=photos&sort=relevance&extras=url_t,url_c,url_l,url_o,url_s,url_sq,url_q,date_taken,date_upload&text=train")
    fun getPlaylist(@Query("page") pageNumber: Int, @Query("per_page") pageSize: Int): Deferred<FlickrNetworkPhotoContainer>
}

/**
 * TODO How does using a singleton like this impact mocking for tests?
 * Main entry point for network access. Call like `DevByteNetwork.devbytes.getPlaylist()`
 */
object DevByteNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
//            .baseUrl("https://android-kotlin-fun-mars-server.appspot.com/")
            .baseUrl("https://api.flickr.com/services/rest/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    val devbytes = retrofit.create(DevbyteService::class.java)

}


