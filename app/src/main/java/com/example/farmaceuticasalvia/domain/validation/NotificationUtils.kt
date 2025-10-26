package com.example.farmaceuticasalvia.domain.validation

import android.Manifest
import com.example.farmaceuticasalvia.R
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.farmaceuticasalvia.CHANNEL_ID

fun showPurchaseNotification(context: Context, productName: String) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        if(ContextCompat.checkSelfPermission(
            context,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
            ) {
            Log.w("NotificationUtils", "Permiso de notificaciones denegado.")
            return
        }
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ibuprofeno)
        .setContentTitle("Compra Exitosa")
        .setContentText("Tu compra de ${productName} se ha realizado.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)){
        notify(productName.hashCode(), builder.build())
    }
}