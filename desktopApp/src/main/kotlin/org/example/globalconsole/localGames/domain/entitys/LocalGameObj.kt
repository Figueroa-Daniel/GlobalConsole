package org.example.globalconsole.localGames.domain.entitys

import org.example.globalconsole.generalDomain.entititys.Game
import org.example.globalconsole.generalDomain.entititys.Platforms

/**
 * Juego instalado con argumentos de ejecución sin interpretación de shell.
 * @param id Identificador estable.
 * @param name Nombre visible.
 * @param arguments Ejecutable y argumentos.
 * @param image Icono local opcional.
 * @param workingDirectory Directorio de trabajo opcional.
 * @return Unit.
 * @throws Nothing No lanza excepciones directamente.
 * @author Daniel Figueroa Vidal
 * @since 2026-10-11
 */
data class LocalGameObj(
    override val id: String,
    override val name: String,
    val arguments: List<String>,
    override val image: String? = null,
    val workingDirectory: String? = null
) : Game(id, name, arguments.joinToString(" "), image, Platforms.LOCALGAME)
