package edu.ucne.loginapi.IA

class StubAiChatClient : AiChatClient {

    override suspend fun generateReply(
        userMessage: String,
        history: List<Pair<String, String>>
    ): String {
        val lower = userMessage.lowercase()

        return when {
            listOf("ruido", "sonido", "golpe", "chirrido").any { it in lower } ->
                "Cuando escuchas un ruido extraño es importante fijarte si ocurre al frenar, acelerar o pasar por baches. Describe cuándo aparece el ruido y en qué zona del vehículo lo sientes para darte una recomendación más precisa."

            listOf("check engine", "motor", "luz amarilla").any { it in lower } ->
                "La luz de check engine indica que la ECU detectó una anomalía. Lo ideal es escanear el vehículo con un scanner OBD2 para leer el código de falla y evitar seguir conduciendo si notas pérdida de potencia, humo o temperatura alta."

            listOf("mantenimiento", "aceite", "cambio de aceite", "servicio").any { it in lower } ->
                "El cambio de aceite suele hacerse entre 5,000 y 10,000 km según el tipo de aceite y uso del vehículo. También conviene revisar filtros de aire, filtro de combustible y bujías según el kilometraje recomendado por el fabricante."

            listOf("no enciende", "no prende", "tarda en encender", "arranque").any { it in lower } ->
                "Si el vehículo tarda en encender o no arranca, revisa batería, estado de bornes, sonido del motor de arranque y si se encienden las luces del tablero. Detalla los síntomas y te ayudo a acotar posibles causas."

            else ->
                "Soy tu asistente de MyCarSetting. Describe el ruido, la luz en el tablero o el problema que presenta tu vehículo y te daré una guía general de qué revisar o cómo explicárselo a tu mecánico."
        }
    }
}
