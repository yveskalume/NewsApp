package dev.yveskalume.newsappp.ui.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dev.yveskalume.newsappp.R
import dev.yveskalume.newsappp.ui.screens.home.HomeRoute
import dev.yveskalume.newsappp.ui.screens.home.HomeScreenRoute
import dev.yveskalume.newsappp.ui.screens.home.navigateToHome
import dev.yveskalume.newsappp.ui.screens.search.SearchRoute
import dev.yveskalume.newsappp.ui.screens.search.SearchScreenRoute
import dev.yveskalume.newsappp.ui.screens.search.navigateToSearch

@Composable
fun NewsNavHost(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(HomeRoute)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = backStack.lastOrNull() is HomeRoute,
                    onClick = backStack::navigateToHome,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_home),
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Feed") }
                )
                NavigationBarItem(
                    selected = backStack.lastOrNull() is SearchRoute,
                    onClick = backStack::navigateToSearch,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Search"
                        )
                    },
                    label = { Text("Search") }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            entryProvider = entryProvider {
                entry<HomeRoute> {
                    HomeScreenRoute()
                }

                entry<SearchRoute> {
                    SearchScreenRoute()
                }
            }
        )
    }
}
