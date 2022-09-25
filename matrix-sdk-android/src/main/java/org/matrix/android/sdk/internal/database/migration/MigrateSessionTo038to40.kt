/*
 * Copyright (c) 2022 The Matrix.org Foundation C.I.C.
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

package org.matrix.android.sdk.internal.database.migration

import io.realm.DynamicRealm
import org.matrix.android.sdk.internal.database.model.ContactUserEntityFields
import org.matrix.android.sdk.internal.util.database.RealmMigrator

internal class MigrateSessionTo038to40(realm: DynamicRealm) : RealmMigrator(realm, 40) {

    override fun doMigrate(realm: DynamicRealm) {
        var entity = realm.schema.get("ContactUserEntity")
        if (entity == null) entity = realm.schema.create("ContactUserEntity")

        entity
                ?.addField(ContactUserEntityFields.USER_ID, String::class.java)
                ?.setRequired(ContactUserEntityFields.USER_ID, true)
                ?.addPrimaryKey(ContactUserEntityFields.USER_ID)
                ?.addField(ContactUserEntityFields.DISPLAY_NAME, String::class.java)
                ?.setRequired(ContactUserEntityFields.DISPLAY_NAME, true)
                ?.addField(ContactUserEntityFields.AVATAR_URL, String::class.java)
                ?.setRequired(ContactUserEntityFields.AVATAR_URL, true)
    }
}
