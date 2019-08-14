package com.jmu.mymadisonapp.ui.jaccardservices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_myservices.*
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
            log("This is the card services info $csInfo")
            val flexBalance = parseCardServicesInfo(csInfo!!.cardServicesInformation)[0]
            val diningBalance = parseCardServicesInfo(csInfo!!.cardServicesInformation)[1]
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
}



data class CardServices(
    @Selector("table")
    var cardServicesInformation: String = ""
)