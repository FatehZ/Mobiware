package com.ktxdevelopment.mobiware.clients.ui

import android.content.Context
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.ActivitySignInBinding
import com.ktxdevelopment.mobiware.databinding.ActivitySignUpBinding
import com.ktxdevelopment.mobiware.ui.recview.SelectionAdapter

object SignInUpClient {
    fun initializeRecyclerView(
        context: Context,
        binding: ActivitySignUpBinding,
        adapter: SelectionAdapter
    ) {
        val lManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        lManager.stackFromEnd = true
        binding.rvSelectionMobiles.layoutManager = lManager

        binding.rvSelectionMobiles.adapter = adapter
        binding.tvSignPhoneModel.visibility = VISIBLE
    }


    fun initializeRecyclerViewIn(
        context: Context,
        binding: ActivitySignInBinding,
        adapter: SelectionAdapter
    ) {
        val lManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        lManager.stackFromEnd = true
        binding.rvSelectionMobilesIn.layoutManager = lManager

        binding.rvSelectionMobilesIn.adapter = adapter
        binding.tvSignPhoneModelIn.visibility = VISIBLE
    }


    fun toastNoConnection(context: Context) = Toast.makeText(context, context.getString(R.string.no_connection_error), Toast.LENGTH_SHORT).show()

    fun toastSelectPhone(context: Context) = Toast.makeText(context, context.getString(R.string.select_a_phone_error), Toast.LENGTH_SHORT).show()
}
