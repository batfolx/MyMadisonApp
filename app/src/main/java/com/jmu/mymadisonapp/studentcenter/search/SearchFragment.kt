package com.jmu.mymadisonapp.studentcenter.search

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.os.Message
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import com.jmu.mymadisonapp.studentcenter.EnrollFragment
import com.jmu.mymadisonapp.studentcenter.SEARCH_IC_ACTION
import com.jmu.mymadisonapp.studentcenter.SEARCH_SELECT_IC_ACTION
import com.jmu.mymadisonapp.studentcenter.SEARCH_SELECT_NEXT_IC_ACTION
import kotlinx.android.synthetic.main.course_item.view.*
import kotlinx.android.synthetic.main.enroll_course_items.view.*
import kotlinx.android.synthetic.main.fragment_enroll.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_classes_items.*
import kotlinx.android.synthetic.main.search_classes_items.view.*
import kotlinx.android.synthetic.main.search_classes_items.view.class_section_search
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector
import retrofit2.Retrofit
import retrofit2.http.FieldMap
import retrofit2.http.Path
import java.text.Normalizer


class SearchFragment : Fragment() {
    lateinit var service: MyMadisonService
    var client: OkHttpClient = get()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        service = get()
        // we set an on-click listener to search
        search_fragment_button.setOnClickListener {

            // we take the search query inputted into the service
            val searchQuery: String = search_fragment_edit_text.text.toString()
            val courseNumber: String = course_number_edit_text.text.toString()

            search_fragment_text_view.text = "Showing classes for ${searchQuery.toUpperCase()}."

            val thread = Thread {

                val responseBody: String? =
                    getResponseBody("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.CLASS_SEARCH.GBL")
                val ICSID: String = getCSSSelector(responseBody!!, "#ICSID")

                val fieldMap = getFieldMap(icsid = ICSID, searchQuery = searchQuery, courseNumber = courseNumber)
                lifecycleScope.launch {
                    val searchedClasses = service.getSearchedClasses(fieldMap).await().body()
                    fragment_search_recycler_view.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    fragment_search_recycler_view.adapter =
                        SearchClassAdapter(searchedClasses!!)

                }

            }
            thread.start()
            thread.join()

        }

    }


    private fun getCSSSelector(responseBody: String, query: String): String {
        return Jsoup.parse(responseBody).select(query).`val`()
    }

    private fun getResponseBody(url: String): String? {
        return client.newCall(
            Request.Builder()
                .url(url)
                .get()
                .build()
        ).execute().body()!!.string()
    }


    private fun getFieldMap(icsid: String, searchQuery: String = "", courseNumber: String = ""): MutableMap<String, String> {
        val fieldMap = mutableMapOf<String, String>(
            "SSR_CLSRCH_WRK_SUBJECT" to searchQuery,
            "ICACTION" to SEARCH_IC_ACTION,
            "ICSID" to icsid,
            "SSR_CLSRCH_WRK_CATALOG_NBR\$1" to courseNumber
        )
        return fieldMap
    }


    inner class SearchClassAdapter(val classes: ListOfSearchResults) :
        RecyclerView.Adapter<SearchClassAdapter.SearchClassHolder>() {


        private fun createSelectFieldMap(
            url: String,
            position: Int,
            icAction: String
        ): MutableMap<String, String> {

            val respBody = getResponseBody(url)
            val icsid = getCSSSelector(respBody!!, "#ICSID")
            val fieldMap = mutableMapOf<String, String>(
                "ICAction" to icAction + position,
                "ICSID" to icsid,
                "DERIVED_SSTSNAV_SSTS_MAIN_GOTO\$27\$" to "0100"
            )

            log("Create select field map", "CLICKED, ${icAction+position}")

            return fieldMap

        }

        private fun createConfirmFieldMap(
            url: String,
            icAction: String
        ): MutableMap<String, String> {

            val respBody = getResponseBody(url)
            val icsid = getCSSSelector(respBody!!, "#ICSID")
            val fieldMap = mutableMapOf<String, String>(
                "ICAction" to icAction, "DERIVED_SSTSNAV_SSTS_MAIN_GOTO\$27\$" to "0100",
                "ICSID" to icsid,
                "DERIVED_SSTSNAV_SSTS_MAIN_GOTO\$27\$" to "0100",
                "DERIVED_CLS_DTL_WAIT_LIST_OKAY\$125\$\$chk" to "N"
            )

            log("Create confirm field map", "CLICKED, $icAction")
            return fieldMap
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchClassHolder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.search_classes_items, parent, false)
            return SearchClassHolder(view)
        }

        override fun getItemCount(): Int {
            return classes.listOfSearchResults.size
        }

        override fun onBindViewHolder(holder: SearchClassHolder, position: Int) {

            with(holder.itemView) {


                //course_description.text = classes.listOfSearchResults[position].className

                select_search_button.setOnClickListener {
                    class_name_search.text = "Added this class! $position"
                    val url =
                        "https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.CLASS_SEARCH.GBL"
                    val thread = Thread {

                        val fieldMap = createSelectFieldMap(url, position, SEARCH_SELECT_IC_ACTION)
                        service.addClass(fieldMap)


                        val secondFieldMap =
                            createConfirmFieldMap(url, SEARCH_SELECT_NEXT_IC_ACTION)

                        service.confirmAddClass(secondFieldMap)
                    }
                    thread.start()
                    thread.join()


                }
                class_name_search.text = classes.classNumber
                class_name_search.text = classes.listOfSearchResults[position].className
                class_number_search.text = classes.listOfSearchResults[position].classNumber
                class_section_search.text = classes.listOfSearchResults[position].section
                instructor_search.text = classes.listOfSearchResults[position].instructor
                room_search.text = classes.listOfSearchResults[position].room
                meeting_dates_search.text = classes.listOfSearchResults[position].meetingDates
                days_and_times_search.text = classes.listOfSearchResults[position].daysAndTimes


            }

        }


        inner class SearchClassHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    val alertDialog = AlertDialog.Builder(context, 1)
                        .setTitle("Are you sure?")
                        .setMessage("Are you sure you want to add this class?")
                        .setCancelable(true)
                        .create()

                    //  alertDialog.setButton(Button(context), "Yes", Message())

                    alertDialog.show()
                }
            }
        }
    }
}

