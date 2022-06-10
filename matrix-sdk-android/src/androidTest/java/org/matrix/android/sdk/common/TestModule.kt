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

package org.matrix.android.sdk.common

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.internal.di.MatrixComponent
import org.matrix.android.sdk.internal.di.MatrixScope
import org.matrix.android.sdk.internal.session.MockHttpInterceptor
import org.matrix.android.sdk.internal.session.TestInterceptor
import org.matrix.android.sdk.internal.util.BackgroundDetectionObserver

@Module
internal abstract class TestModule {
    @Binds
    abstract fun providesMatrixComponent(testMatrixComponent: TestMatrixComponent): MatrixComponent

    @Module
    companion object {

        val interceptors = ArrayList<TestInterceptor>()

        fun interceptorForSession(sessionId: String): TestInterceptor? = interceptors.firstOrNull { it.sessionId == sessionId }

        @Provides
        @JvmStatic
        @MockHttpInterceptor
        fun providesTestInterceptor(): TestInterceptor? {
            return MockOkHttpInterceptor().also {
                interceptors.add(it)
            }
        }

        @Provides
        @JvmStatic
        @MatrixScope
        fun providesBackgroundDetectionObserver(): BackgroundDetectionObserver {
            return TestBackgroundDetectionObserver()
        }
    }
}