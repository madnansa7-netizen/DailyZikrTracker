package com.example.dailyzikrtracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private val DarkBlue = Color(0xFF102A43)
private val DarkBlue2 = Color(0xFF163D5C)
private val LightYellow = Color(0xFFFFF4BF)
private val Gold = Color(0xFFE4B64A)
private val Cream = Color(0xFFFFFBEB)
private val TextDark = Color(0xFF1F2933)

data class ZikrItem(
    val key: String,
    val title: String
)

val zikrItems = listOf(
    ZikrItem("darood", "Darood Shareef"),
    ZikrItem("kalma", "Pehla Kalma"),
    ZikrItem("astaghfar", "Astaghfar"),
    ZikrItem("ikhlas", "Surah Ikhlas"),
    ZikrItem("fatiha", "Surah Fatiha")
)

private const val PREFS_NAME = "daily_zikr_tracker"
private const val KEY_DAROOD = "darood"
private const val KEY_KALMA = "kalma"
private const val KEY_ASTAGHFAR = "astaghfar"
private const val KEY_IKHLAS = "ikhlas"
private const val KEY_FATIHA = "fatiha"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DailyZikrTheme {
                MainApp()
            }
        }
    }
}

private fun getPrefs(context: Context) =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

private fun getZikrTotal(
    context: Context,
    key: String
): Long {
    return getPrefs(context).getLong(key, 0L)
}

private fun addZikrTotal(
    context: Context,
    key: String,
    amount: Long
): Long {

    val prefs = getPrefs(context)

    val current = prefs.getLong(key, 0L)

    val newTotal = current + amount

    prefs.edit()
        .putLong(key, newTotal)
        .apply()

    return newTotal
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {

    val navController = rememberNavController()

    var selectedScreen by remember {
        mutableStateOf("home")
    }

    Scaffold(
        containerColor = Cream,

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = "Daily Zikr",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Your Zikr is saved on this device",
                            color = LightYellow,
                            fontSize = 12.sp
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {
                            // No online account/logout required.
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Local App",
                            tint = Color.White
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue
                )
            )
        },

        bottomBar = {

            NavigationBar(
                containerColor = DarkBlue
            ) {

                NavigationBarItem(

                    selected = selectedScreen == "home",

                    onClick = {

                        selectedScreen = "home"

                        navController.navigate("home") {
                            launchSingleTop = true
                        }
                    },

                    icon = {

                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Zikr"
                        )
                    },

                    label = {
                        Text("Zikr")
                    },

                    colors = NavigationBarItemDefaults.colors(

                        selectedIconColor = DarkBlue,
                        selectedTextColor = LightYellow,
                        indicatorColor = LightYellow,

                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White
                    )
                )

                NavigationBarItem(

                    selected = selectedScreen == "report",

                    onClick = {

                        selectedScreen = "report"

                        navController.navigate("report") {
                            launchSingleTop = true
                        }
                    },

                    icon = {

                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Record"
                        )
                    },

                    label = {
                        Text("Record")
                    },

                    colors = NavigationBarItemDefaults.colors(

                        selectedIconColor = DarkBlue,
                        selectedTextColor = LightYellow,
                        indicatorColor = LightYellow,

                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White
                    )
                )
            }
        }

    ) { padding ->

        NavHost(

            navController = navController,

            startDestination = "home",

            modifier = Modifier.padding(padding)
        ) {

            composable("home") {

                ZikrHomeScreen()
            }

            composable("report") {

                ReportScreen()
            }
        }
    }
}

