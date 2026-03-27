package com.example.localmarketplace

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var gridAllProducts: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        gridAllProducts = view.findViewById(R.id.gridAllProducts)
        return view
    }

    override fun onResume() {
        super.onResume()
        loadProductsProgressively()
    }

    private fun loadProductsProgressively() {
        gridAllProducts.removeAllViews()
        ProductRepository.getProductIds { ids ->
            activity?.runOnUiThread {
                if (ids != null) {
                    for (id in ids) {
                        val placeholder = createPlaceholderCard()
                        gridAllProducts.addView(placeholder)
                        fetchIndividualProduct(id, placeholder)
                    }
                } else {
                    Toast.makeText(context, "Failed to load marketplace", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchIndividualProduct(id: String, placeholder: CardView) {
        ProductRepository.getProductById(id) { product ->
            activity?.runOnUiThread {
                if (product != null) {
                    updateCardWithProduct(placeholder, product)
                } else {
                    gridAllProducts.removeView(placeholder)
                }
            }
        }
    }

    private fun createPlaceholderCard(): CardView {
        val cardView = CardView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 180.dpToPx()
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
            }
            radius = 12.dpToPx().toFloat()
            cardElevation = 2.dpToPx().toFloat()
        }

        val progressBar = ProgressBar(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.CENTER
            )
        }
        
        val frameLayout = FrameLayout(requireContext())
        frameLayout.addView(progressBar)
        cardView.addView(frameLayout)
        
        return cardView
    }

    private fun updateCardWithProduct(cardView: CardView, product: Product) {
        cardView.removeAllViews()
        cardView.cardElevation = 6.dpToPx().toFloat()
        
        val linearLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
        }

        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                100.dpToPx()
            )
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setImageResource(R.drawable.ic_marketproducts)
        }

        val tvName = TextView(requireContext()).apply {
            text = product.name
            setTextColor(resources.getColor(R.color.black, null))
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 8.dpToPx() }
        }

        val tvPrice = TextView(requireContext()).apply {
            text = product.price
            setTextColor(resources.getColor(R.color.dark_blue, null))
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        linearLayout.addView(imageView)
        linearLayout.addView(tvName)
        linearLayout.addView(tvPrice)
        cardView.addView(linearLayout)

        cardView.setOnClickListener {
            ProductRepository.selectedProduct = product
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}
