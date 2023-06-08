<img src="https://user-images.githubusercontent.com/72509152/213873342-7638e830-8a95-4b5d-ad3e-5a9a0b4bf538.png" alt="drawing" width="250"/>

### The remote file browser for professionals

XPipe is a brand-new type of remote file browser that works by interacting with your installed command-line programs via stdout, stderr, and stdin to establish local and remote shell connections. This approach makes it much more flexible as it doesn't have to deal with any file system APIs, remote file handling protocols, or libraries at all as that part is delegated to other programs.

It comes with all file management features you would expect plus a dedicated remote connection manager to organize and open remote shell connections to your entire infrastructure.

XPipe fully integrates with your existing tools such as your favourite text/code editors, terminals, shells, command-line tools and more. The platform is designed to be extensible, allowing anyone to add easily support for more tools or to implement custom functionality through a modular extension system.

The full feature set is currently supported for:
- Containers located on any host such as [docker](https://www.docker.com/) or [LXD](https://linuxcontainers.org/lxd/introduction/) container instances
- [SSH](https://www.ssh.com/academy/ssh/protocol) connections
- [Windows Subsystem for Linux](https://ubuntu.com/wsl) instances
- [Powershell Remote Sessions](https://learn.microsoft.com/en-us/powershell/scripting/learn/remoting/running-remote-commands?view=powershell-7.3)
- [Kubernetes](https://kubernetes.io/) clusters and their contained pods and containers
- Any other custom remote connection methods that works through the command-line
- Arbitrary types of proxies to establish connections

The project is still in a relatively early stage and will benefit massively from your feedback, issue reports, feature request, and more. There are also a lot more features to come in the future.

You have more questions? Then check out the new [FAQ](/FAQ.md).

## Downloads

### Installers

Installers are the easiest way to get started and come with an optional automatic update functionality. The following installers are available:

- [Windows .msi Installer (x86-64)](https://github.com/xpipe-io/xpipe/releases/latest/download/xpipe-installer-windows-x86_64.msi)
- [Linux .deb Installer (x86-64)](https://github.com/xpipe-io/xpipe/releases/latest/download/xpipe-installer-linux-x86_64.deb)
- [Linux .rpm Installer (x86-64)](https://github.com/xpipe-io/xpipe/releases/latest/download/xpipe-installer-linux-x86_64.rpm)
- [MacOS .pkg Installer (x86-64)](https://github.com/xpipe-io/xpipe/releases/latest/download/xpipe-installer-macos-x86_64.pkg)
- [MacOS .pkg Installer (ARM 64)](https://github.com/xpipe-io/xpipe/releases/latest/download/xpipe-installer-macos-arm64.pkg)

### Portable

If you don't like installers, you can also use portable versions that are packaged as an archive. The following portable versions are available:

- [Windows .zip Portable (x86-64)](https://github.com/xpipe-io/xpipe/releases/latest/download/xpipe-portable-windows-x86_64.zip)
- [Linux .tar.gz Portable (x86-64)](https://github.com/xpipe-io/xpipe/releases/latest/download/xpipe-portable-linux-x86_64.tar.gz)
- [MacOS .dmg Portable (x86-64)](https://github.com/xpipe-io/xpipe/releases/latest/download/xpipe-portable-macos-x86_64.dmg)
- [MacOS .dmg Portable (ARM 64)](https://github.com/xpipe-io/xpipe/releases/latest/download/xpipe-portable-macos-arm64.dmg)

### Install Script (Linux / MacOS)

You can also install XPipe by pasting the installation command into your terminal. This will perform the full setup automatically.

```
bash <(curl -sL https://raw.githubusercontent.com/xpipe-io/xpipe/master/get-xpipe.sh)
```

## Remote File Browser

- Interact with the file system of any remote system using a workflow optimized for professionals
- Quickly open a terminal into any directory
- Utilize your favourite local programs to open and edit remote files
- Has the same feature set for all supported connection types
- Dynamically elevate sessions with sudo when required

The feature set is the same for all supported connection types. It of course also supports browsing the file system on your local machine.

![Remote file explorer](https://user-images.githubusercontent.com/72509152/230100929-4476f76c-ea81-43d9-ac4a-b3b02df2334e.png)

## Connection Hub

- Easily create and manage all kinds of remote connections, all in one place
- Allows you to fully customize the init environment of the launched shell sessions with custom scripts
- Securely stores all information exclusively on your computer and encrypts all secret information. See the [security page](/SECURITY.md) for more information
- Create desktop shortcuts that automatically open remote connections in your terminal

![connections](https://github.com/xpipe-io/xpipe/assets/72509152/75869979-df32-4106-880c-7036137e0c96)

## Terminal Launcher

- Automatically login into a shell in your favourite terminal with one click (no need to fill password prompts, etc.)
- Works for all kinds of shells and connections, locally and remote.
- Supports command shells (e.g. bash, PowerShell, cmd, etc.) and some database shells (e.g. PostgreSQL Shell)
- Comes with support for all commonly used terminal emulators across all operating systems
- Supports launches from the GUI or directly from the command-line
- Solves all encoding issues on Windows systems as all Windows shells are launched in UTF8 mode by default

## Further information

For information about the security model of XPipe, see the [security page](/SECURITY.md).

For information about the privacy policy of XPipe, see the [privacy page](/PRIVACY.md).

In case you're interested in development, check out the [development page](/DEVELOPMENT.md).

If you want to talk you can also join:

- The [XPipe Discord Server](https://discord.gg/8y89vS8cRb)
- The [XPipe Slack Server](https://join.slack.com/t/XPipe/shared_invite/zt-1awjq0t5j-5i4UjNJfNe1VN4b_auu6Cg)

