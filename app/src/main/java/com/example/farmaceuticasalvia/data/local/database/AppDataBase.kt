package com.example.farmaceuticasalvia.data.local.database

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.farmaceuticasalvia.data.local.products.ProductDao
import com.example.farmaceuticasalvia.data.local.products.ProductEntity
import com.example.farmaceuticasalvia.data.local.user.UserDao
import com.example.farmaceuticasalvia.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope                        // Para corrutinas en callback
import kotlinx.coroutines.Dispatchers                           // Dispatcher IO
import kotlinx.coroutines.launch
import com.example.farmaceuticasalvia.R

@Database(
    entities = [UserEntity::class, ProductEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao

    companion object{

        @Volatile
        private var INSTANCE: AppDataBase? = null

        private const val DB_NAME = "FarmaceuticaSalvia.db"

        fun getInstance(context: Context): AppDataBase{
            return INSTANCE ?: synchronized(this){

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    DB_NAME
                )

                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).userDao()
                                val daoProd = getInstance(context).productDao()

                                val seed = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "admin@gmail.com",
                                        phone = "+56911111111",
                                        password = "Admin123!"
                                    ),
                                    UserEntity(
                                        name = "Ale",
                                        email = "ale@gmail.com",
                                        phone = "+56922222222",
                                        password = "Ale123!"
                                    )
                                )

                                if(dao.count() == 0) {
                                    seed.forEach { dao.insert(it) }
                                }

                                val seedProducts = listOf(
                                    ProductEntity(
                                        name = "Paracetamol",
                                        descr = "Alivia el dolor y reduce la fiebre",
                                        price = 1500,
                                        imageRes = R.drawable.paracetamol,
                                        featured = true
                                    ),
                                    ProductEntity(
                                        name = "Ibuprofeno",
                                        descr = "Antiinflamatorio y análgesico",
                                        price = 1800,
                                        imageRes = R.drawable.ibuprofeno,
                                        featured = false
                                    )
                                )
                                if(daoProd.count() == 0){
                                    seedProducts.forEach { daoProd.insert(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}