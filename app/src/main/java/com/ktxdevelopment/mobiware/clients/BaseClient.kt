package com.ktxdevelopment.mobiware.clients

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.ktxdevelopment.mobiware.models.rest.search.Phone

object BaseClient {

    fun playStoreIntent(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                )
            )
        } catch (e: Exception) {
            Log.i("MR_TAG", "openPlayStore: ${e.message}")
        }
    }

    fun setDarkMode(isDark: Boolean) {
        if (isDark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }


    @Suppress("DEPRECATION")
    fun hasInternetConnection(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
            return false
        }
    }







    fun whichModelSuits(list: List<Phone>) : List<Phone> {
        val note: ArrayList<Phone> = ArrayList()
        val lite: ArrayList<Phone> = ArrayList()
        val plus: ArrayList<Phone> = ArrayList()
        val mate: ArrayList<Phone> = ArrayList()
        val ultra: ArrayList<Phone> = ArrayList()
        val pro: ArrayList<Phone> = ArrayList()
        val notePro: ArrayList<Phone> = ArrayList()
        val none: ArrayList<Phone> = ArrayList()

        for (i in list) {
            if (i.phone_name.contains(DModel.NOTE) && i.phone_name.contains(DModel.PRO)) { notePro.add(i) }
            else if (i.phone_name.contains(DModel.NOTE)) { note.add(i) }
            else if (i.phone_name.contains(DModel.LITE)) { lite.add(i) }
            else if (i.phone_name.contains(DModel.PRO)) { pro.add(i) }
            else if (i.phone_name.contains(DModel.ULTRA)) { ultra.add(i) }
            else if (i.phone_name.contains(DModel.MATE)) { mate.add(i) }
            else if (i.phone_name.contains(DModel.PLUS)) { plus.add(i) }
            else { none.add(i) }
        }

        if (list.size == 1) { return list }
        else{
            val sign = checkDeviceBuildSign()

            if (sign == DModel.NOTE) return note
            if (sign == DModel.LITE) return lite
            if (sign == DModel.PLUS) return plus
            if (sign == DModel.PRO) return pro
            if (sign == DModel.ULTRA) return ultra
            if (sign == DModel.NOTE_PRO) return notePro
            if (sign == DModel.MATE) return mate
            if (sign == DModel.NO) return none
            return list
        }
    }

    private fun checkDeviceBuildSign(): String {
        val m = Build.MODEL
        return if (m.lowercase().contains("note") && m.contains("pro")) DModel.NOTE_PRO
        else if (m.lowercase().contains("note")) DModel.NOTE
        else if (m.lowercase().contains("lite")) DModel.LITE
        else if (m.lowercase().contains("pro")) DModel.PRO
        else if (m.lowercase().contains("ultra")) DModel.ULTRA
        else if (m.lowercase().contains("plus")) DModel.PLUS
        else if (m.lowercase().contains("mate")) DModel.MATE
        else DModel.NO
    }

    fun getDeviceModel(): String {
        return "gh emulatror"
    }

    object DModel {
        const val NO = "no"
        const val NOTE = "note"
        const val LITE = "lite"
        const val PRO = "pro"
        const val PLUS = "plus"
        const val MATE = "mate"
        const val NOTE_PRO = "note_pro"
        const val ULTRA = "ultra"
    }
}