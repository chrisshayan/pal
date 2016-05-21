#!/bin/sh
scp -i id_rsa_iamprogrammer_work -r dist admin@iamprogrammer.work:~/deploy/pal
# ssh -i id_rsa_iamprogrammer_work -t admin@iamprogrammer.work "sudo ln -s ~/deploy/pal/dist/ /usr/share/nginx/html/pal"
