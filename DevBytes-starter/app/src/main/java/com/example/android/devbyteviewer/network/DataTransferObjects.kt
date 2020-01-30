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

import android.text.TextUtils
import com.example.android.devbyteviewer.database.DatabaseVideo
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 *
 * @see domain package for
 */

/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkVideoContainer(
        val videos: List<NetworkVideo>)


@JsonClass(generateAdapter = true)
data class FlickrNetworkPhotoContainer(
        val photos: FlickrPhotoResp
)

@JsonClass(generateAdapter = true)
data class FlickrPhotoResp(
        val page: Int,
        val pages: Int,
        val perpage: Int,
        val total: Int,
        val photo: List<NetworkVideo>
)
/**
 * Videos represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class NetworkVideo(
        val id: Long,
        val title: String,
        val description: String?,
        val url_c: String?,//medium 800px max side
        val url_q: String?,//150px square
        val dateupload: Long,
        val url_t: String,//thumb 100px max
        val closedCaptions: String?) {
    fun largestUri(): String {
        val resp: String = if (!TextUtils.isEmpty(url_c)) {
            url_c!!
        } else if (!TextUtils.isEmpty(url_q)) {
            url_q!!
        } else { //TODO originalUri isn't guaranteed, but thumb is
            url_t
        }
        return resp
    }
}

/**
 * Convert Network results to database objects
 * CONFUSING terminology from Flickr. See json example below
 */
fun FlickrPhotoResp.asDatabaseModel(): List<DatabaseVideo> {
    return photo.map {
        DatabaseVideo(
                id = it.id,
                page = this.page,//pagination
                title = it.title,
                description = it.title,
                url = it.largestUri(),
                updated = it.dateupload,
                thumbnail = it.url_t)
    }
}

//Example Flickr Photo Response
//{ "photos": { "page": 2, "pages": "110879", "perpage": 5, "total": "554394",
//    "photo": [
//    { "id": "37779575966", "owner": "63743571@N08", "secret": "448e4e628f", "server": "4444", "farm": 5, "title": "The Inspiring Madeline Smith", "ispublic": 1, "isfriend": 0, "isfamily": 0 },
//    { "id": "4667402698", "owner": "24462442@N02", "secret": "1524cd8c40", "server": "4054", "farm": 5, "title": "II", "ispublic": 1, "isfriend": 0, "isfamily": 0 },
//    { "id": "49395592882", "owner": "47665112@N08", "secret": "28b845c76b", "server": "65535", "farm": 66, "title": "Obra de arte da natureza...", "ispublic": 1, "isfriend": 0, "isfamily": 0 },
//    { "id": "49443145898", "owner": "32273289@N05", "secret": "96edae079a", "server": "65535", "farm": 66, "title": "zweisamkeit", "ispublic": 1, "isfriend": 0, "isfamily": 0 },
//    { "id": "49390677186", "owner": "90064781@N08", "secret": "1bdb8c236b", "server": "65535", "farm": 66, "title": "Blonde in Leather Skirt Getting Ready for Her Date", "ispublic": 1, "isfriend": 0, "isfamily": 0 }
//    ] }, "stat": "ok" }

/**
 * Convert Network results to domain objects
 * (Only called if you don't use a db to cache the objects.)
 */
//fun NetworkVideoContainer.asDomainModel(): List<DevByteVideo> {
//    return photos.map {
//        DevByteVideo(
//                title = it.title,
//                description = it.title,
//                url = it.url_c,
//                updated = it.date_taken,
//                thumbnail = it.url_t)
//    }
//}

