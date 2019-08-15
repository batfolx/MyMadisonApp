package com.jmu.mymadisonapp.studentcenter.search

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.enroll_course_items.view.*
import kotlinx.android.synthetic.main.fragment_enroll.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector
import retrofit2.Retrofit
import java.text.Normalizer


class SearchFragment : Fragment() {

    lateinit var service: MyMadisonService
    lateinit var client: OkHttpClient

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
        client = get()
        // we set an on-click listener to search
        search_fragment_button.setOnClickListener {

            // we take the search query inputted into the service
            val searchQuery: String = search_fragment_edit_text.text.toString()
            Thread {

                val responseBody: String? = getResponseBody("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.CLASS_SEARCH.GBL")
                val ICSID: String = getCSSSelector(responseBody!!, "#ICSID")
                val formBody = getFormBody(icsid = ICSID, searchQuery = searchQuery)
                lifecycleScope.launch {
                    val searchedClasses = service.getSearchedClasses(formBody).await().body()
                    log("THESE ARE THE SEARCH CLASSESSSSS ${searchedClasses?.listOfSearchResults}")
                    fragment_search_recycler_view.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    fragment_search_recycler_view.adapter = SearchClassAdapter(searchedClasses!!)

                }




            }.start()


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
        ).execute().body()?.string()

    }

    private fun getFormBody(icsid: String, searchQuery: String): FormBody {

        val formBody = FormBody.Builder()
            .add("ICAction","CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH")
            .add("ICSID", icsid)
            .add("SSR_CLSRCH_WRK_SUBJECT",searchQuery)
            .add("CLASS_SRCH_WRK2_INSTITUTION\$31\$","JMDSN").build()

        return formBody
    }



    inner class SearchClassAdapter(val classes: ListOfSearchResults) : RecyclerView.Adapter<SearchClassAdapter.SearchClassHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchClassHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.enroll_course_items, parent, false)
            return SearchClassHolder(view)
        }

        override fun getItemCount(): Int {
            return classes.listOfSearchResults.size
        }

        override fun onBindViewHolder(holder: SearchClassHolder, position: Int) {

            with(holder.itemView) {

                description_enroll.text = classes.listOfSearchResults[position].className
                class_number_enroll.text = classes.listOfSearchResults[position].classNumber
                instructor_enroll.text = classes.listOfSearchResults[position].instructor
                room_number_enroll.text = classes.listOfSearchResults[position].room

/*                description_enroll.text = classes.table[position].className
                for (i in 0..classes.table.size) {
                    class_number_enroll.text =
                        classes.table[i].listOfSearchResults[position].classNumber
                    days_and_times_enroll.text =
                        classes.table[i].listOfSearchResults[position].daysAndTimes
                    instructor_enroll.text =
                        classes.table[i].listOfSearchResults[position].instructor
                    room_number_enroll.text = classes.table[i].listOfSearchResults[position].room
                    credits_enroll.text =
                        classes.table[i].listOfSearchResults[position].meetingDates
                }*/
            }

        }


        inner class SearchClassHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}

//data class ListOfListOfSearchResults(
//    @Selector("table[id^=ACE_SSR_CLSRSLT_WRK_GROUPBOX1]")
//    val table: List<ListOfSearchResults> = emptyList()
//)

data class ListOfSearchResults(
    @Selector("div[id^=win0divSSR_CLSRSLT_WRK_GROUPBOX2]")
    var listOfSearchResults: List<SearchResults> = emptyList(),

    @Selector("div[id^=win0divSSR_CLSRSLT_WRK_GROUPBOX2GP]")
    var className: String = ""
)

data class SearchResults(
    @Selector("a[id^=MTG_CLASS_NBR]")
    var classNumber: String = "",

    @Selector("a[id^=MTG_CLASSNAME]")
    var section: String = "",

    @Selector("span[id^=MTG_DAYTIME]")
    var daysAndTimes: String = "",

    @Selector("span[id^=MTG_ROOM]")
    var room: String = "",

    @Selector("span[id^=MTG_INSTR]")
    var instructor: String = "",

    @Selector("span[id^=MTG_TOPIC]")
    var meetingDates: String = "",

    @Selector("div[id^=win0divSSR_CLSRSLT_WRK_GROUPBOX2GP]")
    var className: String = ""


)