# RDP masa�st� entegrasyonu

Bu RDP ba?lant?s?n? XPipe'da uygulamalar? ve komut dosyalar?n? h?zl? bir ?ekilde ba?latmak i�in kullanabilirsiniz. Ancak, RDP'nin do?as? gere?i, bunun �al??mas? i�in sunucunuzdaki uzak uygulama izin listesini d�zenlemeniz gerekir. Ayr?ca, bu se�enek uzak sunucunuzda komut dosyalar?n?z? �al??t?rmak i�in s�r�c� payla??m?n? etkinle?tirir.

Bunu yapmamay? da se�ebilir ve herhangi bir geli?mi? masa�st� entegrasyon �zelli?i kullanmadan RDP istemcinizi ba?latmak i�in sadece XPipe'? kullanabilirsiniz.

## RDP izin listeleri

Bir RDP sunucusu, uygulama ba?latma i?lemlerini ger�ekle?tirmek i�in izin listeleri kavram?n? kullan?r. Bu, izin listesi devre d??? b?rak?lmad?k�a veya belirli uygulamalar a�?k�a izin listesine eklenmedik�e, herhangi bir uzak uygulaman?n do?rudan ba?lat?lmas?n?n ba?ar?s?z olaca?? anlam?na gelir.

?zin listesi ayarlar?n? sunucunuzun kay?t defterinde `HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList` adresinde bulabilirsiniz.

### T�m uygulamalara izin veriliyor

T�m uzak uygulamalar?n do?rudan XPipe'dan ba?lat?lmas?na izin vermek i�in izin listesini devre d??? b?rakabilirsiniz. Bunun i�in sunucunuzda PowerShell'de a?a??daki komutu �al??t?rabilirsiniz: `Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList' -Name "fDisabledAllowList" -Value 1`.

### ?zin verilen uygulamalar? ekleme

Alternatif olarak, listeye tek tek uzak uygulamalar da ekleyebilirsiniz. Bu sayede listelenen uygulamalar? do?rudan XPipe'tan ba?latabilirsiniz.

`TSAppAllowList`'in `Applications` anahtar?n?n alt?nda, rastgele bir adla yeni bir anahtar olu?turun. ?sim i�in tek gereklilik, "Uygulamalar" anahtar?n?n alt anahtarlar? i�inde benzersiz olmas?d?r. Bu yeni anahtar, i�inde ?u de?erlere sahip olmal?d?r: `Name`, `Path` ve `CommandLineSetting`. Bunu PowerShell'de a?a??daki komutlarla yapabilirsiniz:

```
$appName="Notepad"
$appPath="C:\Windows\System32\notepad.exe"

$regKey="HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Terminal Server\TSAppAllowList\Applications"
New-item -Path "$regKey\$appName"
New-ItemProperty -Path "$regKey\$appName" -Name "Name" -Value "$appName" -Force
New-ItemProperty -Path "$regKey\$appName" -Name "Path" -Value "$appPath" -Force
New-ItemProperty -Path "$regKey\$appName" -Name "CommandLineSetting" -Value "1" -PropertyType DWord -Force
```

XPipe'?n komut dosyalar? �al??t?rmas?na ve terminal oturumlar? a�mas?na da izin vermek istiyorsan?z, `C:\Windows\System32\cmd.exe` dosyas?n? da izin verilenler listesine eklemeniz gerekir.

## G�venlik hususlar?

Bir RDP ba?lant?s? ba?lat?rken ayn? uygulamalar? her zaman manuel olarak �al??t?rabilece?iniz i�in bu, sunucunuzu hi�bir ?ekilde g�vensiz hale getirmez. ?zin listeleri daha �ok istemcilerin kullan?c? giri?i olmadan herhangi bir uygulamay? an?nda �al??t?rmas?n? �nlemeye y�neliktir. G�n�n sonunda, XPipe'?n bunu yapaca??na g�venip g�venmemek size kalm??. Bu ba?lant?y? kutudan �?kt??? gibi ba?latabilirsiniz, bu yaln?zca XPipe'daki geli?mi? masa�st� entegrasyon �zelliklerinden herhangi birini kullanmak istiyorsan?z kullan??l?d?r.
