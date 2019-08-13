package com.jmu.mymadisonapp.ui.myaccounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jmu.mymadisonapp.R
import com.jmu.mymadisonapp.log
import com.jmu.mymadisonapp.net.MyMadisonService
import kotlinx.android.synthetic.main.fragment_my_account_information.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import pl.droidsonroids.jspoon.annotation.Selector


class MyAccountsFragment : Fragment() {

    lateinit var service: MyMadisonService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_account_information, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // gets the singleton instance of the MyMadisonService being passed around
        service = get<MyMadisonService>()

        lifecycleScope.launch {
            val accountInfo = service.getMyAccountInformation().await().body()

            MainScope().launch {
                log("this is the information from the eid ${accountInfo!!.eidLastChanged}")
                eid_password_last_changed.text = accountInfo.eidLastChanged
                eid_expiration_date.text = "\nYour eID password will expire on: ${accountInfo?.eidExpirationDate}"

            }
        }

    }

}


data class MyAccountAccounts(
    @Selector("span[id^=JPP_ID_JPP_EID_LAST_CHANG]")
    var eidLastChanged: String = "",

    @Selector("span[id^=JPP_ID_JPP_EID_EXPIRE]")
    var eidExpirationDate: String = ""
)