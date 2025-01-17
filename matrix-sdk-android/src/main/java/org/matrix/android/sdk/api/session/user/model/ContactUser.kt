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

package org.matrix.android.sdk.api.session.user.model

import org.matrix.android.sdk.api.session.profile.ProfileService
import org.matrix.android.sdk.api.util.JsonDict

/**
 * Data class which holds information about a user from app's local contact book.
 * It can be retrieved with [org.matrix.android.sdk.api.session.user.UserService]
 */
data class ContactUser(
        val userId: String,
        /**
         * For usage in UI, consider converting to MatrixItem and call getBestName().
         */
        val displayName: String? = null,
        val avatarUrl: String? = null
) {

    fun toUser() = User(
            userId = userId,
            displayName = displayName,
            avatarUrl = avatarUrl
    )

    companion object {

        fun fromUser(user: User) = ContactUser(
                userId = user.userId,
                displayName = user.displayName,
                avatarUrl = user.avatarUrl
        )
    }
}
