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

import io.realm.kotlin.TypedRealm
import io.realm.kotlin.query.RealmQuery
import org.matrix.android.sdk.api.session.pushrules.RuleKind
import org.matrix.android.sdk.internal.database.andIf
import org.matrix.android.sdk.internal.database.model.PushRuleEntity
import org.matrix.android.sdk.internal.database.model.PushRuleEntityFields
import org.matrix.android.sdk.internal.database.model.PushRulesEntity
import org.matrix.android.sdk.internal.database.model.PushRulesEntityFields
import org.matrix.android.sdk.internal.database.model.PusherEntity

internal fun PusherEntity.Companion.where(
        realm: TypedRealm,
        pushKey: String? = null
): RealmQuery<PusherEntity> {
    return realm.query(PusherEntity::class)
            .andIf(pushKey != null) {
                query("pushKey == $0", pushKey!!)
            }
}

internal fun PushRulesEntity.Companion.where(
        realm: TypedRealm,
        scope: String,
        kind: RuleKind
): RealmQuery<PushRulesEntity> {
    return realm.query(PushRulesEntity::class)
            .query("scope == $0", scope)
            .query("kindStr == $0", kind.name)
}

/*
internal fun PushRuleEntity.Companion.where(
        realm: TypedRealm,
        scope: String,
        ruleId: String
): RealmQuery<PushRuleEntity> {
    return realm.query(PushRuleEntity::class)
            .equalTo(PushRuleEntityFields.PARENT.SCOPE, scope)
            .equalTo(PushRuleEntityFields.RULE_ID, ruleId)
}

 */
