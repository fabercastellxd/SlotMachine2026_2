------------------------------------------------------------------------
PROYECTO INICIAL SLOTMACHINE - CICLO 5 (Refactoring y Extension)
------------------------------------------------------------------------

PROJECT TITLE: SlotMachine
PURPOSE OF PROJECT: Simulador de una maquina tragamonedas inspirado en el
    Problem I "Slot Machine" de la maraton internacional ICPC 2025.
VERSION or DATE: 5.0 - Ciclo 5, octubre de 2026
AUTHORS: Maria Angelica Perez Mesa - Mateo Sanchez Garcia
REPOSITORY: https://github.com/fabercastellxd/SlotMachine2026_2

HOW TO START THIS PROJECT (VS Code):
    1. Abrir esta carpeta en Visual Studio Code con el Extension Pack for
       Java. .vscode/settings.json ya indica las carpetas de codigo
       (src/main/java y src/test/java) y la libreria de JUnit (lib).
    2. Requiere JDK 21.
    3. Pruebas de unidad: vista Testing de VS Code.
       SlotMachineC1Test, SlotMachineC2Test, SlotMachineC4Test,
       SlotMachineCC4Test, SlotMachineContestTest, SlotMachineContestCTest.
    4. Prueba de aceptacion: SlotMachineAceptacionTest. Se corre a mano desde
       la vista Testing, porque al final le pregunta al usuario si se
       distinguen los tipos de ruedas y simbolos.

HERRAMIENTAS:
    lib\junit-platform-console-standalone-1.10.2.jar   JUnit 5
    herramientas\jacoco\lib\jacocoagent.jar, jacococli.jar   JaCoCo 0.8.15
        https://www.jacoco.org/jacoco/
    herramientas\pmd-bin-7.13.0                         PMD 7.13.0
        https://github.com/pmd/pmd/releases/tag/pmd_releases%2F7.13.0
    Si no estan, descargarlas y descomprimirlas en esas rutas.

INFORMES (JaCoCo y PMD):
    informes.bat inicial    o    informes.bat final
      inicial: usa el codigo del ciclo 4, antes de los cambios (codigo-inicial\src)
      final:   usa el codigo actual (src)
    Al terminar muestra en la terminal el resumen: cubrimiento y avisos de PMD
    por prioridad.
    Compila el simulador (build\main) y las pruebas (build\test), corre las
    pruebas de unidad con el agente de JaCoCo (sin la prueba de aceptacion),
    ejecuta PMD (reglas quickstart) y deja en ..\informes\<nombre>:
      - jacoco\index.html     cubrimiento de las pruebas de unidad
      - jacoco.csv            el mismo cubrimiento en tabla
      - pmd.html, pmd.xml     avisos de PMD
      - pmd_prioridades.html  resumen de avisos por prioridad (1 = rojo)

USER INSTRUCTIONS:
    SlotMachine()            maquina visible con 3 ruedas.
    SlotMachine(n)           maquina de n ruedas y n simbolos, aleatoria.
    addWheel / delWheel      agrega o elimina ruedas (tipos normal, lefty, rebel).
    addSymbol / delSymbol    agrega o elimina simbolos (tipos normal,
                             ephemeral, shy, joker).
    spin, swap, lock, unlock, placeSymbol, configuration, isJackpot ...
    SlotMachineContest.solve(n) / simulate(n): solucion del problema de la maraton.
