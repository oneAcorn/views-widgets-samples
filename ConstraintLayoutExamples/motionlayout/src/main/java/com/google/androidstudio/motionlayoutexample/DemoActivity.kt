/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.androidstudio.motionlayoutexample

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout

@RequiresApi(Build.VERSION_CODES.LOLLIPOP) // for View#clipToOutline
class DemoActivity : AppCompatActivity() {

    private lateinit var container: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = intent.getIntExtra("layout_file_id", R.layout.motion_01_basic)
        setContentView(layout)
        container = findViewById(R.id.motionLayout)

        if (layout == R.layout.motion_11_coordinatorlayout) {
            val icon = findViewById<ImageView>(R.id.icon)
            icon?.clipToOutline = true
        }

        val debugMode = if (intent.getBooleanExtra("showPaths", false)) {
            MotionLayout.DEBUG_SHOW_PATH
        } else {
            MotionLayout.DEBUG_SHOW_NONE
        }
        (container as? MotionLayout)?.setDebugMode(debugMode)

        //test 无限循环
        (container as? MotionLayout)?.run {
            Handler().postDelayed({
                transitionToEnd()
                addTransitionListener(object : MotionLayout.TransitionListener {
                    var mStartId = 0
                    var mEndId = 0
                    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
                        Log.i("acorn", "onTransitionStarted,startId:${startId},endId:${endId}")
                        mStartId = startId
                        mEndId = endId
                    }

                    override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                        Log.i("acorn", "onTransitionChange,startId:${startId},endId:${endId},progress:$progress")
                    }

                    override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                        Log.i("acorn", "onTransitionCompleted,currentId:$currentId")
                        if (currentId == mStartId) {
                            transitionToEnd()
                        } else if (currentId == mEndId) {
                            transitionToStart()
                        }
                    }

                    override fun onTransitionTrigger(p0: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
                        Log.i("acorn", "onTransitionTrigger,triggerId:$triggerId,positive:$positive,progress:$progress")
                    }

                })
            }, 1000)
        }
    }

    fun changeState(v: View?) {
        val motionLayout = container as? MotionLayout ?: return
        if (motionLayout.progress > 0.5f) {
            motionLayout.transitionToStart()
        } else {
            motionLayout.transitionToEnd()
        }
    }
}