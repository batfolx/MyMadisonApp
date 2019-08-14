package com.jmu.mymadisonapp.studentcenter

import android.os.Bundle
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
import com.jmu.mymadisonapp.net.MYMADISON_LOGIN_BASE_URL
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_add_enroll.*
import kotlinx.android.synthetic.main.shopping_cart_items.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector
import retrofit2.Call
import retrofit2.Retrofit


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
        lifecycleScope.launch {
            val enrolledClasses = service.getShoppingCartClasses().await().body()
            shopping_cart_recycler_view.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            shopping_cart_recycler_view.adapter = AddClassAdapter(enrolledClasses!!)
            for (item in enrolledClasses.shoppingCart) {
                log("List of enrolled classes $item")
            }
        }

    }

    fun addButtonsToCourses(
        linearLayoutParams: LinearLayout.LayoutParams,
        linearLayout: LinearLayout,
        buttonNames: Array<String>,
        holder: AddClassAdapter.AddClassHolder
    ) {
        var count: Int = 0
        with(holder.itemView) {

            for (name in buttonNames) {

                val tmpButton = Button(context)
                tmpButton.text = name
                when (name) {
                    "Drop" -> tmpButton.setOnClickListener {
                        //TODO send post data to JMU server telling the server that the user wants to drop the class
                        description_enroll_sc.text = "Description changed when the name is Drop!"


                        val ICAction = "P_DELETE\$${count}"
                        count++
                        lifecycleScope.launch {
                            service.deleteSelectedClass(
                                FormBody.Builder().add("ICAction", ICAction).build()
                            ).await()
                        }




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

inner class AddClassAdapter(private val shoppingCart: ListOfAddEnrollShoppingCart) :
    RecyclerView.Adapter<AddClassAdapter.AddClassHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddClassHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shopping_cart_items, parent, false)
        return AddClassHolder(view)
    }

    override fun getItemCount(): Int {
        return shoppingCart.shoppingCart.size
    }

    override fun onBindViewHolder(holder: AddClassHolder, position: Int) {


        with(holder.itemView) {

            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val linearLayout = findViewById<LinearLayout>(R.id.shopping_cart_items_layout)


            addButtonsToCourses(params, linearLayout, buttonNames, holder)

            description_enroll_sc.text = shoppingCart.shoppingCart[position].className
            days_and_times_enroll_sc.text = shoppingCart.shoppingCart[position].daysAndTimes
            instructor_enroll_sc.text = shoppingCart.shoppingCart[position].instructor
            room_number_enroll_sc.text = shoppingCart.shoppingCart[position].room


        }
    }


    inner class AddClassHolder(textView: View) : RecyclerView.ViewHolder(textView) {

    }
}
}

data class ListOfAddEnrollShoppingCart(
    @Selector("table[id^=SSR_REGFORM_VW]")
    var shoppingCart: List<AddEnrollShoppingCart> = emptyList()

)

data class AddEnrollShoppingCart(

    @Selector("span[id^=DERIVED_REGFRM1_SSR_MTG_LOC_LONG]")
    var room: String = "",

    @Selector("span[id^=P_CLASS_NAME\\\$span]")
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