package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import kotlin.reflect.typeOf

class QuizQuestionsActivity : AppCompatActivity() {
    private var userName: String? = null

    private val questionsList: ArrayList<Question> = ArrayList(Constants.getQuestions().shuffled())
    private var currentQuestionIndex = 0;
    private var selectedOptionsIndex = -1;
    private var isAnswerChecked = false;
    private var totalScore = 0;

    private var tvQuestion: TextView? = null
    private var ivImage: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var tvProgress: TextView? = null
    private var btnSubmit: Button? = null
    private var tvOptions: ArrayList<TextView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        userName = intent.getStringExtra(Constants.USER_NAME)

        tvQuestion = findViewById(R.id.tvQuestion)
        ivImage = findViewById(R.id.ivImage)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tvProgress)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvOptions = ArrayList(arrayListOf<TextView>(
            findViewById(R.id.optionOne),
            findViewById(R.id.optionTwo),
            findViewById(R.id.optionThree),
            findViewById(R.id.optionFour),
        ).shuffled())

        updateQuestion()

        btnSubmit?.setOnClickListener {
            if (!isAnswerChecked) {
                val anyAnswerIsChecked = selectedOptionsIndex != -1
                if (!anyAnswerIsChecked) {
                    Toast.makeText(this, "Please, select an answer", Toast.LENGTH_SHORT).show()
                } else {
                    val currentQuestion = questionsList[currentQuestionIndex]
                    if (
                        selectedOptionsIndex == currentQuestion.correctAnswerIndex
                    ) {
                        // choose the correct answer

                        // color option green
                        answerView(tvOptions!![selectedOptionsIndex], R.drawable.correct_option_border_bg)
                        totalScore++
                    } else {
                        // choose the wrong answer

                        // color option red
                        answerView(tvOptions!![selectedOptionsIndex], R.drawable.wrong_option_border_bg)
                        // color CORRECT option green
                        answerView(tvOptions!![currentQuestion.correctAnswerIndex], R.drawable.correct_option_border_bg)
                    }

                    isAnswerChecked = true
                    btnSubmit?.text = if (currentQuestionIndex == questionsList.size - 1) "FINISH" else "GO TO NEXT QUESTION"
                    selectedOptionsIndex = -1
                }
            } else {
                if (currentQuestionIndex < questionsList.size - 1) {
                    currentQuestionIndex++
                    updateQuestion()
                } else {
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(Constants.USER_NAME, userName)
                    intent.putExtra(Constants.TOTAL_QUESTIONS, questionsList.size)
                    intent.putExtra(Constants.SCORE, totalScore)
                    startActivity(intent)
                    finish()
                }

                isAnswerChecked = false
            }
        }

        // adds event listener to highlight current selected option before submitting
        tvOptions?.let {
            for (optionIndex in it.indices) {
                it[optionIndex].let { itB ->
                    itB.setOnClickListener{
                        if (!isAnswerChecked) {
                            selectedAlternativeView(itB, optionIndex)
                        }
                    }
                }
            }
        }
    }

    private fun updateQuestion() {
        defaultOptionsView()

        // Render Question Text
        tvQuestion?.text = questionsList[currentQuestionIndex].questionText
        // Render Question Image
        ivImage?.setImageResource(questionsList[currentQuestionIndex].image)
        // progressBar
        progressBar?.progress = currentQuestionIndex + 1
        // Text of progress bar
        "${currentQuestionIndex + 1}/${questionsList.size}".also { tvProgress?.text = it }

        for (alternativeIndex in questionsList[currentQuestionIndex].alternatives.indices) {
            tvOptions!![alternativeIndex].text = questionsList[currentQuestionIndex].alternatives[alternativeIndex]
        }

        btnSubmit?.text = if (currentQuestionIndex == questionsList.size - 1) "FINISH" else "SUBMIT"
    }
    
    // redraws all options with the new question options in default state
    private fun defaultOptionsView() {
        for (alternativeTv in tvOptions!!) {
            alternativeTv.typeface = Typeface.DEFAULT
            alternativeTv.setTextColor(Color.parseColor("#7A8089"))
            alternativeTv.background = ContextCompat.getDrawable(
                this@QuizQuestionsActivity,
                R.drawable.default_option_border_bg
            )
        }
    }

    // colors selected option based with a default border [BEFORE SUBMITTING]
    private fun selectedAlternativeView(option: TextView, index: Int) {
        defaultOptionsView()
        selectedOptionsIndex = index

        option.setTextColor(
            Color.parseColor("#363A43")
        )
        option.setTypeface(option.typeface, Typeface.BOLD)
        option.background = ContextCompat.getDrawable(
            this@QuizQuestionsActivity,
            R.drawable.selected_option_border_bg
        )
    }

    // colors selected option based on parameters [AFTER SUBMITTING]
    private fun answerView(view: TextView, drawableId: Int) {
        view.background = ContextCompat.getDrawable(
            this@QuizQuestionsActivity,
            drawableId
        )
        tvOptions!![selectedOptionsIndex].setTextColor(
            Color.parseColor("#FFFFFF")
        )
    }
}