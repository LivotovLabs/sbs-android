/*
 * Copyright (c) 2022 New Vector Ltd
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

package im.vector.app.features.home.room.list.home.release

import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.platform.ScreenOrientationLocker
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivitySimpleBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReleaseNotesActivity : VectorBaseActivity<ActivitySimpleBinding>() {

    @Inject lateinit var orientationLocker: ScreenOrientationLocker
    @Inject lateinit var releaseNotesPreferencesStore: ReleaseNotesPreferencesStore

    override fun getBinding() = ActivitySimpleBinding.inflate(layoutInflater)

    override fun getCoordinatorLayout() = views.coordinatorLayout

    override fun initUiAndData() {
        orientationLocker.lockPhonesToPortrait(this)
        if (isFirstCreation()) {
            addFragment(views.simpleFragmentContainer, ReleaseNotesFragment::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            // mark release nores displayed for the current version (if necessary)
        }
    }
}
