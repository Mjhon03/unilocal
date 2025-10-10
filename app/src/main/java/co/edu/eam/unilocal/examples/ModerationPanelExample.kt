package co.edu.eam.unilocal.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme

/**
 * Ejemplo de cómo usar la pantalla de moderación
 * 
 * Para acceder al Panel de Moderación desde cualquier pantalla:
 * 
 * 1. Agregar el parámetro de navegación en la función de la pantalla:
 *    onModerationClick: () -> Unit = {}
 * 
 * 2. En la navegación, agregar la ruta:
 *    composable<RouteScreen.ModerationPanel> {
 *        ModerationPanelScreen(
 *            onBackClick = { navController.popBackStack() },
 *            onApprovePlace = { placeId -> /* Lógica para aprobar */ },
 *            onRejectPlace = { placeId -> /* Lógica para rechazar */ }
 *        )
 *    }
 * 
 * 3. Navegar desde cualquier pantalla:
 *    navController.navigate(RouteScreen.ModerationPanel)
 * 
 * Ejemplo de uso en SearchScreen:
 * - Agregar un botón de administración (solo visible para administradores)
 * - Al hacer clic, navegar al panel de moderación
 */
@Composable
fun ModerationPanelExample(
    modifier: Modifier = Modifier,
    onNavigateToModeration: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Panel de Moderación - Ejemplo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Esta es una demostración de cómo acceder al Panel de Moderación",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Button(
                onClick = onNavigateToModeration,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Ir al Panel de Moderación")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModerationPanelExamplePreview() {
    MyApplicationTheme {
        ModerationPanelExample()
    }
}
