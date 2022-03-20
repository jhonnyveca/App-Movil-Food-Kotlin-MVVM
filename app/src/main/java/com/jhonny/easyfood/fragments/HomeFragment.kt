package com.jhonny.easyfood.fragments


import android.content.Intent
import androidx.lifecycle.ViewModelProviders

import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jhonny.easyfood.activities.CategoryMealsActivity
import com.jhonny.easyfood.activities.MealActivity
import com.jhonny.easyfood.adapters.CategoriesAdapter
import com.jhonny.easyfood.adapters.MostPopularAdapter

import com.jhonny.easyfood.databinding.FragmentHomeBinding
import com.jhonny.easyfood.pojo.MealsByCategory
import com.jhonny.easyfood.pojo.Meal

import com.jhonny.easyfood.videoModel.HomeViewModel



class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeMvvm:HomeViewModel
    private lateinit var randomeMeal:Meal
    private lateinit var popularItemsAdapter:MostPopularAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    companion object{
        const val MEAL_ID = "com.jhonny.easyfood.fragments.idMeal"
        const val MEAL_NAME = "com.jhonny.easyfood.fragments.nameMeal"
        const val MEAL_THUMB = "com.jhonny.easyfood.fragments.thumbMeal"
        const val CATEGORY_NAME = "com.jhonny.easyfood.fragments.categoryName"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeMvvm = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        popularItemsAdapter = MostPopularAdapter()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preparePopularItemsRecyclerView()

        homeMvvm.getRandomMeal()
        observerRandomMeal()
        onRandomMealClick()

        homeMvvm.getPopularItems()
        observerPopularItemsLiveData()
        onPopularItemClick()

        prepareCategoriesRecyclerView()
        homeMvvm.getCategories()
        observerCategoriesLiveData()
        onCategoryClick()


    }

    private fun onCategoryClick() {
        categoriesAdapter.onItemClick = {category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME,category.strCategory)
            startActivity(intent)
        }
    }

    private fun prepareCategoriesRecyclerView() {
        categoriesAdapter = CategoriesAdapter()
        binding.recViewCategories.apply {
            layoutManager = GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false)
            adapter = categoriesAdapter
        }
    }

    private fun observerCategoriesLiveData() {
       homeMvvm.observeCategoriesLiveData().observe(viewLifecycleOwner, Observer {
           categories ->
            categoriesAdapter.setCategoryList(categories)

       })
    }

    private fun onPopularItemClick() {
        popularItemsAdapter.onItemClick = {
            meal -> val intent = Intent(activity,MealActivity::class.java)
            intent.putExtra(MEAL_ID,meal.idMeal)
            intent.putExtra(MEAL_NAME,meal.strMeal)
            intent.putExtra(MEAL_THUMB,meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun preparePopularItemsRecyclerView() {
      binding.recViewMealsPopular.apply {
          layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
          adapter = popularItemsAdapter
      }
    }

    private fun observerPopularItemsLiveData() {
        homeMvvm.observePopularItemsLiveData().observe(viewLifecycleOwner,
            {mealList ->
                popularItemsAdapter.setMeals(mealsByCategoryList = mealList as ArrayList<MealsByCategory>)
            })
    }

    private fun onRandomMealClick() {
        binding.randomMealCard.setOnClickListener{
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID,randomeMeal.idMeal)
            intent.putExtra(MEAL_NAME,randomeMeal.strMeal)
            intent.putExtra(MEAL_THUMB,randomeMeal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun observerRandomMeal() {
        homeMvvm.observeRandomMealLivedata().observe(viewLifecycleOwner
        ) { meal ->
            Glide.with(this@HomeFragment)
                .load(meal!!.strMealThumb)
                .into(binding.imgRandomMeal)
            this.randomeMeal = meal
        }
    }

}