WHERE clink >NUL 2>NUL
IF %ERRORLEVEL%==0 (
    exit /b 0
)

SET "PATH=%PATH%;%TEMP%\xpipe\scriptdata\clink"
WHERE clink >NUL 2>NUL
IF %ERRORLEVEL%==0 (
    exit /b 0
)

echo ^
$downloader = New-Object System.Net.WebClient;^
$defaultCreds = [System.Net.CredentialCache]::DefaultCredentials;^
if ($defaultCreds) {^
    $downloader.Credentials = $defaultCreds^
}^
$downloader.DownloadFile("https://github.com/chrisant996/clink/releases/download/v1.6.5/clink.1.6.5.8f46a4.zip", "$env:TEMP\clink.zip");^
Expand-Archive -Force -LiteralPath "$env:TEMP\clink.zip" -DestinationPath "$env:TEMP\xpipe\scriptdata\clink"; | powershell -NoLogo >NUL
