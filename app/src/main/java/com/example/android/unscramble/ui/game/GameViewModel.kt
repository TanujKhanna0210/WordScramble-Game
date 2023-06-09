package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlin.math.log

class GameViewModel : ViewModel() {

    private val _score = MutableLiveData(0)
    val score : LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount : LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if(it == null)
            SpannableString(" ")
        else{
            val scrambledWord = it.toString()
            val spannable : Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    private var wordList : MutableList<String> = mutableListOf()
    private lateinit var currentWord : String

    private fun getNextWord(){
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while(String(tempWord).equals(currentWord,false))
            tempWord.shuffle()
        if(wordList.contains(currentWord)){
            getNextWord()
        }
        else{
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordList.add(currentWord)
        }
    }

    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS){
            getNextWord()
            true
        }else false
    }

    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord:String): Boolean {
        if(playerWord.equals(currentWord,true)) {
            increaseScore()
            return true
        }
        else return false
    }

    fun reinitializeData() {
        _score.value=0
        _currentWordCount.value=0
        wordList.clear()
        getNextWord()
    }

    init {
        Log.d("Game Fragment","GameViewModel created!")
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Game Fragment","GameViewModel destroyed!")
    }




}