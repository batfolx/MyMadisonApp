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

package com.jmu.mymadisonapp.ui.gallery

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.pluralize
import com.jmu.mymadisonapp.room.model.Course
import com.jmu.mymadisonapp.room.model.Term
import kotlinx.android.synthetic.main.course_item.view.*
import kotlinx.android.synthetic.main.fragment_grades.*
import kotlinx.android.synthetic.main.term_grid_item.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GradesFragment : Fragment() {

    private val gradesViewModel: GradesViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_grades, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        terms_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        terms_recycler.adapter = TermsRecyclerAdapter()
        terms_recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            val marginDimen = resources.getDimensionPixelSize(R.dimen.item_decoration_margin)
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = marginDimen
                outRect.right = marginDimen
                outRect.top = marginDimen
                outRect.bottom = marginDimen
            }
        })
        gradesViewModel.gradesLiveData.observe(this, Observer {
            (terms_recycler.adapter as TermsRecyclerAdapter).termData = it
        })
    }


    inner class TermsRecyclerAdapter : RecyclerView.Adapter<TermsRecyclerAdapter.TermsViewHolder>() {
        var termData: List<Term> = ArrayList()
            set(value) {
                field = value
                expandedTerms.retainAll(field)
                notifyDataSetChanged()
            }
        private val expandedTerms = HashSet<Term>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermsViewHolder =
            TermsViewHolder(layoutInflater.inflate(R.layout.term_grid_item, parent, false))

        override fun getItemCount(): Int = termData.size

        override fun onBindViewHolder(holder: TermsViewHolder, position: Int) {
            with(holder.itemView) {
                termData[position].let { term ->
                    term_title.text = "${term.semester} Semester ${term.year}"
                    career_institution.text = "${term.career.capitalize()} at ${term.institution.capitalize()}"
                    academic_standing.text = term.academicStanding
                    semester_gpa.text = "Semester GPA: ${term.semesterGPA}"
                    num_courses.text = "${term.courses.size} ${"Course".pluralize(term.courses.size)}"
                    val isExpanded = term in expandedTerms
                    isActivated = isExpanded
                    courses_container.visibility = if (isExpanded) VISIBLE else GONE
                    expand_button.apply {
                        visibility = VISIBLE
                        setImageResource(if (isExpanded) R.drawable.ic_keyboard_arrow_up_light else R.drawable.ic_keyboard_arrow_down_light)
                        setOnClickListener {
                            setImageResource(if (isExpanded) R.drawable.ic_keyboard_arrow_down_light else R.drawable.ic_keyboard_arrow_up_light)
                            if (isExpanded) expandedTerms -= term else expandedTerms += term
                            notifyItemChanged(position)
                        }
                    }
                    populateCourses(term.courses)
                }
            }
        }

        private fun View.populateCourses(courses: List<Course>) {
            courses.forEachIndexed { index, course ->
                (courses_container.getChildAt(index) ?: layoutInflater.inflate(
                    R.layout.course_item,
                    courses_container,
                    false
                ).also { courses_container.addView(it) })
                    .apply {
                        if (visibility == GONE) visibility = VISIBLE
                        course_title.text = "${course.department} ${course.number}"
                        course_description.text = course.description
                        grade.text = course.grade
                        num_credits.text = "${course.credits} ${"Credit".pluralize(course.credits)}"
                        grade_points.text = "${course.gradePoints} Grade ${"Point".pluralize(course.gradePoints)}"
                        setOnClickListener { }
                    }
            }
            for (i in courses.size until courses_container.childCount) courses_container[i].visibility = GONE
        }


        inner class TermsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener { }
            }
        }
    }

}