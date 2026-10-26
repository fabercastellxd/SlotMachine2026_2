# Muestra en la terminal el resumen de JaCoCo (jacoco.csv) y PMD (pmd.xml).
# Uso: powershell -ExecutionPolicy Bypass -File herramientas\resumen.ps1 <carpeta de informes> <nombre>
param([string]$carpeta, [string]$nombre)

$filas = Import-Csv (Join-Path $carpeta "jacoco.csv")
$im = ($filas | Measure-Object INSTRUCTION_MISSED -Sum).Sum
$ic = ($filas | Measure-Object INSTRUCTION_COVERED -Sum).Sum
$bm = ($filas | Measure-Object BRANCH_MISSED -Sum).Sum
$bc = ($filas | Measure-Object BRANCH_COVERED -Sum).Sum
$instr = [math]::Floor(100 * $ic / ($im + $ic))
$ramas = [math]::Floor(100 * $bc / ($bm + $bc))

[xml]$xml = Get-Content -Raw -Encoding UTF8 (Join-Path $carpeta "pmd.xml")
$prioridades = @()
foreach ($file in $xml.pmd.file) { foreach ($v in $file.violation) { $prioridades += [int]$v.priority } }

Write-Host ""
Write-Host "================ RESUMEN ($nombre) ================"
Write-Host "JaCoCo: $instr % de instrucciones y $ramas % de ramas cubiertas (meta: mas del 75 %)"
Write-Host "PMD:    $($prioridades.Count) avisos en total"
foreach ($p in 1..5) {
    $n = @($prioridades | Where-Object { $_ -eq $p }).Count
    $linea = "        prioridad $p : $n"
    if ($p -eq 1) {
        $linea += "   (alta, rojo; meta: 0)"
        if ($n -gt 0) { Write-Host $linea -ForegroundColor Red } else { Write-Host $linea -ForegroundColor Green }
    } else {
        Write-Host $linea
    }
}
Write-Host "==================================================="
Write-Host ""
