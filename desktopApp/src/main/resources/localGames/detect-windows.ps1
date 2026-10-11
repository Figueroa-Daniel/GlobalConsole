$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$items = [System.Collections.Generic.List[object]]::new()
$ws = New-Object -ComObject WScript.Shell
$folders = @(
    "$env:ProgramData\Microsoft\Windows\Start Menu\Programs\Games",
    "$env:APPDATA\Microsoft\Windows\Start Menu\Programs\Games",
    "$env:ProgramData\Microsoft\Windows\Start Menu\Programs\Juegos",
    "$env:APPDATA\Microsoft\Windows\Start Menu\Programs\Juegos",
    "$env:LOCALAPPDATA\Microsoft\Windows\GameExplorer"
)
foreach ($folder in $folders) {
    Get-ChildItem -LiteralPath $folder -Filter '*.lnk' -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
        $link = $ws.CreateShortcut($_.FullName)
        if ($link.TargetPath -match '\.exe$' -and (Test-Path -LiteralPath $link.TargetPath -PathType Leaf)) {
            $items.Add(@{ id = $_.FullName; name = $_.BaseName; target = $link.TargetPath; arguments = $link.Arguments; directory = $link.WorkingDirectory; icon = '' })
        }
    }
}
$keys = @(
    'HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\*',
    'HKLM:\SOFTWARE\WOW6432Node\Microsoft\Windows\CurrentVersion\Uninstall\*',
    'HKCU:\Software\Microsoft\Windows\CurrentVersion\Uninstall\*'
)
foreach ($key in $keys) {
    Get-ItemProperty -Path $key -ErrorAction SilentlyContinue | ForEach-Object {
        $isGame = ($_.Category -match '^(Game|Games|Juego|Juegos)$') -or ($_.InstallLocation -match '[\\/](Games|Juegos)[\\/]')
        $target = ([string]$_.DisplayIcon -replace ',\s*-?\d+$', '').Trim('"')
        if ($isGame -and $_.DisplayName -and $target -match '\.exe$' -and (Test-Path -LiteralPath $target -PathType Leaf)) {
            $items.Add(@{ id = $_.PSPath; name = $_.DisplayName; target = $target; arguments = ''; directory = $_.InstallLocation; icon = '' })
        }
    }
}
foreach ($key in @('HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\GameUX', 'HKCU:\Software\Microsoft\Windows\CurrentVersion\GameUX')) {
    Get-ChildItem -Path $key -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
        $entry = Get-ItemProperty -LiteralPath $_.PSPath -ErrorAction SilentlyContinue
        $target = $entry.ConfigApplicationPath
        if ($target -and $target -match '\.exe$' -and (Test-Path -LiteralPath $target -PathType Leaf)) {
            $items.Add(@{ id = $_.PSPath; name = [System.IO.Path]::GetFileNameWithoutExtension($target); target = $target; arguments = ''; directory = [System.IO.Path]::GetDirectoryName($target); icon = '' })
        }
    }
}
ConvertTo-Json -InputObject @($items.ToArray()) -Compress -Depth 4
