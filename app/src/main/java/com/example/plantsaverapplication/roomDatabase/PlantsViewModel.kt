package com.example.plantsaverapplication.roomDatabase

import android.app.Application
import android.content.Context
import android.provider.ContactsContract.Data
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.danlew.android.joda.JodaTimeAndroid.init
import javax.inject.Inject

/**
 * Krizbai Csaba - 2023.1.18
 *
 * @ViewModel_benefits:
 * The alternative to a ViewModel is a plain class that holds the data you display in your UI. This
 * can become a problem when navigating between activities or Navigation destinations. Doing so destroys
 * that data if you don't store it using the saving instance state mechanism. ViewModel provides a
 * convenient API for data persistence that resolves this issue.
 *
 */


class PlantsViewModel (application: Application) : ViewModel() {

    var plant by mutableStateOf(Plants())
    var plantsList  by mutableStateOf(emptyList<Plants>())
    var dtbHandler: DatabaseHandler = DatabaseHandler.getInstance(application)

    val allProducts: LiveData<List<Plants>>
    private val repository: PlantsRepository

    init {
        dtbHandler = DatabaseHandler.getInstance(application)
        repository = PlantsRepository(dtbHandler.plantDao())
        allProducts = repository.allPlants
    }

    fun insertPlant(newPlant: Plants){
        viewModelScope.launch {
            dtbHandler.insertPlants(newPlant)
        }
    }

    fun deletePlant(plantId: Int){
        viewModelScope.launch{
            dtbHandler.deletePlant(plantId)
        }
    }

}

class PlantViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlantsViewModel(application) as T
    }
}

class PlantsRepository(productDao: PlantsDAO) {
    val allPlants: LiveData<List<Plants>> = productDao.getPlantsByDate()
}


