package com.ktxdevelopment.mobiware.clients.firebase

import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.util.Constants

object ListUtilClient {

     fun getDeviceModelLogo(brand: String): Int {
          return when (brand.lowercase()) {
               (Constants.XIAOMI) -> R.drawable.xiaomi
               (Constants.SAMSUNG) -> R.drawable.samsung
               (Constants.APPLE) -> R.drawable.apple
               (Constants.HONOR) -> R.drawable.honor
               (Constants.HUAWEI) -> R.drawable.huawei
               (Constants.LG) -> R.drawable.lg
               (Constants.BLU) -> R.drawable.blu
               (Constants.REALME) -> R.drawable.realme
               (Constants.PHILIPS) -> R.drawable.philips
               (Constants.LENOVO) -> R.drawable.lenovo
               (Constants.ONEPLUS) -> R.drawable.oneplus
               (Constants.OPPO) -> R.drawable.oppo
               (Constants.VIVO) -> R.drawable.vivo
               (Constants.SONY) -> R.drawable.sony
               (Constants.NOKIA) -> R.drawable.nokia
               (Constants.VERTU) -> R.drawable.vertu
               (Constants.GOOGLE) -> R.drawable.google
               (Constants.ZTE) -> R.drawable.zte
               (Constants.ALCATEL) -> R.drawable.alcatel
               (Constants.ASUS) -> R.drawable.asus
               (Constants.BLACKVIEW) -> R.drawable.blackview
               (Constants.CAT) -> R.drawable.cat
               (Constants.HTC) -> R.drawable.htc
               (Constants.MOTOROLA) -> R.drawable.motorola
               (Constants.MEIZU) -> R.drawable.meizu
               (Constants.TCL) -> R.drawable.tcl
               (Constants.ENERGIZER) -> R.drawable.energizer
               (Constants.NOTHING) -> R.drawable.nothing
               (Constants.TECNO) -> R.drawable.tecno
               (Constants.ULEFONE) -> R.drawable.ulefone
               (Constants.VODAFONE) -> R.drawable.vodafone
               else -> -200
          }
     }
}