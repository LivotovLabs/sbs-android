/*
 * Copyright 2021 New Vector Ltd
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

package im.vector.app.features.home.room.threads

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivityFilteredRoomsBinding
import im.vector.app.databinding.ActivityRoomThreadsBinding
import im.vector.app.features.home.RoomListDisplayMode
import im.vector.app.features.home.room.list.RoomListFragment
import im.vector.app.features.home.room.list.RoomListParams

class RoomThreadsActivity : VectorBaseActivity<ActivityRoomThreadsBinding>() {

//    private val roomListFragment: RoomListFragment?
//        get() {
//            return supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as? RoomListFragment
//        }

    override fun getBinding() = ActivityRoomThreadsBinding.inflate(layoutInflater)

    override fun getCoordinatorLayout() = views.coordinatorLayout

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    override fun getMenuRes() = R.menu.menu_room_threads

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureToolbar(views.roomThreadsToolbar)
//        if (isFirstCreation()) {
//            val params = RoomListParams(RoomListDisplayMode.FILTERED)
//            replaceFragment(R.id.filteredRoomsFragmentContainer, RoomListFragment::class.java, params, FRAGMENT_TAG)
//        }
    }

    companion object {
        private const val FRAGMENT_TAG = "RoomListFragment"

        fun newIntent(context: Context): Intent {
            return Intent(context, RoomThreadsActivity::class.java)
        }
    }
}