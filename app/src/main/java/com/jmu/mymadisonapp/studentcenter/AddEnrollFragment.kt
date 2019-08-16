package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.buttonNames
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MYMADISON_LOGIN_BASE_URL
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_add_enroll.*
import kotlinx.android.synthetic.main.shopping_cart_items.view.*
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector
import retrofit2.Call
import retrofit2.Retrofit
import java.text.Normalizer


class AddEnrollFragment : Fragment() {


    lateinit var service: MyMadisonService
    lateinit var client: OkHttpClient
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_enroll, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        client = get()
        service = get()
        add_all_classes_button.setOnClickListener {
            val thread = Thread {
                val responseBody: String? =
                    client.newCall(
                        Request.Builder()
                            .url("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
                            .get()
                            .build()
                    ).execute().body()?.string()

                val ICSID: String =
                    Jsoup.parse(responseBody).select("#ICSID").`val`()

                val formBody = FormBody.Builder()
                    .add("ICSID", ICSID)
                    .add("ICAction", "DERIVED_REGFRM1_SSR_PB_SUBMIT").build()

                service.confirmClassSelection(formBody)

            }
            thread.start()
            thread.join()

        }



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

    }


    private fun getFormBody(
        ICSIDKey: String,
        icStateNumKey: String,
        position: Int
    ): FormBody {

        var formBody = FormBody.Builder()
            .add("ICAJAX", "1")
            .add("ICNAVTYPEDROPDOWN", "1")
            .add("ICType", "Panel")
            .add("ICElementNum", "0")
            .add("ICStateNum", icStateNumKey)
            .add("ICAction", "P_DELETE\$${position}")
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
            .add("DERIVED_REGFRM1_SSR_CLS_SRCH_TYPE\$252\$", "10").build()

        return formBody
    }

    inner class AddClassAdapter(private val shoppingCart: ListOfAddEnrollShoppingCart) :
        RecyclerView.Adapter<AddClassAdapter.AddClassHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddClassHolder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.shopping_cart_items, parent, false)
            return AddClassHolder(view)
        }

        override fun getItemCount(): Int {
            return shoppingCart.shoppingCart.size
        }

        override fun onBindViewHolder(holder: AddClassHolder, position: Int) {


            with(holder.itemView) {


                drop_button_shopping_cart.setOnClickListener {
                    Thread {
                        val responseBody: String? =
                            client.newCall(
                                Request.Builder()
                                    .url("https://mymadison.ps.jmu.edu/psc/ecampus/JMU/SPRD/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL")
                                    .get()
                                    .build()
                            ).execute().body()?.string()

                        val ICAction: String =
                            Jsoup.parse(responseBody).selectFirst("#ICAction").`val`()
                        val ICStateNum: String =
                            Jsoup.parse(responseBody).selectFirst("input[name=ICStateNum]").`val`()
                        val ICSID: String =
                            Jsoup.parse(responseBody).select("#ICSID").`val`()


                        val formBody = getFormBody(
                            ICSIDKey = ICSID,
                            icStateNumKey = ICStateNum,
                            position = position
                        )
                        service.deleteSelectedClass(formBody)
                        description_enroll_sc.text = "Dropped the class!"
                    }.start()
                }
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

    @Selector("div[id^=win0divDERIVED_REGFRM1_SSR_STATUS_LONG]")
    var status: String = ""


)

