package com.money.xpensesbookproject.ui.category



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.money.xpensesbookproject.ui.dialogs.AddCategoryDialog
import com.money.xpensesbookproject.databinding.ActivityCategoryListBinding


class CategoryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButton()
    }

    private fun setupButton() {
        binding.btnAdd.setOnClickListener {
            AddCategoryDialog().show(supportFragmentManager,"AddCategory")
        }
    }
}