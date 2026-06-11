package com.deliverit.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.deliverit.app.ui.createtask.CreateTaskScreen
import com.deliverit.app.ui.navigation.Args.TASK_ID
import com.deliverit.app.ui.taskdetail.TaskDetailScreen
import com.deliverit.app.ui.tasklist.TaskListScreen

object Routes {
    const val TASK_LIST = "tasks"

    const val CREATE_TASK = "$TASK_LIST/create"
    const val TASK_DETAIL = "$TASK_LIST/{$TASK_ID}"

    fun taskDetail(taskId: String) = "$TASK_LIST/$taskId"
}
object Args {
    const val TASK_ID = "taskId"
}

@Composable
fun DeliverItNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.TASK_LIST) {
        composable(Routes.TASK_LIST) {
            TaskListScreen(
                onNavigateToDetail = { taskId -> navController.navigate(Routes.taskDetail(taskId)) },
                onNavigateToCreate = { navController.navigate(Routes.CREATE_TASK) }
            )
        }
        composable(Routes.CREATE_TASK) {
            CreateTaskScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.TASK_DETAIL,
            arguments = listOf(navArgument(TASK_ID) { type = NavType.StringType })
        ) {
            TaskDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
