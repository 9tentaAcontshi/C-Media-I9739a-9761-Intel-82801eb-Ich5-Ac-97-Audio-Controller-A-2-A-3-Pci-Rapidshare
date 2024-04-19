# Int�gration de bureau RDP

Tu peux utiliser cette connexion RDP dans XPipe pour lancer rapidement des applications et des scripts. Cependant, en raison de la nature du RDP, tu dois modifier la liste d'autorisation des applications distantes sur ton serveur pour que cela fonctionne. De plus, cette option permet le partage de lecteur pour ex�cuter tes scripts sur ton serveur distant.

Tu peux aussi choisir de ne pas le faire et d'utiliser simplement XPipe pour lancer ton client RDP sans utiliser de fonctions d'int�gration de bureau avanc�es.

## RDP allow lists

Un serveur RDP utilise le concept des listes d'autorisation pour g�rer le lancement des applications. Cela signifie essentiellement qu'� moins que la liste d'autorisation ne soit d�sactiv�e ou que des applications sp�cifiques n'aient �t� explicitement ajout�es � la liste d'autorisation, le lancement direct d'applications distantes �chouera.

Tu peux trouver les param�tres de la liste d'autorisation dans le registre de ton serveur � `HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList`.

### Autoriser toutes les applications

Tu peux d�sactiver la liste d'autorisation pour permettre � toutes les applications distantes d'�tre lanc�es directement � partir de XPipe. Pour cela, tu peux ex�cuter la commande suivante sur ton serveur en PowerShell : `Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAllowList' -Name "fDisabledAllowList" -Value 1`.

### Ajout d'applications autoris�es

Tu peux aussi ajouter des applications distantes individuelles � la liste. Cela te permettra alors de lancer les applications list�es directement � partir de XPipe.

Sous la cl� `Applications` de `TSAppAllowList`, cr�e une nouvelle cl� avec un nom arbitraire. La seule exigence pour le nom est qu'il soit unique parmi les enfants de la cl� "Applications". Cette nouvelle cl� doit contenir les valeurs suivantes : `Name`, `Path` et `CommandLineSetting`. Tu peux effectuer cette op�ration dans PowerShell � l'aide des commandes suivantes :

```
$appName="Notepad"
$appPath="C:\NWindows\NSystem32\NNotepad.exe"

$regKey="HKLM:\NSOFTWARE\NMicrosoft\NWindows NT\NCurrentVersion\NTerminal Server\NTSAllowList\NApplications"
New-item -Path "$regKey\$appName"
New-ItemProperty -Path "$regKey\NappName" -Name "Name" -Value "$appName" -Force
New-ItemProperty -Path "$regKey\$appName" -Name "Path" -Value "$appPath" -Force
New-ItemProperty -Path "$regKey\$appName" -Name "CommandLineSetting" -Value "1" -PropertyType DWord -Force
```

Si tu veux autoriser XPipe � ex�cuter des scripts et � ouvrir des sessions de terminal, tu dois �galement ajouter `C:\NWindows\NSystem32\cmd.exe` � la liste des autorisations.

## Consid�rations de s�curit�

Cela ne rend en aucun cas ton serveur non s�curis�, car tu peux toujours ex�cuter les m�mes applications manuellement lors du lancement d'une connexion RDP. Les listes d'autorisation ont plut�t pour but d'emp�cher les clients d'ex�cuter instantan�ment n'importe quelle application sans l'intervention de l'utilisateur. En fin de compte, c'est � toi de d�cider si tu fais confiance � XPipe pour cela. Tu peux lancer cette connexion sans probl�me, cela n'est utile que si tu veux utiliser l'une des fonctions d'int�gration de bureau avanc�es de XPipe.
