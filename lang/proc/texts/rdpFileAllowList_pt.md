# Integra��o do ambiente de trabalho RDP

Podes utilizar esta liga��o RDP no XPipe para lan�ar rapidamente aplica��es e scripts. No entanto, devido � natureza do RDP, tens de editar a lista de permiss�es de aplica��es remotas no teu servidor para que isto funcione. Al�m disso, esta op��o permite a partilha de unidades para executar os teus scripts no teu servidor remoto.

Tamb�m podes optar por n�o fazer isto e utilizar apenas o XPipe para lan�ar o cliente RDP sem utilizar quaisquer funcionalidades avan�adas de integra��o do ambiente de trabalho.

## Listas de permiss�es RDP

Um servidor RDP usa o conceito de listas de permiss�o para lidar com lan�amentos de aplicativos. Isso significa essencialmente que, a menos que a lista de permiss�es esteja desativada ou que aplicativos espec�ficos tenham sido explicitamente adicionados � lista de permiss�es, o lan�amento de qualquer aplicativo remoto diretamente falhar�.

Podes encontrar as defini��es da lista de permiss�es no registo do teu servidor em `HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList`.

### Permitir todas as aplica��es

Podes desativar a lista de permiss�es para permitir que todas as aplica��es remotas sejam iniciadas diretamente a partir do XPipe. Para tal, podes executar o seguinte comando no teu servidor em PowerShell: `Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList' -Name "fDisabledAllowList" -Value 1`.

### Adicionar aplica��es permitidas

Em alternativa, podes tamb�m adicionar aplica��es remotas individuais � lista. Isto permitir-te-� iniciar as aplica��es listadas diretamente a partir do XPipe.

Sob a chave `Applications` de `TSAppAllowList`, cria uma nova chave com um nome arbitr�rio. O �nico requisito para o nome � que ele seja exclusivo dentro dos filhos da chave "Applications". Essa nova chave deve ter os seguintes valores: `Name`, `Path` e `CommandLineSetting`. Podes fazer isto no PowerShell com os seguintes comandos:

```
$appName="Bloco de Notas"
$appPath="C:\Windows\System32\notepad.exe"

$regKey="HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList\Applications"
Novo item -Path "$regKey\$appName"
Novo-ItemProperty -Path "$regKey\$appName" -Name "Nome" -Value "$appName" -Force
Novo-ItemProperty -Path "$regKey\$appName" -Nome "Caminho" -Valor "$appPath" -Force
Novo-ItemProperty -Path "$regKey\$appName" -Name "CommandLineSetting" -Value "1" -PropertyType DWord -Force
<c�digo>`</c�digo>

Se quiseres permitir que o XPipe tamb�m execute scripts e abra sess�es de terminal, tens de adicionar `C:\Windows\System32\cmd.exe` � lista de permiss�es tamb�m.

## Considera��es de seguran�a

Isto n�o torna o teu servidor inseguro de forma alguma, uma vez que podes sempre executar as mesmas aplica��es manualmente quando inicias uma liga��o RDP. As listas de permiss�o s�o mais destinadas a impedir que os clientes executem instantaneamente qualquer aplicativo sem a entrada do usu�rio. No final do dia, cabe-te a ti decidir se confias no XPipe para fazer isto. Podes iniciar esta liga��o sem problemas, isto s� � �til se quiseres utilizar qualquer uma das funcionalidades avan�adas de integra��o de ambiente de trabalho no XPipe.
