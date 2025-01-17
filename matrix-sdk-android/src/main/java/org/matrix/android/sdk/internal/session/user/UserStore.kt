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

package org.matrix.android.sdk.internal.session.user

import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.internal.database.model.ContactUserEntity
import org.matrix.android.sdk.internal.database.model.UserEntity
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.util.awaitTransaction
import javax.inject.Inject

internal interface UserStore {
    suspend fun createOrUpdate(userId: String, displayName: String? = null, avatarUrl: String? = null)
    suspend fun updateAvatar(userId: String, avatarUrl: String? = null)
    suspend fun updateDisplayName(userId: String, displayName: String? = null)
}

internal class RealmUserStore @Inject constructor(@SessionDatabase private val monarchy: Monarchy) : UserStore {

    override suspend fun createOrUpdate(userId: String, displayName: String?, avatarUrl: String?) {
        monarchy.awaitTransaction {
            val userEntity = UserEntity(userId, displayName ?: "", avatarUrl ?: "")
            val contactEntity = ContactUserEntity(userId, displayName ?: "", avatarUrl ?: "")
            it.insertOrUpdate(userEntity)
            it.insertOrUpdate(contactEntity)
        }
    }

    override suspend fun updateAvatar(userId: String, avatarUrl: String?) {
        monarchy.awaitTransaction { realm ->
            UserEntity.where(realm, userId).findFirst()?.let {
                it.avatarUrl = avatarUrl ?: ""
            }

            ContactUserEntity.where(realm, userId).findFirst()?.let {
                it.avatarUrl = avatarUrl ?: ""
            }
        }
    }

    override suspend fun updateDisplayName(userId: String, displayName: String?) {
        monarchy.awaitTransaction { realm ->
            UserEntity.where(realm, userId).findFirst()?.let {
                it.displayName = displayName ?: ""
            }

            ContactUserEntity.where(realm, userId).findFirst()?.let {
                it.displayName = displayName ?: ""
            }
        }
    }
}
