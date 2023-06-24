<#
    .SYNOPSIS
    Downloads and installs XPipe on the local machine.

    .DESCRIPTION
    Retrieves the XPipe msi for the latest or a specified version, and
    downloads and installs the application to the local machine.

    .NOTES
    =====================================================================
    Copyright 2023 Christopher Schnick
    Copyright 2017 - 2020 Chocolatey Software, Inc, and the
    original authors/contributors from ChocolateyGallery
    Copyright 2011 - 2017 RealDimensions Software, LLC, and the
    original authors/contributors from ChocolateyGallery
    at https://github.com/chocolatey/chocolatey.org

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    =====================================================================

    Environment Variables, specified as $env:NAME in PowerShell.exe and %NAME% in cmd.exe.
    For an explicit version of XPipe, please set $env:xpipeVersion = 'versionnumber'

#>
[CmdletBinding(DefaultParameterSetName = 'Default')]
param(
    # Specifies a target version of XPipe to install. By default, the latest
    # stable version is installed.
    [Parameter(Mandatory = $false)]
    [string]
    $XPipeVersion = $null,

    # If set, will download releases from the staging repository instead.
    [Parameter(Mandatory = $false)]
    [switch]
    $UseStageDownloads = $false
)

#region Functions

function Get-Downloader {
    <#
    .SYNOPSIS
    Gets a System.Net.WebClient that respects relevant proxies to be used for
    downloading data.

    .DESCRIPTION
    Retrieves a WebClient object that is pre-configured according to specified
    environment variables for any proxy and authentication for the proxy.
    Proxy information may be omitted if the target URL is considered to be
    bypassed by the proxy (originates from the local network.)

    .PARAMETER Url
    Target URL that the WebClient will be querying. This URL is not queried by
    the function, it is only a reference to determine if a proxy is needed.

    .EXAMPLE
    Get-Downloader -Url $fileUrl

    Verifies whether any proxy configuration is needed, and/or whether $fileUrl
    is a URL that would need to bypass the proxy, and then outputs the
    already-configured WebClient object.
    #>
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $false)]
        [string]
        $Url
    )

    $downloader = New-Object System.Net.WebClient

    $defaultCreds = [System.Net.CredentialCache]::DefaultCredentials
    if ($defaultCreds) {
        $downloader.Credentials = $defaultCreds
    }

    $downloader
}

function Request-File {
    <#
    .SYNOPSIS
    Downloads a file from a given URL.

    .DESCRIPTION
    Downloads a target file from a URL to the specified local path.
    Any existing proxy that may be in use will be utilised.

    .PARAMETER Url
    URL of the file to download from the remote host.

    .PARAMETER File
    Local path for the file to be downloaded to.

    Downloads the file to the path specified in $targetFile.
    #>
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $false)]
        [string]
        $Url,

        [Parameter(Mandatory = $false)]
        [string]
        $File
    )

    Write-Host "Downloading $url to $file"
    (Get-Downloader $url).DownloadFile($url, $file)
}

function Set-PSConsoleWriter {
    <#
    .SYNOPSIS
    Workaround for a bug in output stream handling PS v2 or v3.

    .DESCRIPTION
    PowerShell v2/3 caches the output stream. Then it throws errors due to the
    FileStream not being what is expected. Fixes "The OS handle's position is
    not what FileStream expected. Do not use a handle simultaneously in one
    FileStream and in Win32 code or another FileStream." error.

    .EXAMPLE
    Set-PSConsoleWriter

    .NOTES
    General notes
    #>

    [CmdletBinding()]
    param()
    if ($PSVersionTable.PSVersion.Major -gt 3) {
        return
    }

    try {
        # http://www.leeholmes.com/blog/2008/07/30/workaround-the-os-handles-position-is-not-what-filestream-expected/ plus comments
        $bindingFlags = [Reflection.BindingFlags] "Instance,NonPublic,GetField"
        $objectRef = $host.GetType().GetField("externalHostRef", $bindingFlags).GetValue($host)

        $bindingFlags = [Reflection.BindingFlags] "Instance,NonPublic,GetProperty"
        $consoleHost = $objectRef.GetType().GetProperty("Value", $bindingFlags).GetValue($objectRef, @())
        [void] $consoleHost.GetType().GetProperty("IsStandardOutputRedirected", $bindingFlags).GetValue($consoleHost, @())

        $bindingFlags = [Reflection.BindingFlags] "Instance,NonPublic,GetField"
        $field = $consoleHost.GetType().GetField("standardOutputWriter", $bindingFlags)
        $field.SetValue($consoleHost, [Console]::Out)

        [void] $consoleHost.GetType().GetProperty("IsStandardErrorRedirected", $bindingFlags).GetValue($consoleHost, @())
        $field2 = $consoleHost.GetType().GetField("standardErrorWriter", $bindingFlags)
        $field2.SetValue($consoleHost, [Console]::Error)
    } catch {
        Write-Warning "Unable to apply redirection fix."
    }
}

function Uninstall {
    [CmdletBinding()]
    param()

    # Quick heuristic to see whether is can be possibly installed
    if (-not (Test-Path "$env:LOCALAPPDATA\XPipe" -PathType Container)) {
        return
    }

    Write-Host "Looking for previous XPipe installations ..."

    $cim = Get-CimInstance Win32_Product | Where {$_.Name -match 'XPipe' } | Select-Object -First 1
    if ($cim) {
        $message = @(
            "Uninstalling existing XPipe $($cim.Version) installation ..."
        ) -join [Environment]::NewLine
        Write-Host $message
        Invoke-CimMethod -InputObject $cim -Name Uninstall
    }
}

#endregion Functions

#region Pre-check

# Ensure we have all our streams setup correctly, needed for older PSVersions.
Set-PSConsoleWriter

Uninstall

#endregion Pre-check

#region Setup

if ($UseStageDownloads) {
    $XPipeRepoUrl = "https://github.com/xpipe-io/xpipe_staging"
} else {
    $XPipeRepoUrl = "https://github.com/xpipe-io/xpipe"
}

if ($XPipeVersion) {
    $XPipeDownloadUrl = "$XPipeRepoUrl/releases/download/$XPipeVersion"
} else {
    $XPipeDownloadUrl = "$XPipeRepoUrl/releases/latest/download"
}

$XPipeDownloadUrl = "$XPipeDownloadUrl/xpipe-installer-windows-x86_64.msi"

if (-not $env:TEMP) {
    $env:TEMP = Join-Path $env:SystemDrive -ChildPath 'temp'
}

$xpipeTempDir = Join-Path $env:TEMP -ChildPath "xpipe"
$tempDir = Join-Path $xpipeTempDir -ChildPath "install"

if (-not (Test-Path $tempDir -PathType Container)) {
    $null = New-Item -Path $tempDir -ItemType Directory
}

#endregion Setup

#region Download & Extract XPipe

$file = Join-Path $tempDir "xpipe-installer.msi"
Write-Host "Getting XPipe from $XPipeRepoUrl."
Request-File -Url $XPipeDownloadUrl -File $file

#endregion Download & Extract XPipe

#region Install XPipe

Write-Host "Installing XPipe ..."
$result = msiexec /i "$file" /quiet

# Wait for Path information from installation to update
Start-Sleep 5

# Update current process PATH environment variable
$env:Path=(
    [System.Environment]::GetEnvironmentVariable("Path", "Machine"),
    [System.Environment]::GetEnvironmentVariable("Path", "User")
) -match '.' -join ';'

xpipe open

#endregion Install XPipe
