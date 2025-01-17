/*
 * Copyright 2020 The Matrix.org Foundation C.I.C.
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

package org.matrix.android.sdk.internal.database.query

import io.realm.Realm
import io.realm.RealmQuery
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.ContactUserEntity
import org.matrix.android.sdk.internal.database.model.ContactUserEntityFields
import org.matrix.android.sdk.internal.database.model.UserEntity
import org.matrix.android.sdk.internal.database.model.UserEntityFields

internal fun UserEntity.Companion.where(realm: Realm, userId: String): RealmQuery<UserEntity> {
    return realm
            .where<UserEntity>()
            .equalTo(UserEntityFields.USER_ID, userId)
}

internal fun ContactUserEntity.Companion.where(realm: Realm, userId: String): RealmQuery<ContactUserEntity> {
    return realm
            .where<ContactUserEntity>()
            .equalTo(ContactUserEntityFields.USER_ID, userId)
}
