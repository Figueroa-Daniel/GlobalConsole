# Detecta accesos ejecutables y excluye registros de juegos y navegadores.
# Autor: Daniel Figueroa Vidal. Fecha: 2026-10-11.
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$items = [System.Collections.Generic.List[object]]::new()
$excludedTargets = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
$gameRoots = [System.Collections.Generic.List[string]]::new()
$ws = New-Object -ComObject WScript.Shell
foreach ($key in @('HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\*', 'HKLM:\SOFTWARE\WOW6432Node\Microsoft\Windows\CurrentVersion\Uninstall\*', 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Uninstall\*')) {
    Get-ItemProperty -Path $key -ErrorAction SilentlyContinue | ForEach-Object {
        if ($_.Category -match '^(Game|Games|Juego|Juegos)$' -or $_.InstallLocation -match '[\\/](Games|Juegos|Epic Games|GOG Games)[\\/]|[\\/]steamapps[\\/]common[\\/]') {
            $target = ([string]$_.DisplayIcon -replace ',\s*-?\d+$', '').Trim('"')
            if ($target) { [void]$excludedTargets.Add($target) }
            if ($_.InstallLocation) { $gameRoots.Add(([string]$_.InstallLocation).TrimEnd('\') + '\') }
        }
    }
}
foreach ($key in @('HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\GameUX', 'HKCU:\Software\Microsoft\Windows\CurrentVersion\GameUX')) {
    Get-ChildItem -Path $key -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
        $entry = Get-ItemProperty -LiteralPath $_.PSPath -ErrorAction SilentlyContinue
        if ($entry.ConfigApplicationPath) { [void]$excludedTargets.Add($entry.ConfigApplicationPath) }
    }
}
foreach ($key in @('HKLM:\SOFTWARE\Clients\StartMenuInternet\*\shell\open\command', 'HKCU:\SOFTWARE\Clients\StartMenuInternet\*\shell\open\command', 'HKLM:\SOFTWARE\WOW6432Node\Clients\StartMenuInternet\*\shell\open\command')) {
    Get-Item -Path $key -ErrorAction SilentlyContinue | ForEach-Object {
        $command = [string]$_.GetValue('')
        if ($command -match '^\s*"([^"]+)"|^\s*(.+?\.exe)(?:\s|$)') {
            $target = if ($Matches[1]) { $Matches[1] } else { $Matches[2] }
            [void]$excludedTargets.Add($target)
        }
    }
}
# Las rutas de desinstalación y DisplayIcon solo clasifican: nunca se ejecutan.
function Add-Application($id, $name, $target, $arguments, $directory, $icon) {
    if (!$target -or $target -notmatch '\.exe$' -or !(Test-Path -LiteralPath $target -PathType Leaf)) { return }
    if ($excludedTargets.Contains($target) -or $id -match '[\\/](Games|Juegos|GameExplorer)[\\/]' -or $target -match '[\\/](Games|Juegos|Epic Games|GOG Games)[\\/]|[\\/]steamapps[\\/]common[\\/]') { return }
    foreach ($root in $gameRoots) { if ($target.StartsWith($root, [System.StringComparison]::OrdinalIgnoreCase)) { return } }
    $items.Add(@{ id = $id; name = $name; target = $target; arguments = $arguments; directory = $directory; icon = $icon })
}
foreach ($folder in @("$env:ProgramData\Microsoft\Windows\Start Menu\Programs", "$env:APPDATA\Microsoft\Windows\Start Menu\Programs")) {
    Get-ChildItem -LiteralPath $folder -Filter '*.lnk' -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
        $link = $ws.CreateShortcut($_.FullName)
        $icon = if ($link.IconLocation) { ($link.IconLocation -replace ',\s*-?\d+$', '').Trim('"') } else { $link.TargetPath }
        Add-Application $_.FullName $_.BaseName $link.TargetPath $link.Arguments $link.WorkingDirectory $icon
    }
}
foreach ($key in @('HKCU:\Software\Microsoft\Windows\CurrentVersion\App Paths\*', 'HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\App Paths\*', 'HKLM:\SOFTWARE\WOW6432Node\Microsoft\Windows\CurrentVersion\App Paths\*')) {
    Get-Item -Path $key -ErrorAction SilentlyContinue | ForEach-Object {
        $target = ([string]$_.GetValue('')).Trim('"')
        Add-Application $_.Name ([System.IO.Path]::GetFileNameWithoutExtension($target)) $target '' ([System.IO.Path]::GetDirectoryName($target)) $target
    }
}
ConvertTo-Json -InputObject @($items.ToArray()) -Compress -Depth 4
