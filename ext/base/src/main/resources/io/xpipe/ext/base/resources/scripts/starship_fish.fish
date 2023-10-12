set dir ~/.xpipe/scriptdata/starship
export PATH="$PATH:$dir"
which starship > /dev/null
if [ $status != 0 ]
    mkdir -p "$dir" && \
    which curl > /dev/null && \
    curl -sS https://starship.rs/install.sh | sh /dev/stdin -y --bin-dir "$dir" > /dev/null
end
starship init fish | source