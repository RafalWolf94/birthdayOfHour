package domain

import java.util.Calendar

class BirthdayTimerService {
    companion object {
        private lateinit var calendar: Calendar
        private const val hourMinuteCycle: Int = 60

        var stringEmpty = ""
        private const val valueToAddZeroInFrontOfTimeFormat: Int = 10
        const val countDownStartValue: Int = 10
        const val oneMinute: Int = 1
        const val midnightHour = 24;
        private var minutesLeft: Int = 0

        fun getCurrentTime(): Triple<Int, Int, Int> {
            calendar = Calendar.getInstance();
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            val second = getSeconds(calendar[Calendar.SECOND])

            return Triple(hour, minute, second);
        }

        fun getTimeLeft(hour: Int, minutes: Int, seconds: Int): String {
            minutesLeft = if (hour + oneMinute == midnightHour) {
                hourMinuteCycle - minutes - oneMinute
            } else if (minutes < hour) {
                hour - minutes - oneMinute
            } else {
                hourMinuteCycle - minutes + hour
            }

            return "${checkAndDisplayValidTimeFormat(minutesLeft)}:${checkAndDisplayValidTimeFormat(seconds)}"
        }

        private fun checkAndDisplayValidTimeFormat(timeToFormat: Int): String =
            if (timeToFormat < valueToAddZeroInFrontOfTimeFormat) {
                "0${timeToFormat}"
            } else {
                timeToFormat.toString()
            }

        private fun getSeconds(seconds: Int): Int {
            var secondsLeft = hourMinuteCycle - seconds

            if (secondsLeft == hourMinuteCycle) {
                return --secondsLeft
            }
            return secondsLeft
        }
    }
}