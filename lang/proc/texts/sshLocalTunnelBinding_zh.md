## 绑定

您提供的绑定信息将以如下方式直接传递给 `ssh` 客户端：`-L [origin_address:]origin_port:remote_address:remote_port`.

默认情况下，如果没有另行指定，origin 将绑定到环回接口。您也可以使用任何地址通配符，例如，将地址设置为 `0.0.0.0` 以绑定到通过 IPv4 访问的所有网络接口。如果完全省略地址，则将使用允许连接所有网络接口的通配符 `*`。请注意，有些网络接口符号可能不被所有操作系统支持。例如，Windows 服务器不支持通配符 `*`。