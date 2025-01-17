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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.extensions.cleanup
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.setupAsSearch
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.platform.VectorMenuProvider
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.core.utils.showIdentityServerConsentDialog
import im.vector.app.core.utils.startSharePlainTextIntent
import im.vector.app.databinding.FragmentUserListBinding
import im.vector.app.features.homeserver.HomeServerCapabilitiesViewModel
import im.vector.app.features.settings.VectorSettingsActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.user.model.User
import reactivecircus.flowbinding.android.widget.textChanges
import javax.inject.Inject

@AndroidEntryPoint
class UserListFragment :
        VectorBaseFragment<FragmentUserListBinding>(),
        UserListController.Callback,
        VectorMenuProvider {

    @Inject lateinit var userListController: UserListController
    @Inject lateinit var dimensionConverter: DimensionConverter

    private val args: UserListFragmentArgs by args()
    private val viewModel: UserListViewModel by activityViewModel()
    private val homeServerCapabilitiesViewModel: HomeServerCapabilitiesViewModel by fragmentViewModel()
    private lateinit var sharedActionViewModel: UserListSharedActionViewModel

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserListBinding {
        return FragmentUserListBinding.inflate(inflater, container, false)
    }

    override fun getMenuRes() = args.menuResId

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedActionViewModel = activityViewModelProvider.get(UserListSharedActionViewModel::class.java)
        if (args.showToolbar) {
            setupToolbar(views.userListToolbar)
                    .allowBack(useCross = true)
            views.userListToolbar.isVisible = true
        } else {
            views.userListToolbar.isVisible = false
        }

        setupRecyclerView()

        viewModel.onEach(UserListViewState::unknownUser) {
            if (it!=null) {
                MaterialDialog(requireActivity()).show {
                    title(R.string.add_contact_title)
                    message(text = getString(R.string.user_not_found, it))
                    positiveButton(R.string.action_close)
                }
                viewModel.resetUnknownUserMessage()
            }
        }

        viewModel.onEach(UserListViewState::busy) {
            views.busyBox.visibility = when(it) {
                true -> View.VISIBLE
                else -> View.GONE
            }
        }

        viewModel.onEach { state ->
            userListController.setData(state)
        }
    }

    override fun onDestroyView() {
        views.userListRecyclerView.cleanup()
        super.onDestroyView()
    }

    override fun handlePrepareMenu(menu: Menu) {
    }

    override fun handleMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            args.addContactMenuId -> {
                onAskForNewContactNameToAdd()
                true
            }
            else -> false
        }
    }

    @SuppressLint("CheckResult")
    private fun onAskForNewContactNameToAdd() {
        MaterialDialog(requireActivity())
                .title(R.string.add_contact_title)

                .show {
                    input(hintRes = R.string.add_contact_hint) { _, text ->
                        viewModel.handle(UserListAction.AddContactIfPresent(text.toString()))
                    }
                    negativeButton(R.string.general_cancel)
                    positiveButton(R.string.ok)
                }
    }

    private fun setupRecyclerView() {
        userListController.callback = this
        // Don't activate animation as we might have way to much item animation when filtering
        views.userListRecyclerView.configureWith(userListController, disableItemAnimation = true)
    }

    override fun invalidate() {

    }

    fun getCurrentState() = withState(viewModel) { it }

    override fun onItemClick(user: User) {
        view?.hideKeyboard()
        withState(viewModel) { state ->
            if (!state.busy) sharedActionViewModel.post(UserListSharedAction.OnMenuItemSubmitClick(setOf(PendingSelection.UserPendingSelection(user))))
        }
    }

    override fun onItemLongClick(user: User) {
        view?.hideKeyboard()
        withState(viewModel) { state ->
            if (!state.busy) MaterialDialog(requireActivity())
                    .title(R.string.delete_contact_title)
                    .message(text = getString(R.string.delete_contact_confirmation, user.displayName?:user.userId))
                    .negativeButton(R.string.general_cancel)
                    .positiveButton(R.string.ok) {
                        viewModel.deleteContact(user)
                    }
                    .show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handle(UserListAction.Resumed)
    }

}
