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

import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.internal.database.model.ContactUserEntity
import org.matrix.android.sdk.internal.database.model.UserEntity

internal object UserEntityFactory {

    fun create(userId: String, roomMember: RoomMemberContent): UserEntity {
        return UserEntity(
                userId = userId,
                displayName = roomMember.displayName.orEmpty(),
                avatarUrl = roomMember.avatarUrl.orEmpty()
        )
    }

    fun create(user: User): UserEntity {
        return UserEntity(
                userId = user.userId,
                displayName = user.displayName.orEmpty(),
                avatarUrl = user.avatarUrl.orEmpty()
        )
    }

    fun createContact(user: User): ContactUserEntity {
        return ContactUserEntity(
                userId = user.userId,
                displayName = user.displayName.orEmpty(),
                avatarUrl = user.avatarUrl.orEmpty()
        )
    }

    fun createContact(userId: String, roomMember: RoomMemberContent): ContactUserEntity {
        return ContactUserEntity(
                userId = userId,
                displayName = roomMember.displayName.orEmpty(),
                avatarUrl = roomMember.avatarUrl.orEmpty()
        )
    }

}
