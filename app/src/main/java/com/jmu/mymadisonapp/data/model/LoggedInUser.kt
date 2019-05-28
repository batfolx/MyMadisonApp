package com.jmu.mymadisonapp.data.model

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val eID: String,
    val displayName: String
)

class HoursEnrolledConverter : ElementConverter<Map<String, Int>> {
    override fun convert(node: Element, selector: Selector): Map<String, Int> =
        with(node.text().trim()) { substring(0, indexOf("Hours Enrolled")).trimEnd() }
            .split(Regex(": |\\s+")).filter { it.isNotBlank() }
            .chunked(2) { (name, amount) -> name to amount.toInt() }
            .toMap()
}

class DeclaredSubjectConverter : ElementConverter<DeclaredSubject> {
    override fun convert(node: Element, selector: Selector): DeclaredSubject =
        with(node.text().trim().split(Regex(": |\\)\\s")).filter { it.isNotBlank() && it.contains(Regex("\\(|\\d{2}/\\d{2}/\\d{4}")) }) {
            val major =
                getOrElse(0) { "Undeclared (GPA 0.000)" }
                    .let {
                        it.substring(0, it.indexOf(" (")) to
                                it.substringAfter("GPA ")
                    }
            val minor =
                getOrElse(1) { "Undeclared (GPA 0.000)" }
                    .let {
                        it.substring(0, it.indexOf(" (")) to
                                it.substringAfter("GPA ")
                    }
            val lastUpdated = SimpleDateFormat("MM/dd/yyyy").parse(getOrElse(2) { "00/00/0000" })
            DeclaredSubject(
                major.first, major.second.toFloat(),
                minor.first, minor.second.toFloat(),
                lastUpdated
            )
        }
}

@Selector("div[data-role=\"content\"]")
data class StudentUndergradInfo(
    @Selector("span.SSSGROUPBOXRIGHTNBO2 > span.PSHYPERLINKDISABLED") var holds: Int = 0,
    @Selector("span.SSSGROUPBOXRIGHTNBO3 > span.PSHYPERLINKDISABLED") var toDos: Int = 0,
    @Selector("span.SSSGROUPBOXRIGHTNBO4") var cumGPA: GPA = GPA(),
    @Selector("span.SSSGROUPBOXRIGHTNBO5") var lastSemGPA: GPA = GPA(),
    @Selector(
        "span.SSSGROUPBOXRIGHTNBO6",
        converter = HoursEnrolledConverter::class
    ) var hoursEnrolled: Map<String, Int> = emptyMap(),
    @Selector(
        "span.PROGRAMCLASS5",
        converter = DeclaredSubjectConverter::class
    ) var subject: DeclaredSubject = DeclaredSubject()
)

data class GPA(
    @Selector("span.PSHYPERLINKDISABLED") var gpa: Float = 0f,
    @Selector("span.DASHBOARDDESCR") var type: String = ""
)

data class DeclaredSubject(
    val major: String = "",
    val majorGPA: Float = 0f,
    val minor: String = "",
    val minorGPA: Float = 0f,
    val gpaLastUpdated: Date = Calendar.getInstance().time
)