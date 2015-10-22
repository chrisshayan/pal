#!/bin/sh
for entry in "dist"/* "dist"/**/* "dist"/**/**/* "dist"/**/**/**/*
do
    folder=$(printf '%s' "$entry" | sed 's/dist//')
    if [ ! -d "$entry" ]; then
        scp -i id_rsa_iamprogrammer_work $entry admin@iamprogrammer.work:/usr/share/nginx/html/pal/$folder
    else
        ssh -i id_rsa_iamprogrammer_work admin@iamprogrammer.work "mkdir -p /usr/share/nginx/html/pal/$folder"
    fi
done
open http://iamprogrammer.work/pal
