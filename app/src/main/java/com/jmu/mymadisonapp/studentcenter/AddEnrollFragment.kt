package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_add_enroll.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector


class AddEnrollFragment : Fragment() {


    lateinit var service: MyMadisonService
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_enroll, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        service = get()
        lifecycleScope.launch {
            val enrolledClasses = service.getShoppingCartClasses().await().body()
            log("List of enrolled classes ${enrolledClasses!!.shoppingCart}")
            MainScope().launch {

                var textViewText: String = ""



                if (enrolledClasses.shoppingCart.isEmpty()) {
                    add_enroll_text_view.text = "Your shopping cart is empty."
                } else {

                    for (info in enrolledClasses.shoppingCart) {
                        textViewText += "${info.className}\n${info.daysAndTimes}\n${info.room}" +
                                "\n${info.instructor}\n${info.units}\n${info.status}"
                    }

                    add_enroll_text_view.text = textViewText
                }
            }

        }

    }


    inner class AddClassAdapter()
    inner class AddClassHolder(textView: View) : RecyclerView.ViewHolder(textView)

}

data class ListOfAddEnrollShoppingCart(
    @Selector("table[id^=SSR_REGFORM_VW]")
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