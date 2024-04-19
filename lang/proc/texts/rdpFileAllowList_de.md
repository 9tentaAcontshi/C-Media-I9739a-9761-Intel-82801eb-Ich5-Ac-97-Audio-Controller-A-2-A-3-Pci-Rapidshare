# RDP-Desktop-Integration

Du kannst diese RDP-Verbindung in XPipe nutzen, um Anwendungen und Skripte schnell zu starten. Aufgrund der Natur von RDP musst du jedoch die Liste der zugelassenen Remote-Anwendungen auf deinem Server bearbeiten, damit dies funktioniert. Au�erdem erm�glicht diese Option die gemeinsame Nutzung von Laufwerken, um deine Skripte auf dem entfernten Server auszuf�hren.

Du kannst auch darauf verzichten und einfach XPipe verwenden, um deinen RDP-Client zu starten, ohne die erweiterten Funktionen der Desktop-Integration zu nutzen.

## RDP allow lists

Ein RDP-Server verwendet das Konzept der Zulassen-Listen, um den Start von Anwendungen zu steuern. Das bedeutet, dass der direkte Start von Remote-Anwendungen fehlschl�gt, es sei denn, die Zulassungsliste ist deaktiviert oder bestimmte Anwendungen wurden explizit in die Zulassungsliste aufgenommen.

Du findest die Einstellungen f�r die Erlaubnisliste in der Registrierung deines Servers unter `HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList`.

### Alle Anwendungen zulassen

Du kannst die Zulassen-Liste deaktivieren, damit alle Remote-Anwendungen direkt von XPipe aus gestartet werden k�nnen. Dazu kannst du den folgenden Befehl auf deinem Server in der PowerShell ausf�hren: `Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList' -Name "fDisabledAllowList" -Value 1`.

### Hinzuf�gen von erlaubten Anwendungen

Alternativ kannst du auch einzelne Remote-Anwendungen zu der Liste hinzuf�gen. Dann kannst du die aufgelisteten Anwendungen direkt von XPipe aus starten.

Erstelle unter dem Schl�ssel `Anwendungen` der `TSAppAllowList` einen neuen Schl�ssel mit einem beliebigen Namen. Die einzige Bedingung f�r den Namen ist, dass er innerhalb der Kinder des Schl�ssels "Anwendungen" eindeutig ist. Dieser neue Schl�ssel muss die folgenden Werte enthalten: `Name`, `Pfad` und `CommandLineSetting`. Du kannst dies in der PowerShell mit den folgenden Befehlen tun:

```
$appName="Notepad"
$appPath="C:\Windows\System32\notepad.exe"

$regKey="HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList\Applications"
New-item -Path "$regKey\$appName"
New-ItemProperty -Path "$regKey\$appName" -Name "Name" -Value "$appName" -Force
New-ItemProperty -Path "$regKey\$appName" -Name "Path" -Wert "$appPath" -Force
New-ItemProperty -Pfad "$regKey\$appName" -Name "CommandLineSetting" -Wert "1" -PropertyType DWord -Force
```

Wenn du XPipe auch das Ausf�hren von Skripten und das �ffnen von Terminalsitzungen erlauben willst, musst du `C:\Windows\System32\cmd.exe` ebenfalls in die Erlaubnisliste aufnehmen.

## Sicherheits�berlegungen

Das macht deinen Server in keiner Weise unsicher, denn du kannst dieselben Anwendungen immer manuell ausf�hren, wenn du eine RDP-Verbindung startest. Erlaubt-Listen sind eher dazu gedacht, Clients daran zu hindern, jede Anwendung ohne Benutzereingabe sofort auszuf�hren. Letzten Endes liegt es an dir, ob du XPipe in dieser Hinsicht vertraust. Du kannst diese Verbindung ganz einfach starten. Das ist nur dann sinnvoll, wenn du eine der erweiterten Desktop-Integrationsfunktionen von XPipe nutzen willst.
