package com.jmu.mymadisonapp.ui.jaccardservices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_myservices.*
import kotlinx.android.synthetic.main.recent_meal_plan_transactions.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector


class MyServicesFragment : Fragment()
{

    lateinit var service: MyMadisonService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_myservices, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        service = get()

        lifecycleScope.launch {
            val csInfo = service.getCardServicesInformation().await().body()
            val mealPlanInfo = service.getMealPlanInformation().await().body()
            val flexBalance = parseCardServicesInfo(csInfo!!.cardServicesInformation)[0]
            val diningBalance = parseCardServicesInfo(csInfo!!.cardServicesInformation)[1]
            meal_plan_transactions_recycler_view.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            meal_plan_transactions_recycler_view.adapter = MyServicesAdapter(mealPlanInfo!!)


            MainScope().launch {
                my_services_text_view.text = "Remaining Flex Balance: $flexBalance\nRemaining Dining Balance $diningBalance"

            }
        }

        jac_card_logo_imageview.setOnClickListener {

            fragmentManager?.commit {
                replace(R.id.myservices_layout, CardServicesBrowserFragment())
                    .addToBackStack(null)
            }
        }
    }

    private fun parseCardServicesInfo(balance: String): List<String> {

        val parsedInfo: List<String> = balance.split("Dining")
        val flexBalance: List<String> = parsedInfo[0].split(":")
        val diningBalance: List<String> = parsedInfo[1].split(":")

        val parsedFlexBalance: String = flexBalance[1]
        val parsedDiningBalance: String = diningBalance[1]
        val parsedList: ArrayList<String> = ArrayList()

        parsedList.add(parsedFlexBalance)
        parsedList.add(parsedDiningBalance)
        return parsedList
    }

    inner class MyServicesAdapter(val cardServicesInformation: CardServicesInformation) : RecyclerView.Adapter<MyServicesAdapter.MyServicesHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyServicesHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.recent_meal_plan_transactions, parent, false)
            return MyServicesHolder(view)
        }

        override fun getItemCount(): Int {
            return cardServicesInformation.table.size
        }

        override fun onBindViewHolder(holder: MyServicesHolder, position: Int) {

            with(holder.itemView) {
                date_and_time_transactions.text = cardServicesInformation.table[position].location
            }
        }


        inner class MyServicesHolder(textView: View) : RecyclerView.ViewHolder(textView) {

        }
    }
}


data class CardServicesInformation(
    @Selector("div[id^=JMU_CARDSVC_SSO_Data]")
    var table: List<MealPlanTransactions> = emptyList()
)

data class MealPlanTransactions(
    @Selector("table")
    var location: String = ""
)

data class CardServices(
    @Selector("table")
    var cardServicesInformation: String = ""


)