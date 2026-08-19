package com.example.dailyzikrtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

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

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, gso)

        setContent {
            DailyZikrTheme {

                if (auth.currentUser == null) {

                    LoginScreen(
                        onGoogleClick = {
                            signInWithGoogle()
                        }
                    )

                } else {

                    MainApp(
                        userName = auth.currentUser?.displayName ?: "User",

                        onLogout = {
                            auth.signOut()
                            googleClient.signOut()
                            recreate()
                        },

                        db = db,
                        auth = auth
                    )
                }
            }
        }
    }

    private fun signInWithGoogle() {
        startActivityForResult(
            googleClient.signInIntent,
            RC_SIGN_IN
        )
    }

    @Deprecated("Use Activity Result APIs in a future refactor.")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == RC_SIGN_IN) {

            try {

                val account = GoogleSignIn
                    .getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)

                val credential =
                    GoogleAuthProvider.getCredential(
                        account.idToken,
                        null
                    )

                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->

                        if (!task.isSuccessful) {

                            Toast.makeText(
                                this,
                                task.exception?.message
                                    ?: "Google sign-in failed",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {

                            recreate()
                        }
                    }

            } catch (e: ApiException) {

                Toast.makeText(
                    this,
                    "Google sign-in failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    userName: String,
    onLogout: () -> Unit,
    db: FirebaseFirestore,
    auth: FirebaseAuth
) {

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
                            text = "Welcome, ${userName.substringBefore(" ")}",
                            color = LightYellow,
                            fontSize = 12.sp
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = onLogout
                    ) {

                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
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

                ZikrHomeScreen(
                    db = db,
                    auth = auth
                )
            }

            composable("report") {

                ReportScreen(
                    db = db,
                    auth = auth
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    onGoogleClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBlue
    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Surface(

                shape =
                    RoundedCornerShape(28.dp),

                color = LightYellow,

                modifier =
                    Modifier.size(96.dp)
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "ذکر",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Text(
                text = "Daily Zikr Tracker",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Keep your Zikr record safe online",

                color = LightYellow,
                fontSize = 15.sp,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier.padding(top = 8.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(38.dp)
            )

            Button(

                onClick =
                    onGoogleClick,

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = LightYellow,
                        contentColor = DarkBlue
                    ),

                shape =
                    RoundedCornerShape(16.dp),

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(54.dp)
            ) {

                Text(
                    text = "Continue with Google",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ZikrHomeScreen(
    db: FirebaseFirestore,
    auth: FirebaseAuth
) {

    var totals by remember {
        mutableStateOf<Map<String, Long>>(emptyMap())
    }

    var inputs by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(
        auth.currentUser?.uid
    ) {

        val uid =
            auth.currentUser?.uid
                ?: return@LaunchedEffect

        db.collection("users")
            .document(uid)
            .get()

            .addOnSuccessListener { doc ->

                totals =
                    zikrItems.associate {

                        it.key to
                            (doc.getLong(it.key) ?: 0L)
                    }

                loading = false
            }

            .addOnFailureListener {

                loading = false
            }
    }

    if (loading) {

        Box(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Cream),

            contentAlignment =
                Alignment.Center
        ) {

            CircularProgressIndicator(
                color = DarkBlue
            )
        }

        return
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
                    "Enter the number you have recited and save it. The amount will be added to your lifetime total.",

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

                    val uid =
                        auth.currentUser?.uid
                            ?: return@ZikrEntryCard

                    val amount =
                        inputs[item.key]
                            ?.toLongOrNull()
                            ?: 0L

                    if (amount <= 0L) {
                        return@ZikrEntryCard
                    }

                    val current =
                        totals[item.key] ?: 0L

                    val newTotal =
                        current + amount

                    db.collection("users")
                        .document(uid)

                        .set(

                            mapOf(
                                item.key to newTotal
                            ),

                            SetOptions.merge()
                        )

                        .addOnSuccessListener {

                            totals =
                                totals + (
                                    item.key to newTotal
                                )

                            inputs =
                                inputs + (
                                    item.key to ""
                                )
                        }
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
                            "Your total record is always available online.",

                        color = LightYellow,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(

                        text =
                            "No date-wise or month-wise history is stored.",

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
fun ReportScreen(

    db: FirebaseFirestore,

    auth: FirebaseAuth
) {

    var totals by remember {

        mutableStateOf<Map<String, Long>>(
            emptyMap()
        )
    }

    LaunchedEffect(
        auth.currentUser?.uid
    ) {

        val uid =
            auth.currentUser?.uid
                ?: return@LaunchedEffect

        db.collection("users")
            .document(uid)

            .addSnapshotListener { doc, _ ->

                if (doc != null && doc.exists()) {

                    totals =
                        zikrItems.associate {

                            it.key to
                                (
                                    doc.getLong(
                                        it.key
                                    ) ?: 0L
                                )
                        }
                }
            }
    }

    val grandTotal =
        totals.values.sum()

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

                text = "Lifetime totals",

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
            lightColorScheme(

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
