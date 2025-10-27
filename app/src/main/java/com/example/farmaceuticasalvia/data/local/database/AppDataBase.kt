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
import com.example.farmaceuticasalvia.data.local.cart.CartDao
import com.example.farmaceuticasalvia.data.local.cart.CartItemEntity
import com.example.farmaceuticasalvia.data.local.history.HistoryDao
import com.example.farmaceuticasalvia.data.local.history.HistoryItemEntity

@Database(
    entities = [UserEntity::class, ProductEntity::class, CartItemEntity::class, HistoryItemEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun CartDao(): CartDao
    abstract fun HistoryDao(): HistoryDao

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
                        }

                        override fun onOpen(db: SupportSQLiteDatabase){
                            super.onOpen(db)

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
                                        descr = "Alivia el dolor y reduce la fiebre.",
                                        price = 2990,
                                        imageRes = R.drawable.paracetamol,
                                        featured = true
                                    ),
                                    ProductEntity(
                                        name = "Ibuprofeno",
                                        descr = "Antiinflamatorio y análgesico.",
                                        price = 3990,
                                        imageRes = R.drawable.ibuprofeno,
                                        featured = false
                                    ),
                                    ProductEntity(
                                        name = "Vitamina C",
                                        descr = "Refuerza tu sistema inmune. Frasco con 30 cápsulas.",
                                        price = 5490,
                                        imageRes = R.drawable.vitamina_c,
                                        featured = true
                                    ),
                                    ProductEntity(
                                        name = "Amoxicilina",
                                        descr = "Antibiotico.",
                                        price = 10950,
                                        imageRes = R.drawable.amoxicilina,
                                        featured = true
                                    ),
                                    ProductEntity(
                                        name = "Omeprazol",
                                        descr = "Alivio de la acidez estomacal. Caja de 14 cápsulas.",
                                        price = 7990,
                                        imageRes = R.drawable.omeprazol,
                                        featured = true
                                    ),
                                    ProductEntity(
                                        name = "Loratadina",
                                        descr = "Alivio de alergias y síntomas nasales. Caja de 10 comprimidos.",
                                        price = 4590,
                                        imageRes = R.drawable.loratadina,
                                        featured = true
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