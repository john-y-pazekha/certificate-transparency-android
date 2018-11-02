/*
 * Copyright 2018 Babylon Healthcare Services Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.certificatetransparency.ctlog.internal.loglist

import org.certificatetransparency.ctlog.datasource.DataSource
import org.certificatetransparency.ctlog.internal.utils.Base64
import org.certificatetransparency.ctlog.verifier.SignatureVerifier
import retrofit2.Retrofit

internal object LogListDataSourceFactory {
    fun create(): DataSource<Map<String, SignatureVerifier>> {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.gstatic.com/ct/log_list/")
            .build()

        val logService = retrofit.create(LogListService::class.java)

        return InMemoryDataSource<Map<String, SignatureVerifier>>()
            .compose(LogListNetworkDataSource(logService).oneWayTransform { logServers ->
                logServers.associateBy({ Base64.toBase64String(it.id) }) { it.signatureVerifier }
            })
            .reuseInflight()
    }
}
