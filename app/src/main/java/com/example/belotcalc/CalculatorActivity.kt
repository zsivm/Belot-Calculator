package com.example.belotcalc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Calculator : AppCompatActivity() {
    var round = 1
    var ourSumResult = 0
    var theirSumResult = 0
    var roundBase = 162
    var call = 0
    var roundMax = 0

    var ourWins = 0
    var theirWins = 0
    var isScoreEmpty = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        val scrollView: ScrollView = findViewById(R.id.scroll_view)

        val ourSum: TextView = findViewById(R.id.our_sum)
        val theirSum: TextView = findViewById(R.id.their_sum)

        var roundContainer: LinearLayout = findViewById(R.id.round_container)

        val nextRound: FloatingActionButton = findViewById(R.id.next_round)
        nextRound.setOnClickListener {

            if(round != 1) {
                calculateRound(roundContainer)
            }

            if(isGameOver(ourSumResult, theirSumResult)) {
                resetSum(ourSum, theirSum)
                resetLayout(roundContainer)
            }

            if(!isScoreEmpty) {
                addRow(roundContainer)
                scrollView.scrollTo(0, scrollView.getBottom())
            }
        }

        val callButton: Button = findViewById(R.id.call_button)
        callButton.setOnClickListener {
            addCall(roundContainer)
        }

        addRow(roundContainer)
    }

    private fun addRow(rowsLayout: LinearLayout) {
        if(round > 14) {
            return
        }

        val view = layoutInflater.inflate(R.layout.row, null)

        val rowContainer: LinearLayout = view.findViewById(R.id.row_container)
        rowContainer.setTag("con" + round)

        val rowNumber: TextView = view.findViewById(R.id.row_number)
        rowNumber.setText(round.toString() + ".")

        val ourHand: EditText = view.findViewById(R.id.our_hand)
        ourHand.setTag("oh" + round)

        val theirHand: EditText = view.findViewById(R.id.their_hand)
        theirHand.setTag("th" + round)

        round++

        rowsLayout.addView(view)
        openSoftKeyboard(this, ourHand)
    }

    private fun updateSum() {

        val ourSum: TextView = findViewById(R.id.our_sum)
        ourSum.text = ourSumResult.toString()

        val theirSum: TextView = findViewById(R.id.their_sum)
        theirSum.text = theirSumResult.toString()

        resetRoundVariables()
    }

    private fun findCurrentRowLayout(roundContainer: LinearLayout): LinearLayout {
        return roundContainer.findViewWithTag("con" + (round - 1))
    }

    private fun isGameOver(ourSum: Int, theirSum: Int): Boolean {
        if(ourSum >= 1001 && ourSum > theirSum) {
            displayToast("MI")
            ourWins++

            val ourResult: TextView = findViewById(R.id.our_result)
            ourResult.setText(ourWins.toString())

            return true

        } else if(theirSum >= 1001 && theirSum > ourSum) {
            displayToast("VI")
            theirWins++

            val theirResult: TextView = findViewById(R.id.their_result)
            theirResult.setText(theirWins.toString())

            return true
        }

        return false
    }

    private fun displayToast(teamName: String) {
        Toast.makeText(applicationContext,"$teamName won!", Toast.LENGTH_SHORT).show()
    }

    private fun resetSum(ourSum: TextView, theirSum: TextView) {
        ourSumResult = 0
        theirSumResult = 0
        ourSum.setText(ourSumResult.toString())
        theirSum.setText(theirSumResult.toString())
    }

    private fun resetLayout(roundContainer: LinearLayout) {
        round = 1
        roundContainer.removeAllViews()
    }

    private fun addCall(roundContainer: LinearLayout) {
        val callAmount: EditText = findViewById(R.id.call_amount)

        if(callAmount.text.toString() != "") {
            call = callAmount.text.toString().toInt()
            roundMax = roundBase + call
        }

        val currentRowLayout = findCurrentRowLayout(roundContainer)
        val displayCallAmount: TextView = currentRowLayout.findViewById(R.id.call_display)
        displayCallAmount.text = call.toString()
    }

    private fun calculateRound(roundContainer: LinearLayout) {

        val rowContainer: LinearLayout = findCurrentRowLayout(roundContainer)

        val currentOurHand: EditText = rowContainer.findViewWithTag("oh" + (round - 1))
        val currentTheirHand: EditText = rowContainer.findViewWithTag("th" + (round - 1))

        var res = 0

        if(roundMax == 0) roundMax = roundBase

        if(currentOurHand.getText().toString() == "" && currentTheirHand.getText().toString() == "") {
            isScoreEmpty = true
            return
        }

        isScoreEmpty = false

        if(currentOurHand.getText().toString() == "") {
            res = roundMax - currentTheirHand.text.toString().toInt()
            ourSumResult += res
            theirSumResult += currentTheirHand.getText().toString().toInt()
            currentOurHand.setText(res.toString())
        } else if(currentTheirHand.getText().toString() == "") {
            res = roundMax - currentOurHand.getText().toString().toInt()
            theirSumResult += res
            ourSumResult += currentOurHand.getText().toString().toInt()
            currentTheirHand.setText(res.toString())
        }

        updateSum()
    }

    private fun falling() {
        // pad - amikor megálltál és nem sikerül megfogni a ((162 + hivas) / 2) + 1-et, az ellenfél visz mindent
    }

    private fun hanging() {
        // visi - amikor pont a játék felét fogjak mind2-en (attól függ ki állt meg, az "lóg", és a következő körben,
        // ha nem az fog többet aki lógot, akkor elveszíti a pl.81-et ((162 + hívás) / 2) és a másik csapat kapja a 81-et és az adott kör pontjait
    }

    private fun ace() {
        // when you get all hands, you get 252 (the last one is worth 90 points) if 162 was in game so it is 162 + call + 90
    }

    // helpers

    private fun openSoftKeyboard(context: Context, view: View) {
        view.requestFocus()
        // open the soft keyboard
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun resetRoundVariables() {
        call = 0
        roundMax = 0
    }
}