@Composable
fun ZikrHomeScreen() {

    val context = LocalContext.current

    var totals by remember {

        mutableStateOf(
            zikrItems.associate {
                it.key to getZikrTotal(
                    context,
                    it.key
                )
            }
        )
    }

    var inputs by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .background(Cream)
                .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        item {

            Text(
                text = "Add Today's Zikr",

                color = DarkBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(

                text =
                    "Enter the number you have recited and save it. Your total is stored safely on this device.",

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

            ZikrEntryCard(

                item = item,

                input =
                    inputs[item.key] ?: "",

                total =
                    totals[item.key] ?: 0L,

                onInputChange = { value ->

                    val filtered =
                        value.filter {
                            it.isDigit()
                        }

                    inputs =
                        inputs + (
                            item.key to filtered
                        )
                },

                onSave = {

                    val amount =
                        inputs[item.key]
                            ?.toLongOrNull()
                            ?: 0L

                    if (amount <= 0L) {
                        return@ZikrEntryCard
                    }

                    val newTotal =
                        addZikrTotal(
                            context = context,
                            key = item.key,
                            amount = amount
                        )

                    totals =
                        totals + (
                            item.key to newTotal
                        )

                    inputs =
                        inputs + (
                            item.key to ""
                        )
                }
            )
        }

        item {

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Card(

                colors =
                    CardDefaults.cardColors(
                        containerColor = DarkBlue
                    ),

                shape =
                    RoundedCornerShape(18.dp),

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(18.dp)
                ) {

                    Text(

                        text =
                            "Your Zikr record is saved on this phone.",

                        color = LightYellow,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(

                        text =
                            "No Google account, Firebase or internet connection is required.",

                        color = Color.White,
                        fontSize = 13.sp,

                        modifier =
                            Modifier.padding(top = 5.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZikrEntryCard(

    item: ZikrItem,

    input: String,

    total: Long,

    onInputChange:
        (String) -> Unit,

    onSave:
        () -> Unit
) {

    Card(

        colors =
            CardDefaults.cardColors(
                containerColor = Color.White
            ),

        shape =
            RoundedCornerShape(18.dp),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),

        modifier =
            Modifier.fillMaxWidth()
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(

                text = item.title,

                color = DarkBlue,
                fontWeight =
                    FontWeight.Bold,

                fontSize = 18.sp
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

                    placeholder = {
                        Text("Enter count")
                    },

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),

                    modifier =
                        Modifier.weight(1f),

                    colors =
                        OutlinedTextFieldDefaults.colors(

                            focusedBorderColor =
                                Gold,

                            unfocusedBorderColor =
                                DarkBlue2,

                            focusedLabelColor =
                                DarkBlue
                        )
                )

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Button(

                    onClick =
                        onSave,

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                DarkBlue,

                            contentColor =
                                LightYellow
                        ),

                    shape =
                        RoundedCornerShape(12.dp),

                    modifier =
                        Modifier.height(54.dp)
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

                color = DarkBlue2,

                fontWeight =
                    FontWeight.SemiBold,

                fontSize = 13.sp,

                modifier =
                    Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun ReportScreen() {

    val context = LocalContext.current

    var refresh by remember {
        mutableStateOf(0)
    }

    // Reading refresh keeps the report updated when navigating back.
    val totals =
        zikrItems.associate {

            it.key to getZikrTotal(
                context,
                it.key
            )
        }

    val grandTotal =
        totals.values.sum()

    // Prevent unused state warning and allow recomposition.
    refresh.hashCode()

    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .background(Cream)
                .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        item {

            Text(

                text = "My Zikr Record",

                color = DarkBlue,
                fontSize = 26.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(

                text = "Lifetime totals saved on this device",

                color = TextDark,
                fontSize = 14.sp,

                modifier =
                    Modifier.padding(
                        top = 3.dp,
                        bottom = 6.dp
                    )
            )
        }

        item {

            Card(

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            DarkBlue
                    ),

                shape =
                    RoundedCornerShape(20.dp),

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(

                    modifier =
                        Modifier.padding(20.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(

                        text = "Grand Total",

                        color = LightYellow,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(

                        text =
                            grandTotal.toString(),

                        color = Color.White,

                        fontSize = 34.sp,

                        fontWeight =
                            FontWeight.Bold,

                        modifier =
                            Modifier.padding(top = 5.dp)
                    )
                }
            }
        }

        items(zikrItems) { item ->

            val total =
                totals[item.key] ?: 0L

            Card(

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),

                shape =
                    RoundedCornerShape(16.dp),

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 17.dp
                            ),

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
}

@Composable
fun DailyZikrTheme(
    content:
        @Composable () -> Unit
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

        content =
            content
    )
}
