import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import data.local.MongoDatabase
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.screen.home.HomeScreen
import presentation.screen.home.HomeViewModel

val lightRedColor = Color(color = 0xFFF57D88)
val darkRedColor = Color(0xFF77000B)

@Composable
fun App() {
    initKoin()

    val lightColors = lightColorScheme(
        primary = lightRedColor,
        onPrimary = darkRedColor,
        primaryContainer = lightRedColor,
        onPrimaryContainer = darkRedColor,
    )
    val darkColors = darkColorScheme(
        primary = lightRedColor,
        onPrimary = darkRedColor,
        primaryContainer = lightRedColor,
        onPrimaryContainer = darkRedColor,
    )
    val colors by mutableStateOf(
        if (isSystemInDarkTheme()) darkColors else lightColors
    )

    MaterialTheme(colorScheme = colors) {
        Navigator(HomeScreen()) {
            SlideTransition(it)
        }
    }
}

fun initKoin() {
    startKoin {
        modules(dbModule)
    }
}

val dbModule = module {
    single { MongoDatabase() }
    factory { HomeViewModel(get()) }
    factory { TaskViewModel(get()) }
}

