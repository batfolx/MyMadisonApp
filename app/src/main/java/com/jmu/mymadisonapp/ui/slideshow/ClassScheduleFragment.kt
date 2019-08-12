/*
 * Copyright 2019 Timothy Logan
 * Copyright 2019 Victor Velea
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

package com.jmu.mymadisonapp.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.pluralize
import com.jmu.mymadisonapp.room.model.Course
import kotlinx.android.synthetic.main.course_item.view.*
import kotlinx.android.synthetic.main.fragment_class_schedule.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ClassScheduleFragment : Fragment() {

    private val classScheduleViewModel: ClassScheduleViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_class_schedule, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        classes_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        classes_recycler.adapter = CourseRecyclerAdapter()
        /*classes_recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            val marginDimen = resources.getDimensionPixelSize(R.dimen.item_decoration_margin)
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = marginDimen
                outRect.right = marginDimen
                outRect.top = marginDimen
                outRect.bottom = marginDimen
            }
        })*/
        classScheduleViewModel.classSchedulesLiveData.observe(this, Observer {
            (classes_recycler.adapter as CourseRecyclerAdapter).termData = it
        })
        classScheduleViewModel.loadingLiveData.observe(this, Observer {
            if (it && !progressBar.isVisible) progressBar.visibility = VISIBLE
            else if (!it && progressBar.isVisible) progressBar.visibility = GONE
        })
    }

    inner class CourseRecyclerAdapter : RecyclerView.Adapter<CourseRecyclerAdapter.CourseViewHolder>() {
        var termData: List<Course> = ArrayList()
            set(value) {
                field = value
                expandedTerms.retainAll(field)
                notifyDataSetChanged()
            }
        private val expandedTerms = HashSet<Course>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder =
            CourseViewHolder(layoutInflater.inflate(R.layout.course_item, parent, false))

        override fun getItemCount(): Int = termData.size

        override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
            with(holder.itemView) {
                termData[position].let { course ->
                    if (visibility == View.GONE) visibility = View.VISIBLE
                    course_title.text = "${course.department} ${course.number} - ${course.section}"
                    course_description.text = course.description
                    grade.text = course.grade
                    num_credits.text = "${course.credits} ${"Credit".pluralize(course.credits)}"
                    grade_points.text = "${course.gradePoints} Grade ${"Point".pluralize(course.gradePoints)}"
                    setOnClickListener { }
                    /*val isExpanded = term in expandedTerms
                    isActivated = isExpanded
                    courses_container.visibility = if (isExpanded) View.VISIBLE else View.GONE
                    expand_button.apply {
                        visibility = View.VISIBLE
                        setImageResource(if (isExpanded) R.drawable.ic_keyboard_arrow_up_light else R.drawable.ic_keyboard_arrow_down_light)
                        setOnClickListener {
                            setImageResource(if (isExpanded) R.drawable.ic_keyboard_arrow_down_light else R.drawable.ic_keyboard_arrow_up_light)
                            if (isExpanded) expandedTerms -= term else expandedTerms += term
                            notifyItemChanged(position)
                        }
                    }*/
                }
            }
        }

        inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener { }
            }
        }
    }

}

