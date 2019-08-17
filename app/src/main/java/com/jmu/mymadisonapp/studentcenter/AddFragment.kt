package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
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
import com.jmu.mymadisonapp.buttonNames
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.shopping_cart_items.view.*
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector


class AddFragment : Fragment() {


    lateinit var service: MyMadisonService
    var client: OkHttpClient = get()
    var numClasses: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    private fun getResponseBody(): String? {
        return client.newCall(
            Request.Builder()
                .url("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
                .get()
                .build()
        ).execute().body()?.string()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        client = get()
        service = get()




        lifecycleScope.launch {
            val enrolledClasses = service.getShoppingCartClasses().await().body()
            shopping_cart_recycler_view.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            shopping_cart_recycler_view.adapter = AddClassAdapter(enrolledClasses!!)
            shopping_cart_text_view.text =
                "My Shopping Cart, with ${enrolledClasses.shoppingCart.size} classes."
            log("DOING  size of this joint ${enrolledClasses.shoppingCart.size}")
            for (item in enrolledClasses.shoppingCart) {
                log("List of enrolled classes ${item.className}")
            }
        }

        log("THIS IS THE NUMBER OF CLASSES IN THE SHOPPING CART $numClasses")
        add_all_button.setOnClickListener {
            val thread: Thread = Thread {
                var responseBody: String? = getResponseBody()

                var ICStateNum: String =
                    Jsoup.parse(responseBody).selectFirst("input[name=ICStateNum]")
                        .`val`()
                val ICSID: String =
                    Jsoup.parse(responseBody).select("#ICSID").`val`()

                var formBody = getFirstFormBody(
                    ICSIDKey = ICSID,
                    icActionKey = "DERIVED_REGFRM1_LINK_ADD_ENRL\$82\$",
                    numClasses = 0,
                    icStateNumKey = ICStateNum
                )

                service.enrollInAllClasses(formBody)

                responseBody = getResponseBody()
                ICStateNum = Jsoup.parse(responseBody).selectFirst("input[name=ICStateNum]").`val`()

                formBody = getSecondFormData(ICSID, icStateNumKey = ICStateNum, icActionKey = "DERIVED_REGFRM1_SSR_PB_SUBMIT" )
                // must do a secondary confirm again
                service.confirmEnrollInAllClasses(formBody)


            }
            thread.start()
            thread.join()
        }

    }


    fun addButtonsToCourses(
        linearLayoutParams: LinearLayout.LayoutParams,
        linearLayout: LinearLayout,
        buttonNames: Array<String>,
        holder: AddClassAdapter.AddClassHolder,
        position: Int
    ) {
        with(holder.itemView) {

            for (name in buttonNames) {

                val tmpButton = Button(context)
                tmpButton.text = name
                when (name) {
                    "Drop" -> tmpButton.setOnClickListener {
                        val thread = Thread {
                            var responseBody: String? =
                                client.newCall(
                                    Request.Builder()
                                        .url("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
                                        .get()
                                        .build()
                                ).execute().body()?.string()


                            var ICStateNum: String =
                                Jsoup.parse(responseBody).selectFirst("input[name=ICStateNum]")
                                    .`val`()
                            val ICSID: String =
                                Jsoup.parse(responseBody).select("#ICSID").`val`()


                            val formBody = getFirstFormBody(
                                ICSIDKey = ICSID,
                                icStateNumKey = ICStateNum,
                                icActionKey = "P_DELETE\$" + position
                            )



                            service.deleteSelectedClass(formBody)
                            description_enroll_sc.text = "Dropped the class!"
                        }
                        thread.start()
                        thread.join()


                    }


                    "Edit" -> tmpButton.setOnClickListener {
                        description_enroll_sc.text =
                            "Description changed when the name is Edit!"
                    }

                    else -> tmpButton.setOnClickListener {
                        description_enroll_sc.text = "Description changed when the name is Else!"
                    }
                }
                linearLayout.addView(tmpButton, linearLayoutParams)
            }

        }
    }

    private fun getFirstFormBody(
        ICSIDKey: String,
        icStateNumKey: String,
        icActionKey: String = "P_DELETE\$",
        numClasses: Int = 0

    ): FormBody {

        var formBody = FormBody.Builder()
            .add("ICAJAX", "1")
            .add("ICNAVTYPEDROPDOWN", "1")
            .add("ICType", "Panel")
            .add("ICElementNum", "0")
            .add("ICStateNum", icStateNumKey)
            .add("ICAction", icActionKey)
            .add("ICModelCancel", "0")
            .add("ICXPos", "0")
            .add("ICYPos", "0")
            .add("ResponsetoDiffFrame", "-1")
            .add("TargetFrameName", "None")
            .add("FacetPath", "None")
            .add("ICFocus", " ")
            .add("ICSaveWarningFilter", "0")
            .add("ICChanged", "-1")
            .add("ICSkipPending", "0")
            .add("ICAutoSave", "0")
            .add("ICResubmit", "0")
            .add("ICSID", ICSIDKey)
            .add("ICActionPrompt", "false")
            .add("ICTypeAheadID", "")
            .add("ICBcDomData", "")
            .add("ICPanelName", "")
            .add("ICAddCount:", "")
            .add("ICAPPCLSDATA", "")
            .add("DERIVED_SSTSNAV_SSTS_MAIN_GOTO\$27\$", "0100")
            .add("DERIVED_REGFRM1_CLASS_NBR", "")
            .add("DERIVED_REGFRM1_SSR_CLS_SRCH_TYPE\$252\$", "10")


        for (i in 0..0) {
            formBody.add("P_SELECT\$chk\$" + i, "Y")
            formBody.add("P_SELECT\$" + i,"Y")
            log("P_SELECT\$chk\$" + i)

        }

        return formBody.build()
    }

    private fun getSecondFormData(
        ICSIDKey: String,
        icStateNumKey: String,
        icActionKey: String): FormBody {
        var formBody = FormBody.Builder()
            .add("ICAJAX", "1")
            .add("ICNAVTYPEDROPDOWN", "1")
            .add("ICType", "Panel")
            .add("ICElementNum", "0")
            .add("ICStateNum", icStateNumKey)
            .add("ICAction", icActionKey)
            .add("ICModelCancel", "0")
            .add("ICXPos", "0")
            .add("ICYPos", "0")
            .add("ResponsetoDiffFrame", "-1")
            .add("TargetFrameName", "None")
            .add("FacetPath", "None")
            .add("ICFocus", " ")
            .add("ICSaveWarningFilter", "0")
            .add("ICChanged", "-1")
            .add("ICSkipPending", "0")
            .add("ICAutoSave", "0")
            .add("ICResubmit", "0")
            .add("ICSID", ICSIDKey)
            .add("ICActionPrompt", "false")
            .add("ICTypeAheadID", "")
            .add("ICBcDomData", "")
            .add("ICPanelName", "")
            .add("ICAddCount:", "")
            .add("ICAPPCLSDATA", "")
            .add("DERIVED_SSTSNAV_SSTS_MAIN_GOTO\$27\$", "0100")

        return formBody.build()

    }

    inner class AddClassAdapter(private val shoppingCart: ListOfAddEnrollShoppingCart) :
        RecyclerView.Adapter<AddClassAdapter.AddClassHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddClassHolder {
            numClasses = shoppingCart.shoppingCart.size

            val view =
                LayoutInflater.from(context).inflate(R.layout.shopping_cart_items, parent, false)
            return AddClassHolder(view)
        }

        override fun getItemCount(): Int {

            numClasses = shoppingCart.shoppingCart.size
            log("THIS IS THE NUMBER OF CLASSES IN THE SHOPPING CART $numClasses")

            return shoppingCart.shoppingCart.size
        }


        override fun onBindViewHolder(holder: AddClassHolder, position: Int) {

            numClasses = shoppingCart.shoppingCart.size
            log("THIS IS THE NUMBER OF CLASSES IN THE SHOPPING CART $numClasses")

            with(holder.itemView) {

                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val linearLayout = findViewById<LinearLayout>(R.id.shopping_cart_items_layout)


                addButtonsToCourses(params, linearLayout, buttonNames, holder, position)

                description_enroll_sc.text = shoppingCart.shoppingCart[position].className
                days_and_times_enroll_sc.text = shoppingCart.shoppingCart[position].daysAndTimes
                instructor_enroll_sc.text = shoppingCart.shoppingCart[position].instructor
                room_number_enroll_sc.text = shoppingCart.shoppingCart[position].room

            }


        }


        inner class AddClassHolder(textView: View) : RecyclerView.ViewHolder(textView)


    }
}

data class ListOfAddEnrollShoppingCart(
    @Selector("tr[id^=trSSR_REGFORM_VW]")
    var shoppingCart: List<AddEnrollShoppingCart> = emptyList()

)

data class AddEnrollShoppingCart(

    @Selector("span[id^=DERIVED_REGFRM1_SSR_MTG_LOC_LONG]")
    var room: String = "",

    @Selector("span[id^=P_CLASS_NAME]")
    var className: String = "",

    @Selector("span[id^=DERIVED_REGFRM1_SSR_MTG_SCHED_LONG]")
    var daysAndTimes: String = "",

    @Selector("span[id^=DERIVED_REGFRM1_SSR_INSTR_LONG]")
    var instructor: String = "",

    @Selector("span[id^=SSR_REGFORM_VW_UNT_TAKEN]")
    var units: String = "",

    @Selector("img[src=\"/cs/ecampus/cache27/PS_CS_STATUS_DROPPED_ICN_1.gif\"]")
    var status: String = "NotDropped"


)

