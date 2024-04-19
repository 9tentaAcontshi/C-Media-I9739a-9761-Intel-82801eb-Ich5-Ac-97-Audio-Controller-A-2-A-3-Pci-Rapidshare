# Aplicaciones remotas RDP

Puedes utilizar conexiones RDP en XPipe para lanzar r�pidamente aplicaciones y scripts remotos sin abrir un escritorio completo. Sin embargo, debido a la naturaleza de RDP, tienes que editar la lista de aplicaciones remotas permitidas en tu servidor para que esto funcione.

## Listas de permitidos RDP

Un servidor RDP utiliza el concepto de listas de permitidos para gestionar el lanzamiento de aplicaciones. Esto significa esencialmente que, a menos que la lista de permitidas est� desactivada o que se hayan a�adido expl�citamente aplicaciones espec�ficas a la lista de permitidas, el lanzamiento directo de cualquier aplicaci�n remota fallar�.

Puedes encontrar la configuraci�n de la lista de permitidas en el registro de tu servidor en `HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList`.

### Permitir todas las aplicaciones

Puedes desactivar la lista de permitidas para permitir que todas las aplicaciones remotas se inicien directamente desde XPipe. Para ello, puedes ejecutar el siguiente comando en tu servidor en PowerShell: `Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList' -Name "fDisabledAllowList" -Value 1`.

### A�adir aplicaciones permitidas

Tambi�n puedes a�adir aplicaciones remotas individuales a la lista. Esto te permitir� lanzar las aplicaciones de la lista directamente desde XPipe.

En la clave `Aplicaciones` de `TSAppAllowList`, crea una nueva clave con un nombre arbitrario. El �nico requisito para el nombre es que sea �nico dentro de los hijos de la clave "Aplicaciones". Esta nueva clave debe contener los siguientes valores: `Nombre`, `Ruta` y `Configuraci�n de la l�nea de comandos`. Puedes hacerlo en PowerShell con los siguientes comandos:

```
$appName="Bloc de notas"
$appPath="C:\Windows\System32\notepad.exe"

$regKey="HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList\Applications"
Nuevo-elemento -Ruta "$regKey\$appName"
Nuevo-elemento-Propiedad -Ruta "$regKey$$appName" -Nombre "Name" -Valor "$appName" -Force
Nueva-Propiedad-Art�culo -Ruta "$regKey\$NombreDeLaAplicacion" -Nombre "Ruta" -Valor "$rutaDeLaAplicacion" -Forzar
Nuevo-Item-Propiedad -Ruta "$regKey\$NombreDeLaAplicacion" -Nombre "CommandLineSetting" -Valor "1" -PropertyType DWord -Force
<c�digo>`</c�digo

Si quieres permitir que XPipe ejecute tambi�n scripts y abra sesiones de terminal, tienes que a�adir tambi�n `C:\Windows\System32\cmd.exe` a la lista de permitidos. 

## Consideraciones de seguridad

Esto no hace que tu servidor sea inseguro en modo alguno, ya que siempre puedes ejecutar las mismas aplicaciones manualmente al iniciar una conexi�n RDP. Las listas de permitidos est�n m�s pensadas para evitar que los clientes ejecuten instant�neamente cualquier aplicaci�n sin la intervenci�n del usuario. A fin de cuentas, depende de ti si conf�as en XPipe para hacer esto. Puedes iniciar esta conexi�n sin problemas, s�lo es �til si quieres utilizar alguna de las funciones avanzadas de integraci�n de escritorio de XPipe.
