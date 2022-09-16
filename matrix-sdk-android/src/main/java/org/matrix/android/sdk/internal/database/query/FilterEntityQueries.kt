/*
 * Copyright 2020 The Matrix.org Foundation C.I.C.
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

package org.matrix.android.sdk.internal.database.query

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.TypedRealm
import org.matrix.android.sdk.internal.database.model.FilterEntity
import org.matrix.android.sdk.internal.session.filter.FilterFactory

/**
 * Get the current filter.
 */
internal fun FilterEntity.Companion.get(realm: TypedRealm): FilterEntity? {
    return realm.query(FilterEntity::class).first().find()
}

/**
 * Get the current filter, create one if it does not exist.
 */
internal fun FilterEntity.Companion.getOrCreate(realm: MutableRealm): FilterEntity {
    return get(realm) ?: create(realm)
}

internal fun FilterEntity.Companion.create(realm: MutableRealm): FilterEntity {
    val filterEntity = FilterEntity().apply {
        filterBodyJson = FilterFactory.createDefaultFilter().toJSONString()
        roomEventFilterJson = FilterFactory.createDefaultRoomFilter().toJSONString()
        filterId = ""
    }
    return realm.copyToRealm(filterEntity)
}
