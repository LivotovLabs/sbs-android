/*
 * Copyright (c) 2020 New Vector Ltd
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

package im.vector.app.features.userdirectory

import android.content.Context
import androidx.lifecycle.asFlow
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.R
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.mvrx.runCatchingToAsync
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.resources.StringProvider
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.RoomSummaryQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.user.model.User
import java.util.UUID

data class ThreePidUser(
        val email: String,
        val user: User?
)

class UserListViewModel @AssistedInject constructor(
        @Assisted initialState: UserListViewState,
        private val stringProvider: StringProvider,
        private val session: Session,
        private val context: Context
) : VectorViewModel<UserListViewState, UserListAction, UserListViewEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<UserListViewModel, UserListViewState> {
        override fun create(initialState: UserListViewState): UserListViewModel
    }

    companion object : MavericksViewModelFactory<UserListViewModel, UserListViewState> by hiltMavericksViewModelFactory()

    init {
        setState {
            copy()
        }

        observeUsers()
    }

    override fun onCleared() {
        super.onCleared()
    }

    override fun handle(action: UserListAction) {
        when (action) {
            is UserListAction.AddContactIfPresent -> handleSearchAndAddContact(action.value)
            UserListAction.Resumed -> handleResumed()
        }
    }

    private fun handleResumed() {
        setState {
            copy(directoryUsers = directoryUsers)
        }
    }

    private fun handleSearchAndAddContact(value: String) {
        viewModelScope.launch {
            setState {
                copy(
                        busy = true
                )
            }

            val user = tryOrNull { session.profileService().getProfileAsUser("@$value:${context.getString(R.string.matrix_org_user_domain)}") }

            if (user != null) {
                session.userService().addToContacts(user)
                val roomId = session.roomService().getExistingDirectRoomWithUser(user.userId)
                if (roomId == null) createLocalRoomWithSelectedUsers(user.userId)

                setState {
                    copy(
                            unknownUser = null,
                            busy = false
                    )
                }
            } else {
                setState {
                    copy(
                            unknownUser = value,
                            busy = false
                    )
                }
            }
        }
    }

    private suspend fun createLocalRoomWithSelectedUsers(userId: String) {
        val adminE2EByDefault = true

        val roomParams = CreateRoomParams().apply {
            invitedUserIds.add(userId)
            setDirectMessage()
            enableEncryptionIfInvitedUsersSupportIt = adminE2EByDefault
        }

        runCatchingToAsync {
            val roomId = session.roomService().createRoom(roomParams)
            roomId
        }
    }

    private fun observeUsers() {
        viewModelScope.launch {
            session.userService().getLocalDirectoryLive().asFlow().map { it.map { c -> c.toUser() } }.collect { contacts ->
                setState {
                    copy(directoryUsers = contacts)
                }
            }
        }

        viewModelScope.launch {
            val builder = RoomSummaryQueryParams.Builder().also {
                it.memberships = listOf(Membership.JOIN)
            }

            session.roomService().getRoomSummariesLive(builder.build()).asFlow().collect {
                setState {
                    copy(directoryUsers = this.directoryUsers.toMutableList(), rnd = UUID.randomUUID().toString())
                }
            }
        }
    }

    fun resetUnknownUserMessage() {
        setState {
            copy(unknownUser = null)
        }
    }

    fun deleteContact(user: User) {
        viewModelScope.launch {
            tryOrNull { session.roomService().getExistingDirectRoomWithUser(user.userId) }?.let { roomId ->
                val room = session.roomService().getRoom(roomId)
                if (room != null) session.roomService().leaveRoom(roomId)
            }
            session.userService().deleteContact(user.userId)
        }
    }
}
