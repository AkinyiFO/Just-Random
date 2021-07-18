package com.simpleapps22.justrandom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import com.simpleapps22.justrandom.databinding.ActivityMainBinding
import kotlin.random.Random

/**
 * Just Random Generates random numbers of given sample size
 * and range. It also allows users to copy the generated result.
 */
class MainActivity : AppCompatActivity() {
    //Used in Menu item to redirect to app store
    private val APP_PROJECT_NAME = "com.simpleapps22.justrandom"

    //Reference to the binding object
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Uses the setContentView() function from the DataBindingUtil class
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply{
            //What happens when Generate Button gets clicked
            generatorButton.setOnClickListener {
                generateNumbers()
                showerAnimation()
                hideKeyboard()
                reset()
            }

            //What happens when ImageButton gets clicked
            actionCopyButton.setOnClickListener { copyToClipboard() }

            //What happens when clearButton gets clicked
            clearButton.setOnClickListener { clearViews() }
        }
    }

    //The number generator
    private fun generateNumbers() {
        binding.apply {
            //Convert EditText value to String
            val stringRangeFrom = start.text.toString()
            val stringRangeTo = end.text.toString()
            val stringRepeat = numberOfOutcomes.text.toString()

            //Handles errors
            if (stringRangeFrom.equals("") || stringRangeTo.equals("")) {
                start.setError("Invalid Range")
                end.setError("Invalid Range")
            } else if (stringRepeat.equals("")) {
                numberOfOutcomes.setError("Enter Number")
            } else {
                start.setError(null)
                end.setError(null)
                numberOfOutcomes.setError(null)

                //Convert EditText value to int
                val rangeFrom = stringRangeFrom.toInt()
                val rangeTo = stringRangeTo.toInt()
                val repeatInt = stringRepeat.toInt()

                //Handle errors
                if (rangeTo < rangeFrom) {
                    start.setError("Invalid Range")
                    end.setError("Invalid Range")
                } else if(rangeFrom == rangeTo){
                    start.setError("Invalid Range")
                    end.setError("Invalid Range")
                }else if (rangeTo < repeatInt) {
                    numberOfOutcomes.setError("Number Cannot be Greater Than Outer Bound")
                } else {
                    start.setError(null)
                    end.setError(null)
                    numberOfOutcomes.setError(null)

                    //Generate random numbers as a List
                    val randomList = List(repeatInt) { Random.nextInt(rangeFrom, rangeTo) }

                    //Display the numbers in TextView as String values with a separator using joinToString
                    resultText.text = randomList.joinToString(separator = ", ")
                }
            }
        }
    }

    private fun hideKeyboard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {

        }
    }

    private fun reset() {
        binding.apply {
            start.clearFocus()
            end.clearFocus()
            numberOfOutcomes.clearFocus()
        }
    }

    private fun copyToClipboard() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.text = binding.resultText.getText()
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_LONG).show()
    }

    private fun clearViews() {
        clearForm(findViewById<View>(R.id.linear_layout) as ViewGroup)
        binding.apply {
            start.setText("")
            start.setError(null)
            end.setText("")
            end.setError(null)
            numberOfOutcomes.setText("")
            numberOfOutcomes.setError(null)
            resultText.setText(getString(R.string.click_button_to_generate_number))
        }
    }

    private fun clearForm(group: ViewGroup) {
        var i = 0
        val count = group.childCount
        while (i < count) {
            val view = group.getChildAt(i)
            if (view is EditText) {
                view.setText("")
            }
            if (view is ViewGroup && view.childCount > 0) clearForm(view)
            ++i
        }
    }

    private fun showerAnimation() {
        //Reference to the parent of current star
        val container = binding.star.parent as ViewGroup
        //Width and height of the container
        val containerW = container.width
        val containerH = container.height
        //Default width and height of star
        var starW: Float = binding.star.width.toFloat()
        var starH: Float = binding.star.height.toFloat()

        //View holding the star graphic is AppCompatImageView
        // because the star is a VectorDrawable asset
        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_baseline_star_24)
        newStar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        container.addView(newStar)

        //Modify star to adopt random size
        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY

        //Positioning the star
        newStar.translationX = Math.random().toFloat() *
                containerW - starW / 2

        //Create animation
        val mover = ObjectAnimator.ofFloat(
            newStar, View.TRANSLATION_Y,
            -starH, containerH + starH
        )
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(
            newStar, View.ROTATION,
            (Math.random() * 1080).toFloat()
        )
        rotator.interpolator = LinearInterpolator()

        //Run animators with Animator set
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()

        //Remove star after animator ends
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })

        //Start animation
        set.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.rateApp -> {
                val ratingsIntent = Intent(Intent.ACTION_VIEW)
                ratingsIntent.data =
                    Uri.parse("market://details?id=" + APP_PROJECT_NAME)
                if (ratingsIntent.resolveActivity(packageManager) != null) {
                    startActivity(ratingsIntent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}