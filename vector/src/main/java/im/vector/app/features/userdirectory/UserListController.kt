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

import com.airbnb.epoxy.EpoxyController
import im.vector.app.R
import im.vector.app.core.epoxy.errorWithRetryItem
import im.vector.app.core.epoxy.loadingItem
import im.vector.app.core.epoxy.noResultItem
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.home.AvatarRenderer
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

class UserListController @Inject constructor(
        private val session: Session,
        private val avatarRenderer: AvatarRenderer,
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider,
        private val errorFormatter: ErrorFormatter
) : EpoxyController() {

    private var state: UserListViewState? = null

    var callback: Callback? = null

    fun setData(state: UserListViewState) {
        this.state = state
        requestModelBuild()
    }

    override fun buildModels() {
        val currentState = state ?: return
        buildDirectoryUsers(currentState.directoryUsers)
    }

    private fun buildDirectoryUsers(users: List<User>) {
        val host = this
        val directoryUsers = users.filter { it.userId!=session.myUserId }

        val pendingAuthContacts = directoryUsers.filter {
            val roomId = session.roomService().getExistingDirectRoomWithUser(it.userId)
            val room = if (roomId!=null) session.roomService().getRoom(roomId) else null
            room!=null && room.roomSummary()?.invitedMembersCount != 0
        }.sortedBy { it.displayName }

        val activeChatsContacts = directoryUsers.filter {
            val roomId = session.roomService().getExistingDirectRoomWithUser(it.userId)
            val room = if (roomId!=null) session.roomService().getRoom(roomId) else null
            room!=null && room.roomSummary()?.invitedMembersCount == 0
        }.sortedBy { it.displayName }

        val otherContacts = directoryUsers.filter { !pendingAuthContacts.contains(it) && !activeChatsContacts.contains(it) }.sortedBy { it.displayName }

        if (directoryUsers.isEmpty()) {
            renderEmptyState()
            return
        }

        if (pendingAuthContacts.isNotEmpty()) {
            userListHeaderItem {
                id("pending")
                header(host.stringProvider.getString(R.string.pending_contacts_header))
            }
            pendingAuthContacts.forEach { user ->
                userDirectoryUserItem {
                    id(user.userId)
                    selected(false)
                    matrixItem(user.toMatrixItem())
                    avatarRenderer(host.avatarRenderer)
                    clickListener {
                        host.callback?.onItemClick(user)
                    }
                    longClickListener {
                        host.callback?.onItemLongClick(user)
                        true
                    }
                }
            }
        }

        if (activeChatsContacts.isNotEmpty()) {
            userListHeaderItem {
                id("active")
                header(host.stringProvider.getString(R.string.active_contacts_header))
            }
            activeChatsContacts.forEach { user ->
                userDirectoryUserItem {
                    id(user.userId)
                    selected(false)
                    matrixItem(user.toMatrixItem())
                    avatarRenderer(host.avatarRenderer)
                    clickListener {
                        host.callback?.onItemClick(user)
                    }
                    longClickListener {
                        host.callback?.onItemLongClick(user)
                        true
                    }
                }
            }
        }

        userListHeaderItem {
            id("recent")
            header(host.stringProvider.getString(R.string.normal_contacts_header))
        }

        otherContacts.forEach { user ->
            userDirectoryUserItem {
                id(user.userId)
                selected(false)
                matrixItem(user.toMatrixItem())
                avatarRenderer(host.avatarRenderer)
                clickListener {
                    host.callback?.onItemClick(user)
                }
                longClickListener {
                    host.callback?.onItemLongClick(user)
                    true
                }
            }
        }
    }

    private fun renderLoading() {
        loadingItem {
            id("loading")
        }
    }

    private fun renderEmptyState() {
        val host = this
        noResultItem {
            id("noResult")
            text(host.stringProvider.getString(R.string.empty_contacts))
        }
    }

    private fun renderFailure(failure: Throwable) {
        val host = this
        errorWithRetryItem {
            id("error")
            text(host.errorFormatter.toHumanReadable(failure))
        }
    }

    interface Callback {
        fun onItemClick(user: User)
        fun onItemLongClick(user: User)
    }
}