data class ListOfListOfSearchResults(
    @Selector("#win0divDERIVED_CLSRCH_GROUP6")
    var table: List<ListOfSearchResults> = emptyList()
)

data class ListOfSearchResults(

    @Selector("tr[id^=trSSR_CLSRCH_MTG1]")
    var listOfSearchResults: List<SearchResults> = emptyList(),

    @Selector("div[id^=win0divSSR_CLSRSLT_WRK_GROUPBOX2GP]")
    var classNumber: String = ""



)

data class SearchResults(

    @Selector("div[id^=win0divSSR_CLSRSLT_WRK_GROUPBOX2GP] > a > img[alt]")
    var courseNumber: String = "",

    // 73179
    @Selector("a[id^=MTG_CLASS_NBR]")
    var classNumber: String = "",


    // 0004-LEC
    //Regular
    @Selector("a[id^=MTG_CLASSNAME]")
    var section: String = "",

    //MoWeFr 9:05AM - 9:55AM
    @Selector("span[id^=MTG_DAYTIME]")
    var daysAndTimes: String = "",


    // ISAT/CS Building 0143
    @Selector("span[id^=MTG_ROOM]")
    var room: String = "",


    //Philip Riley
    @Selector("span[id^=MTG_INSTR]")
    var instructor: String = "",

    //08/26/2019 - 12/13/2019
    @Selector("span[id^=MTG_TOPIC]")
    var meetingDates: String = "",


    //CS 149 - INTRODUCTION TO PROGRAMMING
    @Selector("div[id^=win0divSSR_CLSRSLT_WRK_GROUPBOX2]")
    var className: String = ""


)