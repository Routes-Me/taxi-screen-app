package com.routesme.taxi_screen.kotlin.SideFragmentAdapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.routesme.taxi_screen.kotlin.Model.DateCell
import com.routesme.taxi_screen.kotlin.Model.DiscountCell
import com.routesme.taxi_screen.kotlin.Model.ISideFragmentCell
import com.routesme.taxi_screen.kotlin.Model.WifiCell

@SuppressLint("SetTextI18n")
fun onBindDate(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderDate
    cell as DateCell
    holder.apply { clockTv.text = cell.clock; weekDayTv.text = cell.weekDay; monthDayTv.text = cell.monthDay }
}

fun onBindWifi(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderWifi
    cell as WifiCell
    holder.apply { nameTv.text = cell.name; passwordTv.text = cell.password }
}

fun onBindDiscount(holder: RecyclerView.ViewHolder, cell: ISideFragmentCell) {
    holder as ViewHolderDiscount
    cell as DiscountCell
    holder.apply { discountTv.text = cell.title }
}