package com.ktxdevelopment.mobiware.clients.firebase

import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.util.Constants

object ListUtilClient {
//     fun getBrandList(): ArrayList<String> {
//          return arrayListOf(
//               Constants.XIAOMI,
//               Constants.REALME,
//               Constants.SAMSUNG,
//               Constants.LG,
//               Constants.APPLE,
//               Constants.HONOR,
//               Constants.HUAWEI,
//               Constants.VIVO,
//               Constants.OPPO,
//               Constants.SONY,
//               Constants.LENOVO,
//               Constants.NOKIA,
//               Constants.PHILIPS,
//               Constants.ONEPLUS
//          ).also { it.sort() }
//     }


     fun getDeviceModelLogo(brand: String): Int {
          return when (brand.lowercase()) {
               (Constants.XIAOMI) -> R.drawable.xiaomi
               (Constants.SAMSUNG) -> R.drawable.samsung
               (Constants.APPLE) -> R.drawable.apple
               (Constants.HONOR) -> R.drawable.honor
               (Constants.HUAWEI) -> R.drawable.huawei
               (Constants.LG) -> R.drawable.lg
               (Constants.REALME) -> R.drawable.realme
               (Constants.PHILIPS) -> R.drawable.philips
               (Constants.LENOVO) -> R.drawable.lenovo
               (Constants.ONEPLUS) -> R.drawable.oneplus
               (Constants.OPPO) -> R.drawable.oppo
               (Constants.VIVO) -> R.drawable.vivo
               (Constants.SONY) -> R.drawable.sony
               (Constants.NOKIA) -> R.drawable.nokia
               (Constants.GOOGLE) -> R.drawable.google
               else -> -200
          }
     }

//     fun getBrands() = arrayListOf(
//          BrandModel(Constants.XIAOMI, R.drawable.xiaomi),
//          BrandModel(Constants.SAMSUNG, R.drawable.samsung),
//          BrandModel(Constants.APPLE, R.drawable.apple),
//          BrandModel(Constants.HONOR, R.drawable.honor),
//          BrandModel(Constants.HUAWEI, R.drawable.huawei),
//          BrandModel(Constants.LG, R.drawable.lg),
//          BrandModel(Constants.REALME, R.drawable.realme),
//          BrandModel(Constants.PHILIPS, R.drawable.philips),
//          BrandModel(Constants.LENOVO, R.drawable.lenovo),
//          BrandModel(Constants.ONEPLUS, R.drawable.oneplus),
//          BrandModel(Constants.OPPO, R.drawable.oppo),
//          BrandModel(Constants.VIVO, R.drawable.vivo),
//          BrandModel(Constants.SONY, R.drawable.sony),
//          BrandModel(Constants.NOKIA, R.drawable.nokia),
//          BrandModel(Constants.GOOGLE, R.drawable.google)
//     )
}