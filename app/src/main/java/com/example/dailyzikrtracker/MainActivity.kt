package com.example.dailyzikrtracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

private val DarkBlue = Color(0xFF102A43)
private val DarkBlue2 = Color(0xFF163D5C)
private val LightYellow = Color(0xFFFFF4BF)
private val Gold = Color(0xFFE4B64A)
private val Cream = Color(0xFFFFFBEB)
private val TextDark = Color(0xFF1F2933)

private const val PREFS_NAME = "daily_zikr_tracker"

data class ZikrItem(
    val key: String,
    val title: String
)

private val zikrItems = listOf(
    ZikrItem("darood", "Darood Shareef"),
    ZikrItem("kalma", "Pehla Kalma"),
    ZikrItem("astaghfar", "Astaghfar"),
    ZikrItem("ikhlas", "Surah Ikhlas"),
    ZikrItem("fatiha", "Surah Fatiha")
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DailyZikrTheme {
                DailyZikrApp()
            }
        }
    }
}

private fun getPrefs(context: Context) =
    context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

private fun getTotal(
    context: Context,
    key: String
): Long {
    return getPrefs(context).getLong(key, 0L)
}

private fun addTotal(
    context: Context,
    key: String,
    amount: Long
): Long {

    val prefs = getPrefs(context)

    val oldValue = prefs.getLong(key, 0L)

    val newValue = oldValue + amount

    prefs.edit()
        .putLong(key, newValue)
        .apply()

    return newValue
}

@Composable
fun DailyZikrApp() {

    val context = LocalContext.current

    var showRecord by remember {
        mutableStateOf(false)
    }

    if (showRecord) {

        RecordScreen(
            onBack = {
                showRecord = false
            }
        )

    } else {

        HomeScreen(
            onRecordClick = {
                showRecord = true
            }
        )
    }
}

@Composable
fun HomeScreen(
    onRecordClick: () -> Unit
) {

    val context = LocalContext.current

    var totals by remember {

        mutableStateOf(
            zikrItems.associate { item ->
                item.key to getTotal(
                    context,
                    item.key
                )
            }
        )
    }

    var inputs by remember {
        mutableStateOf<Map<String, String>>(
            emptyMap()
        )
    }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(Cream)
    ) {

        Header(
            title = "Daily Zikr",
            subtitle = "Your Zikr is saved on this device"
        )

        LazyColumn(

            modifier =
                Modifier
                    .weight(1f)
                    .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            item {

                Text(

                    text = "Add Today's Zikr",

                    color = DarkBlue,

                    fontSize = 25.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(

                    text =
                        "Enter your recitation count and press Save.",

                    color = TextDark,

                    fontSize = 14.sp,

                    modifier =
                        Modifier.padding(
                            top = 4.dp,
                            bottom = 8.dp
                        )
                )
            }

            items(zikrItems) { item ->

                ZikrCard(

                    item = item,

                    input =
                        inputs[item.key] ?: "",

                    total =
                        totals[item.key] ?: 0L,

                    onInputChange = { value ->

                        val cleanValue =
                            value.filter {
                                it.isDigit()
                            }

                        inputs =
                            inputs +
                                (
                                    item.key
                                        to cleanValue
                                )
                    },

                    onSave = {

                        val amount =
                            inputs[item.key]
                                ?.toLongOrNull()
                                ?: 0L

                        if (amount > 0L) {

                            val newTotal =
                                addTotal(
                                    context,
                                    item.key,
                                    amount
                                )

                            totals =
                                totals +
                                    (
                                        item.key
                                            to newTotal
                                    )

                            inputs =
                                inputs +
                                    (
                                        item.key
                                            to ""
                                    )
                        }
                    }
                )
            }

            item {

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                DarkBlue
                        )
                ) {

                    Column(

                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        Text(

                            text =
                                "100% Local Storage",

                            color =
                                LightYellow,

                            fontSize = 18.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(

                            text =
                                "No Google Login • No Firebase • No Internet Required",

                            color =
                                Color.White,

                            fontSize = 13.sp,

                            modifier =
                                Modifier.padding(
                                    top = 5.dp
                                )
                        )
                    }
                }
            }
        }

        Button(

            onClick = onRecordClick,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),

            shape =
                RoundedCornerShape(14.dp),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        DarkBlue,

                    contentColor =
                        LightYellow
                )
        ) {

            Text(
                text = "View My Zikr Record",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Header(
    title: String,
    subtitle: String
) {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .background(DarkBlue)
                .padding(18.dp)
    ) {

        Text(

            text = title,

            color = Color.White,

            fontSize = 23.sp,

            fontWeight =
                FontWeight.Bold
        )

        Text(

            text = subtitle,

            color = LightYellow,

            fontSize = 12.sp,

            modifier =
                Modifier.padding(
                    top = 3.dp
                )
        )
    }
}

@Composable
fun ZikrCard(

    item: ZikrItem,

    input: String,

    total: Long,

    onInputChange:
        (String) -> Unit,

    onSave:
        () -> Unit
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(

            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(

                text =
                    item.title,

                color =
                    DarkBlue,

                fontSize = 18.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                OutlinedTextField(

                    value = input,

                    onValueChange =
                        onInputChange,

                    modifier =
                        Modifier.weight(1f),

                    singleLine = true,

                    placeholder = {
                        Text("Enter count")
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Button(

                    onClick = onSave,

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                DarkBlue,

                            contentColor =
                                LightYellow
                        ),

                    shape =
                        RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "Save",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Text(

                text =
                    "Current total: $total",

                color =
                    DarkBlue2,

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.SemiBold,

                modifier =
                    Modifier.padding(
                        top = 8.dp
                    )
            )
        }
    }
}

@Composable
fun RecordScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val totals =
        zikrItems.associate { item ->

            item.key to getTotal(
                context,
                item.key
            )
        }

    val grandTotal =
        totals.values.sum()

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(Cream)
    ) {

        Header(

            title = "My Zikr Record",

            subtitle =
                "All data saved on this phone"
        )

        LazyColumn(

            modifier =
                Modifier
                    .weight(1f)
                    .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            item {

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(20.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                DarkBlue
                        )
                ) {

                    Column(

                        modifier =
                            Modifier.padding(20.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(

                            text =
                                "Grand Total",

                            color =
                                LightYellow,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(

                            text =
                                grandTotal.toString(),

                            color =
                                Color.White,

                            fontSize = 36.sp,

                            fontWeight =
                                FontWeight.Bold,

                            modifier =
                                Modifier.padding(
                                    top = 5.dp
                                )
                        )
                    }
                }
            }

            items(zikrItems) { item ->

                val total =
                    totals[item.key] ?: 0L

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(16.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        )
                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(17.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(

                            text =
                                item.title,

                            color =
                                TextDark,

                            fontSize = 16.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            modifier =
                                Modifier.weight(1f)
                        )

                        Text(

                            text =
                                total.toString(),

                            color =
                                DarkBlue,

                            fontSize = 21.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }

        Button(

            onClick = onBack,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),

            shape =
                RoundedCornerShape(14.dp),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        DarkBlue,

                    contentColor =
                        LightYellow
                )
        ) {

            Text(
                text = "Back to Zikr",
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@Composable
fun DailyZikrTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(

        colorScheme =
            androidx.compose.material3.lightColorScheme(

                primary =
                    DarkBlue,

                secondary =
                    Gold,

                background =
                    Cream,

                surface =
                    Color.White,

                onPrimary =
                    Color.White,

                onSecondary =
                    DarkBlue,

                onBackground =
                    TextDark,

                onSurface =
                    TextDark
            ),

        content = content
    )
}
